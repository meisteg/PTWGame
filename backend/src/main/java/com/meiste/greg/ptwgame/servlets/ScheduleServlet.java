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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ScheduleServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(ScheduleServlet.class.getName());
    private static final Object sRacesSync = new Object();
    private static String sRaces;

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
}
