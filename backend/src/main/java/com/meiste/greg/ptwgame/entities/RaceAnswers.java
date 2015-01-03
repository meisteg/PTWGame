/*
 * Copyright (C) 2012, 2014-2015 Gregory S. Meiste  <http://gregmeiste.com>
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

import java.util.Calendar;
import java.util.List;

import static com.meiste.greg.ptwgame.OfyService.ofy;

@Entity
@Index
@Cache
public class RaceAnswers {
    private static class LoadPlayer {}

    @SuppressWarnings("unused")
    @Id
    private Long id;

    public long timestamp = System.currentTimeMillis();
    public int mYear = Calendar.getInstance().get(Calendar.YEAR);
    public int mRaceId = -1;

    @Load(LoadPlayer.class)
    public Ref<Player> playerRef;

    @SuppressWarnings("unused")
    @Expose
    public Integer a1;

    @SuppressWarnings("unused")
    @Expose
    public Integer a2;

    @SuppressWarnings("unused")
    @Expose
    public Integer a3;

    @SuppressWarnings("unused")
    @Expose
    public Integer a4;

    @SuppressWarnings("unused")
    @Expose
    public Integer a5;

    public static RaceAnswers get(final int raceId, final Player player) {
        return ofy().load().type(RaceAnswers.class)
                .filter("mYear", Calendar.getInstance().get(Calendar.YEAR))
                .filter("mRaceId", raceId)
                .filter("playerRef", player.getRef())
                .first().now();
    }

    public static List<RaceAnswers> getAllForUser(final Player player) {
        return ofy().load().type(RaceAnswers.class)
                .filter("mYear", Calendar.getInstance().get(Calendar.YEAR))
                .filter("playerRef", player.getRef())
                .list();
    }

    public static List<RaceAnswers> getAllForRace(final int raceId) {
        return ofy().load().group(LoadPlayer.class).type(RaceAnswers.class)
                .filter("mYear", Calendar.getInstance().get(Calendar.YEAR))
                .filter("mRaceId", raceId)
                .list();
    }

    public static void put(final RaceAnswers answers) {
        ofy().save().entity(answers).now();
    }

    @SuppressWarnings("unused")
    public RaceAnswers() {
        // Needed by objectify
    }

    public void setPlayer(final Player player) {
        playerRef = player.getRef();
    }

    public Player getPlayer() {
        return playerRef.get();
    }

    public void setRaceId(final int id) {
        mRaceId = id;
    }

    public static RaceAnswers fromJson(final String json) {
        return new Gson().fromJson(json, RaceAnswers.class);
    }

    public String toJson() {
        final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(this);
    }
}
