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

import com.google.appengine.api.users.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Unindexed;

@Cached
public class RaceAnswers extends DatastoreObject {
    public String mUserId;

    @Unindexed
    @Expose
    public Integer a1;

    @Unindexed
    @Expose
    public Integer a2;

    @Unindexed
    @Expose
    public Integer a3;

    @Unindexed
    @Expose
    public Integer a4;

    @Unindexed
    @Expose
    public Integer a5;

    public RaceAnswers() {
    }

    public RaceAnswers(int race_id, User user) {
        super(race_id);
        setUserId(user);
    }

    public void setUserId(User user) {
        mUserId = user.getUserId();
    }

    public static RaceAnswers fromJson(String json) {
        return new Gson().fromJson(json, RaceAnswers.class);
    }

    public String toJson() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(this);
    }
}
