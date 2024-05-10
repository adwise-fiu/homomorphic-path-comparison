import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.util.List;

import security.dgk.DGKKeyPairGenerator;
import security.misc.HomomorphicException;
import security.paillier.PaillierKeyPairGenerator;
import security.socialistmillionaire.bob_joye;

public class SpeedTestingBob {
    private KeyPair paillier;
    private KeyPair dgk;

    public SpeedTestingBob(int key_size) {
        generate_keys(key_size);
    }

    public void generate_keys(int key_size) {

        // Generate Key Pairs
        DGKKeyPairGenerator p = new DGKKeyPairGenerator();
        p.initialize(key_size, null);
        dgk = p.generateKeyPair();


        PaillierKeyPairGenerator pa = new PaillierKeyPairGenerator();
        pa.initialize(key_size, null);
        paillier = pa.generateKeyPair();
    }


    public static void main(String[] args) throws HomomorphicException, IOException, ClassNotFoundException {
        // Parse input
        if (args.length != 2) {
            System.err.println("Invalid number of arguments, need path and port number");
        }

        String input_file = args[0];
        int port = 0;

        try {
            port = Integer.parseInt(args[1]);
        }
        catch (NumberFormatException e) {
            System.err.println("Invalid port provided");
            System.exit(1);
        }

        SpeedTestingBob iam = new SpeedTestingBob(2048);
        bob_joye bob = new bob_joye(iam.paillier, iam.dgk);
        bob.setDGKMode(false);
        String answers_path = new File(input_file).toString();

        // Parse CSV file
        try (ServerSocket bob_socket = new ServerSocket(port)) {

            System.out.println("Waiting on Alice");
            try (Socket bob_client = bob_socket.accept()) {
                bob.set_socket(bob_client);
                bob.sendPublicKeys();

                try (BufferedReader br = new BufferedReader(new FileReader(answers_path))) {
                    String line;

                    while ((line = br.readLine()) != null) {
                        List<BigIntPoint> bob_route = shared.parse_line(line);
                        List<BigIntPoint> bob_route_encrypted = shared.encrypt_paillier(bob_route, bob.getPaillierPublicKey());

                        ObjectOutputStream objectOutput = new ObjectOutputStream(bob_client.getOutputStream());
                        objectOutput.writeObject(bob_route_encrypted);
                        objectOutput.flush();

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
            } // while, each line of file
            System.out.println("Note that Bob written " + bob.get_bytes_sent() + " bytes to Alice");
        }
    }
}
