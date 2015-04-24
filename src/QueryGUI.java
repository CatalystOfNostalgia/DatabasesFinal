import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;


/**
 * Created by Sophie on 4/19/2015.
 */
public class QueryGUI extends JFrame {

    private JFrame frame;
    private JButton playerButton;
    private JButton teamButton;
    private JButton coachButton;
    private JButton tournamentButton;
    private JButton gameButton;
    private JButton participatesInButton;
    private JButton playInButton;
    private JPanel west;
    private JPanel center;
    private JPanel centerUpdate;
    private String[] queryStrings;
    private JTextField numPlayers;
    private JComboBox statisticCombo;
    private String[] statisticList;
    private JTextField numTeams;
    private JButton adminButton;
    private JTextField teamID;
    private JTextField playerID;
    private JTextField threshold;
    private JTextField playerID2;
    private JTextField tournamentID;
    private JTextField gameID;
    private JComboBox queryList;
    private JComboBox hOrC;
    private JTextField nameInput;
    private JComboBox teamIDComboBox;

    private static String driver ="com.mysql.jdbc.Driver" ;
    private static String server
            ="jdbc:mysql://localhost:3306/Assignment5";
    private static String username = "user";
    private static String password = "password";
    private static Connection con=null;

    //constructor
    public QueryGUI(){
        frame = new JFrame("Frisbee Database");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainScreen();
    }

