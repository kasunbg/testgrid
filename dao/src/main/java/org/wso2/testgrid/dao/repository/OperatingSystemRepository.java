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

import org.wso2.testgrid.common.OperatingSystem;
import org.wso2.testgrid.dao.TestGridDAOException;

import java.util.List;
import javax.persistence.EntityManagerFactory;

/**
 * Repository class for {@link org.wso2.testgrid.common.OperatingSystem} table.
 *
 * @since 1.0.0
 */
public class OperatingSystemRepository extends AbstractRepository<OperatingSystem> {

    /**
     * Constructs an instance of the repository class.
     *
     * @param entityManagerFactory {@link EntityManagerFactory} instance
     */
    public OperatingSystemRepository(EntityManagerFactory entityManagerFactory) {
        super(entityManagerFactory);
    }

    /**
     * Persists an {@link OperatingSystem} instance in the database.
     *
     * @param entity OperatingSystem to persist in the database
     * @throws TestGridDAOException thrown when error on persisting the OperatingSystem instance
     */
    public void persist(OperatingSystem entity) throws TestGridDAOException {
        super.persist(entity);
    }

    /**
     * Removes an {@link OperatingSystem} instance from database.
     *
     * @param entity OperatingSystem instance to be removed from database.
     * @throws TestGridDAOException thrown when error on removing entry from database
     */
    public void delete(OperatingSystem entity) throws TestGridDAOException {
        super.delete(entity);
    }

    /**
     * Find a specific {@link OperatingSystem} instance from database for the given primary key.
     *
     * @param id primary key of the entity to be searched for
     * @return instance of an {@link OperatingSystem} matching the given primary key
     * @throws TestGridDAOException thrown when error on searching for entity
     */
    public OperatingSystem findByPrimaryKey(String id) throws TestGridDAOException {
        return findByPrimaryKey(OperatingSystem.class, id);
    }

    /**
     * Find {@link OperatingSystem} instances by a specific field and field value.
     *
     * @param field      name of the database field to lookup
     * @param fieldValue value of the field to be matched for
     * @return a list of values for the matched criteria
     * @throws TestGridDAOException thrown when error on searching for entity
     */
    public List<OperatingSystem> findByField(String field, Object fieldValue) throws TestGridDAOException {
        return super.findByField(OperatingSystem.class, field, fieldValue);
    }

    /**
     * Returns all the entries from the OperatingSystem table.
     *
     * @return List<OperatingSystem> all the entries from the table matching the given entity type
     * @throws TestGridDAOException thrown when error on searching for entity
     */
    public List<OperatingSystem> findAll() throws TestGridDAOException {
        return super.findAll(OperatingSystem.class);
    }
}