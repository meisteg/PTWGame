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

@SuppressWarnings("serial")
public class AnswersServlet extends HttpServlet {
    private final ObjectifyDao<RaceCorrectAnswers> mAnswersDao =
            new ObjectifyDao<RaceCorrectAnswers>(RaceCorrectAnswers.class);

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();

        if (user != null) {
            String year = req.getParameter("year");
            String race_id = req.getParameter("race_id");
            if ((year != null) && (race_id != null)) {
                int y = Integer.parseInt(year);
                int r = Integer.parseInt(race_id);
                RaceCorrectAnswers a = mAnswersDao.get(y, r);
                if (a != null) {
                    resp.setContentType("text/plain");
                    resp.getWriter().print(a.toJson());
                } else {
                    resp.sendError(405);
                }
            } else {
                resp.sendError(405);
            }
        } else {
            resp.sendRedirect(userService.createLoginURL(req.getRequestURI()));
        }
    }
}
