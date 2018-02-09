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

import org.apache.commons.lang.StringUtils;
import org.wso2.testgrid.automation.TestEngine;

import java.util.Optional;

/**
 * The factory class to get a {@link TestReader} implementation.
 *
 * @since 1.0.0
 */
public class TestReaderFactory {

    /**
     * This method returns the {@link TestReader} implementation of the given type.
     *
     * @param testType type of tests
     * @return instance of an {@link TestReader} for the given test type
     */
    public static Optional<TestReader> getTestReader(String testType) {
        Optional<TestEngine> testEngine = getTestEngineFromString(testType);
        if (!testEngine.isPresent()) {
            return Optional.empty();
        }

        switch (testEngine.get()) {
            case JMETER:
                return Optional.of(new JMeterTestReader());
            case TESTNG:
                return Optional.of(new TestNGTestReader());
            default:
                return Optional.empty();
        }
    }

    /**
     * Returns the {@link TestEngine} enum value from string.
     *
     * @param testType string to get {@link TestEngine} enum value
     * @return {@link TestEngine} enum value for the given string or an Optional.empty() if the string do not
     * correspond to an {@link TestEngine} enum value
     */
    private static Optional<TestEngine> getTestEngineFromString(String testType) {
        try {
            return Optional.of(TestEngine.valueOf(StringUtils.upperCase(testType)));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
