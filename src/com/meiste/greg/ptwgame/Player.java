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
import com.google.gson.annotations.Expose;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Unindexed;

@Cached
public class Player extends DatastoreObject {
    public String mUserId;

    @Unindexed
    @Expose
    public String name = "<Private>";

    @Expose
    public Integer rank;

    @Expose
    public Integer points;

    @Expose
    public Integer races;

    @Expose
    public Integer wins;

    public Player() {
    }

    // TODO: Remove this constructor once Standings pulls players from database
    public Player(int r, int p, int s, int w) {
        rank = r;
        points = p;
        races = s;
        wins = w;
    }

    public void setUserId(User user) {
        mUserId = user.getUserId();
    }
}
