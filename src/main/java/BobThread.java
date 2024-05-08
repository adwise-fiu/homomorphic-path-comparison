import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import security.misc.HomomorphicException;
import security.socialistmillionaire.bob_joye;

// Used only for testing
public class BobThread implements Runnable {

    private static final Logger logger = LogManager.getLogger(BobThread.class);
    private final int port;
    private final bob_joye bob;

    public BobThread(bob_joye bob, int port) {
        this.bob = bob;
        this.port = port;
    }

    public void run() {
        try (ServerSocket bob_socket = new ServerSocket(port)) {
            try (Socket bob_client = bob_socket.accept()) {
                bob.set_socket(bob_client);
                bob.sendPublicKeys();

                while (true) {
                    int var = bob.readInt();
                    if (var == 1) {
                        bob.multiplication();
                    }

                    if (var == 2) {
                        bob.Protocol2();
                    }

                    if (var == 0) {
                        break;
                    }
                } // while
            }
        }
        catch (IOException | ClassNotFoundException | HomomorphicException e) {
            logger.fatal(e);
        }
    }
}