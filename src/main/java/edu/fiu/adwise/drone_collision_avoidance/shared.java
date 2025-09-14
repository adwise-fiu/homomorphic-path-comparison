/*
 * Copyright (c) 2024 ADWISE Lab, Florida International University (FIU), AndrewQuijano
 * Licensed under the MIT License. See LICENSE file in the project root for details.
 */
package edu.fiu.adwise.drone_collision_avoidance;

import edu.fiu.adwise.drone_collision_avoidance.structs.BigIntPoint;
import org.apache.commons.io.serialization.ValidatingObjectInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import edu.fiu.adwise.ciphercraft.dgk.DGKOperations;
import edu.fiu.adwise.ciphercraft.dgk.DGKPublicKey;
import edu.fiu.adwise.ciphercraft.misc.HomomorphicException;
import edu.fiu.adwise.ciphercraft.paillier.PaillierCipher;
import edu.fiu.adwise.ciphercraft.paillier.PaillierPublicKey;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class providing shared methods for handling paths, encryption, and deserialization.
 */
public class shared {

    /** Logger for logging messages and errors. */
    private static final Logger logger = LogManager.getLogger(shared.class);

    /**
     * Creates a ValidatingObjectInputStream for secure deserialization from a socket.
     *
     * @param socket the socket to read from
     * @return a ValidatingObjectInputStream for deserialization
     * @throws IOException if an I/O error occurs
     */
    public static ValidatingObjectInputStream get_ois(Socket socket) throws IOException {
        ValidatingObjectInputStream ois = new ValidatingObjectInputStream(socket.getInputStream());
        ois.accept(
                java.util.ArrayList.class,
                java.util.List.class,
                BigIntPoint.class,
                java.lang.Number.class,
                java.math.BigInteger.class
        );
        ois.accept("[B");
        ois.accept("[L*");
        return ois;
    }

    /**
     * Reads all paths from a file and parses them into a list of BigIntPoint objects.
     *
     * @param file_path the path to the file
     * @return a list of BigIntPoint objects representing the paths
     */
    public static List<BigIntPoint> read_all_paths(String file_path) {
        String route = null;
        try {
            route = Files.readString(Path.of(file_path), StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.fatal(e);
        }
        return parse_line(route);
    }

    /**
     * Reads a specific line from a file and parses it into a list of BigIntPoint objects.
     *
     * @param file_path the path to the file
     * @param line_index the index of the line to read
     * @return a list of BigIntPoint objects representing the line
     */
    public static List<BigIntPoint> read_one_line(String file_path, int line_index) {
        String route = null;
        try (LineNumberReader rdr = new LineNumberReader(new FileReader(file_path))) {
            rdr.setLineNumber(line_index);
            route = rdr.readLine();
        } catch (IOException e) {
            logger.fatal(e);
        }
        return parse_line(route);
    }

    /**
     * Parses a string containing coordinate pairs into a list of BigIntPoint objects.
     *
     * @param input the input string containing coordinate pairs
     * @return a list of BigIntPoint objects
     */
    public static List<BigIntPoint> parse_line(String input) {
        List<BigIntPoint> result = new ArrayList<>();

        // Define a regex pattern for extracting pairs of numbers within parentheses
        Pattern pattern = Pattern.compile("\\((-?\\d+),(-?\\d+)\\)");

        // Use a Matcher to find matches in the input string
        Matcher matcher = pattern.matcher(input);

        // Iterate through the matches and extract BigInteger values
        while (matcher.find()) {
            String group1 = matcher.group(1);
            String group2 = matcher.group(2);
            BigIntPoint pair = new BigIntPoint(new BigInteger(group1), new BigInteger(group2));
            result.add(pair);
        }
        return result;
    }

    /**
     * Encrypts a list of BigIntPoint objects using the Paillier encryption scheme.
     *
     * @param input_path the list of BigIntPoint objects to encrypt
     * @param paillier_public_key the Paillier public key
     * @return a list of encrypted BigIntPoint objects
     * @throws HomomorphicException if an encryption error occurs
     */
    public static List<BigIntPoint> encrypt_paillier(List<BigIntPoint> input_path,
                                                     PaillierPublicKey paillier_public_key)
            throws HomomorphicException {

        List<BigIntPoint> encrypted_path = new ArrayList<>();
        for (BigIntPoint bigIntPoint : input_path) {
            BigInteger their_x = PaillierCipher.encrypt(bigIntPoint.x.longValue(), paillier_public_key);
            BigInteger their_y = PaillierCipher.encrypt(bigIntPoint.y.longValue(), paillier_public_key);

            BigIntPoint theirs = new BigIntPoint(their_x, their_y);
            encrypted_path.add(theirs);
        }
        return encrypted_path;
    }

    /**
     * Encrypts a list of BigIntPoint objects using the DGK encryption scheme.
     *
     * @param input_path the list of BigIntPoint objects to encrypt
     * @param dgk_public_key the DGK public key
     * @return a list of encrypted BigIntPoint objects
     * @throws HomomorphicException if an encryption error occurs
     */
    public static List<BigIntPoint> encrypt_dgk(List<BigIntPoint> input_path,
                                                DGKPublicKey dgk_public_key) throws HomomorphicException {
        List<BigIntPoint> encrypted_path = new ArrayList<>();
        for (BigIntPoint bigIntPoint : input_path) {
            BigInteger their_x = DGKOperations.encrypt(bigIntPoint.x.longValue(), dgk_public_key);
            BigInteger their_y = DGKOperations.encrypt(bigIntPoint.y.longValue(), dgk_public_key);

            BigIntPoint theirs = new BigIntPoint(their_x, their_y);
            encrypted_path.add(theirs);
        }
        return encrypted_path;
    }
}