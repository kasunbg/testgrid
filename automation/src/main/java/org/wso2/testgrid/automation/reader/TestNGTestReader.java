/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.testgrid.automation.reader;

import org.apache.commons.lang3.ArrayUtils;
import org.wso2.testgrid.automation.Test;
import org.wso2.testgrid.automation.TestAutomationException;
import org.wso2.testgrid.automation.TestEngine;
import org.wso2.testgrid.common.TestScenario;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is responsible for reading testNG tests from test jars.
 * <p>
 * Test jars are jars with dependencies that contain the test classes + testng.xml
 */
public class TestNGTestReader implements TestReader {

    private static final String JAR_EXTENSION = ".jar";

    /**
     * This method goes through the file structure and create an object model of the tests.
     *
     * @param file         File object for the test folder.
     * @param testScenario test scenario associated with the test
     * @return a List of Test objects.
     */
    private List<Test> processTestStructure(File file, TestScenario testScenario) throws TestAutomationException {
        List<Test> testsList = new ArrayList<>();
        File tests = new File(file.getAbsolutePath());
        List<String> testNGList = new ArrayList<>();

        if (tests.exists()) {
            for (String testFile : ArrayUtils.nullToEmpty(tests.list())) {
                if (testFile.endsWith(JAR_EXTENSION)) {
                    testNGList.add(Paths.get(tests.getAbsolutePath(), testFile).toString());
                }
            }
        }

        Collections.sort(testNGList);
        Test test = new Test(file.getName(), TestEngine.TESTNG, testNGList, testScenario);
        testsList.add(test);

        return testsList;
    }

    @Override
    public List<Test> readTests(String testLocation, TestScenario scenario) throws TestAutomationException {
        return processTestStructure(new File(testLocation), scenario);
    }
}
