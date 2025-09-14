/*
 * Copyright (c) 2024 ADWISE Lab, Florida International University (FIU), AndrewQuijano
 * Licensed under the MIT License. See LICENSE file in the project root for details.
 */
package edu.fiu.adwise.drone_collision_avoidance;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import edu.fiu.adwise.ciphercraft.misc.HomomorphicException;
import edu.fiu.adwise.ciphercraft.socialistmillionaire.bob;

/**
 * A thread that handles communication with Bob in a homomorphic encryption protocol.
 * This class listens for incoming connections, processes requests, and executes
 * specific operations based on the received commands.
 */
public class BobThread implements Runnable {

    /** Logger for logging messages and errors. */
    private static final Logger logger = LogManager.getLogger(BobThread.class);

    /** The port number on which the server socket listens to. */
    private final int port;

    /** The Bob instance used for handling cryptographic operations. */
    private final bob bob_connect;

    /**
     * Constructs a BobThread with the specified Bob instance and port number.
     *
     * @param bob_connect the Bob instance for cryptographic operations
     * @param port the port number for the server socket
     */
    public BobThread(bob bob_connect, int port) {
        this.bob_connect = bob_connect;
        this.port = port;
    }

    /**
     * Starts the thread to listen for incoming connections and process commands.
     * Handles cryptographic operations based on the received commands.
     */
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