// "C:\\Users\\sumay\\OneDrive\\Pictures\\PP.png";

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class GuiClient extends JFrame {//implements Runnable

    private final long POLLWAIT = 100;
    private final long STARTWAIT = 100;

    private final BlockingQueue<GameEvent> serverQueue;
    //the queue the client puts messages on
    private final BlockingQueue<GameEvent> clientQueue;
    // Define the size of the game board
    private byte columns;// = 7;
    private byte rows;// = 6;

    private boolean ready;
    private boolean started;

    // Countdown
    private short timerLength;// = 10;
    //private int countdown = 10;

    // Swing components
    private JTextField player1Field; // Input field for player 1's name
    private JTextField player2Field; // Input field for player 2's name
    private JLabel timerLabel; // Label to display the timer
    //private Timer timer; // Timer for the game
    private JLabel turnLabel;
    private int time; // Time elapsed (do we need this?)
    private JPanel centerPanel; // Panel to hold the game board
    // Create a 2D array of CircleButton objects to represent the game board
    private CircleButton[][] buttons;
    private PieceColor[][] slots;
    private PlayerNumber turn;
    //private boolean isPlayer1Turn = true; // for the turn switching method
    //private Color player1Color;//= Color.GREEN;
    //private Color player2Color;//= Color.BLUE;
    private String player1Name;
    private String player2Name;
    private PieceColor player1Color;//= Color.GREEN;
    private PieceColor player2Color;//= Color.BLUE;
    private boolean waitBetweenTurns;

    // Create a new instance of the Connect4 class
    //private Connect4 connect4;

    // first move
    private boolean firstMove = true;
    // for popup
    private boolean popupShown = false;

    private Thread receiver;

    public GuiClient(BlockingQueue serverQueue, BlockingQueue clientQueue) {

        //Initialize these for now since we haven't yet added input fields:
        //Classic connect four is 7 columns wide, 6 columns high

        //colSize / rowSize is confusing, because a column-size of N means there are N rows.
        //changing to columns and rows. I assume this is why the earlier version of the gui
        //had the two flipped compared to the classic game.
        columns = 7;
        rows = 6;
        timerLength = 20;
        waitBetweenTurns = true;
        player1Color = PieceColor.YELLOW;
        player2Color = PieceColor.RED;

        ready = false;
        started = false;
        //Set The Queues
        this.serverQueue = serverQueue;
        this.clientQueue = clientQueue;



        // Initialize the main window
        loadStartScreen();
    }
    class MessageReceiver extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    //System.out.println("outer loop");
                    //if (ready) {
                        //System.out.println("in ready loop");
                        GameEvent ge = serverQueue.poll(POLLWAIT, TimeUnit.MILLISECONDS);
                        if (ge != null) switch (ge.type()) {
                            case START:
                                break;
                            case TIME:
                                System.out.println("GOT TIME");
                                time = ge.getCurrentTime();
                                updateHeaders();
                                break;
                            case TURN:
                                System.out.println("GOT TURN");
                                turn = ge.getPlayerNumber();
                                updateHeaders();
                                turnGoDialog((time >= 0) ? "Ready to go?" : "Time's up! Switching turns.");
                                break;
                            case UPDATE_SLOTS:
                                System.out.println("UPDATE SLOTS");
                                slots = ge.getSlots();
                                updateBoardColors();
                                break;
                            case GAME_OVER:
                                System.out.println("Received GAME_OVER.");
                                displayGameOver(ge.getGameOutcome());
//                                switch (ge.getGameOutcome()) {
//                                    case P1_WIN:
//                                        break;
//                                    case P2_WIN:
//                                        break;
//                                    case DRAW:
//                                        break;
//                                }
                                // TreeSet<ComparableTreeSet<Coord>> p1Set = ge.getPlayer1().getSets();
                                // TreeSet<ComparableTreeSet<Coord>> p2Set = ge.getPlayer2().getSets();
                                // System.out.println("Player 1 set count: " + p1Set.size());
                                // System.out.println("Player 1 sets: " + Util.coordSetSetToString(p1Set));
                                // System.out.println("Player 2 set count: " + p2Set.size());
                                // System.out.println("Player 2 sets: " + Util.coordSetSetToString(p2Set));
                                break;

                        }
                    }
                    //just to keep the thread from 100%ing
                    //wait(10);
               // }
            } catch (InterruptedException ex) {

            }

        }
    }

    private void loadStartScreen() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close the application when the window is closed
        this.setSize(800, 600); // Set the size of the window
        this.setLayout(new BorderLayout()); // Use BorderLayout for the main window

        // Specify the absolute file path to your image
        String filePath = "PP.png";

        // Load the background image using the absolute file path
        ImageIcon imageIcon = new ImageIcon(getClass().getClassLoader().getResource(filePath));
        Image image = imageIcon.getImage();

        // Initialize the center panel with a custom paintComponent method to draw the
        // background image
        centerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this); // Draw the background image
            }
        };
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS)); // Use BoxLayout for the center panel

        // Initialize the welcome label

        // Initialize the begin button
        JButton beginButton = new JButton("Begin");
        beginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Get the player names when the button is clicked
                getPlayerNames();
            }

        });

        // Set button properties
        beginButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the button
        beginButton.setBackground(Color.WHITE); // Set the background color
        beginButton.setForeground(Color.PINK); // Set the text color
        beginButton.setFont(new Font("Arial", Font.BOLD, 23)); // Set font
        beginButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15)); // Set padding
        beginButton.setPreferredSize(new Dimension(100, 50)); // Set preferred size
        beginButton.setMaximumSize(new Dimension(100, 50)); // Set maximum size

        // Add the welcome label and begin button to the center panel
        centerPanel.add(Box.createVerticalGlue());

        centerPanel.add(beginButton);
        centerPanel.add(Box.createVerticalGlue());

        // Add the center panel to the main window
        this.add(centerPanel, BorderLayout.CENTER);

        // Make the window visible
        this.setVisible(true);
    }

    private void getPlayerNames() {
        // Remove all components from the center pane
        centerPanel.removeAll();
        // Set the layout of the center panel to BoxLayout with vertical alignment
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        // Create a panel for player 1's name input
        JPanel player1Panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        player1Panel.setOpaque(false); // Make the panel transparent
        JLabel player1Label = new JLabel("Player 1:"); // Label for player 1's name
        player1Label.setForeground(Color.WHITE); // Set the label's color to BLACK

        player1Field = new JTextField(); // Text field for player 1 to enter their name
        player1Field.setPreferredSize(new Dimension(215, 30)); // Set the preferred size of the text field

        player1Panel.add(player1Label); // Add the label to the panel
        player1Panel.add(player1Field); // Add the text field to the panel

        // Create a panel for player 2's name input, similar to the above
        JPanel player2Panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        player2Panel.setOpaque(false);

        JLabel player2Label = new JLabel("Player 2:");
        player2Label.setForeground(Color.WHITE);

        player2Field = new JTextField();
        player2Field.setPreferredSize(new Dimension(215, 30));

        player2Panel.add(player2Label);
        player2Panel.add(player2Field);

        // Create a button to start the game
        JButton okButton = new JButton("Start Game");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (player1Field.getText().isEmpty() || player2Field.getText().isEmpty()) {
                    // Display error message if any of the player name fields is empty
                    JOptionPane.showMessageDialog(null, "Both player names are required!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                } else if (player1Field.getText().equals(player2Field.getText())) {
                    // Display error message if player names are the same
                    JOptionPane.showMessageDialog(null, "Player names cannot be the same!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    player1Name = player1Field.getText();
                    player2Name = player2Field.getText();
                    //System.out.println("Ready set to true");
                    //this should be elsewhere but it's easiest for now.

                    //connect4 = new Connect4(player1Field.getText(), player2Field.getText());
                    displayBoard(); // When the button is clicked and both fields are filled, start the game
                    // Start playing background music
//                    try {
//                        File musicFile = new File("C:\\Users\\sumay\\OneDrive\\Pictures\\background.wav");
//                        if (musicFile.exists()) {
//                            AudioInputStream audioIn = AudioSystem.getAudioInputStream(musicFile);
//                            Clip clip = AudioSystem.getClip();
//                            clip.open(audioIn);
//                            clip.loop(Clip.LOOP_CONTINUOUSLY);
//                        } else {
//                            System.out.println("The specified audio file was not found: " + musicFile.getAbsolutePath());
//                        }
//                    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
//                      ex.printStackTrace();
//                    }
                }
            }
        });
        okButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the button horizontally
        okButton.setPreferredSize(new Dimension(100, 50)); // Set the preferred size of the button

        // Add the panels and the button to the center panel, with vertical glue between
        // them for spacing
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(player1Panel);
        centerPanel.add(player2Panel);
        centerPanel.add(okButton);
        centerPanel.add(Box.createVerticalGlue());

        // Validate and repaint the frame to reflect the changes
        this.validate();
        this.repaint();
    }

    private void displayBoard() {
        // Remove all components from the frame
        this.getContentPane().removeAll();

        // Set the layout of the frame to BorderLayout
        this.setLayout(new BorderLayout());

        // Set the background color of the frame to Blue
        this.setBackground(Color.BLUE);

        // Call the displayHeaders() method to display the headers
        displayHeaders();

        // Call the displayBoardPanel() method to display the game board
        displayBoardPanel();

        // Validate and repaint the frame to reflect the changes
        this.validate();
        this.repaint();
        startAndSendCreateGame();
    }

    //this needs to be replaced with a proper menu bar. - Lucas
    private void handleSessionOption(String option) {
        switch (option) {
            case "Host":
                // Handle host option
                break;
            case "Join":
                // Handle join option
                break;
            case "Exit":
                // Handle exit option
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?", "Exit",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }

                break;
        }
    }

    private void displayHeaders() {

        // Create a label for the title and set its font
        JLabel headerJLabel = new JLabel("Timed Connect 4", SwingConstants.CENTER);
        headerJLabel.setFont(new Font("Serif", Font.BOLD, 30));

        // Create a panel for the headers and add the title label to it
        JPanel headerPanel = new JPanel(new GridLayout(4, 1));
        headerPanel.add(headerJLabel);
        // Set the background color of the header panel to yellow
        ;

        // Create a dropdown for the session options
        String[] sessionOptions = { "Session", "Host", "Join", "Exit" };
//        JComboBox<String> sessionDropdown = new JComboBox<>(sessionOptions);
//        sessionDropdown.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                // When an option is selected, call the handleSessionOption() method to handle
//                // it
//                String selectedOption = (String) sessionDropdown.getSelectedItem();
//                if (!"Session".equals(selectedOption)) {
//                    handleSessionOption(selectedOption);
//                    // After handling the option, set the selected item back to "Session"
//                    sessionDropdown.setSelectedItem("Session");
//                }
//            }
//        });
        // Add the dropdown to the header panel
        //headerPanel.add(sessionDropdown);

        // Create a label for the timer

        timerLabel = new JLabel("Time:" + String.format("%-6d", time), SwingConstants.LEFT);

        // Create a label for the turn
        turnLabel = new JLabel("Turn:" + (turn == PlayerNumber.PLAYER_1 ? player1Name: player2Name),
                SwingConstants.LEFT);

        // Create a panel for the game data, which includes the players' names and the
        // timer
        JPanel gameDataPanel = new JPanel(new GridLayout(1, 2));
        gameDataPanel.add(new JLabel(
                String.format("%s (%s) vs %s (%s)",
                        player1Name, pieceToColorName(player1Color),
                        player2Name, pieceToColorName(player2Color)),
                    SwingConstants.LEFT));
        gameDataPanel.add(turnLabel);
        gameDataPanel.add(timerLabel);
        // Add the game data panel to the header panel
        headerPanel.add(gameDataPanel);

        // Add the header panel to the frame
        this.add(headerPanel, BorderLayout.NORTH);
    }
    private void updateHeaders() {
        timerLabel.setText("Time:" + String.format("%-6d", time));
        turnLabel.setText("Turn:" + (turn == PlayerNumber.PLAYER_1 ? player1Name: player2Name));
    }

    private void displayGameOver(GameOutcome go) {
        String message;
        switch (go) {
            case DRAW:
                message = "It was a draw!";
                break;
            case P1_WIN:
                message = player1Name + " wins!";
                break;
            case P2_WIN:
                message = player2Name + " wins!";
                break;
            default://should never get here
                return;
        }
        JOptionPane optionPane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE);
        JButton restartButton = new JButton("Restart");
        JButton exitButton = new JButton("Exit");
        restartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Code to restart the game
                //connect4 = new Connect4(player1Field.getText(), player2Field.getText());
                started = false; // Restart the game
                startAndSendCreateGame();
                ((JDialog) ((JButton) e.getSource()).getTopLevelAncestor()).dispose(); // Dispose the dialog
            }
        });
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
//                frame.dispose(); // Dispose the frame
                System.exit(0);
            }
        });
        optionPane.setOptions(new Object[] { restartButton, exitButton });
        JDialog dialog = optionPane.createDialog(this, "Game Over");
        dialog.setVisible(true);
    }

    private void startAndSendCreateGame() {
        if (receiver == null) {
            receiver = new MessageReceiver();
            receiver.start();
        }
        if (!started) {
            System.out.println("Sending CREATE_GAME");
            GameEvent starter = new GameEvent(EventType.CREATE_GAME, columns, rows, GameType.FIRST_TO_SET,
                    timerLength, waitBetweenTurns, player1Name, player1Color, player2Name, player2Color);
            clientQueue.offer(starter);
            started = true;
        }
    }

    private void turnGoDialog(String message) {
        JOptionPane optionPane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE);
        JButton goButton = new JButton("Go!");
        optionPane.setOptions(new Object[] { goButton });
        goButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clientQueue.offer(new GameEvent(EventType.TURN_READY));
                ((JDialog) ((JButton) e.getSource()).getTopLevelAncestor()).dispose();
            }
        });
        JDialog dialog = optionPane.createDialog(this, "Time to switch");
        dialog.setVisible(true);
    }
 //       String message = winner == 0 ? "It was a draw!"
