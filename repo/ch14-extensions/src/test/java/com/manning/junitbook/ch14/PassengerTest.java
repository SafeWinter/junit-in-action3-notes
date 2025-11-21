/*
 * ========================================================================
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ========================================================================
 */
package com.manning.junitbook.ch14;

import com.manning.junitbook.ch14.extensions.*;
import com.manning.junitbook.ch14.jdbc.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith({ExecutionContextExtension.class, DatabaseOperationsExtension.class,
        DataAccessObjectParameterResolver.class,
        LogPassengerExistsExceptionExtension.class,
        PassengerParameterResolver.class
})
public class PassengerTest {

    private final PassengerDao passengerDao;

    public PassengerTest(PassengerDao passengerDao) {
        this.passengerDao = passengerDao;
    }

    @Test
    void testPassenger(Passenger passenger) {
        assertEquals("Passenger John Smith with identifier: 123-456-789", passenger.toString());
    }

    @Test
    void testInsertPassenger(Passenger passenger) throws PassengerExistsException {
        passengerDao.insert(passenger);
        assertEquals("John Smith", passengerDao.getById("123-456-789").getName());
    }

    @Test
    void testUpdatePassenger(Passenger passenger) throws PassengerExistsException {
        passengerDao.insert(passenger);
        passengerDao.update("123-456-789", "Michael Smith");
        assertEquals("Michael Smith", passengerDao.getById("123-456-789").getName());
    }

    @Test
    void testDeletePassenger(Passenger passenger) throws PassengerExistsException {
        passengerDao.insert(passenger);
        passengerDao.delete(passenger);
        assertNull(passengerDao.getById("123-456-789"));
    }

    @Test
    void testInsertExistingPassenger(Passenger passenger) {
        final PassengerExistsException ex = assertThrows(PassengerExistsException.class, () -> {
            passengerDao.insert(passenger);
            passengerDao.insert(passenger);
        });
        assertEquals("John Smith", passengerDao.getById("123-456-789").getName());
        assertEquals(passenger.toString(), ex.getMessage());
    }

}
