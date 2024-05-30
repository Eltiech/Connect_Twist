//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TerminalClientMain{

    public static void main(String[] args) {
        BlockingQueue<GameEvent> serverQueue = new LinkedBlockingQueue<GameEvent>();
        BlockingQueue<GameEvent> clientQueue = new LinkedBlockingQueue<GameEvent>();
        Game gameServer = new Game(serverQueue, clientQueue);
        TerminalClient gameClient = new TerminalClient(serverQueue, clientQueue);
        new Thread(gameServer).start();
        new Thread(gameClient).start();
    }

}