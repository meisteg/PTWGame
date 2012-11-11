/*
 * Copyright 2012 Google Inc.
 * Copyright 2012 Gregory S. Meiste  <http://gregmeiste.com>
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
package com.meiste.greg.ptwgame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;

/**
 * Servlet that adds a new message to all registered devices.
 * <p>
 * This servlet is used just by the browser (i.e., not device).
 */
@SuppressWarnings("serial")
public class StandingsSendAllServlet extends GCMBaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        List<String> devices = GCMDatastore.getDevices();
        String status;

        if (devices.isEmpty()) {
            status = "Message ignored as there are no devices registered!";
        } else {
            Queue queue = QueueFactory.getDefaultQueue();

            // must split in chunks of 1000 devices (GCM limit)
            int total = devices.size();
            List<String> partialDevices = new ArrayList<String>(total);
            int counter = 0;
            int tasks = 0;
            for (String device : devices) {
                counter++;
                partialDevices.add(device);
                int partialSize = partialDevices.size();
                if (partialSize == GCMDatastore.MULTICAST_SIZE || counter == total) {
                    String multicastKey = GCMDatastore.createMulticast(partialDevices);
                    logger.fine("Queuing " + partialSize + " devices on multicast " +
                            multicastKey);
                    TaskOptions taskOptions = TaskOptions.Builder
                            .withUrl("/tasks/send")
                            .param(SendMessageServlet.PARAMETER_MULTICAST, multicastKey)
                            .method(Method.POST);
                    queue.add(taskOptions);
                    partialDevices.clear();
                    tasks++;
                }
            }
            status = "Queued tasks to send " + tasks + " multicast messages to " +
                    total + " devices";
        }

        resp.setContentType("text/plain");
        resp.getWriter().print(status);
    }
}
