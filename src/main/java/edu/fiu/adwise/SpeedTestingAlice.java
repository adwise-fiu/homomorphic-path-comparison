package edu.fiu.adwise;

import edu.fiu.adwise.structs.BigIntPoint;
import org.apache.commons.io.serialization.ValidatingObjectInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import edu.fiu.adwise.homomorphic_encryption.misc.HomomorphicException;
import edu.fiu.adwise.homomorphic_encryption.socialistmillionaire.alice_joye;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SpeedTestingAlice {

    private static final Logger logger = LogManager.getLogger(SpeedTestingAlice.class);
    private final int port;

    public SpeedTestingAlice(int port) {
            this.port = port;
        }
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
        }
        catch (NumberFormatException e) {
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

                List<BigIntPoint> alices_encrypted_route = shared.encrypt_paillier(alice_route, alice.getPaillierPublicKey());
                EncryptedPathsComparison testing = new EncryptedPathsComparison(alice);
                List<Integer> result = testing.encryptedWhereIntersection(alices_encrypted_route, bobs_route);
                System.out.println(result);
                System.out.println("Note that alice written " + alice.get_bytes_sent() + " bytes to Bob");
            }
        } catch (IOException | ClassNotFoundException | HomomorphicException e) {
            e.printStackTrace();
            logger.fatal(e);
        }
    }
}
