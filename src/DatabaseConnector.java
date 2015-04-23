import sun.reflect.annotation.ExceptionProxy;

import java.sql.*;

/**
 * Created by Eric on 4/19/2015.
 * Utility functions for UI to edit and retrieve from database
 * Use these to access and edit the RDBMS
 */
public class DatabaseConnector {
    public enum USERTYPE{GUEST, ADMIN}; /*The types of user for our database*/

    private static final String DATABASE_NAME = ""; /*TODO: get the name of the database from Dario*/
    private static final String DRIVER = "com.mysql.jdbc.Driver"; /*JDBC driver for mysql*/
    private static final String SERVER_USER = "jdbc:mysql://localhost:3306/" + DATABASE_NAME + "?user=user&password=password";
    private static final String SERVER_ADMIN = "jdbc:mysql://localhost:3306/" + DATABASE_NAME + "?user=admin&passsword=password";
    private USERTYPE type;

    private Statement statement;

    public DatabaseConnector(USERTYPE user) throws Exception{
        try{
            this.type = user;
            Class.forName(DRIVER);
            Connection conn;
            if(user == USERTYPE.GUEST){
                conn = DriverManager.getConnection(SERVER_USER);
            }else{
                conn = DriverManager.getConnection(SERVER_ADMIN);
            }

            statement = conn.createStatement();
        }
        catch(Exception e){
            throw e;
        }
    }

    /*
        Retrieve all stats for a given team
        Give "*" to retrieve all stats for all teams
     */
    public ResultSet getTeam(String s) throws Exception{
        try {
            ResultSet rs;
            String query = "Select * from Teams where teamID = \'" + s + "\'";
            if(s.equals("*")){
                query = "Select * from Teams";
            }
            rs = statement.executeQuery(query);
            return rs;
        }catch(Exception e){
            throw e;
        }
    }

    /*
        Gets the players
    * */
    public ResultSet getPlayer(String s) throws Exception{
        try{
            ResultSet rs;
            String query = "Select * from Players where playerID = \'" + s + "\'";
            if(s.equals("*")){
                query = "Select * from Players";
            }
            rs = statement.executeQuery(query);
            return rs;
        }catch(Exception e){
            throw e;
        }
    }
    /*
        Gets a coach
        Use * to return the entire table
     */
    public ResultSet getCoach(String s) throws Exception{
        try{
            ResultSet rs;
            //Select a given coach
            String query = "Select * from Coaches where coachID = \'" + s + "\'";
            //Special case handling
            if(s.equals("*")){
                query = "Select * from Coaches";
            }
            rs = statement.executeQuery(query);
            return rs;
        }catch(Exception e){
            throw e;
        }
    }

    /*
        Gets the tuple for a tournament
        User * to return the entire table
     */
    public ResultSet getTournament(String s) throws Exception{
        try{
            ResultSet rs;
            String query = "Select * from tournaments where tournamentID = \'" + s + "\'";
            if(s.equals("*")){
                query = "Select * from tournaments";
            }
            rs = statement.executeQuery(query);
            return rs;
        }catch(Exception e){
            throw e;
        }
    }

    /*
        Gets the tuple for a game
        Use * to get the entire table
     */
    public ResultSet getGame(String s) throws Exception{
        try{
            ResultSet rs;
            String query = "Select * from games where gameid = \'" + s + "\'";
            if(s.equals("*")){
                query = "Select * from games";
            }
            rs = statement.executeQuery(query);
            return rs;
        }catch(Exception e){
            throw e;
        }
    }

    /**
     * Queries the playin relation
     * @param game
     * @param player
     * @return
     * @throws Exception
     */
    public ResultSet playsIn(String game, String player) throws Exception{
        try{
            String query;
            if(game == null && player == null){
                query = "Select * from playin";
            }
            else if(player == null){
                query = "Select * from playin where gameid = \'" + game + "\'";
            }
            else if(game == null){
                query = "Select * from playin where playerid = \'" + player + "\'";
            }
            else{
                query = "Select * from playin where gameid = \'" + game + "\' and playerid = \'" + player + "\'";
            }
            return statement.executeQuery(query);
        }catch(Exception e){
            throw e;
        }
    }

