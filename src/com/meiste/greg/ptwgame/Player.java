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

import com.google.appengine.api.users.User;
import com.google.gson.annotations.Expose;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.NotSaved;

@Cached
public class Player extends DatastoreObject {
    public String mUserId;

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
    @NotSaved
    public boolean friend;

    public Player() {
    }

    public Player(final int race_id, final User user) {
        super(race_id);
        setUserId(user);

        // Do not set rank. A null rank indicates "not ranked".
        points = races = wins = 0;
    }

    public Player(final int race_id, final String user_id) {
        super(race_id);
        mUserId = user_id;

        // Do not set rank. A null rank indicates "not ranked".
        points = races = wins = 0;
    }

    public void setUserId(final User user) {
        mUserId = user.getUserId();
    }

    public boolean isIdentifiable() {
        return (name != null) || (rank != null);
    }
}
