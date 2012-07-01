<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>

<%@ page import="com.meiste.greg.ptwgame.Driver" %>
<%@ page import="com.meiste.greg.ptwgame.ObjectifyDao" %>
<%@ page import="com.meiste.greg.ptwgame.Race" %>
<%@ page import="com.meiste.greg.ptwgame.RaceCorrectAnswers" %>
<%@ page import="com.meiste.greg.ptwgame.RaceQuestions" %>
<%@ page import="com.meiste.greg.ptwgame.Races" %>

<%
final String[] drivers = {
    "{\"mName\":\"A.J. Allmendinger\",\"mNumber\":22}",
    "{\"mName\":\"Aric Almirola\",\"mNumber\":43}",
    "{\"mName\":\"Marcos Ambrose\",\"mNumber\":9}",
    "{\"mName\":\"Trevor Bayne\",\"mNumber\":21}",
    "{\"mName\":\"Greg Biffle\",\"mNumber\":16}",
    "{\"mName\":\"Dave Blaney\",\"mNumber\":36}",
    "{\"mName\":\"Mike Bliss\",\"mNumber\":32}",
    "{\"mName\":\"Clint Bowyer\",\"mNumber\":15}",
    "{\"mName\":\"Jeff Burton\",\"mNumber\":31}",
    "{\"mName\":\"Kurt Busch\",\"mNumber\":51}",
    "{\"mName\":\"Kyle Busch\",\"mNumber\":18}",
    "{\"mName\":\"Landon Cassill\",\"mNumber\":83}",
    "{\"mName\":\"Stacy Compton\",\"mNumber\":74}",
    "{\"mName\":\"Dale Earnhardt Jr.\",\"mNumber\":88}",
    "{\"mName\":\"Carl Edwards\",\"mNumber\":99}",
    "{\"mName\":\"Bill Elliott\",\"mNumber\":97}",
    "{\"mName\":\"Brendan Gaughan\",\"mNumber\":33}",
    "{\"mName\":\"David Gilliland\",\"mNumber\":38}",
    "{\"mName\":\"Jeff Gordon\",\"mNumber\":24}",
    "{\"mName\":\"Robby Gordon\",\"mNumber\":7}",
    "{\"mName\":\"Denny Hamlin\",\"mNumber\":11}",
    "{\"mName\":\"Kevin Harvick\",\"mNumber\":29}",
    "{\"mName\":\"Timmy Hill\",\"mNumber\":37}",
    "{\"mName\":\"Jimmie Johnson\",\"mNumber\":48}",
    "{\"mName\":\"Kasey Kahne\",\"mNumber\":5}",
    "{\"mName\":\"Matt Kenseth\",\"mNumber\":17}",
    "{\"mName\":\"Brad Keselowski\",\"mNumber\":2}",
    "{\"mName\":\"Travis Kvapil\",\"mNumber\":93}",
    "{\"mName\":\"Terry Labonte\",\"mNumber\":320}",
    "{\"mName\":\"Bobby Labonte\",\"mNumber\":47}",
    "{\"mName\":\"Joey Logano\",\"mNumber\":20}",
    "{\"mName\":\"Mark Martin\",\"mNumber\":55}",
    "{\"mName\":\"Michael McDowell\",\"mNumber\":98}",
    "{\"mName\":\"Jamie McMurray\",\"mNumber\":1}",
    "{\"mName\":\"Casey Mears\",\"mNumber\":13}",
    "{\"mName\":\"Paul Menard\",\"mNumber\":27}",
    "{\"mName\":\"Juan Montoya\",\"mNumber\":42}",
    "{\"mName\":\"Joe Nemechek\",\"mNumber\":87}",
    "{\"mName\":\"Ryan Newman\",\"mNumber\":39}",
    "{\"mName\":\"Danica Patrick\",\"mNumber\":100}",
    "{\"mName\":\"David Ragan\",\"mNumber\":34}",
    "{\"mName\":\"Tony Raines\",\"mNumber\":26}",
    "{\"mName\":\"David Reutimann\",\"mNumber\":10}",
    "{\"mName\":\"Robert Richardson Jr.\",\"mNumber\":23}",
    "{\"mName\":\"Scott Riggs\",\"mNumber\":230}",
    "{\"mName\":\"Elliott Sadler\",\"mNumber\":330}",
    "{\"mName\":\"Ken Schrader\",\"mNumber\":321}",
    "{\"mName\":\"Regan Smith\",\"mNumber\":78}",
    "{\"mName\":\"Reed Sorenson\",\"mNumber\":740}",
    "{\"mName\":\"Ricky Stenhouse Jr.\",\"mNumber\":6}",
    "{\"mName\":\"Tony Stewart\",\"mNumber\":14}",
    "{\"mName\":\"David Stremme\",\"mNumber\":30}",
    "{\"mName\":\"Martin Truex Jr.\",\"mNumber\":56}",
    "{\"mName\":\"Brian Vickers\",\"mNumber\":550}",
    "{\"mName\":\"Kenny Wallace\",\"mNumber\":109}",
    "{\"mName\":\"Mike Wallace\",\"mNumber\":370}",
    "{\"mName\":\"Michael Waltrip\",\"mNumber\":140}",
    "{\"mName\":\"Josh Wise\",\"mNumber\":260}",
    "{\"mName\":\"J.J. Yeley\",\"mNumber\":249}"
};
%>

