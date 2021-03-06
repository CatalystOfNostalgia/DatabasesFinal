import java.sql.*;

/**
 * Created by Eric on 4/19/2015.
 * Utility functions for UI to edit and retrieve from database
 * Use these to access and edit the RDBMS
 */
public class DatabaseConnector {
    public enum USERTYPE{GUEST, ADMIN} /*The types of user for our database*/

    private static final String DATABASE_NAME = "FrisbeeTest";
    private static final String DRIVER = "com.mysql.jdbc.Driver"; /*JDBC driver for mysql*/
    private static final String SERVER = "jdbc:mysql://localhost:3306/" + DATABASE_NAME + "?zeroDateTimeBehavior=convertToNull";
    private static final String GUEST = "user";
    private static final String GUESTPASS ="password";
    private static final String ADMIN = "user";
    private static final String ADMINPASS = "password";
    private USERTYPE type;

    private Statement statement;
    private Statement statement2;

    public DatabaseConnector(USERTYPE user) throws Exception{
        this.type = user;
        Class.forName(DRIVER);
        Connection conn;
        if(user == USERTYPE.GUEST){
            conn = DriverManager.getConnection(SERVER, GUEST, GUESTPASS);
        }else{
            conn = DriverManager.getConnection(SERVER, ADMIN, ADMINPASS);
        }

        statement = conn.createStatement();
        statement2 = conn.createStatement();
    }

    /*
        Retrieve all stats for a given team
        Give "*" to retrieve all stats for all teams
     */
    public ResultSet getTeam(String s) throws Exception{
        ResultSet rs;
        String query = "Select * from Teams where teamID = \'" + s + "\'";
        if(s.equals("*")){
            query = "Select * from Teams";
        }
        rs = statement.executeQuery(query);
        return rs;
    }

    /*
        Gets the players
    * */
    public ResultSet getPlayer(String s) throws Exception{
        ResultSet rs;
        String query = "Select * from Players where playerID = \'" + s + "\'";
        if(s.equals("*")){
            query = "Select * from Players";
        }
        rs = statement.executeQuery(query);
        return rs;
    }

    /*
        Gets all of a players games
     */
    public ResultSet getPlayersGames(int playerId) throws Exception{
        String query = "Select gameId from participatesIn where playerId = " + playerId;
        return statement.executeQuery(query);
    }
    /*
        Gets a coach
        Use * to return the entire table
     */
    public ResultSet getCoach(String s) throws Exception{
        ResultSet rs;
        //Select a given coach
        String query = "Select * from Coaches where coachID = \'" + s + "\'";
        //Special case handling
        if(s.equals("*")){
            query = "Select * from Coaches";
        }
        rs = statement.executeQuery(query);
        return rs;
    }

    /*
        Gets the tuple for a tournament
        User * to return the entire table
     */
    public ResultSet getTournament(String s) throws Exception{
        ResultSet rs;
        String query = "Select * from tournaments where tournamentID = \'" + s + "\'";
        if(s.equals("*")){
            query = "Select * from tournaments";
        }
        rs = statement.executeQuery(query);
        return rs;
    }

    /*
        Gets the tuple for a game
        Use * to get the entire table
     */
    public ResultSet getGame(String s) throws Exception{
        ResultSet rs;
        String query = "Select * from games where gameid = \'" + s + "\'";
        if(s.equals("*")){
            query = "Select * from games";
        }
        rs = statement.executeQuery(query);
        return rs;
    }

    public ResultSet participatesIn(String game, String player) throws Exception{
        String query;
        if(game == null && player == null){
            query = "Select * from participatesin";
        }
        else if(player == null){
            query = "Select * from participatesin where gameid = \'" + game + "\'";
        }
        else if(game == null){
            query = "Select * from participatesin where playerid = \'" + player + "\'";
        }
        else{
            query = "SELECT * FROM participatesin";
            //query = "Select * from participatesin where gameid = \'" + game + "\' and playerid = \'" + player + "\'";
        }
        return statement.executeQuery(query);
    }

    /**
     * Queries the playin relation
     * @param game      the game we want to find
     * @param player    the player we are interested in
     * @return          the result of the query
     * @throws Exception
     */
    public ResultSet playIn(String game, String player) throws Exception{
        String query;
        if(game == null && player == null){
            query = "Select * from participatesIn";
        }
        else if(player == null){
            query = "Select * from participatesIn where gameid = \'" + game + "\'";
        }
        else if(game == null){
            query = "Select * from participatesIn where playerID = \'" + player + "\'";
        }
        else{
            query = "SELECT * FROM participatesIn where gameid = \'" + game + "\' and playerid = \'" + player + "\'";
        }
        return statement.executeQuery(query);
    }

