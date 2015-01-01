/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.meiste.greg.ptwgame.servlets;

import com.meiste.greg.ptwgame.GCMDatastore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that unregisters a device, whose registration id is identified by
 * {@link #PARAMETER_REG_ID}.
 * <p>
 * The client app should call this servlet everytime it receives a
 * {@code com.google.android.c2dm.intent.REGISTRATION} with an
 * {@code unregistered} extra.
 */
public class UnregisterServlet extends GCMBaseServlet {

    private static final String PARAMETER_REG_ID = "regId";

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException {
        final String regId = getParameter(req, PARAMETER_REG_ID);
        GCMDatastore.unregister(regId);
        setSuccess(resp);
    }
}
