<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>

<%@ page import="com.meiste.greg.ptwgame.Race" %>
<%@ page import="com.meiste.greg.ptwgame.Races" %>

<html>
  <head>
    <title>Pick The Winner Administration</title>
  </head>
  <body bgcolor="#BBBBBB">
    <h1>Pick The Winner Administration</h1>
    
    <% UserService userService = UserServiceFactory.getUserService(); %>
    <p><a href="<%= userService.createLogoutURL(request.getRequestURI()) %>">Sign out</a></p>
    
<%
    if (false) {
%>
      <h2>Submit Answers</h2>
      <p>Coming Soon!</p>
<%
    } else {
        Race race = Races.getNext(false, false);
        if (race != null) {
%>
            <h2>Submit Questions for <%= race.getTrack(Race.NAME_LONG) %></h2>
            <form action="/admin" method="post">
                <input type="hidden" name="op" value="questions">
                
                <div><input name="q2" type="text" size="70" placeholder="Question 2" required="required" />*</div>
                <div style="margin-left:40px"><input name="q2a1" type="text" size="20" placeholder="Answer 1" required="required" />*</div>
                <div style="margin-left:40px"><input name="q2a2" type="text" size="20" placeholder="Answer 2" required="required" />*</div>
                <div style="margin-left:40px"><input name="q2a3" type="text" size="20" placeholder="Answer 3" /></div>
                <div style="margin-left:40px"><input name="q2a4" type="text" size="20" placeholder="Answer 4" /></div>
                <div style="margin-left:40px"><input name="q2a5" type="text" size="20" placeholder="Answer 5" /></div>
                
                <div style="margin-top:30px"><input name="q3" type="text" size="70" placeholder="Question 3" required="required" />*</div>
                <div style="margin-left:40px"><input name="q3a1" type="text" size="20" placeholder="Answer 1" required="required" />*</div>
                <div style="margin-left:40px"><input name="q3a2" type="text" size="20" placeholder="Answer 2" required="required" />*</div>
                <div style="margin-left:40px"><input name="q3a3" type="text" size="20" placeholder="Answer 3" /></div>
                <div style="margin-left:40px"><input name="q3a4" type="text" size="20" placeholder="Answer 4" /></div>
                <div style="margin-left:40px"><input name="q3a5" type="text" size="20" placeholder="Answer 5" /></div>
                
                <div style="margin-top:30px"><input type="submit" value="Submit" /></div>
            </form>
<%
        } else {
%>
            <p>Season is over!</p>
<%
        }
    }
%>
  </body>
</html>