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
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Unindexed;

@Cached
public class Suggestion extends DatastoreObject {
    public String mUserId;

    @Unindexed
    public String mSuggestion;

    public Suggestion() {
    }

    public Suggestion(int race_id, User user, String json) {
        super(race_id);
        setUserId(user);
        setSuggestionFromJson(json);
    }

    public void setUserId(User user) {
        mUserId = (user != null) ? user.getUserId() : null;
    }

    public void setSuggestionFromJson(String json) {
        // Limit the input to 200 characters. Done here instead of the app
        // because someone can and will try to submit without the app.
        String temp = new Gson().fromJson(json, String.class);
        mSuggestion = temp.substring(0, Math.min(200, temp.length()));
    }
}