<html>
  <head>
    <title>Pick The Winner Administration</title>
  </head>
  <body bgcolor="#BBBBBB">
    <h1>Pick The Winner Administration</h1>
    
    <% UserService userService = UserServiceFactory.getUserService(); %>
    <p><a href="<%= userService.createLogoutURL(request.getRequestURI()) %>">Sign out</a></p>
    
<%
    ObjectifyDao<RaceQuestions> qDao = new ObjectifyDao<RaceQuestions>(RaceQuestions.class);
    ObjectifyDao<RaceCorrectAnswers> aDao = new ObjectifyDao<RaceCorrectAnswers>(RaceCorrectAnswers.class);
    Iterable<RaceQuestions> qAll = qDao.getAll(-1);
    RaceQuestions questions = null;
    if (qAll != null) {
        for (RaceQuestions temp : qAll) {
            if (aDao.get(temp.getRaceId()) == null) {
                questions = temp;
                break;
            }
        }
    }
    if (questions != null) {
        Race race = Race.getInstance(questions.getRaceId());
%>
        <h2>Submit Answers for <%= race.getTrack(Race.NAME_LONG) %></h2>
<%
        if (race.isFuture()) {
%>
            <p>Race not finished. Come back later.</p>
<%
        } else {
%>
            <form action="/admin" method="post">
                <input type="hidden" name="op" value="answers">
                <input type="hidden" name="race_id" value="<%= race.getId() %>">
                <p>Pick The Winner</p>
                <div><select name="a1">
<%
                for (String json : drivers) {
                    Driver driver = Driver.fromJson(json);
%>
                    <option value="<%= driver.mNumber %>"><%= driver.mName %></option>
<%
                }
%>
                </select></div>
                <p><%= questions.getQ2() %></p>
                <div><select name="a2">
<%
                for (int i = 0; i < questions.getA2().length; ++i) {
%>
                    <option value="<%= i %>"><%= questions.getA2()[i] %></option>
<%
                }
%>
                </select></div>
                <p><%= questions.getQ3() %></p>
                <div><select name="a3">
<%
                for (int i = 0; i < questions.getA3().length; ++i) {
%>
                    <option value="<%= i %>"><%= questions.getA3()[i] %></option>
<%
                }
%>
                </select></div>
                <p>Which driver will lead the most laps?</p>
                <div><select name="a4">
<%
                for (String json : drivers) {
                    Driver driver = Driver.fromJson(json);
%>
                    <option value="<%= driver.mNumber %>"><%= driver.mName %></option>
<%
                }
%>
                </select></div>
                <p>How many drivers will lead a lap?</p>
                <div><select name="a5">
                    <option value="0">1 - 5 drivers</option>
                    <option value="1">6 - 10 drivers</option>
                    <option value="2">11 - 15 drivers</option>
                    <option value="3">16 - 20 drivers</option>
                    <option value="4">21 - 25 drivers</option>
                    <option value="5">26 - 30 drivers</option>
                    <option value="6">31 - 43 drivers</option>
                </select></div>
                <div style="margin-top:30px"><input type="submit" value="Submit" /></div>
            </form>
<%
        }
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