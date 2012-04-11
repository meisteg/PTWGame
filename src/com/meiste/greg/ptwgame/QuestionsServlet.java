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

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;

@SuppressWarnings("serial")
public class QuestionsServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
	    
		if (user != null) {
			RaceQuestions obj = new RaceQuestions();
			Gson gson = new Gson();
			String json = gson.toJson(obj);
			
			resp.setContentType("text/plain");
			resp.getWriter().print(json);
		} else {
		    resp.sendRedirect(userService.createLoginURL(req.getRequestURI()));
		}
	}
	
	private class RaceQuestions {
		private String q2 = "Which manufacturer will have more cars finish in the top 10?";
		private String[] a2 = {"Chevrolet", "Dodge", "Ford", "Toyota"};
		private String q3 = "Will there be a new points leader after the race?";
		private String[] a3 = {"Yes", "No"};
	}
}
