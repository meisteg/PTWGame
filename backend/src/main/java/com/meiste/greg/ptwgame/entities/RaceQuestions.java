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

import java.util.Calendar;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.meiste.greg.ptwgame.Driver;

import static com.meiste.greg.ptwgame.OfyService.ofy;

@Entity
@Cache
public class RaceQuestions {
    @SuppressWarnings("unused")
    @Id
    private Long id;

    @Index
    public int mYear = Calendar.getInstance().get(Calendar.YEAR);

    @Index
    public int mRaceId = -1;

    /*
     * Drivers field left null by default intentionally. If an admin fails to
     * set the questions, drivers array should be null so PTW app falls back to
     * old hard-coded drivers behavior.
     */
    @Expose
    public List<Driver> drivers;

    @Expose
    public String q2 = "Which manufacturer will have more cars finish in the top 10?";

    @Expose
    public String[] a2 = {"Chevrolet", "Ford", "Toyota"};

    @Expose
    public String q3 = "Will there be a new points leader after the race?";

    @Expose
    public String[] a3 = {"Yes", "No"};

    public static RaceQuestions get(final Race race) {
        return get(race.raceId);
    }

    public static RaceQuestions get(final int raceId) {
        return ofy().load().type(RaceQuestions.class)
                .filter("mYear", Calendar.getInstance().get(Calendar.YEAR))
                .filter("mRaceId", raceId)
                .first().now();
    }

    public static List<RaceQuestions> getAll() {
        return ofy().load().type(RaceQuestions.class)
                .filter("mYear", Calendar.getInstance().get(Calendar.YEAR))
                .order("mRaceId")
                .list();
    }

    public static void put(final RaceQuestions rq) {
        ofy().save().entity(rq).now();
    }

    @SuppressWarnings("unused")
    public RaceQuestions() {
        // Needed by objectify
    }

    public RaceQuestions(final Race race) {
        mRaceId = race.raceId;
    }

    public void setQ2(final String q) {
        q2 = fixUp(q);
    }

    public void setA2(final String[] a) {
        a2 = a;
    }

    public void setQ3(final String q) {
        q3 = fixUp(q);
    }

    public void setA3(final String[] a) {
        a3 = a;
    }

    public String toJson() {
        final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(this);
    }

    private String fixUp(final String s) {
        return s.replace("’", "'");
    }
}