    /**
     * Finds top N players for a stat
     * @param stat
     * @return
     * @throws Exception
     */
    public ResultSet topNStat(int n, String stat) throws Exception{
        try{
            String number = Integer.toString(n);
            String query = "SELECT SUM(PI." + stat + "), P.name \n" +
                    "FROM ParticipatesIn AS PI, Players AS P \n" +
                    "WHERE P.playerID = PI.playerID \n" +
                    "GROUP BY P.name\n" +
                    "ORDER BY SUM(PI." + stat + ") desc\n" +
                    "LIMIT " + number;
            return statement.executeQuery(query);
        }catch(Exception e){
            throw e;
        }
    }

    /**
     * Finds the top n teams with best records (determined as wins/losses)
     * ...What happens if the team has no losses?
     * @param rs
     */
    public ResultSet bestRecords(int n) throws Exception{
        try{
            String number = Integer.toString(n);
            String query = "SELECT T.wins/T.Losses, T.name\n" +
                    "FROM Teams AS T\n" +
                    "ORDER BY T.wins/T.losses desc\n" +
                    "LIMIT " + number;
            return statement.executeQuery(query);
        }catch(Exception e){
            throw e;
        }
    }

    /**
     * Finds top n teams with the highest stats
     * @param rs
     */
    public ResultSet bestTeamsStats(int n, String stat) throws Exception{
        try{
            String number = Integer.toString(n);
            String query = "SELECT \tSUM(PI." + stat +"), T.name\n" +
                    "FROM \tTeams AS T, Players AS P, ParticipatesIn AS PI\n" +
                    "WHERE \tT.teamID = P.teamID AND\n" +
                    "\t\tPI.playerID = P.playerID\n" +
                    "GROUP BY T.name\n" +
                    "ORDER BY SUM(PI.pointsPlayed) desc\n" +
                    "LIMIT " + number;
            return statement.executeQuery(query);
        }catch(Exception e){
            throw e;
        }
    }

    /**
     * Stats for all players on a given team
     * @param rs
     */
    public ResultSet statsForPlayersOnTeam(String team) throws Exception{
        try{
            String query = "SELECT P.name, SUM(PI.drops), SUM(PI.assists), SUM(PI.goals), SUM(PI.pointsPlayed), SUM(PI.throwaways)\n" +
                    "FROM Players AS P, Teams AS T, ParticipatesIn AS PI\n" +
                    "Where T.teamId = \'"+ team + "\'AND P.teamId = T.teamId AND PI.playerId = P.playerId\n" +
                    "GROUP BY P.playerId" ;
            return statement.executeQuery(query);
        }catch(Exception e){
            throw e;
        }
    }

    public ResultSet statsForPlayer(String player) throws Exception{
        try{
            String query = "SELECT P.name, P.playerId, SUM(PI.drops), SUM(PI.assists), SUM(PI.goals), \n" +
                    "SUM(PI.pointsPlayed), SUM(PI.throwaways)\n" +
                    "FROM Players P, ParticipatesIn PI\n" +
                    "WHERE P.name = \'"+ player +"\'AND\n" +
                    "tPI.playerID = P.playerID ";
            return statement.executeQuery(query);
        }catch(Exception e){
            throw e;
        }
    }

    public ResultSet statsForTeam(String team) throws Exception{
        try{
            String query = "SELECT SUM(drops), SUM(assists), SUM(goals), SUM(pointsPlayed), SUM(throwaways)\n" +
                    "FROM (\tSELECT \tP.name, PI.playerID, SUM(PI.drops) AS drops, SUM(PI.assists) AS assists, SUM(PI.goals) AS goals, SUM(PI.pointsPlayed) AS pointsPlayed, SUM(throwaways) AS throwaways\n" +
                    "FROM \tPlayers P, Teams T, ParticipatesIn PI\n" +
                    "WHERE \tP.teamID = T.teamID AND\n" +
                    "P.playerID = PI.playerID AND\n" +
                    "T.Name =" + team +"\' " +
                    "GROUP BY PI.playerID) AS A";
            return statement.executeQuery(query);
        }catch(Exception e){
            throw e;
        }
    }