//                            : winner == 1 ? player1Field.getText() + " wins!" : player2Field.getText() + " wins!";
//                    JOptionPane optionPane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE);
//                    JButton restartButton = new JButton("Restart");
//                    JButton exitButton = new JButton("Exit");
//                    restartButton.addActionListener(new ActionListener() {
//                        public void actionPerformed(ActionEvent e) {
//                            // Code to restart the game
//                            connect4 = new Connect4(player1Field.getText(), player2Field.getText());
//                            firstMove = true;
//                            isPlayer1Turn = true;
//                            countdown = TIMERLIMIT;
//                            displayBoard(); // Restart the game
//                            ((JDialog) ((JButton) e.getSource()).getTopLevelAncestor()).dispose(); // Dispose the dialog
//                        }
//                    });
    //}
//    public void handleTurn(int col) {
//        boolean result = connect4.placeToken(col);
//        if (result) {
//            // Find the lowest empty cell in the column
//            int row = findLowestEmptyRow(col);
//            if (row != -1) {
//                Color color = connect4.isPlayer1Turn() ? player1Color : player2Color;
//                buttons[row][col].setBackground(color);
//                switchTurns();
//
//                int winner = connect4.isGameOver();
//                if (winner != -1) {
//                    String message = winner == 0 ? "It was a draw!"
//                            : winner == 1 ? player1Field.getText() + " wins!" : player2Field.getText() + " wins!";
//                    JOptionPane optionPane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE);
//                    JButton restartButton = new JButton("Restart");
//                    JButton exitButton = new JButton("Exit");
//                    restartButton.addActionListener(new ActionListener() {
//                        public void actionPerformed(ActionEvent e) {
//                            // Code to restart the game
//                            connect4 = new Connect4(player1Field.getText(), player2Field.getText());
//                            firstMove = true;
//                            isPlayer1Turn = true;
//                            countdown = TIMERLIMIT;
//                            displayBoard(); // Restart the game
//                            ((JDialog) ((JButton) e.getSource()).getTopLevelAncestor()).dispose(); // Dispose the dialog
//                        }
//                    });
//                    exitButton.addActionListener(new ActionListener() {
//                        public void actionPerformed(ActionEvent e) {
//                            frame.dispose(); // Dispose the frame
//                            System.exit(0);
//                        }
//                    });
//                    optionPane.setOptions(new Object[] { restartButton, exitButton });
//                    JDialog dialog = optionPane.createDialog(frame, "Game Over");
//                    stopTimer();
//                    dialog.setVisible(true);
//                }
//            }
//        } else {
//            JOptionPane.showMessageDialog(frame, "Column is full!", "Error", JOptionPane.ERROR_MESSAGE);
//        }
//
//        if (firstMove) {
//            firstMove = false;
//            startTimer();
//        }
//    }

