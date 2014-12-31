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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import com.google.gson.Gson;

public final class Races {
    private static final Logger log = Logger.getLogger(Races.class.getName());
    private static final Object sRacesSync = new Object();
    private static Race[] sRaces;

    public static Race[] get() {
        synchronized (sRacesSync) {
            if (sRaces == null) {
                log.info("Populating race array");

                try {
                    final InputStream is = new FileInputStream(new File("schedule"));
                    final BufferedReader in = new BufferedReader(new InputStreamReader(is));
                    String line;
                    final StringBuilder buffer = new StringBuilder();
                    while ((line = in.readLine()) != null)
                        buffer.append(line).append('\n');
                    in.close();
                    sRaces = new Gson().fromJson(buffer.toString(), Race[].class);
                } catch (final IOException e) {
                    log.warning("Unable to open schedule: " + e);
                }
            }
        }
        return sRaces;
    }

    public static Race getNext(final boolean allowExhibition, final boolean allowInProgress) {
        for (final Race race : get()) {
            if (race.isFuture()) {
                if (!allowExhibition && race.isExhibition())
                    continue;

                if (!allowInProgress && race.inProgress())
                    continue;

                return race;
            }
        }

        return null;
    }
}
