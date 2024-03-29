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

    private static int port;
    private static ServerSocket bob_socket = null;
    private static Socket bob_client = null;
    private KeyPair paillier;
    private  KeyPair dgk;
    private  DGKPublicKey dgk_public_key;
    private  DGKPrivateKey dgk_private_key;
    private  PaillierPublicKey paillier_public_key;
    private  PaillierPrivateKey paillier_private_key;

    public PathsBob(int port, int key_size){
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

        PathsBob iam = new PathsBob(9200,2048);

        bob_joye bob = new bob_joye(iam.paillier, iam.dgk);
        String my_path = new File("cryptroutefile.txt").toString();

        try(BufferedReader br = new BufferedReader(new FileReader(my_path))) {
            String line;
            List<BigIntPoint> bob_route_encrypted = new ArrayList<>();

            List<BigIntPoint> bob_route = CleartextPathsComparison.read_all_paths(my_path);
            for (int i = 0; i < bob_route.size(); i++){
                BigInteger bobx = PaillierCipher.encrypt(bob_route.get(i).x.longValue(), iam.paillier_public_key);
                BigInteger boby = PaillierCipher.encrypt(bob_route.get(i).y.longValue(), iam.paillier_public_key);

                BigIntPoint point = new BigIntPoint(bobx, boby);
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
