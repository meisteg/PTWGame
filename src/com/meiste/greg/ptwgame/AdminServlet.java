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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class AdminServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(AdminServlet.class.getName());

    private final ObjectifyDao<RaceQuestions> mQuestionsDao =
            new ObjectifyDao<RaceQuestions>(RaceQuestions.class);
    private final ObjectifyDao<RaceCorrectAnswers> mAnswersDao =
            new ObjectifyDao<RaceCorrectAnswers>(RaceCorrectAnswers.class);

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.sendRedirect("/admin.jsp");
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String op = req.getParameter("op");

        if (op.equals("questions")) {
            submitQuestions(req);
        } else if (op.equals("answers")) {
            submitAnswers(req);
        }

        resp.sendRedirect("/admin.jsp");
    }

    private void submitQuestions(HttpServletRequest req) {
        // Set allowInProgress to true here in case a tardy admin tries to
        // submit questions at the last second. If allowInProgress is false
        // they could inadvertently submit questions for the next race.
        Race race = Races.getNext(false, true);
        if ((race != null) && !race.inProgress()) {
            RaceQuestions q = mQuestionsDao.get(race.getId());
            if (q == null) {
                q = new RaceQuestions(race.getId());
                q.setQ2(req.getParameter("q2"));
                q.setQ3(req.getParameter("q3"));

                List<String> a2 = new ArrayList<String>();
                for (int i = 1; i <= 5; ++i) {
                    String temp = req.getParameter("q2a" + i);
                    if (temp.length() > 0)
                        a2.add(temp);
                }
                q.setA2(a2.toArray(new String[a2.size()]));

                List<String> a3 = new ArrayList<String>();
                for (int i = 1; i <= 5; ++i) {
                    String temp = req.getParameter("q3a" + i).trim();
                    if (temp.length() > 0)
                        a3.add(temp);
                }
                q.setA3(a3.toArray(new String[a3.size()]));

                mQuestionsDao.put(q);
            } else {
                log.warning("Race already has questions in database! Questions not set.");
            }
        } else {
            log.warning("Race in progess! Questions not set.");
        }
    }

    private void submitAnswers(HttpServletRequest req) {
        Race race = Race.getInstance(Integer.parseInt(req.getParameter("race_id")));
        if (race.isFuture()) {
            log.warning("Race not finished! Answers not set.");
            return;
        }

        RaceCorrectAnswers a = mAnswersDao.get(race.getId());
        if (a != null) {
            log.warning("Race already has answers in database! Answers not set.");
            return;
        }

        UserService userService = UserServiceFactory.getUserService();
        a = new RaceCorrectAnswers(race.getId(), userService.getCurrentUser());
        a.a1 = Integer.parseInt(req.getParameter("a1"));
        a.a2 = Integer.parseInt(req.getParameter("a2"));
        a.a3 = Integer.parseInt(req.getParameter("a3"));
        a.a4 = Integer.parseInt(req.getParameter("a4"));
        a.a5 = Integer.parseInt(req.getParameter("a5"));
        mAnswersDao.put(a);
    }
}
