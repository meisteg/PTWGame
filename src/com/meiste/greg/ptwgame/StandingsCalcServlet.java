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

@SuppressWarnings("serial")
public class StandingsCalcServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(StandingsCalcServlet.class.getName());

    private final ObjectifyDao<RaceAnswers> mAnswersDao =
            new ObjectifyDao<RaceAnswers>(RaceAnswers.class);
    private final ObjectifyDao<RaceCorrectAnswers> mCorrectAnswersDao =
            new ObjectifyDao<RaceCorrectAnswers>(RaceCorrectAnswers.class);
    private final ObjectifyDao<Player> mPlayerDao =
            new ObjectifyDao<Player>(Player.class);

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

        Iterable<RaceAnswers> submissions = mAnswersDao.getAll(race_id);
        for (RaceAnswers a : submissions) {
            Player player = mPlayerDao.getByProperty("mUserId", a.mUserId);
            if (player == null) {
                log.info("User " + a.mUserId + " not found in standings. Creating player...");
                player = new Player(race_id, a.mUserId);
            }

            if (a.a1.equals(answers.a1)) {
                player.wins++;
                player.points += 100;
            }
            if (a.a2.equals(answers.a2))
                player.points += 50;
            if (a.a3.equals(answers.a3))
                player.points += 35;
            if (a.a4.equals(answers.a4))
                player.points += 10;
            if (a.a5.equals(answers.a5))
                player.points += 5;

            player.races++;
            player.setRaceId(race_id);
            mPlayerDao.put(player);
        }

        // TODO: Need to also add "submitted questions first" tie breaker
        int rank = 1;
        List<Player> players = mPlayerDao.getList("-points", "-wins", "-races");
        for (Player player : players) {
            player.rank = rank++;
            mPlayerDao.put(player);
        }
    }
}
