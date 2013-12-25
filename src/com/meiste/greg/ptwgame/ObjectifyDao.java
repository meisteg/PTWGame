/*
 * Copyright (C) 2012-2013 Gregory S. Meiste  <http://gregmeiste.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.meiste.greg.ptwgame;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Transient;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;
import com.googlecode.objectify.util.DAOBase;

public class ObjectifyDao<T extends DatastoreObject> extends DAOBase {
    private static final int BAD_MODIFIERS = Modifier.FINAL | Modifier.STATIC | Modifier.TRANSIENT;

    static {
        ObjectifyService.register(FriendLink.class);
        ObjectifyService.register(Player.class);
        ObjectifyService.register(RaceAnswers.class);
        ObjectifyService.register(RaceCorrectAnswers.class);
        ObjectifyService.register(RaceQuestions.class);
        ObjectifyService.register(Suggestion.class);
    }

    protected Class<T> mClazz;

    public ObjectifyDao(final Class<T> clazz) {
        mClazz = clazz;
    }

    public T get(final int race_id) {
        return get(Calendar.getInstance().get(Calendar.YEAR), race_id);
    }

    public T get(final int year, final int race_id) {
        final Query<T> q = ofy().query(mClazz);
        q.filter("mYear", year);
        q.filter("mRaceId", race_id);
        return q.get();
    }

    public Iterable<T> getAll(final int race_id) {
        final Query<T> q = ofy().query(mClazz);
        q.filter("mYear", Calendar.getInstance().get(Calendar.YEAR));
        if (race_id >= 0)
            q.filter("mRaceId", race_id);
        return q.fetch();
    }

    public List<T> getAllForUser(final String user_id) {
        return getAllByProperty("mUserId", user_id);
    }

    public List<T> getAllByProperty(final String propName, final Object propValue) {
        final Query<T> q = ofy().query(mClazz);
        q.filter("mYear", Calendar.getInstance().get(Calendar.YEAR));
        q.filter(propName, propValue);
        return q.list();
    }

    public List<T> getList(final String order, final int limit) {
        final Query<T> q = ofy().query(mClazz);
        q.filter("mYear", Calendar.getInstance().get(Calendar.YEAR));
        if (order != null) {
            q.order(order);
            q.filter((order.startsWith("-") ? order.substring(1) : order) + " !=", null);
        }
        if (limit > 0)
            q.limit(limit);
        return q.list();
    }

    public List<T> getList(final String... orders) {
        final Query<T> q = ofy().query(mClazz);
        q.filter("mYear", Calendar.getInstance().get(Calendar.YEAR));
        for (final String order : orders) {
            q.order(order);
        }
        return q.list();
    }

    public Key<T> put(final T entity) throws IllegalStateException {
        if (entity.getRaceId() < 0)
            throw new IllegalStateException("Race ID is not set!");

        return ofy().put(entity);
    }

    public void delete(final T entity) {
        ofy().delete(entity);
    }

    public T getByProperty(final String propName, final Object propValue) {
        return getByPropertyAndYear(propName, propValue, Calendar.getInstance().get(Calendar.YEAR));
    }

    public T getByPropertyAndYear(final String propName, final Object propValue, final int year) {
        final Query<T> q = ofy().query(mClazz);
        q.filter("mYear", year);
        q.filter(propName, propValue);
        return q.get();
    }

    public T getByPropertyMax(final String propName) {
        final Query<T> q = ofy().query(mClazz);
        q.filter("mYear", Calendar.getInstance().get(Calendar.YEAR));
        q.order("-" + propName);
        return q.get();
    }

    public T getByExample(final T exampleObj) {
        final Query<T> queryByExample = buildQueryByExample(exampleObj);
        final Iterable<T> iterableResults = queryByExample.fetch();
        final Iterator<T> i = iterableResults.iterator();
        if (!i.hasNext())
            return null;
        final T obj = i.next();
        if (i.hasNext())
            throw new RuntimeException("Too many results");
        return obj;
    }

    @SuppressWarnings("rawtypes")
    protected Query<T> buildQueryByExample(final T exampleObj) {
        final Query<T> q = ofy().query(mClazz);
        Class obj = mClazz;

        // Add all non-null properties to query filter
        do {
            for (final Field field : obj.getDeclaredFields()) {
                // Ignore transient, embedded, array, and collection properties
                if (field.isAnnotationPresent(Transient.class)
                        || (field.isAnnotationPresent(Embedded.class))
                        || (field.getType().isArray())
                        || (Collection.class.isAssignableFrom(field.getType()))
                        || ((field.getModifiers() & BAD_MODIFIERS) != 0)) {
                    continue;
                }

                field.setAccessible(true);

                Object value;
                try {
                    value = field.get(exampleObj);
                } catch (final IllegalArgumentException e) {
                    throw new RuntimeException(e);
                } catch (final IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                if (value != null) {
                    q.filter(field.getName(), value);
                }
            }
            obj = obj.getSuperclass();
        } while (obj != null);

        return q;
    }
}
