import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
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
    private DatabaseConnector databaseAdmin;
    private DatabaseConnector database;
    private JTextField score;
    private JTextField startTime;
    private JTextField endTime;
    private JComboBox tournamentIDBox;
    private JComboBox team1Box;
    private JComboBox team2Box;
    private JTextField locationInput;
    private JTextField dateInput;
    private JTextField winsInput;
    private JTextField lossesInput;
    private JComboBox coachIDComboBox;
    private JComboBox playerIDComboBox;
    private JTextField playerIDtextBox;
    private JTextField positionInput;
    private JComboBox gameIDComboBox;
    private JTextField gameIDTextBox;
    private JTextField coachIDTextBox;
    private JTextField teamIDTextBox;
    private JTextField dropsTextBox;
    private JTextField assistsTextBox;
    private JTextField goalsTextBox;
    private JTextField pointsPlayedTextBox;
    private JTextField throwawaysTextBox;



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

        JButton insertButton = new JButton("Insert");
        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adminDisplayAdd();
            }
        });

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adminDisplayUpdate();
            }
        });


        JPanel buttons = new JPanel(new GridLayout(3,3));
        buttons.add(insertButton);
        buttons.add(updateButton);
        centerUpdate.add(buttons);

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

    public void adminDisplayAdd(){
        frame.setVisible(false);
        frame.remove(centerUpdate);
        centerUpdate = new JPanel();

        JTextField chosenTable = new JTextField("Admin: Insert");
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

        JPanel buttons = new JPanel(new GridLayout(3,3));
        buttons.add(aPlayer);
        buttons.add(aGame);
        buttons.add(aTournament);
        buttons.add(aCoach);
        buttons.add(aTeam);
        centerUpdate.add(buttons);

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

    public void adminDisplayUpdate(){
        frame.setVisible(false);
        frame.remove(centerUpdate);
        centerUpdate = new JPanel();

        JTextField chosenTable = new JTextField("Admin: Update");
        chosenTable.setEditable(false);
        centerUpdate.add(chosenTable);

        JButton uPlayer = new JButton("Update Player");
        uPlayer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updatePlayer();
            }
        });
        JButton uGame = new JButton("Update Game");
        uGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateGame();
            }
        });
        JButton uTournament = new JButton("Update Tournament");
        uTournament.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTournament();
            }
        });
        JButton uCoach = new JButton("Update Coach");
        uCoach.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCoach();
            }
        });
        JButton uTeam = new JButton("Update Team");
        uTeam.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTeam();
            }
        });
        JButton uParticipatesIn = new JButton("Update Participates In");
        uParticipatesIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateParticipatesIn();
            }
        });

        JPanel buttons = new JPanel(new GridLayout(3,3));
        buttons.add(uPlayer);
        buttons.add(uGame);
        buttons.add(uTournament);
        buttons.add(uCoach);
        buttons.add(uTeam);
        buttons.add(uParticipatesIn);
        centerUpdate.add(buttons);

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

        nameInput = new JTextField("Name");
        hOrC = new JComboBox(new String[] {"Handler", "Cutter"});

        try{
            databaseAdmin = new DatabaseConnector(DatabaseConnector.USERTYPE.ADMIN);
            ResultSet rs = databaseAdmin.getTeam("*");

            ArrayList<Integer> idList = new ArrayList<Integer>();
            while (rs.next()){
                idList.add(rs.getInt("teamID"));
            }

            teamIDComboBox = new JComboBox(idList.toArray());
            teamIDComboBox.setRenderer(new MyComboBoxRenderer("teamID"));
            teamIDComboBox.setSelectedIndex(-1);
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

                    int teamId = teamIDComboBox.getSelectedIndex() + 1;
                    try {
                        databaseAdmin.insertPlayer(playerName, playerPosition, teamId);
                    }
                    catch(Exception e2){
                        System.out.println(e2.toString());
                        System.out.println("4");
                    }
                    finally {
                    }
                }
            });

            centerUpdate.add(back);
            centerUpdate.add(add);
            frame.add(centerUpdate);
            frame.setVisible(true);

        }
        catch(Exception e1){
            e1.toString();
            System.out.println("2342");
        }
        finally{
        }
    }

    public void addGame(){
        frame.setVisible(false);
        frame.remove(centerUpdate);
        centerUpdate = new JPanel(new GridLayout(3,2));

        score = new JTextField("Score");
        startTime = new JTextField("Start Time");
        endTime = new JTextField("End Time");


        try{
            databaseAdmin = new DatabaseConnector(DatabaseConnector.USERTYPE.ADMIN);
            ResultSet rs = databaseAdmin.getTournament("*");

            ArrayList<Integer> tournamentIDList = new ArrayList<Integer>();

            while (rs.next()){
                tournamentIDList.add(rs.getInt("tournamentID"));
            }
            rs.close();

            ResultSet rs2 = databaseAdmin.getTeam("*");
            ArrayList<Integer> idList = new ArrayList<Integer>();
            while (rs2.next()){
                idList.add(rs2.getInt("teamID"));
            }

            team1Box = new JComboBox(idList.toArray());
            team1Box.setRenderer(new MyComboBoxRenderer("team1ID"));
            team1Box.setSelectedIndex(-1);
            team2Box = new JComboBox(idList.toArray());
            team2Box.setRenderer(new MyComboBoxRenderer("team2ID"));
            team2Box.setSelectedIndex(-1);

            tournamentIDBox = new JComboBox(tournamentIDList.toArray());
            tournamentIDBox.setRenderer(new MyComboBoxRenderer("tournamentID"));
            tournamentIDBox.setSelectedIndex(-1);
            centerUpdate.add(team1Box);
            centerUpdate.add(team2Box);
            centerUpdate.add(score);
            centerUpdate.add(startTime);
            centerUpdate.add(endTime);
            centerUpdate.add(tournamentIDBox);

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
                    int team1Text = team1Box.getSelectedIndex() + 1;
                    int team2Text = team2Box.getSelectedIndex() + 1;
                    String scoreText = score.getText();
                    String startTimeText = startTime.getText();
                    String endtimeText = endTime.getText();
                    int tournamentIDIndex = tournamentIDBox.getSelectedIndex() + 1;

                    try {
                        databaseAdmin.insertGame(Integer.toString(team1Text), Integer.toString(team2Text), Integer.toString(tournamentIDIndex), scoreText, startTimeText, endtimeText);
                    }
                    catch(Exception e2){
                        System.out.println(e2.toString());
                        System.out.println("1");
                    }
                    finally {
                    }
                }
            });

            centerUpdate.add(back);
            centerUpdate.add(add);
            frame.add(centerUpdate);
            frame.setVisible(true);

        }
        catch(Exception e3){
            System.out.println(e3.toString());
            System.out.println("2");
        }
        finally{
        }

    }

    public void addTournament(){
        frame.setVisible(false);
        frame.remove(centerUpdate);
        centerUpdate = new JPanel(new GridLayout(3,2));

        nameInput = new JTextField("Name");
        locationInput = new JTextField("Location");
        dateInput = new JTextField("Date");
        centerUpdate.add(nameInput);
        centerUpdate.add(locationInput);
        centerUpdate.add(dateInput);

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
                String nameText = nameInput.getText();
                String locationText = locationInput.getText();
                String dateText = dateInput.getText();

                try {
                    databaseAdmin = new DatabaseConnector(DatabaseConnector.USERTYPE.ADMIN);
                    /*SimpleDateFormat from = new SimpleDateFormat("dd/MM/yyyy");
                    SimpleDateFormat to = new SimpleDateFormat("yyyy-MM-dd");

                    Date date = new java.sql.Date(from.parse(dateText).getTime());
                    String mysqlDate = to.format(date);

                    System.out.println(mysqlDate);*/
                    databaseAdmin.insertTournament(nameText, locationText, "1993-11-12");
                }
                catch(Exception e2){
                    System.out.println(e2.toString());
                    System.out.println("1");
                }
                finally {
                }
            }
        });

        centerUpdate.add(back);
        centerUpdate.add(add);
        frame.add(centerUpdate);
        frame.setVisible(true);


    }

    public void addCoach(){
        frame.setVisible(false);
        frame.remove(centerUpdate);
        centerUpdate = new JPanel(new GridLayout(3,2));

        nameInput = new JTextField("Name");
        try {
            databaseAdmin = new DatabaseConnector(DatabaseConnector.USERTYPE.ADMIN);

            centerUpdate.add(nameInput);

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
                    String coachName = nameInput.getText();
                    try {
                        databaseAdmin.insertCoach(coachName);

                    } catch (Exception e2) {
                        System.out.println(e2.toString());
                        System.out.println("1");
                    } finally {
                    }
                }
            });

            centerUpdate.add(back);
            centerUpdate.add(add);
            frame.add(centerUpdate);
            frame.setVisible(true);
        }
        catch(Exception e3){
            System.out.println(e3.toString());
            System.out.println("2");
        }
        finally{
        }

    }

    public void addTeam(){
        frame.setVisible(false);
        frame.remove(centerUpdate);
        centerUpdate = new JPanel(new GridLayout(3,2));

        nameInput = new JTextField("Name");
        locationInput = new JTextField("Location");
        winsInput = new JTextField("Wins");
        lossesInput = new JTextField("Losses");

        try{
            databaseAdmin = new DatabaseConnector(DatabaseConnector.USERTYPE.ADMIN);
            ResultSet rs = databaseAdmin.getCoach("*");

            ArrayList<Integer> coachList = new ArrayList<Integer>();
            while (rs.next()){
                coachList.add(rs.getInt("coachID"));
            }

            coachIDComboBox = new JComboBox(coachList.toArray());
            coachIDComboBox.setRenderer(new MyComboBoxRenderer("coachID"));
            coachIDComboBox.setSelectedIndex(-1);
            centerUpdate.add(nameInput);
            centerUpdate.add(locationInput);
            centerUpdate.add(winsInput);
            centerUpdate.add(lossesInput);
            centerUpdate.add(coachIDComboBox);

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
                    String name = nameInput.getText();
                    String location = locationInput.getText();
                    int wins = Integer.parseInt(winsInput.getText());
                    int losses = Integer.parseInt(lossesInput.getText());

                    int coachId = coachIDComboBox.getSelectedIndex() + 1;

                    try {
                        databaseAdmin.insertTeam(name, location, wins, losses, coachId);
                    }
                    catch(Exception e2){
                        System.out.println(e2.toString());
                        System.out.println("4");
                    }
                    finally {
                    }
                }
            });

            centerUpdate.add(back);
            centerUpdate.add(add);
            frame.add(centerUpdate);
            frame.setVisible(true);

        }
        catch(Exception e1){
            System.out.println(e1.toString());
            System.out.println("2342");
        }
        finally{
        }
    }

    public void updatePlayer(){
        frame.setVisible(false);
        frame.remove(centerUpdate);
        centerUpdate = new JPanel(new GridLayout(3,2));

        try{
            databaseAdmin = new DatabaseConnector(DatabaseConnector.USERTYPE.ADMIN);
            ResultSet rs = databaseAdmin.getPlayer("*");

            ArrayList<Integer> playerIDList = new ArrayList<Integer>();
            while (rs.next()){
                playerIDList.add(rs.getInt("playerID"));
            }

            playerIDComboBox = new JComboBox(playerIDList.toArray());
            playerIDComboBox.setRenderer(new MyComboBoxRenderer("playerID"));
            playerIDComboBox.setSelectedIndex(-1);
            centerUpdate.add(playerIDComboBox);

            //button to go back to original screen
            JButton back = new JButton("Back");
            back.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    mainScreen();
                }
            });

            JButton pickPlayer = new JButton("Pick Player");
            pickPlayer.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updatePlayerClick();
                }
            });

            centerUpdate.add(back);
            centerUpdate.add(pickPlayer);
            frame.add(centerUpdate);
            frame.setVisible(true);

        }
        catch(Exception e1){
            System.out.println(e1.toString());
            System.out.println("2342");
        }
        finally{
        }
    }

    public void updatePlayerClick(){
        frame.setVisible(false);
        frame.remove(centerUpdate);
        centerUpdate = new JPanel(new GridLayout(3,2));

        int playerID = playerIDComboBox.getSelectedIndex() + 1;
        try {

            databaseAdmin = new DatabaseConnector(DatabaseConnector.USERTYPE.ADMIN);
            ResultSet rs = databaseAdmin.getPlayer(Integer.toString(playerID));


            rs.next();
            String playerIDstr = rs.getString(1);
            String name = rs.getString(2);
            String position = rs.getString(3);
            String teamID = rs.getString(4);

            rs.close();

            playerIDtextBox = new JTextField(playerIDstr);
            playerIDtextBox.setEditable(false);

            nameInput = new JTextField(name);
            positionInput = new JTextField(position);

            ResultSet rs2 = databaseAdmin.getTeam("*");

            ArrayList<Integer> idList = new ArrayList<Integer>();
            while (rs2.next()){
                idList.add(rs2.getInt("teamID"));
            }

            teamIDComboBox = new JComboBox(idList.toArray());
            //teamIDComboBox.setRenderer(new MyComboBoxRenderer("teamID"));
            teamIDComboBox.setSelectedIndex(Integer.parseInt(teamID));

            //button to go back to original screen
            JButton back = new JButton("Back");
            back.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    mainScreen();
                }
            });

            JButton updateButton = new JButton("Update");
            updateButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int playerID = Integer.parseInt(playerIDtextBox.getText());
                    String name = nameInput.getText();
                    String position = positionInput.getText();
                    int teamID = teamIDComboBox.getSelectedIndex() + 1;
                    try {
                        databaseAdmin.updatePlayer(playerID, name, position, teamID);
                    } catch (Exception e3) {
                        System.out.println(e3);
                    }
                }
            });

            centerUpdate.add(playerIDtextBox);
            centerUpdate.add(nameInput);
            centerUpdate.add(positionInput);
            centerUpdate.add(teamIDComboBox);

            centerUpdate.add(back);
            centerUpdate.add(updateButton);
            frame.add(centerUpdate);
            frame.setVisible(true);


        } catch (Exception e2) {
            System.out.println(e2.toString());
            System.out.println("2");
        } finally {
        }
    }

    public void updateGame(){
        frame.setVisible(false);
        frame.remove(centerUpdate);
        centerUpdate = new JPanel(new GridLayout(3,2));

        try{
            databaseAdmin = new DatabaseConnector(DatabaseConnector.USERTYPE.ADMIN);
            ResultSet rs = databaseAdmin.getGame("*");

            ArrayList<Integer> gameIDList = new ArrayList<Integer>();
            while (rs.next()){
                gameIDList.add(rs.getInt("gameID"));
            }

            gameIDComboBox = new JComboBox(gameIDList.toArray());
            gameIDComboBox.setRenderer(new MyComboBoxRenderer("gameID"));
            gameIDComboBox.setSelectedIndex(-1);
            centerUpdate.add(gameIDComboBox);

            //button to go back to original screen
            JButton back = new JButton("Back");
            back.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    mainScreen();
                }
            });

            JButton pickGame = new JButton("Pick Game");
            pickGame.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateGameClick();
                }
            });

            centerUpdate.add(back);
            centerUpdate.add(pickGame);
            frame.add(centerUpdate);
            frame.setVisible(true);

        }
        catch(Exception e1){
            System.out.println(e1.toString());
            System.out.println("2342");
        }
        finally{
        }
    }

    public void updateGameClick(){
        frame.setVisible(false);
        frame.remove(centerUpdate);
        centerUpdate = new JPanel(new GridLayout(3,2));

        int gameID = gameIDComboBox.getSelectedIndex() + 1;

        try {

            databaseAdmin = new DatabaseConnector(DatabaseConnector.USERTYPE.ADMIN);
            ResultSet rs = databaseAdmin.getGame(Integer.toString(gameID));

            rs.next();
            String gameIDstr = rs.getString(1);
            String scoreStr = rs.getString(2);
            String startTimeStr = rs.getString(3);
            String endTimeStr = rs.getString(4);
            String tournamentIDStr = rs.getString(5);

            rs.close();
            gameIDTextBox = new JTextField(gameIDstr);
            gameIDTextBox.setEditable(false);

            score = new JTextField(scoreStr);
            startTime = new JTextField(startTimeStr);
            endTime = new JTextField(endTimeStr);


            ResultSet rs2 = databaseAdmin.getTeam("*");

            ArrayList<Integer> idList = new ArrayList<Integer>();
            while (rs2.next()){
                idList.add(rs2.getInt("teamID"));
            }

            tournamentIDBox = new JComboBox(idList.toArray());
            tournamentIDBox.setSelectedIndex(Integer.parseInt(tournamentIDStr));

            //button to go back to original screen
            JButton back = new JButton("Back");
            back.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    mainScreen();
                }
            });

            JButton updateButton = new JButton("Update");
            updateButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int gameIDStr = Integer.parseInt(gameIDTextBox.getText());
                    String scoreStr = score.getText();
                    String startTimeStr = startTime.getText();
                    String endTimeStr = endTime.getText();
                    int tournamentIDStr = tournamentIDBox.getSelectedIndex() + 1;

                    try {
                        databaseAdmin.updateGames(gameIDStr, scoreStr, startTimeStr, endTimeStr, tournamentIDStr);
                    } catch (Exception e3) {
                        System.out.println(e3);
                    }
                }
            });

            centerUpdate.add(gameIDTextBox);
            centerUpdate.add(score);
            centerUpdate.add(startTime);
            centerUpdate.add(endTime);
            centerUpdate.add(tournamentIDBox);

            centerUpdate.add(back);
            centerUpdate.add(updateButton);
            frame.add(centerUpdate);
            frame.setVisible(true);


        } catch (Exception e2) {
            System.out.println(e2.toString());
            System.out.println("2");
        } finally {
        }
    }

    public void updateTournament(){
        frame.setVisible(false);
        frame.remove(centerUpdate);
        centerUpdate = new JPanel(new GridLayout(3,2));

        try{
            databaseAdmin = new DatabaseConnector(DatabaseConnector.USERTYPE.ADMIN);
            ResultSet rs = databaseAdmin.getTournament("*");

            ArrayList<Integer> tournamentIDList = new ArrayList<Integer>();
            while (rs.next()){
                tournamentIDList.add(rs.getInt("tournamentID"));
            }

            tournamentIDBox = new JComboBox(tournamentIDList.toArray());
            tournamentIDBox.setRenderer(new MyComboBoxRenderer("tournamentID"));
            tournamentIDBox.setSelectedIndex(-1);
            centerUpdate.add(tournamentIDBox);

            //button to go back to original screen
            JButton back = new JButton("Back");
            back.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    mainScreen();
                }
            });

            JButton pickTournament = new JButton("Pick Tournament");
            pickTournament.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateTournamentClick();
                }
            });

            centerUpdate.add(back);
            centerUpdate.add(pickTournament);
            frame.add(centerUpdate);
            frame.setVisible(true);

        }
        catch(Exception e1){
            System.out.println(e1.toString());
            System.out.println("2342");
        }
        finally{
        }
    }

    public void updateTournamentClick(){
        frame.setVisible(false);
        frame.remove(centerUpdate);
        centerUpdate = new JPanel(new GridLayout(3,2));

        int tournamentIDint = tournamentIDBox.getSelectedIndex() + 1;

        try {

            databaseAdmin = new DatabaseConnector(DatabaseConnector.USERTYPE.ADMIN);
            ResultSet rs = databaseAdmin.getTournament(Integer.toString(tournamentIDint));

            rs.next();
            String tournamentIDstr = rs.getString(1);
            String nameStr = rs.getString(2);
            String locationStr = rs.getString(3);
            String dateStr = rs.getString(4);

            rs.close();

            tournamentID = new JTextField(tournamentIDstr);
            tournamentID.setEditable(false);

            nameInput = new JTextField(nameStr);
            locationInput = new JTextField(locationStr);
            dateInput = new JTextField(dateStr);

            //button to go back to original screen
            JButton back = new JButton("Back");
            back.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    mainScreen();
                }
            });

            JButton updateButton = new JButton("Update");
            updateButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int tournamentIDint = Integer.parseInt(tournamentID.getText());
                    String nameStr = nameInput.getText();
                    String locationStr = locationInput.getText();
                    String dateStr = dateInput.getText();

                    try {
                        databaseAdmin.updateTournaments(tournamentIDint, nameStr, locationStr, dateStr);
                    } catch (Exception e3) {
                        System.out.println(e3);
                    }
                }
            });

            centerUpdate.add(tournamentID);
            centerUpdate.add(nameInput);
            centerUpdate.add(locationInput);
            centerUpdate.add(dateInput);

            centerUpdate.add(back);
            centerUpdate.add(updateButton);
            frame.add(centerUpdate);
            frame.setVisible(true);


        } catch (Exception e2) {
            System.out.println(e2.toString());
            System.out.println("2");
        } finally {
        }
    }

    public void updateCoach(){
        frame.setVisible(false);
        frame.remove(centerUpdate);
        centerUpdate = new JPanel(new GridLayout(3,2));

        try{
            databaseAdmin = new DatabaseConnector(DatabaseConnector.USERTYPE.ADMIN);
            ResultSet rs = databaseAdmin.getCoach("*");

            ArrayList<Integer> coachIDList = new ArrayList<Integer>();
            while (rs.next()){
                coachIDList.add(rs.getInt("coachID"));
            }

            coachIDComboBox = new JComboBox(coachIDList.toArray());
            coachIDComboBox.setRenderer(new MyComboBoxRenderer("coachID"));
            coachIDComboBox.setSelectedIndex(-1);
            centerUpdate.add(coachIDComboBox);

            //button to go back to original screen
            JButton back = new JButton("Back");
            back.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    mainScreen();
                }
            });

            JButton pickCoach = new JButton("Pick Coach");
            pickCoach.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateCoachClick();
                }
            });

            centerUpdate.add(back);
            centerUpdate.add(pickCoach);
            frame.add(centerUpdate);
            frame.setVisible(true);

        }
        catch(Exception e1){
            System.out.println(e1.toString());
            System.out.println("2342");
        }
        finally{
        }
    }

    public void updateCoachClick(){
        frame.setVisible(false);
        frame.remove(centerUpdate);
        centerUpdate = new JPanel(new GridLayout(3,2));

        int coachID = coachIDComboBox.getSelectedIndex() + 1;

        try {

            databaseAdmin = new DatabaseConnector(DatabaseConnector.USERTYPE.ADMIN);
            ResultSet rs = databaseAdmin.getCoach(Integer.toString(coachID));

            rs.next();
            String coachIDstr = rs.getString(1);
            String nameStr = rs.getString(2);

            rs.close();
            coachIDTextBox = new JTextField(coachIDstr);
            coachIDTextBox.setEditable(false);

            nameInput = new JTextField(nameStr);

            //button to go back to original screen
            JButton back = new JButton("Back");
            back.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    mainScreen();
                }
            });

            JButton updateButton = new JButton("Update");
            updateButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int coachIDStr = Integer.parseInt(coachIDTextBox.getText());
                    String nameStr = nameInput.getText();

                    try {
                        databaseAdmin.updateCoach(coachIDStr, nameStr);
                    } catch (Exception e3) {
                        System.out.println(e3);
                    }
                }
            });

            centerUpdate.add(coachIDTextBox);
            centerUpdate.add(nameInput);

            centerUpdate.add(back);
            centerUpdate.add(updateButton);
            frame.add(centerUpdate);
            frame.setVisible(true);


        } catch (Exception e2) {
            System.out.println(e2.toString());
            System.out.println("2");
        } finally {
        }
    }

    public void updateTeam(){
        frame.setVisible(false);
        frame.remove(centerUpdate);
        centerUpdate = new JPanel(new GridLayout(3,2));

        try{
            databaseAdmin = new DatabaseConnector(DatabaseConnector.USERTYPE.ADMIN);
            ResultSet rs = databaseAdmin.getTeam("*");

            ArrayList<Integer> teamIDList = new ArrayList<Integer>();
            while (rs.next()){
                teamIDList.add(rs.getInt("teamID"));
            }

            teamIDComboBox = new JComboBox(teamIDList.toArray());
            teamIDComboBox.setRenderer(new MyComboBoxRenderer("teamID"));
            teamIDComboBox.setSelectedIndex(-1);
            centerUpdate.add(teamIDComboBox);

            //button to go back to original screen
            JButton back = new JButton("Back");
            back.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    mainScreen();
                }
            });

            JButton pickTeam = new JButton("Pick Team");
            pickTeam.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateTeamClick();
                }
            });

            centerUpdate.add(back);
            centerUpdate.add(pickTeam);
            frame.add(centerUpdate);
            frame.setVisible(true);

        }
        catch(Exception e1){
            System.out.println(e1.toString());
            System.out.println("2342");
        }
        finally{
        }
    }

    public void updateTeamClick(){
        frame.setVisible(false);
        frame.remove(centerUpdate);
        centerUpdate = new JPanel(new GridLayout(3,2));

        int teamID = teamIDComboBox.getSelectedIndex() + 1;
        try {

            databaseAdmin = new DatabaseConnector(DatabaseConnector.USERTYPE.ADMIN);
            ResultSet rs = databaseAdmin.getTeam(Integer.toString(teamID));


            rs.next();
            String teamIDstr = rs.getString(1);
            String name = rs.getString(2);
            String location = rs.getString(3);
            String wins = rs.getString(4);
            String losses = rs.getString(5);
            String coachID = rs.getString(6);

            rs.close();

            teamIDTextBox = new JTextField(teamIDstr);
            teamIDTextBox.setEditable(false);

            nameInput = new JTextField(name);
            locationInput = new JTextField(location);
            winsInput = new JTextField(wins);
            lossesInput = new JTextField(losses);

            ResultSet rs2 = databaseAdmin.getCoach("*");

            ArrayList<Integer> idList = new ArrayList<Integer>();
            while (rs2.next()){
                idList.add(rs2.getInt("coachID"));
            }

            coachIDComboBox = new JComboBox(idList.toArray());
            coachIDComboBox.setSelectedIndex(Integer.parseInt(coachID));

            //button to go back to original screen
            JButton back = new JButton("Back");
            back.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    mainScreen();
                }
            });

            JButton updateButton = new JButton("Update");
            updateButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int teamID = Integer.parseInt(teamIDTextBox.getText());
                    String name = nameInput.getText();
                    String location = locationInput.getText();
                    int wins = Integer.parseInt(winsInput.getText());
                    int losses = Integer.parseInt(lossesInput.getText());
                    int coachID = coachIDComboBox.getSelectedIndex() + 1;
                    try {
                        databaseAdmin.updateTeam(teamID, name, location, wins, losses, coachID);
                    } catch (Exception e3) {
                        System.out.println(e3);
                    }
                }
            });

            centerUpdate.add(teamIDTextBox);
            centerUpdate.add(nameInput);
            centerUpdate.add(locationInput);
            centerUpdate.add(winsInput);
            centerUpdate.add(lossesInput);
            centerUpdate.add(coachIDComboBox);

            centerUpdate.add(back);
            centerUpdate.add(updateButton);
            frame.add(centerUpdate);
            frame.setVisible(true);


        } catch (Exception e2) {
            System.out.println(e2.toString());
            System.out.println("2");
        } finally {
        }
    }

    public void updateParticipatesIn(){
        frame.setVisible(false);
        frame.remove(centerUpdate);
        centerUpdate = new JPanel(new GridLayout(3,2));

        try {
            databaseAdmin = new DatabaseConnector(DatabaseConnector.USERTYPE.ADMIN);
            ResultSet rs = databaseAdmin.getPlayer("*");

            ArrayList<Integer> playerIDList = new ArrayList<Integer>();
            while (rs.next()) {
                playerIDList.add(rs.getInt("playerID"));
            }

            playerIDComboBox = new JComboBox(playerIDList.toArray());
            playerIDComboBox.setRenderer(new MyComboBoxRenderer("playerID"));
            playerIDComboBox.setSelectedIndex(-1);
            centerUpdate.add(playerIDComboBox);

            //button to go back to original screen
            JButton back = new JButton("Back");
            back.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    mainScreen();
                }
            });

            JButton pickPlayer = new JButton("Pick Player");
            pickPlayer.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateParticipatesInFirstClick();
                }
            });

            centerUpdate.add(back);
            centerUpdate.add(pickPlayer);
            frame.add(centerUpdate);
            frame.setVisible(true);
        } catch (Exception e2) {
            System.out.println(e2.toString());
            System.out.println("2");
        } finally {
        }

    }

    public void updateParticipatesInFirstClick(){
        frame.setVisible(false);
        frame.remove(centerUpdate);
        centerUpdate = new JPanel(new GridLayout(3,2));

        int playerID = playerIDComboBox.getSelectedIndex() + 1;
        playerIDtextBox = new JTextField(Integer.toString(playerID));
        playerIDtextBox.setEditable(false);
        centerUpdate.add(playerIDtextBox);

        try{
            databaseAdmin = new DatabaseConnector(DatabaseConnector.USERTYPE.ADMIN);

            //need an sql statement to get all the game IDs of the games that this player plays in
            //
            //
            //
            //
            //
            ResultSet rs = databaseAdmin.getGame("*");

            ArrayList<Integer> gameIDList = new ArrayList<Integer>();
            while (rs.next()){
                gameIDList.add(rs.getInt("gameID"));
            }

            gameIDComboBox = new JComboBox(gameIDList.toArray());
            gameIDComboBox.setRenderer(new MyComboBoxRenderer("gameID"));
            gameIDComboBox.setSelectedIndex(-1);
            centerUpdate.add(gameIDComboBox);

            //button to go back to original screen
            JButton back = new JButton("Back");
            back.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateParticipatesIn();
                }
            });

            JButton pickTeam = new JButton("Pick Team");
            pickTeam.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateParticipatesInSecondClick();
                }
            });

            centerUpdate.add(back);
            centerUpdate.add(pickTeam);
            frame.add(centerUpdate);
            frame.setVisible(true);

        }
        catch(Exception e1){
            System.out.println(e1.toString());
            System.out.println("2342");
        }
        finally{
        }
    }

    public void updateParticipatesInSecondClick(){
        frame.setVisible(false);
        frame.remove(centerUpdate);
        centerUpdate = new JPanel(new GridLayout(3,2));

        int playerID = Integer.parseInt(playerIDtextBox.getText());
        int gameID = gameIDComboBox.getSelectedIndex() + 1;
        try {

            databaseAdmin = new DatabaseConnector(DatabaseConnector.USERTYPE.ADMIN);
            ResultSet rs = databaseAdmin.participatesIn(Integer.toString(playerID), Integer.toString(gameID));


            rs.next();
            String playerIDint = rs.getString(1);
            String gameIDint = rs.getString(2);
            String drops = rs.getString(3);
            String assists = rs.getString(4);
            String goals = rs.getString(5);
            String pointPlayed = rs.getString(6);
            String throwaways = rs.getString(7);


            rs.close();

            playerIDtextBox = new JTextField(playerIDint);
            playerIDtextBox.setEditable(false);

            gameIDTextBox = new JTextField(gameIDint);
            gameIDTextBox.setEditable(false);

            dropsTextBox = new JTextField("Drops: " + drops);
            assistsTextBox = new JTextField("Assists: " + assists);
            goalsTextBox = new JTextField("Goals: " + goals);
            pointsPlayedTextBox = new JTextField("PointsPlayed: " + pointPlayed);
            throwawaysTextBox = new JTextField("Throwaways: " + throwaways);


            //button to go back to original screen
            JButton back = new JButton("Back");
            back.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    mainScreen();
                }
            });

            JButton updateButton = new JButton("Update");
            updateButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int playerID = Integer.parseInt(playerIDtextBox.getText());
                    int gameID = Integer.parseInt(gameIDTextBox.getText());
                    int drops = Integer.parseInt(dropsTextBox.getText());
                    int assists = Integer.parseInt(assistsTextBox.getText());
                    int goals = Integer.parseInt(goalsTextBox.getText());
                    int pointsPlayed = Integer.parseInt(pointsPlayedTextBox.getText());
                    int throwaways = Integer.parseInt(throwawaysTextBox.getText());
                    try {
                        databaseAdmin.updatePart(playerID, gameID, drops, assists, goals, pointsPlayed, throwaways);
                    } catch (Exception e3) {
                        System.out.println(e3);
                    }
                }
            });

            centerUpdate.add(playerIDtextBox);
            centerUpdate.add(gameIDTextBox);
            centerUpdate.add(dropsTextBox);
            centerUpdate.add(assistsTextBox);
            centerUpdate.add(goalsTextBox);
            centerUpdate.add(pointsPlayedTextBox);
            centerUpdate.add(throwawaysTextBox);

            centerUpdate.add(back);
            centerUpdate.add(updateButton);
            frame.add(centerUpdate);
            frame.setVisible(true);


        } catch (Exception e2) {
            System.out.println(e2.toString());
            System.out.println("2");
        } finally {
        }
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
            database = new DatabaseConnector(DatabaseConnector.USERTYPE.GUEST);
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
            database = new DatabaseConnector(DatabaseConnector.USERTYPE.GUEST);
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
            database = new DatabaseConnector(DatabaseConnector.USERTYPE.GUEST);
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
            database = new DatabaseConnector(DatabaseConnector.USERTYPE.GUEST);
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
            database = new DatabaseConnector(DatabaseConnector.USERTYPE.GUEST);
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
            database = new DatabaseConnector(DatabaseConnector.USERTYPE.GUEST);
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
            database = new DatabaseConnector(DatabaseConnector.USERTYPE.GUEST);
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
            database = new DatabaseConnector(DatabaseConnector.USERTYPE.GUEST);
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

    class MyComboBoxRenderer extends JLabel implements ListCellRenderer
    {
        private String _title;

        public MyComboBoxRenderer(String title)
        {
            _title = title;
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                                                      int index, boolean isSelected, boolean hasFocus)
        {
            if (index == -1 && value == null) setText(_title);
            else setText(value.toString());
            return this;
        }
    }

    public static void main(String[] args){
        QueryGUI gui = new QueryGUI();
    }
}