    /**
     * Finds top N players for a stat
     * @param stat  The stat we are interested in
     * @return         The top N players for the given stat
     * @throws Exception
     */
    public ResultSet topNStat(int n, String stat) throws Exception{
        String number = Integer.toString(n);
        String query = "SELECT SUM(PI." + stat + ") AS " + stat +", P.name \n" +
                "FROM ParticipatesIn AS PI, Players AS P \n" +
                "WHERE P.playerID = PI.playerID \n" +
                "GROUP BY P.name\n" +
                "ORDER BY SUM(PI." + stat + ") desc\n" +
                "LIMIT " + number;
        return statement.executeQuery(query);
    }

    /**
     * Finds the top n teams with best records (determined as wins/losses)
     * ...What happens if the team has no losses?
     * @param n The number of teams to return
     * @return  The n teams with the best record
     */
    public ResultSet bestRecords(int n) throws Exception{
        String number = Integer.toString(n);
        String query = "SELECT T.wins - T.Losses, T.name\n" +
                "FROM Teams AS T\n" +
                "ORDER BY T.wins - T.losses desc\n" +
                "LIMIT " + number;
        return statement.executeQuery(query);
    }

    /**
     * Finds top n teams with the highest stats
     * @param   n       The number of teams we want to return
     * @param   stat    The stat we want to sort the teams by
     * @return          The n teams with the best stat stat
     */
    public ResultSet bestTeamsStats(int n, String stat) throws Exception{
        String number = Integer.toString(n);
        String query = "SELECT \tSUM(PI." + stat +") AS " + stat + ", T.name\n" +
                "FROM \tTeams AS T, Players AS P, ParticipatesIn AS PI\n" +
                "WHERE \tT.teamID = P.teamID AND\n" +
                "\t\tPI.playerID = P.playerID\n" +
                "GROUP BY T.name\n" +
                "ORDER BY SUM(PI." + stat + ") desc\n" +
                "LIMIT " + number;
        return statement.executeQuery(query);
    }

    /**
     * Stats for all players on a given team
     * @param team  The team we are interested in
     * @return      all the stats for the players on team
     */
    public ResultSet statsForPlayersOnTeam(String team) throws Exception{
        String query = "SELECT P.name, SUM(PI.drops) AS Drops, SUM(PI.assists) AS Assists, SUM(PI.goals) AS Goals, SUM(PI.pointsPlayed) AS PointsPlayed, SUM(PI.throwaways) AS Throwaways\n" +
                "FROM Players AS P, Teams AS T, ParticipatesIn AS PI\n" +
                "Where T.teamId = \'"+ team + "\'AND P.teamId = T.teamId AND PI.playerId = P.playerId\n" +
                "GROUP BY P.playerId" ;
        return statement.executeQuery(query);
    }

    public ResultSet statsForPlayer(String player) throws Exception{
        String query = "SELECT P.name, P.playerId, SUM(PI.drops) AS Drops, SUM(PI.assists) AS Assists, SUM(PI.goals) AS Goals, \n" +
                "SUM(PI.pointsPlayed) AS PointsPlayed, SUM(PI.throwaways) AS Throwaways\n" +
                "FROM Players P, ParticipatesIn PI\n" +
                "WHERE P.playerID = \'"+ player +"\'AND\n" +
                "PI.playerID = P.playerID ";
        return statement.executeQuery(query);
    }

    public ResultSet statsForTeam(String team) throws Exception{
        String query = "SELECT SUM(drops) AS Drops, SUM(assists) AS Assists, SUM(goals) AS Goals, SUM(pointsPlayed) AS PointsPlayed, SUM(throwaways) AS Throwaways\n" +
                "FROM (\tSELECT \tP.name, PI.playerID, SUM(PI.drops) AS drops, SUM(PI.assists) AS assists, SUM(PI.goals) AS goals, SUM(PI.pointsPlayed) AS pointsPlayed, SUM(throwaways) AS throwaways\n" +
                "FROM \tPlayers P, Teams T, ParticipatesIn PI\n" +
                "WHERE \tP.teamID = T.teamID AND\n" +
                "P.playerID = PI.playerID AND\n" +
                "T.teamID =" + team +"\n " +
                "GROUP BY PI.playerID) AS A";
        return statement.executeQuery(query);
    }

