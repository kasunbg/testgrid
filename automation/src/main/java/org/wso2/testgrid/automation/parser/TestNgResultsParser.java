/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.testgrid.automation.parser;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.testgrid.automation.exception.ResultParserException;
import org.wso2.testgrid.common.TestCase;
import org.wso2.testgrid.common.TestGridConstants;
import org.wso2.testgrid.common.TestScenario;
import org.wso2.testgrid.common.util.DataBucketsHelper;
import org.wso2.testgrid.common.util.FileUtil;
import org.wso2.testgrid.common.util.TestGridUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * Surefire reports parser implementation related to parsing testng integration
 * test results.
 * <p>
 * List of file and dir names that'll be archived are 'surefire-reports',
 * and 'automation.log' currently.
 *
 * @since 1.0.0
 */
public class TestNgResultsParser extends ResultParser {

    public static final String RESULTS_INPUT_FILE = "testng-results.xml";
    private static final String[] ARCHIVABLE_FILES = new String[] { "surefire-reports", "automation.log" };
    private static final Logger logger = LoggerFactory.getLogger(TestNgResultsParser.class);
    private static final String TOTAL = "total";
    private static final String FAILED = "failed";
    private static final String PASSED = "passed";
    private static final String SKIPPED = "skipped";
    private final XMLInputFactory factory = XMLInputFactory.newInstance();

    /**
     * This constructor is used to create a {@link TestNgResultsParser} object with the
     * scenario details.
     *
     * @param testScenario TestScenario to be parsed
     * @param testLocation location of the test artifacts
     */
    public TestNgResultsParser(TestScenario testScenario, String testLocation) {
        super(testScenario, testLocation);
    }

