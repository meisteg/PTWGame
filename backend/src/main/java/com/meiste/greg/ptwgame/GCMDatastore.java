/*
 * Copyright 2012 Google Inc.
 * Copyright (C) 2013-2015 Gregory S. Meiste  <http://gregmeiste.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.meiste.greg.ptwgame;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;

public final class GCMDatastore {

    public static final int MULTICAST_SIZE = 1000;

    private static final String MULTICAST_TYPE = "Multicast";
    private static final String MULTICAST_REG_IDS_PROPERTY = "regIds";

    private static final Logger logger =
            Logger.getLogger(GCMDatastore.class.getName());
    private static final DatastoreService datastore =
            DatastoreServiceFactory.getDatastoreService();

    private GCMDatastore() {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates a persistent record with the devices to be notified using a
     * multicast message.
     *
     * @param devices registration ids of the devices.
     * @return encoded key for the persistent record.
     */
    public static String createMulticast(final List<String> devices) {
        logger.info("Storing multicast for " + devices.size() + " devices");
        String encodedKey;
        final Transaction txn = datastore.beginTransaction();
        try {
            final Entity entity = new Entity(MULTICAST_TYPE);
            entity.setProperty(MULTICAST_REG_IDS_PROPERTY, devices);
            datastore.put(entity);
            final Key key = entity.getKey();
            encodedKey = KeyFactory.keyToString(key);
            logger.fine("multicast key: " + encodedKey);
            txn.commit();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
        return encodedKey;
    }

    /**
     * Gets a persistent record with the devices to be notified using a
     * multicast message.
     *
     * @param encodedKey encoded key for the persistent record.
     */
    public static List<String> getMulticast(final String encodedKey) {
        final Key key = KeyFactory.stringToKey(encodedKey);
        Entity entity;
        final Transaction txn = datastore.beginTransaction();
        try {
            entity = datastore.get(key);
            @SuppressWarnings("unchecked")
            final List<String> devices =
                    (List<String>) entity.getProperty(MULTICAST_REG_IDS_PROPERTY);
            txn.commit();
            return devices;
        } catch (final EntityNotFoundException e) {
            logger.severe("No entity for key " + key);
            return Collections.emptyList();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }

    /**
     * Updates a persistent record with the devices to be notified using a
     * multicast message.
     *
     * @param encodedKey encoded key for the persistent record.
     * @param devices new list of registration ids of the devices.
     */
    public static void updateMulticast(final String encodedKey, final List<String> devices) {
        final Key key = KeyFactory.stringToKey(encodedKey);
        Entity entity;
        final Transaction txn = datastore.beginTransaction();
        try {
            try {
                entity = datastore.get(key);
            } catch (final EntityNotFoundException e) {
                logger.severe("No entity for key " + key);
                return;
            }
            entity.setProperty(MULTICAST_REG_IDS_PROPERTY, devices);
            datastore.put(entity);
            txn.commit();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }

    /**
     * Deletes a persistent record with the devices to be notified using a
     * multicast message.
     *
     * @param encodedKey encoded key for the persistent record.
     */
    public static void deleteMulticast(final String encodedKey) {
        final Transaction txn = datastore.beginTransaction();
        try {
            final Key key = KeyFactory.stringToKey(encodedKey);
            datastore.delete(key);
            txn.commit();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }

}
