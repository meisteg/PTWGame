/*
 * Copyright (C) 2012-2015 Gregory S. Meiste  <http://gregmeiste.com>
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

import com.google.appengine.api.users.User;
import com.google.gson.annotations.Expose;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;

import java.util.Calendar;
import java.util.List;

import static com.meiste.greg.ptwgame.OfyService.ofy;

@Entity
@Index
@Cache
public class Player {
    @SuppressWarnings("unused")
    @Id
    private Long id;

    public int mYear = Calendar.getInstance().get(Calendar.YEAR);
    public String mUserId;
    public String userEmail;

    @Expose
    public String name;

    @Expose
    public Integer rank;

    @Expose
    public Integer points;

    @Expose
    public Integer races;

    @Expose
    public Integer wins;

    @Expose
    @Ignore
    public boolean friend;

    @Expose
    @Ignore
    public boolean inChase;

    public static Player getByUserId(final String userId) {
        return getByProperty("mUserId", userId);
    }

    public static Player getByRank(final int rank) {
        return getByProperty("rank", rank);
    }

    public static Player getByName(final String name) {
        return getByProperty("name", name);
    }

    public static Player getLastYear(final String userId) {
        return getByPropertyAndYear("mUserId", userId,
                Calendar.getInstance().get(Calendar.YEAR) - 1);
    }

    public static List<Player> getList(final int limit) {
        return ofy().load().type(Player.class)
                .filter("mYear", Calendar.getInstance().get(Calendar.YEAR))
                .filter("rank !=", null)
                .order("rank")
                .limit(limit)
                .list();
    }

    public static List<Player> getForRanking() {
        return ofy().load().type(Player.class)
                .filter("mYear", Calendar.getInstance().get(Calendar.YEAR))
                .order("-points").order("-wins").order("-races")
                .list();
    }

    private static Player getByProperty(final String propName, final Object propValue) {
        return getByPropertyAndYear(propName, propValue, Calendar.getInstance().get(Calendar.YEAR));
    }

    private static Player getByPropertyAndYear(final String propName, final Object propValue,
                                               final int year) {
        return ofy().load().type(Player.class)
                .filter("mYear", year)
                .filter(propName, propValue)
                .first().now();
    }

    public static void put(final Player player) {
        ofy().save().entity(player).now();
    }

    public Player() {
    }

    public Player(final User user) {
        mUserId = user.getUserId();
        userEmail = user.getEmail();

        // Do not set rank. A null rank indicates "not ranked".
        points = races = wins = 0;
    }

    @Deprecated
    public Player(final String userId) {
        mUserId = userId;

        // Do not set rank. A null rank indicates "not ranked".
        points = races = wins = 0;
    }

    public boolean isIdentifiable() {
        return (name != null) || (rank != null);
    }

    public Ref<Player> getRef() {
        return Ref.create(this);
    }
}
