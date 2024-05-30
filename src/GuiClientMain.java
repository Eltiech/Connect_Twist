import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.swing.*;

public class GuiClientMain{

    public static void main(String[] args) {
        BlockingQueue<GameEvent> serverQueue = new LinkedBlockingQueue<GameEvent>();
        BlockingQueue<GameEvent> clientQueue = new LinkedBlockingQueue<GameEvent>();
        Game gameServer = new Game(serverQueue, clientQueue);
        //TerminalClient gameClient = new TerminalClient(serverQueue, clientQueue);
        new Thread(gameServer).start();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    new GuiClient(serverQueue, clientQueue);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //new Thread(gameClient).start();
    }

}