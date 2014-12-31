/*
 * Copyright (C) 2013 Gregory S. Meiste  <http://gregmeiste.com>
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
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;

@SuppressWarnings("serial")
public class FriendServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(FriendServlet.class.getName());

    private final ObjectifyDao<FriendLink> mFriendLinkDao =
            new ObjectifyDao<FriendLink>(FriendLink.class);
    private final ObjectifyDao<Player> mPlayerDao =
            new ObjectifyDao<Player>(Player.class);

    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp)
            throws IOException {
        final UserService userService = UserServiceFactory.getUserService();
        final User user = userService.getCurrentUser();
        if (user == null) {
            resp.sendRedirect(userService.createLoginURL(req.getRequestURI()));
            return;
        }

        final Player self = mPlayerDao.getByProperty("mUserId", user.getUserId());
        final String json = req.getReader().readLine();
        if ((self == null) || (json == null)) {
            resp.sendError(405);
            return;
        }

        final FriendRequest fReq = new Gson().fromJson(json, FriendRequest.class);
        if (!fReq.player.isIdentifiable()) {
            resp.sendError(405);
            return;
        }

        final Player other;
        if (fReq.player.rank != null)
            other = mPlayerDao.getByProperty("rank", fReq.player.rank);
        else
            other = mPlayerDao.getByProperty("name", fReq.player.name);

        if ((other == null) || other.mUserId.equals(user.getUserId())) {
            resp.sendError(405);
            return;
        }

        FriendLink fLink = new FriendLink(user.getUserId(), other.mUserId);
        FriendLink fLinkDB = mFriendLinkDao.getByExample(fLink);
        final List<String> deviceList = new ArrayList<String>();
        if (fReq.player.friend) {
            if (fLinkDB == null) {
                mFriendLinkDao.put(fLink);
                deviceList.addAll(getUserDevices(user.getUserId()));
            }
            // If GCM registration ID present, it indicates friend request
            // is the result of NFC. Need to friend in reverse as well, then
            // notify other player.
            if ((fReq.gcmRegId != null) && (GCMDatastore.findDeviceByRegId(fReq.gcmRegId) != null)) {
                fLink = new FriendLink(other.mUserId, user.getUserId());
                fLinkDB = mFriendLinkDao.getByExample(fLink);
                if (fLinkDB == null) {
                    mFriendLinkDao.put(fLink);
                    deviceList.addAll(getFriendDevices(other.mUserId, fReq.gcmRegId));
                }
            }
        } else if (fLinkDB != null) {
            mFriendLinkDao.delete(fLinkDB);
            deviceList.addAll(getUserDevices(user.getUserId()));
        }

        if (!deviceList.isEmpty()) {
            sendGcm(deviceList);
        }
    }

    private static List<String> getUserDevices(final String userId) {
        final List<String> deviceList = GCMDatastore.getDevicesForUser(userId);
        if (deviceList.size() == 1) {
            // If just one device, don't need to ping it. The device already is aware
            // of the situation. Only need to ping when where are multiple devices.
            deviceList.clear();
        }
        return deviceList;
    }

    private static List<String> getFriendDevices(final String userId, final String regId) {
        final List<String> deviceList = GCMDatastore.getDevicesForUser(userId);
        if (!deviceList.contains(regId)) {
            deviceList.add(regId);

            // Don't attempt to fix by registering here. The friend may not be
            // running latest version of the app, so it may not support all the
            // new GCM messages.
            //GCMDatastore.register(regId, userId);
        }
        return deviceList;
    }

    private static void sendGcm(final List<String> deviceList) {
        logger.info("Sending sync GCM to " + deviceList.size() + " devices");

        final String multicastKey = GCMDatastore.createMulticast(deviceList);
        final TaskOptions taskOptions = TaskOptions.Builder
                .withUrl("/tasks/send")
                .param(SendMessageServlet.PARAMETER_MULTICAST, multicastKey)
                .method(Method.POST);
        QueueFactory.getDefaultQueue().add(taskOptions);
    }
}