    public ResultSet statsForGame(String team, String game) throws Exception{
        String query = "SELECT T.name, SUM(PI.drops) AS Drops, SUM(PI.assists) AS Assists, SUM(PI.goals) AS Goals, SUM(PI.pointsPlayed) AS PointsPlayed, SUM(throwaways) AS Throwaways\n" +
                "FROM Players P, Teams T, ParticipatesIn PI, Games G\n" +
                "WHERE G.gameID = \'" + game +"\' and T.teamID = \'" +  team +"\'and PI.gameID = G.gameId and P.teamID = T.teamID and PI.playerID = P.playerID\n" +
                "GROUP BY T.teamID\n";
        return statement.executeQuery(query);
    }

    public ResultSet playersAboveThreshold(String stat, int threshold) throws Exception{
        String thresholdString = Integer.toString(threshold);
        String query = "SELECT P.name, SUM(PI." + stat+") AS " + stat + "\n" +
                "FROM players AS P, ParticipatesIn AS PI\n" +
                "WHERE P.playerID = PI.playerID\n" +
                "GROUP BY P.name\n" +
                "HAVING SUM(PI." + stat +") > " + thresholdString + "\n" +
                "ORDER BY SUM(PI."+ stat +") desc\n";
        return statement.executeQuery(query);
    }

    //returns only 0s for everyone right now
    public ResultSet yearsPlayed() throws Exception{
        return statement.executeQuery("SELECT DISTINCT PY.playerID, PY.name, MAX(PY.year ) - MIN(PY.year) AS yearsPlayed\n" +
                "FROM (SELECT P.playerID, P.name, YEAR(T.tournamentDate) AS year\n" +
                "FROM Players P, ParticipatesIn PI, Games G, Tournaments T\n" +
                "WHERE P.playerID = PI.playerID AND \n" +
                "PI.gameID = G.gameID AND G.tournamentID = T.tournamentID) AS PY\n" +
                "GROUP BY PY.playerID");
    }

    public ResultSet playerComparison(int player1, int player2) throws Exception{
        return statement.executeQuery("SELECT \tPL1.name, SUM(P1.drops) AS Drops, SUM(P1.assists) AS Assists, SUM(P1.goals) AS Goals, SUM(P1.pointsPlayed) AS PointsPlayed, \n" +
                "   \t\tSUM(P1.throwaways) AS Throwaways, PL2.name, SUM(P2.drops) AS Drops, SUM(P2.assists) AS Assists, SUM(P2.goals) AS Goals,   \n" +
                "   \t\tSUM(P2.pointsPlayed) AS PointsPlayed, SUM(P2.throwaways) AS Throwaways\n" +
                "FROM \tPlayers PL1, Players PL2, ParticipatesIn P1, ParticipatesIn P2\n" +
                "WHERE \tPL1.playerID = P1.playerID AND\n" +
                "\t\tPL2.playerID = P2.playerID AND\n" +
                "        PL1.playerID = "+ player1 + " AND\n" +
                "        PL2.playerID = "+ player2 + "\n");
    }

    public ResultSet teamsSharingLoc() throws Exception{
        return statement.executeQuery("SELECT T1.name, T2.name, T1.location\n" +
                "FROM Teams T1, Teams T2\n" +
                "WHERE T1.location = T2.location AND\n" +
                "T1.teamID <> T2.teamID\n" +
                "GROUP BY T1.location\n");
    }

    public ResultSet gamesInTourney(String tourney) throws Exception{
        String query = "SELECT G.gameID, T2.name, T3.name\n" +
                "FROM Tournaments T1, Games G, PlayIn P1, PlayIn P2, Teams T2, Teams T3\n" +
                "WHERE T1.tournamentID = G.tournamentID AND\n" +
                "T1.tournamentID = \'"+ tourney +"\' AND\n" +
                "P1.gameID = G.gameID AND\n" +
                "P2.gameId = G.gameID AND\n" +
                "T2.teamID = P2.teamID AND\n" +
                "T3.teamId = P1.teamID AND\n" +
                "T2.teamID <> T3.teamID AND\n" +
                "T2.teamID < T3.teamID\n";
        return statement.executeQuery(query);
    }

