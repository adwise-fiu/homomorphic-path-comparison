/*
 * Copyright (c) 2024 ADWISE Lab, Florida International University (FIU), AndrewQuijano
 * Licensed under the MIT License. See LICENSE file in the project root for details.
 */
package edu.fiu.adwise.drone_collision_avoidance;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.util.List;

import edu.fiu.adwise.drone_collision_avoidance.structs.BigIntPoint;
import edu.fiu.adwise.homomorphic_encryption.dgk.DGKKeyPairGenerator;
import edu.fiu.adwise.homomorphic_encryption.misc.HomomorphicException;
import edu.fiu.adwise.homomorphic_encryption.paillier.PaillierKeyPairGenerator;
import edu.fiu.adwise.homomorphic_encryption.socialistmillionaire.bob_joye;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A class for testing encrypted path comparisons using Bob's perspective.
 * This class generates encryption keys, listens for connections from Alice,
 * and performs encrypted operations.
 */
public class SpeedTestingBob {

    /** The Paillier key pair for encryption and decryption. */
    private KeyPair paillier;

    /** The DGK key pair for encryption and decryption. */
    private KeyPair dgk;

    /** Logger for logging messages and errors. */
    private static final Logger logger = LogManager.getLogger(SpeedTestingBob.class);

    /**
     * Constructs a SpeedTestingBob instance and generates encryption keys.
     *
     * @param key_size the size of the encryption keys
     */
    public SpeedTestingBob(int key_size) {
        generate_keys(key_size);
    }

    /**
     * Generates encryption keys for both Paillier and DGK schemes.
     *
     * @param key_size the size of the encryption keys
     */
    public void generate_keys(int key_size) {
        // Generate DGK Key Pair
        DGKKeyPairGenerator p = new DGKKeyPairGenerator();
        p.initialize(key_size, null);
        dgk = p.generateKeyPair();

        // Generate Paillier Key Pair
        PaillierKeyPairGenerator pa = new PaillierKeyPairGenerator();
        pa.initialize(key_size, null);
        paillier = pa.generateKeyPair();
    }

    /**
     * The main method for executing the program.
     * Parses input arguments, listens for connections from Alice, and performs encrypted operations.
     *
     * @param args command-line arguments: file path and port number
     * @throws HomomorphicException if a homomorphic encryption error occurs
     * @throws IOException if an I/O error occurs
     * @throws ClassNotFoundException if a class is not found during deserialization
     */
    public static void main(String[] args) throws HomomorphicException, IOException, ClassNotFoundException {
        // Parse input
        if (args.length != 2) {
            logger.fatal("Invalid number of arguments, need path and port number");
        }

        String input_file = args[0];
        int port = 0;

        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            logger.fatal("Invalid port provided");
            System.exit(1);
        }

        SpeedTestingBob iam = new SpeedTestingBob(2048);
        bob_joye bob = new bob_joye(iam.paillier, iam.dgk);
        bob.setDGKMode(false);
        String answers_path = new File(input_file).toString();

        // Parse CSV file and listen for connections
        try (ServerSocket bob_socket = new ServerSocket(port)) {

            logger.info("Waiting on Alice");
            try (Socket bob_client = bob_socket.accept()) {
                bob.set_socket(bob_client);
                bob.sendPublicKeys();

                try (BufferedReader br = new BufferedReader(new FileReader(answers_path))) {
                    String line;

                    while ((line = br.readLine()) != null) {
                        // Parse and encrypt Bob's route
                        List<BigIntPoint> bob_route = shared.parse_line(line);
                        List<BigIntPoint> bob_route_encrypted = shared.encrypt_paillier(bob_route, bob.getPaillierPublicKey());

                        // Send encrypted route to Alice
                        ObjectOutputStream objectOutput = new ObjectOutputStream(bob_client.getOutputStream());
                        objectOutput.writeObject(bob_route_encrypted);
                        objectOutput.flush();

                        // Handle operations requested by Alice
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
                        }
                    }
                }
            }
            logger.info("Note that Bob written {} bytes to Alice", bob.get_bytes_sent());
        }
    }
}