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
public class HistoryServlet extends HttpServlet {
    private final ObjectifyDao<RaceQuestions> mQuestionsDao =
            new ObjectifyDao<RaceQuestions>(RaceQuestions.class);
    private final ObjectifyDao<RaceAnswers> mAnswersDao =
            new ObjectifyDao<RaceAnswers>(RaceAnswers.class);

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();

        if (user != null) {
            resp.setContentType("text/plain");
            resp.getWriter().print(new PlayerHistory(user).toJson());
        } else {
            resp.sendRedirect(userService.createLoginURL(req.getRequestURI()));
        }
    }

    private class PlayerHistory {
        @Expose
        private List<Integer> ids = new ArrayList<Integer>();
        @Expose
        private List<RaceQuestions> questions = new ArrayList<RaceQuestions>();
        @Expose
        private List<RaceAnswers> answers;

        PlayerHistory(User user) {
            answers = mAnswersDao.getAllForUser(user.getUserId());
            if ((answers != null) && (answers.size() > 0)) {
                for (RaceAnswers a : answers) {
                    ids.add(a.getRaceId());
                    questions.add(mQuestionsDao.get(a.getRaceId()));
                }
            }
        }

        public String toJson() {
            // Need to exclude fields without Expose for sub-classes
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            return gson.toJson(this);
        }
    }
}
