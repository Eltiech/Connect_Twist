// "C:\\Users\\sumay\\OneDrive\\Pictures\\PP.png";

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class GuiClient extends JFrame {//implements Runnable

    private final long POLLWAIT = 100;
    //the queue the server puts messages on
    private final BlockingQueue<GameEvent> serverQueue;
    //the queue the client puts messages on
    private final BlockingQueue<GameEvent> clientQueue;
    // Define the size of the game board
    private byte columns;// = 7;
    private byte rows;// = 6;

    private boolean ready;
    private boolean started;
    private boolean dispatcherAdded = false;

    // Countdown
    private short timerLength;// = 10;

    // Swing components

    private JTextField player1Field; // Input field for player 1's name
    private JTextField player2Field; // Input field for player 2's name
    private JSpinner connectValuesSpinner;
    private JSpinner boardWidthSpinner;
    private JSpinner boardHeightSpinner;
    private JComboBox<GameType> gameModeBox;
    private JCheckBox musicCheckBox;
    private JCheckBox waitTurnsCheckBox;

    private JMenuBar menuBar;
    private JMenu sessionMenu;
    private JMenuItem restartItem, exitItem;

    private JLabel timerLabel; // Label to display the timer
    //private Timer timer; // Timer for the game
    private JLabel turnLabel;
    private short time; // Time elapsed (do we need this?)
    private JPanel centerPanel; // Panel to hold the game board
    // Create a 2D array of CircleButton objects to represent the game board
    private CircleButton[][] buttons;
    private PieceColor[][] slots;
    private PlayerNumber turn;
    private byte setLength;
    private GameType gameType;
    //private boolean isPlayer1Turn = true; // for the turn switching method
    //private Color player1Color;//= Color.GREEN;
    //private Color player2Color;//= Color.BLUE;
    private String player1Name;
    private String player2Name;
    private PieceColor player1Color;//= Color.GREEN;
    private PieceColor player2Color;//= Color.BLUE;
    private boolean waitBetweenTurns;
    private boolean playMusic;
    private boolean listenForKeys;
    

    private Thread receiver;

    public GuiClient(BlockingQueue serverQueue, BlockingQueue clientQueue) {

        //Classic connect four is 7 columns wide, 6 columns high

        //colSize / rowSize is confusing, because a column-size of N means there are N rows.
        //changing to columns and rows..
        //todo: use these initial values as the defaults for the gui controls, rather specifying directly in
        // creation of gui elements
        columns = 7;
        rows = 6;
        timerLength = 20;
        setLength = 4;
        gameType = GameType.FIRST_TO_SET;
        waitBetweenTurns = true;
        playMusic = true;
        player1Color = PieceColor.YELLOW;
        player2Color = PieceColor.RED;
        listenForKeys = true;

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
                            short tempTime = ge.getCurrentTime();
                            //it goes to -1, because displaying 0 for a moment looks nice, but the -1
                            //can become visible if waitBetweenTurns is on. We don't want that.
                            time = tempTime >= 0 ? tempTime : 0;
                            updateHeaders();
                            break;
                        case TURN:
                            System.out.println("GOT TURN");
                            turn = ge.getPlayerNumber();
                            updateHeaders();
                            if (waitBetweenTurns) {
                                turnGoDialog((time >= 0) ? "Ready to go?" : "Time's up! Switching turns.");
                            }
                            break;
                        case UPDATE_SLOTS:
                            System.out.println("UPDATE SLOTS");
                            slots = ge.getSlots();
                            updateBoardColors();
                            break;
                        case GAME_OVER:
                            System.out.println("Received GAME_OVER.");
                            displayGameOver(ge.getGameOutcome(),ge.getPlayer1(),ge.getPlayer2());
                            break;

                    }
                }
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
                getGameSettings();
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

