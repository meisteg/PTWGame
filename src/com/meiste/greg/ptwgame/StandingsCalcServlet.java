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

@SuppressWarnings("serial")
public class StandingsCalcServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(StandingsCalcServlet.class.getName());

    private final ObjectifyDao<RaceAnswers> mAnswersDao =
            new ObjectifyDao<RaceAnswers>(RaceAnswers.class);
    private final ObjectifyDao<RaceCorrectAnswers> mCorrectAnswersDao =
            new ObjectifyDao<RaceCorrectAnswers>(RaceCorrectAnswers.class);

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        int race_id = Integer.parseInt(req.getParameter("race_id"));
        log.info("Updating standings for race " + race_id);

        RaceCorrectAnswers answers = mCorrectAnswersDao.get(race_id);
        if (answers == null) {
            log.warning("Answers for race " + race_id + " not yet set.");
            resp.sendError(405);
            return;
        }

        Iterable<RaceAnswers> submissions = mAnswersDao.getAll();
        for (RaceAnswers a : submissions) {
            // TODO: Score and update player stats
        }
    }
}