    public ResultSet tournamentsOnSameDay() throws Exception{
        String query = "SELECT DISTINCT T1.name, T2.name\n" +
                "FROM Tournaments T1, Tournaments T2\n" +
                "WHERE T1.tournamentID <> T2.tournamentID AND\n" +
                "T1.tournamentDate = T2.tournamentDate";
        return statement.executeQuery(query);
    }

    public ResultSet retiredPlayers() throws Exception{
        return statement.executeQuery(  "SELECT Players.playerID, players.name " +
                                        "FROM Players " +
                                        "WHERE Players.playerID NOT IN  (SELECT  P1.playerID " +
                                                                        "FROM    ParticipatesIn P1, ParticipatesIn P2, Games G1, Games G2, Tournaments T1, Tournaments T2 " +
                                                                        "WHERE   P1.playerID = P2.playerID AND " +
                                                                                "P1.gameID = G1.gameID AND " +
                                                                                "P2.gameID = G2.gameID AND " +
                                                                                "G1.tournamentID = T1.tournamentID AND " +
                                                                                "G2.tournamentID = T2.tournamentID AND " +
                                                                                "T1.tournamentID <> T2.tournamentID AND " +
                                                                                "ABS(DATEDIFF(T1.tournamentDate, T2.tournamentDate)) < 365)");
    }

    public ResultSet teamsAllTournaments() throws Exception{
        return statement.executeQuery(  "SELECT TournamentsPlayed.name, TournamentsPlayed.teamID " +
                                        "FROM   (SELECT Teams.name AS name, PlayIn.teamID AS teamID, COUNT(DISTINCT Games.tournamentID) AS tournaments " +
                                                "FROM   Games, PlayIn, Teams " +
                                                "WHERE  Games.gameID = PlayIn.gameID AND " +
                                                       "Teams.teamID = PlayIn.teamID " +
                                                "GROUP BY PlayIn.teamID) AS TournamentsPlayed, " +
                                               "(SELECT COUNT(Tournaments.tournamentID) AS tournamentsPlayed " +
                                                "FROM Tournaments) AS totalTournaments " +
                                        "WHERE TournamentsPlayed.tournaments = totalTournaments.tournamentsPlayed");
    }

    public ResultSet playersOnlyOnOneTeam() throws Exception{
        return statement.executeQuery(  "SELECT Players.playerID, Players.name " +
                                        "FROM Players " +
                                        "WHERE Players.playerID not in(Select p1.playerID " +
                                        "from Players p1, ParticipatesIn pi " +
                                        "where p1.playerID = pi.playerID and pi.gameID not in(Select PlayIn.gameId " +
                                        "from PlayIn " +
                                        "where PlayIn.gameId = pi.gameId and p1.teamId = PlayIn.teamId))");
    }

    /**
     * inserts a player into the database
     * @param name      the name of the player
     * @param position  the position of the player
     * @param teamID    the team the player is on
     */
    public void insertPlayer(String name, String position, int teamID) throws Exception{
        statement.execute("INSERT INTO PLAYERS (name, position, teamID) values (" +
                "\'" + name + "\', \'" + position + "\', " + teamID + ")");
    }

    /**
     *
     * @param name The team name
     * @param location The location of the team
     * @param wins  Number of wins
     * @param losses Number of losses
     * @param coachID Coach of the team
     * @throws Exception    Catches any SQL Exception
     */
    public void insertTeam(String name, String location, int wins, int losses, int coachID) throws Exception{
        String update = "INSERT INTO TEAMS(name, location, wins, losses, coachID) values (" +  "\'" +
                name + "\' , \'" + location + "\' , " + wins + "," + losses + "," + coachID + ")";
        statement.executeUpdate(update);
    }

    /**
     * @param name          Name of the coach
     * @throws Exception    Catches any SQL exception
     */
    public void insertCoach(String name) throws Exception{
        String update = "INSERT INTO COACHES(name) values (\'" + name + "\')";
        statement.executeUpdate(update);
    }

    public void insertTournament(String name, String location, String date) throws Exception{

        //String update = "INSERT INTO TOURNAMENTS(name, location, tournamentDate) values (\' " + name + "\', \'" + location + "\', " + date + ")";
        String update = "INSERT INTO TOURNAMENTS(name, location, tournamentDate) values (\' " + name + "\', \'" + location + "\', DATE \'" + date + " \')";

        statement.executeUpdate(update);
    }

