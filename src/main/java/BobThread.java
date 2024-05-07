import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import security.misc.HomomorphicException;
import security.socialistmillionaire.bob_joye;


public class BobThread implements Runnable {
    private static final Logger logger = LogManager.getLogger(BobThread.class);

    private final int port;
    private static ServerSocket bob_socket = null;
    private static Socket bob_client = null;
    private final bob_joye this_guy;

    public BobThread(bob_joye this_guy, int port) {
        this.this_guy = this_guy;
        this.port = port;
    }

    public void run() {
        try {
            bob_socket = new ServerSocket(port);
            bob_client = bob_socket.accept();
            this_guy.set_socket(bob_client);
            this_guy.sendPublicKeys();

            while (true) {

               int var = this_guy.readInt();

               if (var == 1) {
                   this_guy.multiplication();
               }

               if (var == 2) {
                   this_guy.Protocol2();
               }

               if (var == 0) {
                   break;
               }
           }
        }
        catch (IOException | ClassNotFoundException | HomomorphicException e) {
            logger.fatal(e.getStackTrace());
        }
        finally {
            try {
                if (bob_client != null) {
                    bob_client.close();
                }
                if (bob_socket != null) {
                    bob_socket.close();
                }
            } catch (IOException e) {
                logger.fatal(e.getStackTrace());
            }
        }
    }
}