    public ResultSet statsForGame(String team, String game) throws Exception{
        try{
            String query = "SELECT T.name, SUM(PI.drops), SUM(PI.assists), SUM(PI.goals), SUM(PI.pointsPlayed), SUM(throwaways)\n" +
                    "FROM Players P, Teams T, ParticipatesIn PI, Games G\n" +
                    "WHERE G.gameID = \'" + game +"\' and T.teamID = \'" +  team +"\'and PI.gameID = G.gameId and P.teamID = T.teamID and PI.playerID = P.playerID\n" +
                    "GROUP T.teamID\n";
            return statement.executeQuery(query);
        }catch(Exception e){
            throw e;
        }
    }

    public ResultSet playersAboveThreshold(String stat, int threshold) throws Exception{
        try{
            String thresholdString = Integer.toString(threshold);
            String query = "SELECT P.name, SUM(PI.goals)\n" +
                    "FROM players AS P, ParticipatesIn AS PI\n" +
                    "WHERE P.playerID = PI.playerID\n" +
                    "GROUP BY P.name\n" +
                    "HAVING SUM(PI." + stat +") > " + thresholdString +
                    "ORDER BY SUM(PI. "+ stat +") desc\n";
            return statement.executeQuery(query);
        }catch(Exception e){
            throw e;
        }
    }


    public ResultSet yearsPlayed() throws Exception{
        try{
            return statement.executeQuery("SELECT DISTINCT PY.playerID, PY.name, MAX(PY.year ) - MIN(PY.year) AS yearsPlayed\n" +
                    "FROM (SELECT P.playerID, P.name, YEAR(T.tournamentDate) AS year\n" +
                    "FROM Players P, ParticipatesIn PI, Games G, Tournaments T\n" +
                    "WHERE P.playerID = PI.playerID AND \n" +
                    "PI.gameID = G.gameID AND G.tournamentID = T.tournamentID) AS PY\n" +
                    "GROUP BY PY.playerID");
        }catch(Exception e){
            throw e;
        }
    }

    public ResultSet teamsSharingLoc() throws Exception{
        try{
            return statement.executeQuery("SELECT T1.name, T2.name, T1.location\n" +
                    "FROM Teams T1, Teams T2\n" +
                    "WHERE T1.location = T2.location AND\n" +
                    "T1.teamID <> T2.teamID\n" +
                    "GROUP BY T1.location\n");
        }catch(Exception e){
            throw e;
        }
    }


    public ResultSet gamesInTourney(String tourney) throws Exception{
        try{
            String query = "SELECT G.gameID, T2.name, T3.name\n" +
                    "FROM Tournaments T1, Games G, PlayIn P1, PlayIn P2, Teams T2, Teams T3\n" +
                    "WHERE T1.tournamentID = G.tournamentID AND\n" +
                    "T1.tournamentID = \'"+ tourney +"\' AND\n" +
                    "P1.gameID = G.gameID AND\n" +
                    "P2.gameId = G.gameID AND\n" +
                    "T2.teamID = P2.teamID AND\n" +
                    "T3.teamId = P1.teamID AND\n" +
                    "T2.teamID <> T3.teamID\n";
            return statement.executeQuery(query);

        }catch(Exception e){
            throw e;
        }
    }


    public ResultSet tournamentsOnSameDay() throws Exception{
        try{
            String query = "SELECT T1.name, T2.name\n" +
                    "FROM Tournaments T1, Tournaments T2\n" +
                    "WHERE T1. tournamentID <> T2.tournamentID AND\n" +
                    "T1.tournamentDate = T2.tournamentDate";
            return statement.executeQuery(query);
        }catch(Exception e){
            throw e;
        }
    }
    /*
    * Utility function for iterating through a result set. Outputs to commandline for now though.
    * */
    public static void getResults(ResultSet rs){
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
    }

}
