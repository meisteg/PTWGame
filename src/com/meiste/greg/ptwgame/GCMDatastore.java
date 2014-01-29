/*
 * Copyright 2012 Google Inc.
 * Copyright (C) 2013-2014 Gregory S. Meiste  <http://gregmeiste.com>
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.datanucleus.util.StringUtils;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Transaction;

public final class GCMDatastore {

    public static final int MULTICAST_SIZE = 1000;

    private static final String DEVICE_TYPE = "Device";
    private static final String DEVICE_REG_ID_PROPERTY = "regId";
    private static final String DEVICE_USER_ID_PROPERTY = "userId";
    private static final String DEVICE_TIMESTAMP_PROPERTY = "timestamp";
    private static final long DEVICE_REG_EXPIRATION = TimeUnit.DAYS.toMillis(180);

    private static final String MULTICAST_TYPE = "Multicast";
    private static final String MULTICAST_REG_IDS_PROPERTY = "regIds";

    private static final FetchOptions DEFAULT_FETCH_OPTIONS = FetchOptions.Builder
            .withPrefetchSize(MULTICAST_SIZE).chunkSize(MULTICAST_SIZE);

    private static final Logger logger =
            Logger.getLogger(GCMDatastore.class.getName());
    private static final DatastoreService datastore =
            DatastoreServiceFactory.getDatastoreService();

    private GCMDatastore() {
        throw new UnsupportedOperationException();
    }

    /**
     * Registers a device.
     *
     * @param regId device's registration id.
     * @param userId current user's id.
     */
    public static synchronized void register(final String regId, final String userId) {
        logger.info("Registering " + regId);
        final Transaction txn = datastore.beginTransaction();
        try {
            Entity entity = findDeviceByRegId(regId);
            if (entity != null) {
                logger.info(regId + " is already registered.");
            } else {
                entity = new Entity(DEVICE_TYPE);
                entity.setProperty(DEVICE_REG_ID_PROPERTY, regId);
            }
            if (!StringUtils.isEmpty(userId)) {
                entity.setProperty(DEVICE_USER_ID_PROPERTY, userId);
            }
            entity.setProperty(DEVICE_TIMESTAMP_PROPERTY, System.currentTimeMillis());
            datastore.put(entity);
            txn.commit();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }

    /**
     * Unregisters a device.
     *
     * @param regId device's registration id.
     */
    public static synchronized void unregister(final String regId) {
        logger.info("Unregistering " + regId);
        final Transaction txn = datastore.beginTransaction();
        try {
            final Entity entity = findDeviceByRegId(regId);
            if (entity == null) {
                logger.warning("Device " + regId + " already unregistered");
            } else {
                final Key key = entity.getKey();
                datastore.delete(key);
            }
            txn.commit();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }

    /**
     * Updates the registration id of a device.
     */
    public static void updateRegistration(final String oldId, final String newId) {
        logger.info("Updating " + oldId + " to " + newId);
        unregister(oldId);
        register(newId, null);
    }

    /**
     * Gets all registered devices.
     */
    public static List<String> getDevices() {
        List<String> devices;
        final Transaction txn = datastore.beginTransaction();
        try {
            final Query query = new Query(DEVICE_TYPE);
            final Iterable<Entity> entities =
                    datastore.prepare(query).asIterable(DEFAULT_FETCH_OPTIONS);
            devices = new ArrayList<String>();
            for (final Entity entity : entities) {
                final String device = (String) entity.getProperty(DEVICE_REG_ID_PROPERTY);
                devices.add(device);
            }
            txn.commit();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
        return devices;
    }

    /**
     * Gets all registered devices for a user.
     */
    public static List<String> getDevicesForUser(final String userId) {
        final List<String> devices = new ArrayList<String>();
        final Transaction txn = datastore.beginTransaction();
        try {
            final Query query = new Query(DEVICE_TYPE)
            .setFilter(new FilterPredicate(DEVICE_USER_ID_PROPERTY, FilterOperator.EQUAL, userId));
            final Iterable<Entity> entities =
                    datastore.prepare(query).asIterable(DEFAULT_FETCH_OPTIONS);
            for (final Entity entity : entities) {
                final String device = (String) entity.getProperty(DEVICE_REG_ID_PROPERTY);
                devices.add(device);
            }
            txn.commit();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
        return devices;
    }

    /**
     * Gets the number of total devices.
     */
    public static int getTotalDevices() {
        final Transaction txn = datastore.beginTransaction();
        try {
            final Query query = new Query(DEVICE_TYPE).setKeysOnly();
            final List<Entity> allKeys =
                    datastore.prepare(query).asList(DEFAULT_FETCH_OPTIONS);
            final int total = allKeys.size();
            logger.fine("Total number of devices: " + total);
            txn.commit();
            return total;
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }

    public static Entity findDeviceByRegId(final String regId) {
        final Query query = new Query(DEVICE_TYPE)
        .setFilter(new FilterPredicate(DEVICE_REG_ID_PROPERTY, FilterOperator.EQUAL, regId));
        final PreparedQuery preparedQuery = datastore.prepare(query);
        final List<Entity> entities = preparedQuery.asList(DEFAULT_FETCH_OPTIONS);
        Entity entity = null;
        if (!entities.isEmpty()) {
            entity = entities.get(0);
        }
        final int size = entities.size();
        if (size > 1) {
            logger.severe(
                    "Found " + size + " entities for regId " + regId + ": " + entities);
        }
        return entity;
    }

    /**
     * Clean up old registered devices.
     * 
     * @param commit Flag indicating whether to commit the cleanup to the datastore
     * 
     * @return List of device registration IDs cleaned up
     */
    public static List<String> cleanupDevices(final boolean commit) {
        final List<String> devices = new ArrayList<String>();
        final long expiration = System.currentTimeMillis() - DEVICE_REG_EXPIRATION;

        logger.info("Cleaning up registrations older than " + expiration +
                ". commit=" + commit);

        final Transaction txn = datastore.beginTransaction();
        try {
            final Query query = new Query(DEVICE_TYPE)
            .setFilter(new FilterPredicate(DEVICE_TIMESTAMP_PROPERTY, FilterOperator.LESS_THAN, expiration));
            final Iterable<Entity> entities =
                    datastore.prepare(query).asIterable(DEFAULT_FETCH_OPTIONS);
            for (final Entity entity : entities) {
                devices.add((String) entity.getProperty(DEVICE_REG_ID_PROPERTY));
            }
            txn.commit();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }

        if (commit) {
            for (final String regId : devices) {
                unregister(regId);
            }
        }

        return devices;
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
            final
            List<String> devices =
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
