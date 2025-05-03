package edu.fiu.adwise;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import edu.fiu.adwise.homomorphic_encryption.misc.HomomorphicException;
import edu.fiu.adwise.homomorphic_encryption.socialistmillionaire.bob;

// Used only for testing
public class BobThread implements Runnable {

    private static final Logger logger = LogManager.getLogger(BobThread.class);
    private final int port;
    private final bob bob_connect;

    public BobThread(bob bob_connect, int port) {
        this.bob_connect = bob_connect;
        this.port = port;
    }

    public void run() {
        try (ServerSocket bob_socket = new ServerSocket(port)) {
            try (Socket bob_client = bob_socket.accept()) {
                bob_connect.set_socket(bob_client);
                bob_connect.sendPublicKeys();

                while (true) {
                    int var = bob_connect.readInt();
                    if (var == 1) {
                        bob_connect.multiplication();
                    }

                    if (var == 2) {
                        bob_connect.Protocol2();
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