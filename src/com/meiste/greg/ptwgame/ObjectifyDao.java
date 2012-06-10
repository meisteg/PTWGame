/*
 * Copyright (C) 2012 Gregory S. Meiste  <http://gregmeiste.com>
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

import javax.persistence.Embedded;
import javax.persistence.Transient;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;
import com.googlecode.objectify.util.DAOBase;

public class ObjectifyDao<T extends DatastoreObject> extends DAOBase {
    private static final int BAD_MODIFIERS = Modifier.FINAL | Modifier.STATIC | Modifier.TRANSIENT;

    static {
        ObjectifyService.register(RaceAnswers.class);
        ObjectifyService.register(RaceQuestions.class);
    }

    protected Class<T> mClazz;

    public ObjectifyDao(Class<T> clazz) {
        mClazz = clazz;
    }

    public T get(int race_id) {
        return get(Calendar.getInstance().get(Calendar.YEAR), race_id);
    }

    public T get(int year, int race_id) {
        Query<T> q = ofy().query(mClazz);
        q.filter("mYear", year);
        q.filter("mRaceId", race_id);
        return q.get();
    }

    public Key<T> put(T entity) throws IllegalStateException {
        if (entity.getRaceId() < 0)
            throw new IllegalStateException("Race ID is not set!");

        return ofy().put(entity);
    }

    public T getByExample(T exampleObj)
    {
        Query<T> queryByExample = buildQueryByExample(exampleObj);
        Iterable<T> iterableResults = queryByExample.fetch();
        Iterator<T> i = iterableResults.iterator();
        if (!i.hasNext())
            return null;
        T obj = i.next();
        if (i.hasNext())
            throw new RuntimeException("Too many results");
        return obj;
    }

    @SuppressWarnings("rawtypes")
    protected Query<T> buildQueryByExample(T exampleObj) {
        Query<T> q = ofy().query(mClazz);
        Class obj = mClazz;

        // Add all non-null properties to query filter
        do {
            for (Field field : obj.getDeclaredFields()) {
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
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
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
