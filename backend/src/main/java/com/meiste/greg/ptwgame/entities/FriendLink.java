/*
 * Copyright (C) 2013-2015 Gregory S. Meiste  <http://gregmeiste.com>
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

package com.meiste.greg.ptwgame.entities;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.Calendar;
import java.util.List;

import static com.meiste.greg.ptwgame.OfyService.ofy;

@Entity
@Index
@Cache
public class FriendLink {
    @SuppressWarnings("unused")
    @Id
    private Long id;

    public int mYear = Calendar.getInstance().get(Calendar.YEAR);
    public String mUserId;
    public String mFriendUserId;

    public static FriendLink get(final String userId, final String friendId) {
        return ofy().load().type(FriendLink.class)
                .filter("mYear", Calendar.getInstance().get(Calendar.YEAR))
                .filter("mUserId", userId)
                .filter("mFriendUserId", friendId)
                .first().now();
    }

    public static List<FriendLink> getByUserId(final String userId) {
        return getByProperty("mUserId", userId);
    }

    public static List<FriendLink> getByFriendUserId(final String friendUserId) {
        return getByProperty("mFriendUserId", friendUserId);
    }

    private static List<FriendLink> getByProperty(final String propName, final Object propValue) {
        return ofy().load().type(FriendLink.class)
                .filter("mYear", Calendar.getInstance().get(Calendar.YEAR))
                .filter(propName, propValue)
                .list();
    }

    public static void put(final FriendLink flink) {
        ofy().save().entity(flink).now();
    }

    public static void del(final FriendLink flink) {
        ofy().delete().entity(flink).now();
    }

    @SuppressWarnings("unused")
    public FriendLink() {
        // Needed by objectify
    }

    public FriendLink(final String userId, final String friendId) {
        mUserId = userId;
        mFriendUserId = friendId;
    }
}
