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
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class QuestionsServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(QuestionsServlet.class.getName());

    private final ObjectifyDao<RaceQuestions> mQuestionsDao =
            new ObjectifyDao<RaceQuestions>(RaceQuestions.class);
    private final ObjectifyDao<RaceAnswers> mAnswersDao =
            new ObjectifyDao<RaceAnswers>(RaceAnswers.class);

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();

        if (user != null) {
            Race race = Races.getNext(false, true);
            if ((race != null) && race.inProgress()) {
                RaceQuestions q = mQuestionsDao.get(race.getId());
                if (q == null) {
                    log.warning("No questions found for race " + race.getId() + "! Using default questions.");
                    q = new RaceQuestions(race.getId());
                    mQuestionsDao.put(q);
                }

                resp.setContentType("text/plain");
                resp.getWriter().print(q.toJson());
            } else {
                resp.sendError(405);
            }
        } else {
            resp.sendRedirect(userService.createLoginURL(req.getRequestURI()));
        }
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();

        if (user != null) {
            Race race = Races.getNext(false, true);
            if ((race != null) && race.inProgress()) {
                RaceAnswers a = mAnswersDao.getByExample(new RaceAnswers(race.getId(), user));
                if (a == null) {
                    a = RaceAnswers.fromJson(req.getReader().readLine());
                    a.setUserId(user);
                    a.setRaceId(race.getId());
                    mAnswersDao.put(a);
                }

                resp.setContentType("text/plain");
                resp.getWriter().print(a.toJson());
            } else {
                resp.sendError(405);
            }
        } else {
            resp.sendRedirect(userService.createLoginURL(req.getRequestURI()));
        }
    }
}
