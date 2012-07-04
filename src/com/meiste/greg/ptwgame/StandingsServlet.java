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

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

@SuppressWarnings("serial")
public class StandingsServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(StandingsServlet.class.getName());

    private final ObjectifyDao<RaceCorrectAnswers> mCorrectAnswersDao =
            new ObjectifyDao<RaceCorrectAnswers>(RaceCorrectAnswers.class);
    private final ObjectifyDao<Player> mPlayerDao =
            new ObjectifyDao<Player>(Player.class);

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();

        if (user != null) {
            resp.setContentType("text/plain");
            resp.getWriter().print(new Standings(user).toJson());
        } else {
            resp.sendRedirect(userService.createLoginURL(req.getRequestURI()));
        }
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();

        if (user != null) {
            Player self = mPlayerDao.getByProperty("mUserId", user.getUserId());
            if (self != null) {
                String json = req.getReader().readLine();
                String newName = null;

                if (json != null)
                    newName = new Gson().fromJson(json, String.class);

                // Limit the input to 5-20 characters. Done here in addition to the
                // app because someone can and will try to submit without the app.
                if (newName != null)
                    newName = newName.substring(0, Math.min(20, newName.length()));
                if ((newName != null) && (newName.length() < 5))
                    newName = self.name;

                if (newName != null) {
                    // No special characters allowed
                    for (int i = 0; i < newName.length(); i++) {
                        if (!Character.isLetterOrDigit(newName.charAt(i))) {
                            newName = self.name;
                            break;
                        }
                    }
                }

                if (newName != null) {
                    // Verify name not taken by someone else
                    Player other = mPlayerDao.getByProperty("name", newName);
                    if ((other != null) && !other.mUserId.equals(self.mUserId)){
                        newName = self.name;
                    }
                }

                self.name = newName;
                mPlayerDao.put(self);
                log.info(user.getEmail() + " changed player name to " + newName);

                doGet(req, resp);
            } else
                resp.sendError(405);
        } else
            resp.sendError(405);
    }

    private class Standings {
        @Expose
        private int race_id = 0;
        @SuppressWarnings("unused")
        @Expose
        private List<Player> standings;
        @Expose
        private Player self;

        public Standings(User user) {
            RaceCorrectAnswers a = mCorrectAnswersDao.getByPropertyMax("mRaceId");
            if (a != null)
                race_id = a.getRaceId();

            self = mPlayerDao.getByProperty("mUserId", user.getUserId());
            if (self == null) {
                log.info("User " + user + " not found in standings. Creating player...");
                self = new Player(race_id, user);
                mPlayerDao.put(self);
            }

            standings = mPlayerDao.getList("rank", 25);
        }

        public String toJson() {
            // Need to exclude fields without Expose for Player class
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            return gson.toJson(this);
        }
    }
}
