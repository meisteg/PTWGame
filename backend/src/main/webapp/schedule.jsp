<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.TimeZone" %>

<%@ page import="com.meiste.greg.ptwgame.entities.Race" %>
<%@ page import="com.meiste.greg.ptwgame.entities.Track" %>

<html>
    <head>
        <title>Pick The Winner Schedule</title>
        <style>
            body {
                color: #FFFFFF;
                background-color: #004D40;
            }
            h1, h2 {
                text-align: center;
            }
            a {
                color: #FF5722;
                text-decoration: none;
            }
            a:hover, a:active {
                text-decoration: underline;
            }
            table {
                width: 100%;
            }
            th {
                background-color: #00897B;
            }
            table, th, td {
                border: 1px solid black;
                border-collapse: collapse;
            }
            th, td {
                padding: 5px;
            }
            p {
                margin: 0;
            }
            #add_track_form, #add_race_form {
                 display: none;
                 background-color: #00695C;
                 padding: 5px 20px 0 20px;
                 margin-left: auto;
                 margin-right: auto;
                 width: 20%;
            }
            #add_race_form {
                 width: 40%;
            }
            input, select {
                margin-bottom: 25px;
            }
            div.submit {
                text-align: center;
            }
        </style>
    </head>

    <body>
        <h1>Pick The Winner Schedule</h1>
<%
        final List<Track> tracks = Track.getAll();
%>
        <div id="main">
<%
            if (tracks.size() > 0) {
%>
                <h2>Scheduled Races</h2>
<%
                final List<Race> races = Race.getList(0);
                if (races.size() > 0) {
%>
                    <table>
                        <tr>
                            <th>Logo</th>
                            <th>ID</th>
                            <th>Num</th>
                            <th>Name</th>
                            <th>Track</th>
                            <th>TV</th>
                            <th>Questions (Eastern)</th>
                            <th>Start (Eastern)</th>
                        </tr>
<%
                        final SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMMM d, yyyy h:mm a");
                        sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
                        for (final Race race : races) {
%>
                            <tr>
                                <td><img src="img/race/<%= race.raceId %>.png" width="108" height="90"></td>
                                <td><%= race.raceId %></td>
                                <td><%= race.raceNum %></td>
                                <td><%= race.name %></td>
                                <td><%= race.trackNameLong %></td>
                                <td><%= race.tv %></td>
                                <td><%= sdf.format(new Date(race.questionTime)) %></td>
                                <td><%= sdf.format(new Date(race.startTime)) %></td>
                            </tr>
<%
                        }
%>
                    </table>
<%
                }
%>
                <a href="#" id="add_race">Add New Race</a>
<%
            }
%>
            <h2>Available Tracks</h2>
<%
            if (tracks.size() > 0) {
%>
                <table>
                    <tr>
                        <th>Long Name</th>
                        <th>Short Name</th>
                        <th>Length</th>
                        <th>Layout</th>
                        <th>City</th>
                        <th>State</th>
                    </tr>
<%
                    for (final Track track : tracks) {
%>
                        <tr>
                            <td><%= track.longName %></td>
                            <td><%= track.shortName %></td>
                            <td><%= track.length %></td>
                            <td><%= track.layout %></td>
                            <td><%= track.city %></td>
                            <td><%= track.state %></td>
                        </tr>
<%
                    }
%>
                </table>
<%
            }
%>
            <a href="#" id="add_track">Add New Track</a>
        </div>

        <div id="add_race_form">
            <h2>Add Race</h2>
            <form id="race_form" action="/schedule" method="post">
                <input type="hidden" name="op" value="add_race">

                <p>Race ID</p>
                <input name="raceId" id="raceId" type="text" size="2" maxlength="2" required="required" />

                <p>Number (0 if exhibition)</p>
                <input name="raceNum" type="text" size="2" maxlength="2" required="required" />

                <p>Name</p>
                <input name="name" type="text" size="70" required="required" />

                <p>Track</p>
                <select name="track">
<%
                    for (final Track track : tracks) {
%>
                        <option value="<%= track.id %>"><%= track.longName %></option>
<%
                    }
%>
                </select>

                <p>TV</p>
                <input name="tv" type="text" size="15" required="required" />

                <p>Question Time (Eastern)</p>
                <input name="questionTime" type="datetime-local" required="required" />

                <p>Start Time (Eastern)</p>
                <input name="startTime" type="datetime-local" required="required" />

                <div class="submit">
                    <input type="button" value="Cancel" id="cancel_add_race" />
                    <input type="submit" value="Submit" />
                </div>
            </form>
        </div>

        <div id="add_track_form">
            <h2>Add Track</h2>
            <form id="track_form" action="/schedule" method="post">
                <input type="hidden" name="op" value="add_track">

                <p>Long Name</p>
                <input name="longName" id="longName" type="text" size="30" required="required" />

                <p>Short Name</p>
                <input name="shortName" type="text" size="30" required="required" />

                <p>Length</p>
                <input name="length" type="text" size="5" maxlength="5" required="required" />

                <p>Layout</p>
                <input name="layout" type="text" size="3" maxlength="3" required="required" />

                <p>City</p>
                <input name="city" type="text" size="20" required="required" />

                <p>State</p>
                <input name="state" type="text" size="2" maxlength="2" required="required" />

                <div class="submit">
                    <input type="button" value="Cancel" id="cancel_add_track" />
                    <input type="submit" value="Submit" />
                </div>
            </form>
        </div>

        <script src="js/jquery-1.10.2.min.js" type="text/javascript"></script>
        <script type="text/javascript">
            $("#add_race").click(function() {
                $("#main").hide();
                $("#add_race_form").show();
                $("#add_track_form").hide();
                $("#raceId").focus();
            });
            $("#add_track").click(function() {
                $("#main").hide();
                $("#add_race_form").hide();
                $("#add_track_form").show();
                $("#longName").focus();
            });
            $("#cancel_add_race").click(function() {
                $("#race_form")[0].reset();
                $("#main").show();
                $("#add_race_form").hide();
            });
            $("#cancel_add_track").click(function() {
                $("#track_form")[0].reset();
                $("#main").show();
                $("#add_track_form").hide();
            });
        </script>
    </body>
</html>