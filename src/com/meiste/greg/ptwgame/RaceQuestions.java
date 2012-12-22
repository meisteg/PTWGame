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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Unindexed;

@Cached
public class RaceQuestions extends DatastoreObject {
    @Unindexed
    @Expose
    private String q2 = "Which manufacturer will have more cars finish in the top 10?";

    @Unindexed
    @Expose
    private String[] a2 = {"Chevrolet", "Ford", "Toyota"};

    @Unindexed
    @Expose
    private String q3 = "Will there be a new points leader after the race?";

    @Unindexed
    @Expose
    private String[] a3 = {"Yes", "No"};

    public RaceQuestions() {
        super();
    }

    public RaceQuestions(int race_id) {
        super(race_id);
    }

    public String getQ2() {
        return q2;
    }

    public void setQ2(String q) {
        q2 = q;
    }

    public String[] getA2() {
        return a2;
    }

    public void setA2(String[] a) {
        a2 = a;
    }

    public String getQ3() {
        return q3;
    }

    public void setQ3(String q) {
        q3 = q;
    }

    public String[] getA3() {
        return a3;
    }

    public void setA3(String[] a) {
        a3 = a;
    }

    public String toJson() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(this);
    }
}