    /**
     * Create a game, auto updates Games, PlaysIn, ParticipatesIn
     */
    public void insertGame(String team1, String team2, String tournamentID, String score, String startTime, String endTime) throws Exception{
        String updateGames = "INSERT INTO GAMES(score, startTime, endTime, tournamentID) values ( \'" + score + "\', \'" + startTime + "\' , \'" +
                endTime + "\'," + tournamentID + ")";
        statement.executeUpdate(updateGames);
        ResultSet rs = statement.executeQuery("Select gameID " +
                "from Games "
                + "ORDER BY gameID desc "
                + "LIMIT 1");
        rs.next();
        String gameID = rs.getString(1);
        String updatePlaysIn = "INSERT INTO PLAYIN(gameID, teamID) values (" + gameID + "," + team1 + ")";
        statement.executeUpdate(updatePlaysIn);
        updatePlaysIn = "INSERT INTO PLAYIN(gameID, teamID) values (" + gameID + "," + team2 + ")";
        statement.executeUpdate(updatePlaysIn);

        rs = statement.executeQuery("Select playerID from Players where teamID = \'" + team1 + "\' or teamID = \'" + team2 + "\'");

        while(rs.next()) {
            String updatePartIn = "INSERT INTO PARTICIPATESIN(playerID, gameID, drops, assists, goals, pointsPlayed, throwaways) values (" +
                    rs.getInt(1) + "," + gameID + ", 0, 0, 0, 0, 0)";
            statement2.executeUpdate(updatePartIn);
        }

    }

    public void updatePart(int playerID, int gameID, int drops, int assists, int goals, int pointsPlayed, int throwaways) throws Exception{
        String updatePlayer = "UPDATE participatesIn SET drops = " + drops + ", assists = " + assists + ", goals =" + goals +
                ", pointsPlayed = " + pointsPlayed + ", throwaways = " + throwaways + " WHERE playerID = " + playerID + " and gameID = " +
                gameID;
        statement.executeUpdate(updatePlayer);

    }

    public void updateTeam(int teamId, String name, String location, int wins, int losses, int coachID) throws Exception{
        String updateTeam = "UPDATE teams SET name = \'" + name + "\', location = \'" + location + "\', wins = " + wins + ", losses ="
                + losses + ", coachID = " + coachID + " WHERE teamID = " + teamId;
        statement.executeUpdate(updateTeam);
    }

    public void updateCoach(int coachID, String name) throws Exception{
        String updateCoach = "UPDATE coaches set name = \'" + name + "\' WHERE coachID = " + coachID;
        statement.executeUpdate(updateCoach);
    }

    public void updatePlayer(int playerID, String name, String position, int teamID) throws Exception{
        String updatePlayer = "UPDATE players SET name = \'" + name + "\', teamID = " + teamID + ", position = \'" + position.toLowerCase() + "\' WHERE playerID = " + playerID;
        statement.executeUpdate(updatePlayer);
    }

    public void updateTournaments(int tournamentID, String name, String location, String date) throws Exception{
        String updateTournament = "UPDATE tournaments SET name = \'" + name + "\', location = \'" + location + "\',tournamentDate = DATE \'" + date +
                "\' WHERE tournamentID = " + tournamentID;
        statement.executeUpdate(updateTournament);
    }

    public void updateGames(int gameId, String score, String start, String end, int tournamentID) throws Exception{
        String updateGame = "UPDATE games SET score = \'" + score + "\', startTime = \'" + start + "\', endTime = \'" + end + "\'," +
                "tournamentId = " + tournamentID + " WHERE gameID = " + gameId;
        statement.executeUpdate(updateGame);
    }

    //public void updatePlaysIn(int teamID, int gameID)
    /*
    * Utility function for iterating through a result set. Outputs to commandline for now though.
    * */
    /*public static void getResults(ResultSet rs){
        try {
            ResultSetMetaData mdata = rs.getMetaData();
            //Loops through the rows of the output
            while (rs.next()) {
                //Loops through the columns of the output
                for (int j = 1; j <= mdata.getColumnCount(); j++) {
                    //TODO: Output to JFRAME
                }
                //TODO: Print new line in JFRAME
            }

        }
        catch(SQLException e){
            System.out.println(e.toString());
        }
    } */

}
