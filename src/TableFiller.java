import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class TableFiller {

    //server
    private static String driver = "com.mysql.jdbc.Driver";
    private static String server = "jdbc:mysql://localhost:3306";

    //credentials
    private static String user = "user";
    private static String pass = "password";
    private static Connection con = null;

    //player data
    //private int[] playerID;
    private int[] playerTeamID;
    private String[] playerNames;
    private String[] playerPositions;

    //team data
    //private int[] teamID;
    private int[] teamWins;
    private int[] teamLosses;
    private int[] teamCoachID;
    private String[] teamNames;
    private String[] teamLocations;

    //coach data
    //private int[] coachID;
    private String[] coachNames;

    //tournament data
    //private int[] tournamentID;
    private String[] tournamentNames;
    private String[] tournamentLocations;
    private String[] tournamentDate;

    //games data
    //private int[] gameID;
    private int[] gameTournamentID;
    private String[] gameScore;
    private String[] gameStartTime;
    private String[] gameEndTime;


    //participates in data
    private int[] participatesPlayerID;
    private int[] participatesGameID;
    private int[] participatesDrops;
    private int[] participatesAssists;
    private int[] participatesGoals;
    private int[] participatesPointsPlayed;
    private int[] participatesThrowaways;

    //play in data
    private int[] playGameID;
    private int[] playTeamID;

    public TableFiller() {

        //player data
        //playerID = new int[100];
        playerTeamID = new int[100];
        playerNames = new String[100];
        playerPositions = new String[100];

        //team data
        //teamID = new int[10];
        teamNames = new String[10];
        teamWins = new int[10];
        teamLosses = new int[10];
        teamCoachID = new int[10];
        teamLocations = new String[10];

        //coach data
        //coachID = new int[10];
        coachNames = new String[10];

        //tournament data
        //tournamentID = new int[10];
        tournamentLocations = new String[10];
        tournamentNames = new String[10];
        tournamentDate = new String[10];

        //games data
        //gameID = new int[100];
        gameScore = new String[100];
        gameStartTime = new String[100];
        gameEndTime = new String[100];
        gameTournamentID = new int[100];

        //participates in data
        participatesPlayerID = new int[200];
        participatesGameID = new int[200];
        participatesAssists = new int[200];
        participatesDrops = new int[200];
        participatesGoals = new int[200];
        participatesPointsPlayed = new int[200];
        participatesThrowaways = new int[200];

        //play in data
        playGameID = new int[200];
        playTeamID = new int[200];

        try {
            //initialize players and games
            BufferedReader playerNameReader = new BufferedReader(new FileReader("lib//names.txt"));
            for (int i = 0; i < 100; i++) {
                //players
                playerNames[i] = playerNameReader.readLine();
                //playerID[i] = i;
                if((int)(Math.random() * i) % 2 == 0) {
                    playerPositions[i] = "cutter";
                }
                else{
                    playerPositions[i] = "handler";
                }
                playerTeamID[i] = (i % 10) + 1;

                //games
                //gameID[i] = i;
                gameScore[i] = (i * 7) % 15 + " - " + (i % 15);
                gameStartTime[i] = (i % 12) + ":" + (i % 60);
                gameEndTime[i] = ((i % 11) + 1)  + ":" + ((i % 50) + 10);
                gameTournamentID[i] = (i % 10) + 1;
            }

            //initialize teams, tournaments and coaches

            BufferedReader teamNameReader = new BufferedReader((new FileReader("lib/teamnames.txt")));
            BufferedReader locationReader = new BufferedReader(new FileReader("lib/teamlocations.txt"));
            BufferedReader coachReader = new BufferedReader(new FileReader("lib/coachnames.txt"));
            BufferedReader tournamentNameReader = new BufferedReader(new FileReader("lib/tournamentnames.txt"));

            for(int i = 0; i < 10; i++) {
                //teams
                //teamID[i] = i;
                teamNames[i] = teamNameReader.readLine();
                teamWins[i] = i;
                teamLosses[i] = 10 - i;
                teamCoachID[i] = i + 1;
                teamLocations[i] = locationReader.readLine();

                //coaches
                //coachID[i] = i;
                coachNames[i] = coachReader.readLine();

                //tournaments
                //tournamentID[i] = i;
                tournamentNames[i] = tournamentNameReader.readLine();
                tournamentLocations[i] = teamLocations[i];
                tournamentDate[i] = "200" + i + "-" + i + "-" + i * 3;

            }

            //initialize play in and participates in
            for (int i = 0; i < 200; i++) {
                //participatesIn
                participatesPlayerID[i] = (i % 100) + 1;
                participatesGameID[i] = (i / 2) + 1;
                participatesPointsPlayed[i] = (int) (Math.random() * 17);
                participatesAssists[i] = (int) (Math.random() * participatesPointsPlayed[i]);
                participatesDrops[i] = (int) (Math.random() * participatesPointsPlayed[i] * 2);
                participatesGoals[i] = (int) (Math.random() * (participatesPointsPlayed[i] - participatesAssists[i]));
                participatesThrowaways[i] = (int) (Math.random() * participatesPointsPlayed[i] * 3);

                //play in
                playGameID[i] = (i / 2) + 1;
                playTeamID[i] = (i % 10) + 1;

            }


        }
        catch(FileNotFoundException e){
            System.out.println("missing file");
        }
        catch(IOException e){
            System.out.println("reading too far");
        }

    }

    public void createDatabase(){
        try {

            //server stuff
            Class.forName(driver);
            con = DriverManager.getConnection(server, user, pass);

            Statement instruction = con.createStatement();

            //drop things in case they already exist
            //instruction.execute("DROP DATABASE FrisbeeTest");

            instruction.execute("CREATE DATABASE FrisbeeTest");
            instruction.execute("USE FrisbeeTest");


            //tables
            instruction.execute("CREATE TABLE Coaches (coachID INT NOT NULL AUTO_INCREMENT, name varchar(20), PRIMARY KEY (coachID))");
            instruction.execute("CREATE TABLE Teams (teamID INT NOT NULL AUTO_INCREMENT, name varchar(20), location varchar(20), wins int, losses int, coachID int, PRIMARY KEY (teamID), FOREIGN KEY(coachID) REFERENCES Coaches(coachID))");
            instruction.execute("CREATE TABLE Players (playerID INT NOT NULL AUTO_INCREMENT, name varchar(20), position ENUM('cutter', 'handler'), teamID int, PRIMARY KEY(playerID), FOREIGN KEY (teamID) REFERENCES Teams(teamID));");
            instruction.execute("CREATE TABLE Tournaments (tournamentID INT NOT NULL AUTO_INCREMENT, name varchar(20), location varchar(20), tournamentDate DATE, PRIMARY KEY (tournamentID))");
            instruction.execute("CREATE TABLE Games (gameID INT NOT NULL AUTO_INCREMENT, score varchar(20), startTime TIME, endTime TIME, tournamentID int, PRIMARY KEY (gameID), FOREIGN KEY (tournamentID) REFERENCES Tournaments(tournamentID) ON DELETE CASCADE)");
            instruction.execute("CREATE TABLE ParticipatesIn(playerID int, gameID int, drops int, assists int, goals int, pointsPlayed int, throwaways int, PRIMARY KEY (playerID, gameID), FOREIGN KEY (playerID) REFERENCES Players(playerID) ON DELETE CASCADE, FOREIGN KEY (gameID) REFERENCES Games(gameID) ON DELETE CASCADE)");
            instruction.execute("CREATE TABLE PlayIn(gameID int, teamID int, PRIMARY KEY(gameID, teamID), FOREIGN KEY (gameID) REFERENCES Games(gameID) ON DELETE CASCADE, FOREIGN KEY (teamID) REFERENCES Teams(teamID) ON DELETE CASCADE)");

        }
        catch (Exception e){
            System.out.println(e.toString());
        }
    }

    public void populateDatabase(){

        try {
            //server stuff
            Class.forName(driver);
            con = DriverManager.getConnection(server, user, pass);

            Statement instruction = con.createStatement();
            instruction.execute("USE FrisbeeTest");

            //coaches, tournaments
            for (int i = 0; i < coachNames.length; i++) {
                //coaches
                instruction.execute("INSERT INTO Coaches(name) values ('" + coachNames[i] + "');");

                //tournaments
                instruction.execute("INSERT INTO Tournaments (name, location, tournamentDate) values('" +
                        tournamentNames[i] + "', '" + tournamentLocations[i] + "', STR_TO_DATE('" +
                        tournamentDate[i] + "', '%Y-%m-%d')" + ");");
            }


            //teams
            for (int i = 0; i < teamNames.length; i++) {
                instruction.execute("INSERT INTO TEAMS (name, location, wins, losses, coachID) values ('" +
                        teamNames[i] + "', '" + teamLocations[i] + "', " + teamWins[i] + ", " + teamLosses[i] +
                        ", " + teamCoachID[i] + ")");


            }

            //players and games
            for (int i = 0; i < playerNames.length; i++) {
                //players
                instruction.execute("INSERT INTO PLAYERS (name, position, teamID) values ('" + playerNames[i] +
                        "', '" + playerPositions[i] + "', " + playerTeamID[i] + ")");

                //games
                instruction.execute("INSERT INTO GAMES (score, startTime, endTime, tournamentID) values ('" +
                        gameScore[i] + "', TIME('" + gameStartTime[i] + "'), TIME('" + gameEndTime[i] +
                        "'), " + gameTournamentID[i] + ")");

            }

            for (int i = 0; i < playGameID.length; i++) {
                //plays in
                instruction.execute("INSERT INTO PlayIn (gameID, teamID) values (" + playGameID[i] + ", " +
                        playTeamID[i] + ")");

                //participates in
                instruction.execute("INSERT INTO ParticipatesIn (playerID, gameID, drops, assists, goals, pointsPlayed, throwaways) values (" +
                        participatesPlayerID[i] + ", " + participatesGameID[i] + ", " + participatesDrops[i] +
                        ", " + participatesAssists[i] + ", " + participatesGoals[i] + ", " + participatesPointsPlayed[i] +
                        ", " + participatesThrowaways[i] + ")");
            }

        }
        catch( Exception e){
            System.out.println(e.toString());
        }
    }

    public static void main(String[] args) {

        TableFiller t = new TableFiller();

        //t.printPlayerData();
        //t.printTeamData();
        //t.printTournamentData();
        //t.printGameData();
        //t.printParticipatesIn();
        t.createDatabase();
        t.populateDatabase();
    }

}