    /**
     * <pre>
     * <testng-results skipped="2" failed="1" total="17" passed="14">
     *  <reporter-output>
     *  </reporter-output>
     *  <suite name="apim-automation-tests-suite-1"
     *          duration-ms="41210" started-at="2018-06-28T10:16:25Z" finished-at="2018-06-28T10:17:06Z">
     *      <test name="apim-startup-tests" duration-ms="15825"
     *          started-at="2018-06-28T10:16:50Z" finished-at="2018-06-28T10:17:06Z">
     *      <class name="org.wso2.am.integration.tests.server.mgt.APIMgtServerStartupTestCase">
     *      <test-method status="PASS" signature="setEnvironment()" name="setEnvironment" is-config="true"
     *          duration-ms="98" started-at="2018-06-28T10:16:24Z" finished-at="2018-06-28T10:16:25Z">
     *      </test-method>
     *      <test-method status="PASS" signature="testVerifyLogs()" name="testVerifyLogs" duration-ms="600"
     *          started-at="2018-06-28T10:16:50Z" description="verify server startup errors"
     *          finished-at="2018-06-28T10:16:51Z">
     *      </test-method>
     *      <test-method status="PASS" signature="disconnectFromOSGiConsole()" name="disconnectFromOSGiConsole"
     *          is-config="true" duration-ms="1" started-at="2018-06-28T10:17:01Z" finished-at="2018-06-28T10:17:01Z">
     *      </test-method>
     *      </class>
     *   </suite>
     *  </testng-results>
     * </pre>
     * <p>
     * one test class == one testgrid testcase.
     *
     * @throws ResultParserException parsing error
     */
    @Override
    public void parseResults() throws ResultParserException {
        final Path dataBucket = DataBucketsHelper.getOutputLocation(testScenario.getTestPlan());
        Set<Path> inputFiles = getResultInputFiles(dataBucket);

        final Path outputLocation = DataBucketsHelper.getOutputLocation(testScenario.getTestPlan());
        logger.info("Found testng-results.xml result files at: " + inputFiles.stream().map
                (outputLocation::relativize).collect(Collectors.toSet()));
        for (Path resultsFile : inputFiles) {
            try (final InputStream stream = Files.newInputStream(resultsFile, StandardOpenOption.READ)) {
                logger.info("Processing results file: " + outputLocation.relativize(resultsFile));
                if (logger.isDebugEnabled()) {
                    logger.debug(
                            "File content: " + new String(Files.readAllBytes(resultsFile), StandardCharsets.UTF_8));
                }

                final XMLEventReader eventReader = XMLInputFactory.newInstance().createXMLEventReader(stream);
                while (eventReader.hasNext()) {
                    XMLEvent event = eventReader.nextEvent();
                    if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
                        StartElement startElement = event.asStartElement();
                        if (startElement.getName().getLocalPart().equals("class")) {
                            final String classNameStr = getClassName(startElement);
                            List<TestCase> testCases = getTestCasesFor(classNameStr, eventReader);
                            logger.info(String.format("Found %s test cases in class '%s'", testCases.size(),
                                    classNameStr));
                            testCases.stream().forEachOrdered(tc -> testScenario.addTestCase(tc));
                        }
                    }
                }
                logger.info(String.format("Found total of %s test cases. %s test cases has failed.", testScenario
                                .getTestCases().size(),
                        testScenario.getTestCases().stream().filter(tc -> !tc.isSuccess()).count()));
            } catch (IOException | XMLStreamException e) {
                logger.error("Error while parsing testng-results.xml at " + resultsFile + " for " +
                        testScenario.getName(), e);
            }
        }
    }

    /**
     * Read the name attribute from the classElement input.
     *
     * @param classElement the class element
     * @return the name attribute
     */
    private String getClassName(StartElement classElement) {
        String classNameStr = "unknown";
        final Iterator attributes = classElement.getAttributes();
        while (attributes.hasNext()) {
            Attribute att = (Attribute) attributes.next();
            if (att.getName().getLocalPart().equals("name")) {
                classNameStr = att.getValue();
            }
        }
        return classNameStr;
    }

    /**
     * Searches the child elements of class element for test-methods where
     * status == !PASS.
     *
     * @param classNameStr class name
     * @param eventReader  XMLEventReader
     * @return true if all test-methods has PASS status, false otherwise.
     * @throws XMLStreamException {@link XMLStreamException}
     */
    private List<TestCase> getTestCasesFor(String classNameStr, XMLEventReader eventReader) throws XMLStreamException {
        List<TestCase> testCases = new ArrayList<>();
        while (eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();
            if (event.getEventType() == XMLStreamConstants.END_ELEMENT &&
                    event.asEndElement().getName().getLocalPart().equals("class")) {
                break;
            }
            if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
                final StartElement element = event.asStartElement();
                if (element.getName().getLocalPart().equals("test-method")) {
                    final TestCase testCase = readTestMethod(classNameStr, element);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Test case found : " + testCase + " for scenario: " + testScenario);
                    }
                    testCases.add(testCase);
                }
            }
        }
        return testCases;
    }

    private TestCase readTestMethod(String classNameStr, StartElement element) {
        final Iterator attrs = element.getAttributes();
        Boolean status = null;  // null means 'SKIP' for now. true/false for PASS/FAIL.
        String name = "unknown";
        String description = "";
        while (attrs.hasNext()) {
            final Attribute attr = (Attribute) attrs.next();
            if ("status".equals(attr.getName().getLocalPart())) {
                switch (attr.getValue()) {
                case "PASS":
                    status = Boolean.TRUE;
                    break;
                case "FAIL":
                    status = Boolean.FALSE;
                    break;
                default:
                    status = Boolean.FALSE; //handle the skipped case
                    description += " :: " + attr.getValue();
                    break;
                }
            }
            if ("name".equals(attr.getName().getLocalPart())) {
                name = attr.getValue();
            }
            if ("description".equals(attr.getName().getLocalPart())) {
                description = description.isEmpty() ? attr.getValue() : attr.getValue() + description;
            }
        }
        if (status == null) { // it's a skipped test!
            // TODO we need to properly handle Skipped test cases.
            name = name + " :: SKIPPED!";
            status = Boolean.FALSE;
        }
        final TestCase testCase = buildTestCase(classNameStr + "." + name, status,
                description); //todo capture failure message
        return testCase;

    }

    private TestCase buildTestCase(String className, boolean isSuccess, String failureMessage) {
        TestCase testCase = new TestCase();
        testCase.setTestScenario(this.testScenario);
        testCase.setName(className);
        testCase.setSuccess(isSuccess);
        testCase.setFailureMessage(failureMessage);
        return testCase;
    }

    /**
     * Searches the provided path for files named "testng-results.xml",
     * and returns the list of paths.
     *
     * @param dataBucket the data bucket folder where build artifacts are located.
     * @return list of paths of testng-results.xml.
     */
    private Set<Path> getResultInputFiles(Path dataBucket) {
        try {
            final Stream<Path> ls = Files.list(dataBucket);
            final Set<Path> files = ls.collect(Collectors.toSet());
            final Set<Path> inputFiles = new HashSet<>();
            for (Path file : files) {
                final Path fileName = file.getFileName();
                if (Files.isDirectory(file)) {
                    final Set<Path> anInputFilesList = getResultInputFiles(file);
                    inputFiles.addAll(anInputFilesList);
                } else if (RESULTS_INPUT_FILE.equals(fileName.toString())) {
                    inputFiles.add(file);
                }
            }

            return inputFiles;
        } catch (IOException e) {
            logger.error("Error while reading " + RESULTS_INPUT_FILE + " in " + dataBucket, e);
            return Collections.emptySet();
        }
    }

    /**
     * Persist the ARCHIVABLE_FILES into the test-scenario artifact dir.
     * These will eventually get uploaded to S3 via jenkins pipeline.
     *
     * @throws ResultParserException if a parser error occurred.
     */
    @Override
    public void archiveResults() throws ResultParserException {
        try {
            int maxDepth = 100;
            final Path outputLocation = DataBucketsHelper.getOutputLocation(testScenario.getTestPlan());
            final Set<Path> archivePaths = Files.find(outputLocation, maxDepth,
                    (path, att) -> Arrays.stream(ARCHIVABLE_FILES).anyMatch(f -> f.equals
                            (path.getFileName().toString()))).collect(Collectors.toSet());

            logger.info("Found results paths at " + outputLocation + ": " + archivePaths.stream().map
                    (outputLocation::relativize).collect(Collectors.toSet()));
            if (!archivePaths.isEmpty()) {
                Path artifactPath = TestGridUtil.getTestScenarioArtifactPath(testScenario);
                for (Path filePath : archivePaths) {
                    File file = filePath.toFile();
                    File destinationFile = new File(
                            TestGridUtil.deriveScenarioArtifactPath(this.testScenario, file.getName()));
                    if (file.isDirectory()) {
                        FileUtils.copyDirectory(file, destinationFile);
                    } else {
                        FileUtils.copyFile(file, destinationFile);
                    }
                }
                Path zipFilePath = artifactPath.resolve(testScenario.getDir() + TestGridConstants
                        .TESTGRID_COMPRESSED_FILE_EXT);
                Files.deleteIfExists(zipFilePath);
                FileUtil.compress(artifactPath.toString(), zipFilePath.toString());
                logger.info("Created the results archive: " + zipFilePath);
            } else {
                logger.info("Could not create results archive. No archived files with names: " + Arrays.toString
                        (ARCHIVABLE_FILES) + " were found at " + outputLocation + ".");
            }
        } catch (IOException e) {
            throw new ResultParserException("Error occurred while persisting scenario test-results." +
                    "Scenario ID: " + testScenario.getId() + ", Scenario Directory: " + testScenario.getDir(), e);
        }

    }
}

