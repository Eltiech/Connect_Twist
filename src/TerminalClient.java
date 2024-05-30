import org.fusesource.jansi.AnsiConsole;

import java.util.Scanner;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class TerminalClient implements Runnable {
    private final long POLLWAIT = 100;
    //the queue the server puts messages on
    private final BlockingQueue<GameEvent> serverQueue;
    //the queue the client puts messages on
    private final BlockingQueue<GameEvent> clientQueue;
    public TerminalClient(BlockingQueue serverQueue, BlockingQueue clientQueue) {
        this.serverQueue = serverQueue;
        this.clientQueue = clientQueue;
    }




    public void run() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Player1 name: ");
        String p1Name = sc.nextLine();
        System.out.print("Enter Player2 name: ");
        String p2Name = sc.nextLine();
        System.out.print("Enter Columns: ");
        byte cols = sc.nextByte();
        System.out.print("Enter Rows: ");
        byte rows = sc.nextByte();
        //The conflict between the timer and Scanner accepting a column
        //is resolvable, but there's not much point in going out of our way
        //for what's effectively a debugging tool client. We can feed the
        //server thread moves another way later if needed. Probably could
        //rig up a file of lines like "<waitTime> <move-to-make>" for scripting behaviors
        System.out.println("Note: 0 or less disables timer. Recommended for text client, as Scanner blocks\n" +
                "and clogs up the queue with timer events causing weird behavior");
        System.out.print("Enter Timer Length: ");
        short timerLength = sc.nextShort();
        try {
            GameEvent starter = new GameEvent(EventType.CREATE_GAME, cols, rows, GameType.SET_TOTAL, timerLength,
                    p1Name, PieceColor.YELLOW, p2Name, PieceColor.RED);

            System.out.println("Sending game parameters..");
            clientQueue.offer(starter);
            while (true) {
                GameEvent ge = serverQueue.poll(POLLWAIT, TimeUnit.MILLISECONDS);
                if (ge != null) switch (ge.type()) {
                    case TIME:
//                        if (ge.getCurrentTime() == -1) {
//                            System.out.println("Sorry, out of time!");
//                        }
                        //System.out.println("Received TIME: "+ge.getCurrentTime());
                        break;
                    case TURN:
                        String name = (ge.getPlayerNumber()==PlayerNumber.PLAYER_1) ? p1Name : p2Name;
                        System.out.print(name + " enter column:");
                        byte col = sc.nextByte();
                        clientQueue.offer(new GameEvent(EventType.PLACE_PIECE,ge.getPlayerNumber(),col));
                        break;
                    case UPDATE_SLOTS:
                        System.out.println(Util.printSlots(ge.getSlots(),true));
                        break;
                    case GAME_OVER:
                        System.out.println("Received GAME_OVER.");
                        switch (ge.getGameOutcome()) {
                            case P1_WIN:
                                System.out.println("Player 1 wins");
                                break;
                            case P2_WIN:
                                System.out.println("Player 2 wins");
                                break;
                            case DRAW:
                                System.out.println("Draw \uD83D\uDE15");
                                break;
                        }
                        TreeSet<ComparableTreeSet<Coord>> p1Set = ge.getPlayer1().getSets();
                        TreeSet<ComparableTreeSet<Coord>> p2Set = ge.getPlayer2().getSets();
                        System.out.println("Player 1 set count: " + p1Set.size());
                        System.out.println("Player 1 sets: " + Util.coordSetSetToString(p1Set));
                        System.out.println("Player 2 set count: " + p2Set.size());
                        System.out.println("Player 2 sets: " + Util.coordSetSetToString(p2Set));
                        break;

                }
            }
            //AnsiConsole.systemUninstall();
        } catch (InterruptedException ex) {

        }
    }
}
