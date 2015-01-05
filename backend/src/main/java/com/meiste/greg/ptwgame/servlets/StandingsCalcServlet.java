/*
 * Copyright (C) 2012, 2014-2015 Gregory S. Meiste  <http://gregmeiste.com>
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

package com.meiste.greg.ptwgame.servlets;

import com.meiste.greg.ptwgame.StandingsCommon;
import com.meiste.greg.ptwgame.entities.Player;
import com.meiste.greg.ptwgame.entities.Race;
import com.meiste.greg.ptwgame.entities.RaceAnswers;
import com.meiste.greg.ptwgame.entities.RaceCorrectAnswers;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class StandingsCalcServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(StandingsCalcServlet.class.getName());

    public static final String PARAM_RACE = "race_id";

    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp)
            throws IOException {
        final Race race = Race.get(Long.parseLong(req.getParameter(PARAM_RACE)));
        log.info("Updating standings for " + race.name);

        final RaceCorrectAnswers answers = RaceCorrectAnswers.get(race);
        if (answers == null) {
            log.warning("Answers for race not yet set.");
            resp.sendError(405);
            return;
        }

        switch (race.raceId) {
        case StandingsCommon.RACE_ID_CHASE_START:
            startChase();
            break;
        case StandingsCommon.RACE_ID_ROUND_2_START:
            startRound2();
            break;
        case StandingsCommon.RACE_ID_ROUND_3_START:
            startRound3();
            break;
        case StandingsCommon.RACE_ID_ROUND_4_START:
            startRound4();
            break;
        }

        final List<RaceAnswers> submissions = RaceAnswers.getAllForRace(race);
        for (final RaceAnswers a : submissions) {
            final Player player = a.getPlayer();

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
            Player.put(player);
        }

        // TODO: Need to also add "submitted questions first" tie breaker
        int rank = 1;
        final List<Player> players = Player.getForRanking();
        for (final Player player : players) {
            player.rank = rank++;
            Player.put(player);
        }
    }

    private void startChase() {
        log.info("The Chase begins now!");

        final List<Player> standings =
                Player.getList(StandingsCommon.NUM_PLAYERS_CHASE_ELIGIBLE);
        final List<Player> chasePlayers = StandingsCommon.getChasePlayers(standings);

        // Reset points
        for (final Player p : chasePlayers) {
            p.points = StandingsCommon.CHASE_POINTS_BASE + (p.wins * 10);
            Player.put(p);
        }

        // Done! No need to reset rank since regular scoring will handle it
    }

    private void startRound2() {
        log.info("Beginning round 2 of the Chase");

        final List<Player> standings =
                Player.getList(StandingsCommon.NUM_PLAYERS_IN_ROUND_2);
        for (final Player p : standings) {
            p.points += StandingsCommon.CHASE_POINTS_PER_ROUND;
            Player.put(p);
        }

        // Done! No need to subtract points from the other Chase players.
    }

    private void startRound3() {
        log.info("Beginning round 3 of the Chase");

        final List<Player> standings =
                Player.getList(StandingsCommon.NUM_PLAYERS_IN_ROUND_2);

        // Remove Round 2 bonus from the players who did not advance to Round 3.
        // No need to reset rank since regular scoring will handle it.
        while (standings.size() > StandingsCommon.NUM_PLAYERS_IN_ROUND_3) {
            final int index = standings.size() - 1;
            final Player p = standings.get(index);
            p.points -= StandingsCommon.CHASE_POINTS_PER_ROUND;
            Player.put(p);
            standings.remove(index);
        }

        // Give Round 3 bonus to be consistent with NASCAR, though it doesn't
        // really make much sense.
        for (final Player p : standings) {
            p.points += StandingsCommon.CHASE_POINTS_PER_ROUND;
            Player.put(p);
        }
    }

    private void startRound4() {
        log.info("Final round of the Chase");

        final List<Player> standings =
                Player.getList(StandingsCommon.NUM_PLAYERS_IN_ROUND_3);

        // Remove Round 2 and 3 bonus from the players who did not advance to Round 4.
        // No need to reset rank since regular scoring will handle it.
        while (standings.size() > StandingsCommon.NUM_PLAYERS_IN_ROUND_4) {
            final int index = standings.size() - 1;
            final Player p = standings.get(index);
            p.points -= (2 * StandingsCommon.CHASE_POINTS_PER_ROUND);
            Player.put(p);
            standings.remove(index);
        }

        // Give Round 4 bonus to be consistent with NASCAR, though it doesn't
        // really make much sense.
        for (final Player p : standings) {
            p.points += StandingsCommon.CHASE_POINTS_PER_ROUND;
            Player.put(p);
        }
    }
}