    public void mainScreen(){
        if (centerUpdate != null){
            frame.remove(centerUpdate);
        }
        //create buttons
        this.playerButton = new JButton("Players");
        playerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPlayers();
            }
        });
        this.teamButton = new JButton("Teams");
        teamButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showTeams();
            }
        });
        this.coachButton = new JButton("Coaches");
        coachButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCoaches();
            }
        });
        this.tournamentButton = new JButton ("Tournaments");
        tournamentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showTournaments();
            }
        });
        this.gameButton = new JButton("Games");
        gameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showGames();
            }
        });
        this.participatesInButton = new JButton("Participates In");
        participatesInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showParticipatesIn();
            }
        });
        this.playInButton = new JButton("Plays In");
        playInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPlayIn();
            }
        });

        //panel to see relations
        JPanel listButtons = new JPanel(new GridLayout(5, 1));
        listButtons.add(playerButton);
        listButtons.add(teamButton);
        listButtons.add(coachButton);
        listButtons.add(tournamentButton);
        listButtons.add(gameButton);
        listButtons.add(participatesInButton);
        listButtons.add(playInButton);

        adminButton = new JButton("Admin");
        adminButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adminDisplay();
            }
        });

        listButtons.add(adminButton);

        //add panel to left side of frame
        west = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.anchor = GridBagConstraints.NORTH;
        g.weighty = 1;
        west.add(listButtons, g);

        //center panel
        center = new JPanel(new GridLayout(1,3));

        center.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        JTextField text = new JTextField("Choose your query:");
        text.setEditable(false);
        center.add(text);

        //drop down box with queries
        queryStrings = new String[] {"Top players in every category", "Teams with the best record",
                "Teams with highest stats for a certain statistic", "Stats for all players on a team",
                "Stats for an individual player", "Stats for one team", "Stats for one team in one game",
                "All players above a threshold in a statistics", "How many years all players have played for",
                "Comparing two players", "Teams from the same location", "All games in a specific tournament"};

        queryList = new JComboBox(queryStrings);
        center.add(queryList);

        //button to select query
        JButton select = new JButton("Select");
        select.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int queryIndex = queryList.getSelectedIndex();
                updateFrame(queryIndex);
            }
        });
        center.add(select);

        frame.add(west, BorderLayout.WEST);
        frame.add(center);
        frame.pack();
        frame.setVisible(true);

    }

    public void adminDisplay(){
        frame.setVisible(false);
        frame.remove(center);
        centerUpdate = new JPanel();

        JTextField chosenTable = new JTextField("Admin");
        chosenTable.setEditable(false);
        centerUpdate.add(chosenTable);

        JButton aPlayer = new JButton("Add Player");
        aPlayer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addPlayer();
            }
        });
        JButton aGame = new JButton("Add Game");
        aGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addGame();
            }
        });
        JButton aTournament = new JButton("Add Tournament");
        aTournament.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTournament();
            }
        });
        JButton aCoach = new JButton("Add Coach");
        aCoach.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCoach();
            }
        });
        JButton aTeam = new JButton("Add Team");
        aTeam.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTeam();
            }
        });
        JButton aParticipatesIn = new JButton("Add Participates In");
        aParticipatesIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addParticipatesIn();
            }
        });

        JPanel buttons = new JPanel(new GridLayout(3,3));
        buttons.add(aPlayer);
        buttons.add(aGame);
        buttons.add(aTournament);
        buttons.add(aCoach);
        buttons.add(aTeam);
        buttons.add(aParticipatesIn);
        centerUpdate.add(buttons);


        /*try{
            DatabaseConnector database = new DatabaseConnector(DatabaseConnector.USERTYPE.ADMIN);
            ResultSet rs = database.getPlayer("*");
            DefaultListModel model = getListInfo(rs);

            JList queryResult = new JList(model);
            JScrollPane scroll = new JScrollPane(queryResult);
            centerUpdate.add(scroll);
            frame.setVisible(true);
        catch(Exception e){
            System.out.println(e.toString());
        }
        finally{
        } */

        //button to go back to original screen
        JButton back = new JButton("Back");
        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainScreen();
            }
        });
        centerUpdate.add(back);
        frame.add(centerUpdate);
        frame.setVisible(true);

    }

    public void addPlayer(){
        frame.setVisible(false);
        frame.remove(centerUpdate);
        centerUpdate = new JPanel(new GridLayout(3,2));

        //JTextField name = new JTextField("Name");
       //name.setEditable(false);
        nameInput = new JTextField("Name");
        hOrC = new JComboBox(new String[] {"Handler", "Cutter"});

        //FIX THIS TO FIND ALL THE TEAMIDS IN THE TABLE
        try{
            DatabaseConnector database = new DatabaseConnector(DatabaseConnector.USERTYPE.GUEST);
            ResultSet rs = database.getTeam("*");
            ResultSetMetaData mdata = rs.getMetaData();

            ArrayList<Integer> idList = new ArrayList<Integer>();
            while (rs.next()){
                idList.add(rs.getInt("teamID"));
            }
            
            teamIDComboBox = new JComboBox(idList.toArray());
            //centerUpdate.add(name);
            centerUpdate.add(nameInput);
            centerUpdate.add(hOrC);
            centerUpdate.add(teamIDComboBox);

            //button to go back to original screen
            JButton back = new JButton("Back");
            back.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    mainScreen();
                }
            });

            JButton add = new JButton("Add");
            add.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String playerName = nameInput.getText();
                    int playerPositionIndex = hOrC.getSelectedIndex();
                    String playerPosition;
                    if (playerPositionIndex == 0)
                        playerPosition = "Handler";
                    else
                        playerPosition = "Cutter";

                    int teamId = teamIDComboBox.getSelectedIndex();


                    //database.addPlayer(playerName, playerPosition, teamId);
                }
            });

            centerUpdate.add(back);
            centerUpdate.add(add);
            frame.add(centerUpdate);
            frame.setVisible(true);

        }
        catch(Exception e){
            System.out.println(e.toString());
        }
        finally{
        }
        String[] teamIDOptions = new String[] {"0", "1", "2", "3", "4", "5"};
    }

    public void addGame(){

    }

    public void addTournament(){

    }

    public void addCoach(){

    }

    public void addTeam(){

    }

    public void addParticipatesIn(){

    }

    public void updateFrame(int index){
        frame.setVisible(false);
        frame.remove(center);
        centerUpdate = new JPanel(new GridLayout(3,3));

        JTextField chosenQuery = new JTextField(queryStrings[index]);
        chosenQuery.setEditable(false);
        centerUpdate.add(chosenQuery);

        statisticList = new String[] {"Drops", "Assists", "Goals", "Points Played", "Throwaways"};

        switch (index) {
            case 0:  //top (some number of) players in certain category
                numPlayers = new JTextField("Number of players");
                statisticCombo = new JComboBox(statisticList);
                centerUpdate.add(numPlayers);
                centerUpdate.add(statisticCombo);
                break;
            case 1: //teams with best records
                numTeams = new JTextField("Number of teams");
                centerUpdate.add(numTeams);
                break;
            case 2: //teams that have highest stats for certain statistic
                statisticCombo = new JComboBox(statisticList);
                numTeams = new JTextField("Number of teams");
                centerUpdate.add(statisticCombo);
                centerUpdate.add(numTeams);
                break;
            case 3: //stats for all players on a team
                teamID = new JTextField("TeamID");
                centerUpdate.add(teamID);
                break;
            case 4: //stats for an individual player
                playerID = new JTextField("playerID");
                centerUpdate.add(playerID);
                break;
            case 5: //cumulative stats for one team
                teamID = new JTextField("TeamID");
                centerUpdate.add(teamID);
                break;
            case 6: //stats for one team in one game (output all the players on that team)
                teamID = new JTextField("TeamID");
                gameID = new JTextField("GameID");
                centerUpdate.add(teamID);
                centerUpdate.add(gameID);
                break;
            case 7: //all players above a threshold for a certain statistic
                threshold = new JTextField("Threshold");
                statisticCombo = new JComboBox(statisticList);
                centerUpdate.add(statisticCombo);
                centerUpdate.add(threshold);
                break;
            case 8: //how many years all players have played for
                break;
            case 9: //comparing two players
                playerID = new JTextField("PlayerID1");
                playerID2 = new JTextField("PlayerID2");
                centerUpdate.add(playerID);
                centerUpdate.add(playerID2);
                break;
            case 10: //teams from the same location
                break;
            case 11: //all games in a specific tournament
                tournamentID = new JTextField("TournamentID");
                centerUpdate.add(tournamentID);
                break;
        }

        JButton viewResult = new JButton("View Result");
        centerUpdate.add(viewResult);
        final int index1 = index;
        viewResult.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] information = new String[9];   //0 - numPlayers, 1-numTeams, 2-statistic, 3-teamID, 4-playerID, 5-threshold, 6-playerID2, 7-tournamentID, 8-gameID
                switch (index1) {
                    case 0:
                        information[0] = numPlayers.getText();
                        information[2] = statisticList[statisticCombo.getSelectedIndex()];
                        if (information[2].equals("Points Played")) {
                            information[2] = "pointsPlayed";
                        }
                        break;
                    case 1:
                        information[1] = numTeams.getText();
                        break;
                    case 2:
                        information[1] = numTeams.getText();
                        information[2] = statisticList[statisticCombo.getSelectedIndex()];
                        if (information[2].equals("Points Played")) {
                            information[2] = "pointsPlayed";
                        }
                        break;
                    case 3:
                        information[3] = teamID.getText();
                        break;
                    case 4:
                        information[4] = playerID.getText();
                        break;
                    case 5:
                        information[3] = teamID.getText();
                        break;
                    case 6:
                        information[3] = teamID.getText();
                        information[8] = gameID.getText();
                        break;
                    case 7:
                        information[5] = threshold.getText();
                        information[2] = statisticList[statisticCombo.getSelectedIndex()];
                        if (information[2].equals("Points Played")) {
                            information[2] = "pointsPlayed";
                        }
                        break;
                    case 8:
                        break;
                    case 9:
                        information[4] = playerID.getText();
                        information[6] = playerID2.getText();
                        break;
                    case 10:
                        break;
                    case 11:
                        information[7] = tournamentID.getText();
                        break;
                }

                viewQuery(index1, information);
            }
        });

        //button to go back to original screen
        JButton back = new JButton("Back");
        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainScreen();
            }
        });
        centerUpdate.add(back);
        frame.add(centerUpdate);
        frame.setVisible(true);
    }

    public void viewQuery(int index, String[] information){
        frame.remove(centerUpdate);
        frame.setVisible(false);
        centerUpdate = new JPanel();


        try{
            DatabaseConnector database = new DatabaseConnector(DatabaseConnector.USERTYPE.GUEST);
            Class.forName(driver);
            con= DriverManager.getConnection(server, username, password);
            Statement instruction = con.createStatement();

            //create a method for each query and send the instruction (Statement type), and the relevant information pieces (stat, ID, etc.)

            DefaultListModel model = new DefaultListModel();

            ResultSet result = null;

            switch(index) {
                case 0:
                    result = database.topNStat(Integer.parseInt(information[0]), information[2]);
                    break;
                case 1:
                    result = database.bestRecords(Integer.parseInt(information[1]));
                    break;
                case 2:
                    result = database.bestTeamsStats(Integer.parseInt(information[1]), information[2]);
                    break;
                case 3:
                    result = database.statsForPlayersOnTeam(information[3]);
                    break;
                case 4:
                    result = database.statsForPlayer(information[4]);
                    break;
                case 5:
                    result = database.statsForTeam(information[3]);
                    break;
                case 6:
                    result = database.statsForGame(information[3], information[8]);
                    break;
                case 7:
                    result = database.playersAboveThreshold(information[2], Integer.parseInt(information[5]));
                    break;
                case 8:
                    result = database.yearsPlayed();
                    break;
                case 9:
                    result = database.playerComparison(information[4], information[6]);
                    break;
                case 10:
                    //NOTHING RETURNED USING THIS QUERY
                    result = database.teamsSharingLoc();
                    break;
                case 11:
                    result = database.gamesInTourney(information[7]);
                    break;
            }

            model = getListInfo(result);
            JList queryResult = new JList(model);
            JScrollPane scroll = new JScrollPane(queryResult);
            centerUpdate.add(scroll);
            frame.setVisible(true);

            con.close();
        } catch(Exception e){
            System.out.println(e.toString());
        }finally{
        }
        //button to go back to original screen
        JButton back = new JButton("Back");
        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainScreen();
            }
        });
        centerUpdate.add(back);
        frame.add(centerUpdate);
        frame.setVisible(true);
    }

    public DefaultListModel getListInfo(ResultSet rs){
        DefaultListModel model = null;
        try{
            ResultSetMetaData mdata = rs.getMetaData();
            String header = "";
            for (int i = 1; i <=mdata.getColumnCount(); i++){
                header = header + " " + mdata.getColumnName(i) + " \t \t \t \t \t \t";
            }
            //Loops through the rows of the output
            model = new DefaultListModel();
            model.addElement(header);

            while (rs.next()) {
                //Loops through the columns of the output
                StringBuilder sb = new StringBuilder();
                for (int j = 1; j <= mdata.getColumnCount(); j++) {
                    sb.append(rs.getString(j));
                    sb.append("\t \t \t \t \t \t \t \t \t");
                }
                String m = sb.toString();
                model.addElement(m);
            }
        }
        catch(Exception e){
            System.out.println(e.toString());
        }
        finally{
        }
        return model;
    }

    public void showPlayers(){
        frame.setVisible(false);
        frame.remove(center);
        centerUpdate = new JPanel();

        JTextField chosenTable = new JTextField("Players");
        chosenTable.setEditable(false);
        centerUpdate.add(chosenTable);

        try{
            DatabaseConnector database = new DatabaseConnector(DatabaseConnector.USERTYPE.GUEST);
            ResultSet rs = database.getPlayer("*");
            DefaultListModel model = getListInfo(rs);

            JList queryResult = new JList(model);
            JScrollPane scroll = new JScrollPane(queryResult);
            centerUpdate.add(scroll);
            frame.setVisible(true);
        }
        catch(Exception e){
            System.out.println(e.toString());
        }
        finally{
        }

        //button to go back to original screen
        JButton back = new JButton("Back");
        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainScreen();
            }
        });
        centerUpdate.add(back);
        frame.add(centerUpdate);
        frame.setVisible(true);
    }

    public void showTeams(){
        frame.setVisible(false);
        frame.remove(center);
        centerUpdate = new JPanel();

        JTextField chosenTable = new JTextField("Teams");
        chosenTable.setEditable(false);
        centerUpdate.add(chosenTable);

        try{
            DatabaseConnector database = new DatabaseConnector(DatabaseConnector.USERTYPE.GUEST);
            ResultSet rs = database.getTeam("*");
            DefaultListModel model = getListInfo(rs);

            JList queryResult = new JList(model);
            JScrollPane scroll = new JScrollPane(queryResult);
            centerUpdate.add(scroll);
            frame.setVisible(true);
        }
        catch(Exception e){
            System.out.println(e.toString());
        }
        finally{
        }

        //button to go back to original screen
        JButton back = new JButton("Back");
        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainScreen();
            }
        });
        centerUpdate.add(back);
        frame.add(centerUpdate);
        frame.setVisible(true);
    }

    public void showCoaches(){
        frame.setVisible(false);
        frame.remove(center);
        centerUpdate = new JPanel();

        JTextField chosenTable = new JTextField("Coaches");
        chosenTable.setEditable(false);
        centerUpdate.add(chosenTable);

        try{
            DatabaseConnector database = new DatabaseConnector(DatabaseConnector.USERTYPE.GUEST);
            ResultSet rs = database.getCoach("*");
            DefaultListModel model = getListInfo(rs);

            JList queryResult = new JList(model);
            JScrollPane scroll = new JScrollPane(queryResult);
            centerUpdate.add(scroll);
            frame.setVisible(true);
        }
        catch(Exception e){
            System.out.println(e.toString());
        }
        finally{
        }

        //button to go back to original screen
        JButton back = new JButton("Back");
        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainScreen();
            }
        });
        centerUpdate.add(back);
        frame.add(centerUpdate);
        frame.setVisible(true);
    }

    public void showTournaments(){
        frame.setVisible(false);
        frame.remove(center);
        centerUpdate = new JPanel();

        JTextField chosenTable = new JTextField("Tournaments");
        chosenTable.setEditable(false);
        centerUpdate.add(chosenTable);

        try{
            DatabaseConnector database = new DatabaseConnector(DatabaseConnector.USERTYPE.GUEST);
            ResultSet rs = database.getTournament("*");
            DefaultListModel model = getListInfo(rs);

            JList queryResult = new JList(model);
            JScrollPane scroll = new JScrollPane(queryResult);
            centerUpdate.add(scroll);
            frame.setVisible(true);
        }
        catch(Exception e){
            System.out.println(e.toString());
        }
        finally{
        }

        //button to go back to original screen
        JButton back = new JButton("Back");
        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainScreen();
            }
        });
        centerUpdate.add(back);
        frame.add(centerUpdate);
        frame.setVisible(true);
    }

    public void showGames(){
        frame.setVisible(false);
        frame.remove(center);
        centerUpdate = new JPanel();

        JTextField chosenTable = new JTextField("Games");
        chosenTable.setEditable(false);
        centerUpdate.add(chosenTable);

        try{
            DatabaseConnector database = new DatabaseConnector(DatabaseConnector.USERTYPE.GUEST);
            ResultSet rs = database.getGame("*");
            DefaultListModel model = getListInfo(rs);

            JList queryResult = new JList(model);
            JScrollPane scroll = new JScrollPane(queryResult);
            centerUpdate.add(scroll);
            frame.setVisible(true);
        }
        catch(Exception e){
            System.out.println(e.toString());
        }
        finally{
        }
        //button to go back to original screen
        JButton back = new JButton("Back");
        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainScreen();
            }
        });
        centerUpdate.add(back);
        frame.add(centerUpdate);
        frame.setVisible(true);
    }

    public void showParticipatesIn(){
        frame.setVisible(false);
        frame.remove(center);
        centerUpdate = new JPanel();

        JTextField chosenTable = new JTextField("Games");
        chosenTable.setEditable(false);
        centerUpdate.add(chosenTable);

        try{
            DatabaseConnector database = new DatabaseConnector(DatabaseConnector.USERTYPE.GUEST);
            ResultSet rs = database.participatesIn("*", "*");
            DefaultListModel model = getListInfo(rs);

            JList queryResult = new JList(model);
            JScrollPane scroll = new JScrollPane(queryResult);
            centerUpdate.add(scroll);
            frame.setVisible(true);
        }
        catch(Exception e){
            System.out.println(e.toString());
        }
        finally{
        }
        //button to go back to original screen
        JButton back = new JButton("Back");
        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainScreen();
            }
        });
        centerUpdate.add(back);
        frame.add(centerUpdate);
        frame.setVisible(true);
    }

    public void showPlayIn(){
        frame.setVisible(false);
        frame.remove(center);
        centerUpdate = new JPanel();

        JTextField chosenTable = new JTextField("Games");
        chosenTable.setEditable(false);
        centerUpdate.add(chosenTable);

        try{
            DatabaseConnector database = new DatabaseConnector(DatabaseConnector.USERTYPE.GUEST);
            ResultSet rs = database.playIn("*", "*");
            DefaultListModel model = getListInfo(rs);

            JList queryResult = new JList(model);
            JScrollPane scroll = new JScrollPane(queryResult);
            centerUpdate.add(scroll);
            frame.setVisible(true);
        }
        catch(Exception e){
            System.out.println(e.toString());
        }
        finally{
        }
        //button to go back to original screen
        JButton back = new JButton("Back");
        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainScreen();
            }
        });
        centerUpdate.add(back);
        frame.add(centerUpdate);
        frame.setVisible(true);
    }

    public static void main(String[] args){
        QueryGUI gui = new QueryGUI();
    }
}

