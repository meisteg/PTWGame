<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>

<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>

<%@ page import="com.meiste.greg.ptwgame.Driver" %>
<%@ page import="com.meiste.greg.ptwgame.DriverDatastore" %>
<%@ page import="com.meiste.greg.ptwgame.ObjectifyDao" %>
<%@ page import="com.meiste.greg.ptwgame.Race" %>
<%@ page import="com.meiste.greg.ptwgame.RaceCorrectAnswers" %>
<%@ page import="com.meiste.greg.ptwgame.RaceQuestions" %>
<%@ page import="com.meiste.greg.ptwgame.Races" %>

<html>
  <head>
    <title>Pick The Winner Administration</title>
    <link href="css/multi-select.css" media="screen" rel="stylesheet" type="text/css">
    <link href="css/admin.css" media="screen" rel="stylesheet" type="text/css">
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
                List<Driver> drivers = DriverDatastore.getAll();
                for (Driver driver : drivers) {
%>
                    <option value="<%= driver.mNumber %>"><%= driver.getName() %></option>
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
                for (Driver driver : drivers) {
%>
                    <option value="<%= driver.mNumber %>"><%= driver.getName() %></option>
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
            <div id="submit_questions"><h2>Submit Questions for <%= race.getTrack(Race.NAME_LONG) %></h2>
            <form action="/admin" method="post">
                <input type="hidden" name="op" value="questions">
                
                <div style="margin-left:3px"><select multiple="multiple" id="drivers" name="drivers">
<%
                // TODO: Preselect drivers if entered in previous race
                List<Driver> drivers = DriverDatastore.getAll();
                for (Driver driver : drivers) {
%>
                    <option value="<%= driver.mNumber %>"><%= driver.getName() %></option>
<%
                }
%>
                </select>
                <a href="#" id="add_driver">Add New Driver</a></div>
                
                <div style="margin-top:30px"><input name="q2" type="text" size="70" placeholder="Question 2" required="required" />*</div>
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
            </div>
            <div id="submit_driver" style="display:none"><h2>Add New Driver</h2>
            <form id="driver_form" action="/admin" method="post">
                <input type="hidden" name="op" value="driver">

                <div>
                    <input name="driver_fname" id="driver_fname" type="text" size="15" placeholder="First Name" autocomplete="off" required="required" />
                    <input name="driver_lname" type="text" size="15" placeholder="Last Name" autocomplete="off" required="required" />
                </div>
                <input name="driver_num" type="number" step="1" max="999" placeholder="Number" autocomplete="off" required="required" />

                <div style="margin-top:30px">
                    <input type="button" value="Cancel" id="cancel_driver" />
                    <input type="submit" value="Add" />
                </div>
            </form>
            </div>
            <script src="js/jquery-1.10.2.min.js" type="text/javascript"></script>
            <script src="js/jquery.multi-select.js" type="text/javascript"></script>
            <script type="text/javascript">
                $('#drivers').multiSelect({
                    selectableHeader: "<div class='custom-header'>Inactive Drivers</div>",
                    selectionHeader: "<div class='custom-header'>Entry List</div>"
                });
                $("#add_driver").click(function() {
                    $("#submit_questions").hide();
                    $("#submit_driver").show();
                    $("#driver_fname").focus();
                    
                });
                $("#cancel_driver").click(function() {
                    $("#driver_form")[0].reset();
                    $("#submit_questions").show();
                    $("#submit_driver").hide();
                });
            </script>
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