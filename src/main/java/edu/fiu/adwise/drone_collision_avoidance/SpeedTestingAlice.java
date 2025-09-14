/*
 * Copyright (c) 2024 ADWISE Lab, Florida International University (FIU), AndrewQuijano
 * Licensed under the MIT License. See LICENSE file in the project root for details.
 */
package edu.fiu.adwise.drone_collision_avoidance;

import edu.fiu.adwise.drone_collision_avoidance.structs.BigIntPoint;
import org.apache.commons.io.serialization.ValidatingObjectInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import edu.fiu.adwise.homomorphic_encryption.misc.HomomorphicException;
import edu.fiu.adwise.homomorphic_encryption.socialistmillionaire.alice_joye;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * A class for testing encrypted path comparisons using Alice's perspective.
 * This class connects to Bob, receives encrypted paths, and performs intersection checks.
 */
public class SpeedTestingAlice {

    /** Logger for logging messages and errors. */
    private static final Logger logger = LogManager.getLogger(SpeedTestingAlice.class);

    /** The port number for connecting to Bob. */
    private final int port;

    /**
     * Constructs a SpeedTestingAlice instance with the specified port.
     *
     * @param port the port number for the connection
     */
    public SpeedTestingAlice(int port) {
        this.port = port;
    }

    /**
     * The main method for executing the program.
     * Parses input arguments, connects to Bob, and performs encrypted path comparisons.
     *
     * @param args command-line arguments: file path, IP address, and port number
     */
    public static void main(String[] args) {

        // Parse input
        if (args.length != 3) {
            System.err.println("Invalid number of arguments, need path and IP and port number");
        }

        String input_file = args[0];
        String ip_address = args[1];
        int port = 0;

        try {
            port = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid port provided");
            System.exit(1);
        }

        alice_joye alice = new alice_joye();
        alice.setDGKMode(false);
        String answers_path = new File(input_file).toString();

        // Parse CSV file
        try (BufferedReader br = new BufferedReader(new FileReader(answers_path))) {

            String line;
            // Set the connection to Bob
            SpeedTestingAlice paths_alice = new SpeedTestingAlice(port);
            Socket socket = new Socket(ip_address, paths_alice.port);
            alice.set_socket(socket);
            alice.receivePublicKeys();

            while ((line = br.readLine()) != null) {
                // Execute the program
                List<BigIntPoint> alice_route = shared.parse_line(line);

                // Only use the indexed line in the alice_segments file
                List<BigIntPoint> bobs_route = new ArrayList<>();

                ValidatingObjectInputStream input = shared.get_ois(socket);
                Object object = input.readObject();
                if (object instanceof List<?>) {
                    for (Object element : (List<?>) object) {
                        if (element instanceof BigIntPoint) {
                            bobs_route.add((BigIntPoint) element);
                        }
                    }
                }
                assert !bobs_route.isEmpty();
                List<BigIntPoint> alice_encrypted_route = shared.encrypt_paillier(alice_route, alice.getPaillierPublicKey());
                EncryptedPathsComparison testing = new EncryptedPathsComparison(alice);
                List<Integer> result = testing.encryptedWhereIntersection(alice_encrypted_route, bobs_route);
                logger.info(result);
                logger.info("Note that alice written {} bytes to Bob", alice.get_bytes_sent());
            }
        } catch (IOException | ClassNotFoundException | HomomorphicException e) {
            logger.fatal(e);
        }
    }
}