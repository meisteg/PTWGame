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

import java.util.Calendar;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;
import com.googlecode.objectify.util.DAOBase;

public class ObjectifyDao<T extends DatastoreObject> extends DAOBase {
    static {
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
}
