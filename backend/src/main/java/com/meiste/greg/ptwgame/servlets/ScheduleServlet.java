/*
 * Copyright (C) 2015 Gregory S. Meiste  <http://gregmeiste.com>
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

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.meiste.greg.ptwgame.entities.Race;
import com.meiste.greg.ptwgame.entities.Track;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ScheduleServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(ScheduleServlet.class.getName());
    private static final Object sRacesSync = new Object();
    private static String sRaces;

    @Override
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws IOException {
        synchronized (sRacesSync) {
            if (sRaces == null) {
                log.info("Reading schedule file");

                try {
                    final InputStream is = new FileInputStream(new File("schedule_2014"));
                    final BufferedReader in = new BufferedReader(new InputStreamReader(is));
                    String line;
                    final StringBuilder buffer = new StringBuilder();
                    while ((line = in.readLine()) != null) {
                        buffer.append(line).append('\n');
                    }
                    in.close();
                    sRaces = buffer.toString();
                } catch (final IOException e) {
                    log.warning("Unable to open schedule: " + e);
                }
            }
        }

        resp.setContentType("text/plain");
        resp.getWriter().print(sRaces);
    }

    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp)
            throws IOException {
        final UserService userService = UserServiceFactory.getUserService();
        if (userService.isUserAdmin()) {
            switch (req.getParameter("op")) {
                case "add_race":
                    addRace(req);
                    break;
                case "add_track":
                    addTrack(req);
                    break;
            }
            resp.sendRedirect("/schedule.jsp");
        } else {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    private void addRace(final HttpServletRequest req) {
        final Track track = Track.get(Long.parseLong(req.getParameter("track")));
        if (track == null) {
            log.severe("Failed to add race: Track not found!");
            return;
        }

        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));

        final Race race = new Race(track);
        race.raceId = Integer.parseInt(req.getParameter("raceId"));
        race.raceNum = Integer.parseInt(req.getParameter("raceNum"));
        race.name = req.getParameter("name");
        race.tv = req.getParameter("tv");
        try {
            race.startTime = sdf.parse(req.getParameter("startTime")).getTime();
            race.questionTime = sdf.parse(req.getParameter("questionTime")).getTime();
            Race.put(race);
        } catch (final ParseException e) {
            log.severe("Failed to add race: " + e);
        }
    }

    private void addTrack(final HttpServletRequest req) {
        final Track track = new Track();
        track.longName  = req.getParameter("longName");
        track.shortName = req.getParameter("shortName");
        track.length    = req.getParameter("length");
        track.layout    = req.getParameter("layout");
        track.city      = req.getParameter("city");
        track.state     = req.getParameter("state");
        Track.put(track);

        log.info(track.longName + " added");
    }
}
