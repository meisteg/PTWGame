/*
 * Copyright (C) 2014 Gregory S. Meiste  <http://gregmeiste.com>
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

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.meiste.greg.ptwgame.entities.FriendLink;
import com.meiste.greg.ptwgame.entities.Player;
import com.meiste.greg.ptwgame.entities.RaceAnswers;
import com.meiste.greg.ptwgame.entities.RaceCorrectAnswers;
import com.meiste.greg.ptwgame.entities.RaceQuestions;
import com.meiste.greg.ptwgame.entities.Suggestion;

/**
 * Objectify service wrapper so we can statically register our persistence classes
 * More on Objectify here : https://code.google.com/p/objectify-appengine/
 */
public class OfyService {

    static {
        ObjectifyService.register(FriendLink.class);
        ObjectifyService.register(Player.class);
        ObjectifyService.register(RaceAnswers.class);
        ObjectifyService.register(RaceCorrectAnswers.class);
        ObjectifyService.register(RaceQuestions.class);
        ObjectifyService.register(Suggestion.class);
    }

    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }
}
