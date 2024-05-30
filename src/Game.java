import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class Game implements Runnable{
    private final long POLLWAIT = 100;
    //the queue the server puts messages on
    private final BlockingQueue<GameEvent> serverQueue;
    //the queue the client puts messages on
    private final BlockingQueue<GameEvent> clientQueue;
    Game(BlockingQueue serverQueue, BlockingQueue clientQueue) {
        this.serverQueue = serverQueue;
        this.clientQueue = clientQueue;
    }
    public boolean start(byte columns, byte rows, GameType gameType, byte setLength,
                         short timerLength, String p1Name, PieceColor p1Color,
                         String p2Name, PieceColor p2Color) {
        this.gameOutcome = null;
        this.gameType = gameType;
        this.setLength = setLength;
        board = new Board(columns, rows, setLength);
        this.timerLength = timerLength;
        this.p1 = new Player(p1Name, p1Color, PlayerNumber.PLAYER_1);
        this.p2 = new Player(p2Name, p2Color, PlayerNumber.PLAYER_2);
        serverQueue.offer(new GameEvent(EventType.UPDATE_SLOTS, board.getSlots()));
        turn = PlayerNumber.PLAYER_1;
        serverQueue.offer(new GameEvent(EventType.TURN, turn));
        if (timerLength > 0) {
            currentTime = timerLength;
            lastTimeUpdate = System.currentTimeMillis();
            serverQueue.offer(new GameEvent(EventType.TIME, currentTime));
        }
        started = true;
        //todo: return false if something is amiss. or throw an exception.
        return true;
    }
    private GameType gameType;
    private Board board;
    private Player p1;
    private Player p2;
    private byte setLength;
    private boolean started = true;
    private PlayerNumber turn;
    private GameOutcome gameOutcome;
    private short timerLength;
    private short currentTime;
    private long lastTimeUpdate;

    private boolean checkGameOverConditions() {
        return (board.isFull() ||
                gameType == GameType.FIRST_TO_SET
                        && getActivePlayer().getSets().size() > 0);
    }

    private void announceGameOutcome() {
        //set it to draw unless we find otherwise
        GameOutcome outcome = GameOutcome.DRAW;
        if (gameType == GameType.FIRST_TO_SET) {
            if (p1.getSets().size() > 0) {
                outcome = GameOutcome.P1_WIN;
            } else if (p2.getSets().size() > 0) {
                outcome = GameOutcome.P2_WIN;
            }
            //if neither above are true, we must be here because
            //the board is full and no sets were made. So draw.
        } else if (gameType == GameType.SET_TOTAL) {
            if (p1.getSets().size() > p2.getSets().size()) {
                outcome = GameOutcome.P1_WIN;
            } else if (p1.getSets().size() < p2.getSets().size()) {
                outcome = GameOutcome.P2_WIN;
            }
        }
        timerLength = -1; //disable the timer
        serverQueue.offer(new GameEvent(EventType.GAME_OVER, outcome, p1, p2));
    }
    private void swapTurn() {
        turn = (turn == PlayerNumber.PLAYER_1) ?
                PlayerNumber.PLAYER_2 : PlayerNumber.PLAYER_1;
        serverQueue.offer(new GameEvent(EventType.TURN, turn));
    }
    private Player getActivePlayer() {
        if (turn == PlayerNumber.PLAYER_1) {
            return p1;
        } else {
            return p2;
        }
    }
    private Player getInactivePlayer() {
        if (turn == PlayerNumber.PLAYER_1) {
            return p2;
        } else {
            return p1;
        }
    }

    public void run() {

        try {
            while(true) {
                //we could use a timer, but for now with only local clients,
                //let's just manage time manually in the main game thread.
                if (timerLength > 0 && started) {
                    long currentMs = System.currentTimeMillis();
                    if (currentMs - lastTimeUpdate > 1000) {
                        currentTime--;
                        lastTimeUpdate = System.currentTimeMillis();
                        if (currentTime < 0) {
                            //0 gets sent for a second, however, because that seems
                            //"friendlier" than going straight from 1 to, 10/20/30 or
                            //0 to 9/19/29
                            //so _technically_ the total duration of a turn is timerLength + 1
                            //but it just looks better to show 0 for a moment
                            //this will send a negative, code for time's up.
                            serverQueue.offer(new GameEvent(EventType.TIME, currentTime));
                            currentTime = timerLength;
                            swapTurn();
                        }
                        serverQueue.offer(new GameEvent(EventType.TIME, currentTime));
                    }
                }
                GameEvent ge = clientQueue.poll(POLLWAIT, TimeUnit.MILLISECONDS);
                if (ge != null) switch (ge.type()) {
                    case EventType.CREATE_GAME:
                        start(ge.getColumn(), ge.getRow(), ge.getGameType(), ge.getSetLength(),
                                ge.getTimerLength(), ge.getP1Name(), ge.getP1Color(),
                                ge.getP2Name(), ge.getP2Color());
                        break;
                    case EventType.PLACE_PIECE:
                        //wait why am I trusting the client at all when we already know whose turn
//                        Player p;
//                        switch (ge.getPlayerNumber()) {
//                            case PLAYER_1:
//                                p = p1;
//                                break;
//                            case PLAYER_2:
//                                p = p2;
//                                break;
//                            default:
//                                //todo: we should throw some sort of
//                                //exception if something crazy happens
//                                //for now just ignore it and continue looping
//                                continue;
//                        }
                        if (board.addPiece(getActivePlayer(), ge.getColumn())) {
                            serverQueue.offer(new GameEvent(EventType.UPDATE_SLOTS, board.getSlots()));
                            if (timerLength > 0) {
                                lastTimeUpdate = System.currentTimeMillis();
                                serverQueue.offer(new GameEvent(EventType.TIME, currentTime));
                            }
                            if (checkGameOverConditions()) {
                                announceGameOutcome();
                            } else {
                                swapTurn();
                            }
                        }

                        break;
                }
            }
        } catch (InterruptedException ex) {

        }

    }
}