//        int[] connectAmmount = { "Session", "Host", "Join", "Exit" };
//        JComboBox<String> sessionDropdown = new JComboBox<>(sessionOptions);


        // Make the window visible
        this.setVisible(true);
    }

    private void getGameSettings() {
        // Remove all components from the center pane
        centerPanel.removeAll();
        // Set the layout of the center panel to BoxLayout with vertical alignment
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(new EmptyBorder(100,0,0,0));
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

        JPanel settingsContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        settingsContainer.setOpaque(false);
        JPanel settingsPanel = new JPanel(new GridLayout(3, 4));

        settingsPanel.setOpaque(false);
        JLabel connectValuesLabel = new JLabel("Connect Length:");
        connectValuesSpinner = new JSpinner(new SpinnerNumberModel(4,3,8,1));
        JLabel gameModeLabel = new JLabel("Game Mode:");
        GameType[] gameModeOptions = { GameType.FIRST_TO_SET, GameType.SET_TOTAL };
        JLabel boardWidthLabel = new JLabel("Board Width:");
        boardWidthSpinner = new JSpinner(new SpinnerNumberModel(7,3,9,1));
        JLabel boardHeightLabel = new JLabel("Board Height:");
        boardHeightSpinner = new JSpinner(new SpinnerNumberModel(6,3,9,1));
        gameModeBox = new JComboBox<>(gameModeOptions);

        musicCheckBox = new JCheckBox("Play Music", true);
        musicCheckBox.setOpaque(false);
        musicCheckBox.setForeground(Color.WHITE);
        waitTurnsCheckBox = new JCheckBox("Pause Between Turns", true);
        waitTurnsCheckBox.setOpaque(false);
        waitTurnsCheckBox.setForeground(Color.WHITE);


        connectValuesLabel.setForeground(Color.WHITE);
        boardWidthLabel.setForeground(Color.WHITE);
        boardHeightLabel.setForeground(Color.WHITE);
        gameModeLabel.setForeground(Color.WHITE);

        settingsPanel.add(connectValuesLabel);
        settingsPanel.add(connectValuesSpinner);

        settingsPanel.add(gameModeLabel);
        settingsPanel.add(gameModeBox);

        settingsPanel.add(boardWidthLabel);
        settingsPanel.add(boardWidthSpinner);

        settingsPanel.add(boardHeightLabel);
        settingsPanel.add(boardHeightSpinner);

        //quick and dirty way of shifting things over by one
        settingsPanel.add(new JLabel(""));

        settingsPanel.add(musicCheckBox);
        settingsPanel.add(waitTurnsCheckBox);

        settingsContainer.add(settingsPanel);


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
                    columns = ((Number)boardWidthSpinner.getValue()).byteValue();
                    rows = ((Number)boardHeightSpinner.getValue()).byteValue();
                    setLength = ((Number)connectValuesSpinner.getValue()).byteValue();
                    gameType = (GameType)gameModeBox.getSelectedItem();
                    waitBetweenTurns = waitTurnsCheckBox.isSelected();
                    playMusic = musicCheckBox.isSelected();
                    displayBoard(); // When the button is clicked and both fields are filled, start the game
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
        centerPanel.add(settingsContainer);
        centerPanel.add(okButton);
        centerPanel.add(Box.createVerticalGlue());

        // Validate and repaint the frame to reflect the changes
        this.validate();
        this.repaint();
    }

    private class NumKeyDispatcher implements KeyEventDispatcher {
        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            byte col = (byte)(numCharToByte(e.getKeyChar()) - 1);
            //might be a better way to do this than regex matching, but it works
            if (col < 0 || col >= columns || !e.paramString().matches("^KEY_RELEASED.*") || !listenForKeys) {
                return false;
            }
            clientQueue.offer(new GameEvent(EventType.PLACE_PIECE, turn, col));
            return false;
        }
    }

    private byte numCharToByte(char c) {
        if (c < '0' || c > '9') {
            return 0;
        }
        return (byte)(c - '0');
    }

    private void displayBoard() {
        // Remove all components from the frame
        this.getContentPane().removeAll();

        // Set the layout of the frame to BorderLayout
        this.setLayout(new BorderLayout());

        menuBar = new JMenuBar();
        sessionMenu = new JMenu("Session");
        restartItem = new JMenuItem("Restart");
        exitItem = new JMenuItem("Exit");

        restartItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                started = false; // Restart the game
                startAndSendCreateGame();
            }
        });
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                System.exit(0);
            }
        });


        sessionMenu.add(restartItem);
        sessionMenu.add(exitItem);
        menuBar.add(sessionMenu);
        this.setJMenuBar(menuBar);


        // Set the background color of the frame to Blue
        this.setBackground(Color.BLUE);

        // Call the displayHeaders() method to display the headers
        displayHeaders();

        // Call the displayBoardPanel() method to display the game board
        displayBoardPanel();

        // Validate and repaint the frame to reflect the changes
        this.validate();
        this.repaint();
        if (playMusic) {
            try {
                InputStream is = getClass().getClassLoader().getResourceAsStream("background.wav");
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(is);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
                ex.printStackTrace();
            }
        }
        startAndSendCreateGame();
        if (!dispatcherAdded) {
            KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
            manager.addKeyEventDispatcher(new NumKeyDispatcher());
            dispatcherAdded = true;
        }
    }


    private void displayHeaders() {

        // Create a label for the title and set its font
        JLabel headerJLabel = new JLabel("Timed Connect 4", SwingConstants.CENTER);
        headerJLabel.setFont(new Font("Serif", Font.BOLD, 30));

        // Create a panel for the headers and add the title label to it
        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.add(headerJLabel);

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

    private void displayGameOver(GameOutcome go, Player p1, Player p2) {
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
        if (gameType == GameType.SET_TOTAL) {
            message += String.format("\n Player 1(%s)'s Set Count: %d", player1Name, p1.getSets().size());
            message += String.format("\n Player 2(%s)'s Set Count: %d", player2Name, p2.getSets().size());
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
//                this.dispose(); // Dispose the frame
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
            GameEvent starter = new GameEvent(EventType.CREATE_GAME, columns, rows, gameType,
                    setLength, timerLength, waitBetweenTurns, player1Name, player1Color, player2Name, player2Color);
            clientQueue.offer(starter);
            started = true;
        }
    }

    private void turnGoDialog(String message) {
        listenForKeys = false;
        JOptionPane optionPane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE);
        JButton goButton = new JButton("Go!");
        optionPane.setOptions(new Object[] { goButton });
        goButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clientQueue.offer(new GameEvent(EventType.TURN_READY));
                ((JDialog) ((JButton) e.getSource()).getTopLevelAncestor()).dispose();
                listenForKeys = true;
            }
        });
        JDialog dialog = optionPane.createDialog(this, "Time to switch");
        dialog.setVisible(true);
    }


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
                buttons[y][x].setMinimumSize(new Dimension(50, 50));
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
                //buttons[y][x].setText(String.format(" <%d,%d> ", x, y));

                // Set the gridx and gridy constraints to the current column and row
                gbc.gridx = x;
                gbc.gridy = y;

                // Set the insets constraint to specify the padding around the button
                gbc.insets = new Insets(3, 0, 0, 2);

                // Add the button to the boardPanel with the specified constraints
                boardPanel.add(buttons[y][x], gbc);
            }
        }
        gbc.gridy = rows;
        for (int x = 0; x < columns; x++) {
            gbc.gridx = x;
            boardPanel.add(new JLabel(""+(x+1)), gbc);
        }

        // Create a wrapper JPanel to hold the boardPanel
        JPanel wrapperPanel = new JPanel();
        this.setSize(800, rows*70 + 130);
        wrapperPanel.setPreferredSize(new Dimension(800, rows*80 + 100));
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

}
