import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;

import security.dgk.DGKKeyPairGenerator;
import security.dgk.DGKOperations;
import security.dgk.DGKPrivateKey;
import security.dgk.DGKPublicKey;
import security.misc.HomomorphicException;
import security.paillier.PaillierCipher;
import security.paillier.PaillierKeyPairGenerator;
import security.paillier.PaillierPrivateKey;
import security.paillier.PaillierPublicKey;
import security.socialistmillionaire.bob_joye;

public class PathsBob {

    private int port;
    private static ServerSocket bob_socket = null;
    private static Socket bob_client = null;
    private KeyPair paillier;
    private  KeyPair dgk;
    private  DGKPublicKey dgk_public_key;
    private  DGKPrivateKey dgk_private_key;
    private  PaillierPublicKey paillier_public_key;
    private  PaillierPrivateKey paillier_private_key;

    public PathsBob(int port, int key_size) {
        this.port = port;
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

        dgk_public_key = (DGKPublicKey) dgk.getPublic();
        paillier_public_key = (PaillierPublicKey) paillier.getPublic();
        dgk_private_key = (DGKPrivateKey) dgk.getPrivate();
        paillier_private_key = (PaillierPrivateKey) paillier.getPrivate();
    }


    public static void main(String[] args) {
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

        PathsBob iam = new PathsBob(port,2048);
        bob_joye bob = new bob_joye(iam.paillier, iam.dgk);
        String my_path = new File(input_file).toString();

        try(BufferedReader br = new BufferedReader(new FileReader(my_path))) {
            String line;
            List<BigIntPoint> bob_route_encrypted = new ArrayList<>();

            List<BigIntPoint> bob_route = CleartextPathsComparison.read_all_paths(my_path);
            for (BigIntPoint bigIntPoint : bob_route) {
                BigInteger bob_x = PaillierCipher.encrypt(bigIntPoint.x.longValue(), iam.paillier_public_key);
                BigInteger bob_y = PaillierCipher.encrypt(bigIntPoint.y.longValue(), iam.paillier_public_key);

                BigIntPoint point = new BigIntPoint(bob_x, bob_y);
                bob_route_encrypted.add(point);
            }

            System.out.println("Waiting on Alice");
            bob_socket = new ServerSocket(port);
            bob_client = bob_socket.accept();
            bob.set_socket(bob_client);
            bob.sendPublicKeys();

            ObjectOutputStream objectOutput = new ObjectOutputStream(bob_client.getOutputStream());
            objectOutput.writeObject(bob_route_encrypted);

            while (true) {
                int var;
                var = bob.readInt();

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

        } catch (IOException | ClassNotFoundException | HomomorphicException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bob_client != null) {
                    bob_client.close();
                }
                if (bob_socket != null) {
                    bob_socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
