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

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class AdminServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(AdminServlet.class.getName());
    private static final int MIN_NUM_DRIVERS = 43;

    private final ObjectifyDao<RaceQuestions> mQuestionsDao =
            new ObjectifyDao<RaceQuestions>(RaceQuestions.class);
    private final ObjectifyDao<RaceCorrectAnswers> mAnswersDao =
            new ObjectifyDao<RaceCorrectAnswers>(RaceCorrectAnswers.class);

    @Override
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws IOException {
        resp.sendRedirect("/admin.jsp");
    }

    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp)
            throws IOException {
        final String op = req.getParameter("op");

        if (op.equals("questions")) {
            submitQuestions(req);
        } else if (op.equals("answers")) {
            submitAnswers(req);
        } else if (op.equals("driver")) {
            submitDriver(req);
        }

        resp.sendRedirect("/admin.jsp");
    }

    private void submitQuestions(final HttpServletRequest req) {
        // Set allowInProgress to true here in case a tardy admin tries to
        // submit questions at the last second. If allowInProgress is false
        // they could inadvertently submit questions for the next race.
        final Race race = Races.getNext(false, true);
        if ((race != null) && !race.inProgress()) {
            RaceQuestions q = mQuestionsDao.get(race.getId());
            if (q == null) {
                q = new RaceQuestions(race.getId());
                q.setQ2(req.getParameter("q2"));
                q.setQ3(req.getParameter("q3"));

                final List<String> a2 = new ArrayList<String>();
                for (int i = 1; i <= 5; ++i) {
                    final String temp = req.getParameter("q2a" + i);
                    if (temp.length() > 0)
                        a2.add(temp);
                }
                q.setA2(a2.toArray(new String[a2.size()]));

                final List<String> a3 = new ArrayList<String>();
                for (int i = 1; i <= 5; ++i) {
                    final String temp = req.getParameter("q3a" + i).trim();
                    if (temp.length() > 0)
                        a3.add(temp);
                }
                q.setA3(a3.toArray(new String[a3.size()]));

                final String[] driver_nums = req.getParameterValues("drivers");
                if (driver_nums == null) {
                    log.warning("No drivers submitted. Questions not set.");
                } else if (driver_nums.length < MIN_NUM_DRIVERS) {
                    log.warning("Need at least " + MIN_NUM_DRIVERS + " drivers. Only submitted "
                            + driver_nums.length + " drivers. Questions not set.");
                } else {
                    q.drivers = new ArrayList<Driver>();
                    for (final String driver_num : driver_nums) {
                        final int num = Integer.parseInt(driver_num);
                        q.drivers.add(DriverDatastore.findDriverByNumber(num));
                    }
                    mQuestionsDao.put(q);
                }
            } else {
                log.warning("Race already has questions in database! Questions not set.");
            }
        } else {
            log.warning("Race in progess! Questions not set.");
        }
    }

    private void submitAnswers(final HttpServletRequest req) {
        final Race race = Race.getInstance(Integer.parseInt(req.getParameter("race_id")));
        if (race.isFuture()) {
            log.warning("Race not finished! Answers not set.");
            return;
        }

        RaceCorrectAnswers a = mAnswersDao.get(race.getId());
        if (a != null) {
            log.warning("Race already has answers in database! Answers not set.");
            return;
        }

        final UserService userService = UserServiceFactory.getUserService();
        a = new RaceCorrectAnswers(race.getId(), userService.getCurrentUser());
        a.a1 = Integer.parseInt(req.getParameter("a1"));
        a.a2 = Integer.parseInt(req.getParameter("a2"));
        a.a3 = Integer.parseInt(req.getParameter("a3"));
        a.a4 = Integer.parseInt(req.getParameter("a4"));
        a.a5 = Integer.parseInt(req.getParameter("a5"));
        mAnswersDao.put(a);

        // Kick off task to calculate new standings
        final Queue queue = QueueFactory.getDefaultQueue();
        queue.add(withUrl("/tasks/standings").param("race_id", String.valueOf(race.getId())));
    }

    private void submitDriver(final HttpServletRequest req) {
        final Driver driver = new Driver();
        driver.mFirstName = req.getParameter("driver_fname");
        driver.mLastName = req.getParameter("driver_lname");
        driver.mNumber = Integer.parseInt(req.getParameter("driver_num"));
        DriverDatastore.add(driver);
    }
}
