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

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;

@SuppressWarnings("serial")
public class StandingsServlet extends HttpServlet {
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

    @SuppressWarnings("unused")
    private class Player {
        private String name;
        private int rank;
        private int points;
        private int races;
        private int wins;

        public Player(String n, int r, int p, int s, int w) {
            name = n;
            rank = r;
            points = p;
            races = s;
            wins = w;
        }
    }

    @SuppressWarnings("unused")
    private class Standings {
        private int race_id = 13;
        private Player[] standings = new Player[25];
        private Player self;

        public Standings(User user) {
            self = new Player(user.getNickname(), 42, 125, 11, 1);

            for (int i = 0; i < standings.length; i++) {
                standings[i] = new Player("Player " + (i+1), i+1, 500-(i*5), 11, 3);
            }
        }

        public String toJson() {
            return new Gson().toJson(this);
        }
    }
}