//    private int findLowestEmptyRow(int col) {
//        for (int row = rowSize - 1; row >= 0; row--) {
//            if (buttons[row][col].getBackground() == Color.WHITE) {
//                return row;
//            }
//        }
//        return -1;
//    }

    private void displayBoardPanel() {

        // Create a JPanel with a GridBagLayout to hold the buttons
        JPanel boardPanel = new JPanel(new GridBagLayout());

        // Create a GridBagConstraints object to specify constraints for the layout
        GridBagConstraints gbc = new GridBagConstraints();
        buttons = new CircleButton[rows][columns];

        // Loop through each row and column of the game board
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                // Create a new CircleButton and add it to the buttons array
                buttons[y][x] = new CircleButton();

                // Set the maximum and preferred size of the button
                buttons[y][x].setMaximumSize(new Dimension(50, 50));
                buttons[y][x].setPreferredSize(new Dimension(50, 50));
                buttons[y][x].setBackground(Color.WHITE);

                // Add an ActionListener to the button
                final int row = y;
                final int col = x;
                buttons[y][x].addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (slots[row][col] == PieceColor.NONE) {
                            clientQueue.offer(new GameEvent(EventType.PLACE_PIECE, turn, (byte) col));
                        } else {
                            JOptionPane.showMessageDialog(null, "Column is full!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                        // When the button is clicked, set its background color to GREEN
                        //handleTurn(col);
                    }
                });
                buttons[y][x].setMargin(new Insets(0, 0, 0, 0));
                buttons[y][x].setText(String.format(" <%d,%d> ", x, y));

                // Set the gridx and gridy constraints to the current column and row
                gbc.gridx = x;
                gbc.gridy = y;

                // Set the insets constraint to specify the padding around the button
                gbc.insets = new Insets(3, 0, 0, 2);

                // Add the button to the boardPanel with the specified constraints
                boardPanel.add(buttons[y][x], gbc);
            }
        }

        // Create a wrapper JPanel to hold the boardPanel
        JPanel wrapperPanel = new JPanel();
        wrapperPanel.setPreferredSize(new Dimension(800, 800));
        wrapperPanel.setBackground(Color.BLUE);
        wrapperPanel.setLayout(new BorderLayout());
        wrapperPanel.add(boardPanel, BorderLayout.CENTER);

        // Add the wrapperPanel to the frame in the center
        this.add(wrapperPanel, BorderLayout.CENTER);
    }

    private void updateBoardColors() {
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                buttons[y][x].setBackground( pieceToColor(slots[y][x]) );
            }
        }
    }

    private Color pieceToColor(PieceColor c) {
        switch (c) {
            case YELLOW:
                return Color.YELLOW;
            case RED:
                return Color.RED;
            case NONE:
            default:
                return Color.WHITE;
        }
    }
    private String pieceToColorName(PieceColor c) {
        switch (c) {
            case YELLOW:
                return "Yellow";
            case RED:
                return "Red";
            case NONE:
            default:
                return "None";
        }
    }

