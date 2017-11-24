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
package org.wso2.testgrid.dao.repository;

import org.wso2.testgrid.common.TestScenario;
import org.wso2.testgrid.dao.TestGridDAOException;

import java.util.List;
import javax.persistence.EntityManagerFactory;

/**
 * Repository class for {@link org.wso2.testgrid.common.TestScenario} table.
 *
 * @since 1.0.0
 */
public class TestScenarioRepository extends AbstractRepository<TestScenario> {

    /**
     * Constructs an instance of the repository class.
     *
     * @param entityManagerFactory {@link EntityManagerFactory} instance
     */
    public TestScenarioRepository(EntityManagerFactory entityManagerFactory) {
        super(entityManagerFactory);
    }

    /**
     * Persists an {@link TestScenario} instance in the database.
     *
     * @param entity TestScenario to persist in the database
     * @throws TestGridDAOException thrown when error on persisting the TestScenario instance
     */
    public void persist(TestScenario entity) throws TestGridDAOException {
        super.persist(entity);
    }

    /**
     * Removes an {@link TestScenario} instance from database.
     *
     * @param entity TestScenario instance to be removed from database.
     * @throws TestGridDAOException thrown when error on removing entry from database
     */
    public void delete(TestScenario entity) throws TestGridDAOException {
        super.delete(entity);
    }

    /**
     * Find a specific {@link TestScenario} instance from database for the given primary key.
     *
     * @param id primary key of the entity to be searched for
     * @return instance of an {@link TestScenario} matching the given primary key
     * @throws TestGridDAOException thrown when error on searching for entity
     */
    public TestScenario findByPrimaryKey(String id) throws TestGridDAOException {
        return findByPrimaryKey(TestScenario.class, id);
    }

    /**
     * Find {@link TestScenario} instances by a specific field and field value.
     *
     * @param field      name of the database field to lookup
     * @param fieldValue value of the field to be matched for
     * @return a list of values for the matched criteria
     * @throws TestGridDAOException thrown when error on searching for entity
     */
    public List<TestScenario> findByField(String field, Object fieldValue) throws TestGridDAOException {
        return super.findByField(TestScenario.class, field, fieldValue);
    }

    /**
     * Returns all the entries from the TestScenario table.
     *
     * @return List<TestScenario> all the entries from the table matching the given entity type
     * @throws TestGridDAOException thrown when error on searching for entity
     */
    public List<TestScenario> findAll() throws TestGridDAOException {
        return super.findAll(TestScenario.class);
    }
}