//    private void startTimer() {
//        // Set the initial time when the timer starts
//        final long startTime = System.currentTimeMillis();
//
//        TimerTask switchTurnsTask = new TimerTask() {
//            @Override
//            public void run() {
//                // calculate elapsed time
//                long elapsedTime = System.currentTimeMillis() - startTime;
//                long elapsedSeconds = elapsedTime / 1000;
//
//                SwingUtilities.invokeLater(new Runnable() {
//                    public void run() {
//                        if (popupShown) {
//                            // popupShown = false;
//                            return;
//                        }
//                        if (countdown == 0) {
//                            // Display Popup
//                            popupShown = true;
//                            JOptionPane.showMessageDialog(frame, "Time's up! Switching turns.", "Time's Up",
//                                    JOptionPane.INFORMATION_MESSAGE);
//                            popupShown = false;
//                            switchTurns();
//                        }
//                        timerLabel.setText("Time: " + countdown);
//                        countdown--;
//                    }
//                });
//
//                // should replace 30 with a variable if we want to have the
//                // players choose how long turns should last
//                // if (elapsedSeconds >= 30) {
//                // // reset the timer
//                // timer.cancel();
//                // startTimer();
//                // // Switch turns
//                // switchTurns();
//                // }
//            }
//        };
//
//        // Create a new Timer
//        timer = new Timer();
//
//        // Schedule the timer task to run every second
//        timer.scheduleAtFixedRate(switchTurnsTask, 0, 1000);
//    }

//    private void switchTurns() {
//        System.out.println("SWITCH TURN!");
//        // switch turns to the other player
//        isPlayer1Turn = !isPlayer1Turn;
//        connect4.switchPlayer();
//        countdown = TIMERLIMIT;
//
//        // refresh display
//        displayHeaders();
//    }

//    private void stopTimer() {
//        time = 0;
//        if (timer != null) {
//            timer.cancel();
//        }
//    }

//    public static void main(String[] args) {
//
//        SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//                // Inside the run method, a new instance of Connect4Game is created.
//                // This presumably sets up and starts the game.
//                new Connect4Game();
//
//            }
//        });
//    }
}
