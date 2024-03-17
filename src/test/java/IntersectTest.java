import org.junit.Before;
import org.junit.Test;
import security.dgk.DGKKeyPairGenerator;
import security.misc.HomomorphicException;
import security.paillier.PaillierCipher;
import security.paillier.PaillierKeyPairGenerator;
import security.paillier.PaillierPrivateKey;
import security.paillier.PaillierPublicKey;
import security.socialistmillionaire.alice_joye;
import security.socialistmillionaire.bob_joye;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class IntersectTest {

    private static KeyPair dgk = null;
    private static KeyPair paillier = null;
    @Before
    public void read_properties() {

    }
    @Before
    public void generate_keys(){
        DGKKeyPairGenerator p = new DGKKeyPairGenerator();
        p.initialize(2048,null);
        dgk = p.generateKeyPair();

        PaillierKeyPairGenerator pa = new PaillierKeyPairGenerator();
        pa.initialize(2048,null);
        paillier = pa.generateKeyPair();
    }

    @Test
    public void encrypted_test_intersections() {
        String answers_path = new File("data/testroutine.csv").toString();
        //Parse CSV file
        try (BufferedReader br = new BufferedReader(new FileReader(answers_path))) {
            String assertstring;
            String line;

            alice_joye alice = new alice_joye();
            bob_joye bob = new bob_joye(paillier,dgk,null);
            Thread multiply = new Thread(new BobOperations(bob,9200));
            multiply.start();
            alice.set_socket(new Socket("127.0.0.1",9200));
            alice.receivePublicKeys();

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                String ownroute = values[0];
                String cryptroute = values[1];
                String expected_result = values[2];
                System.out.println(ownroute);

                //Parsing routes
                List<BigIntPoint> ownroute_list = CleartextPathsComparison.read_all_paths(ownroute);
                List<BigIntPoint> cryptroute_list = CleartextPathsComparison.read_all_paths(cryptroute);
                //encrypt routes
                PaillierPublicKey paillier_public_key = (PaillierPublicKey) paillier.getPublic();
                PaillierPrivateKey paillier_private_key = (PaillierPrivateKey) paillier.getPrivate();
                List<BigIntPoint> encryptedownroute_list = new ArrayList<>();
                List<BigIntPoint> encryptedcryptroute_list = new ArrayList<>();

                BigInteger encryptedzero = PaillierCipher.encrypt(0, paillier_public_key);

                for (int i = 0; i < ownroute_list.size(); i++) {
                    BigInteger ownx = PaillierCipher.encrypt(ownroute_list.get(i).x.longValue(), paillier_public_key);
                    BigInteger owny = PaillierCipher.encrypt(ownroute_list.get(i).y.longValue(), paillier_public_key);

                    BigIntPoint own = new BigIntPoint(ownx, owny);
                    encryptedownroute_list.add(own);
                }
                for (int i = 0; i < cryptroute_list.size(); i++){
                    BigInteger theirx = PaillierCipher.encrypt(cryptroute_list.get(i).x.longValue(), paillier_public_key);
                    BigInteger theiry = PaillierCipher.encrypt(cryptroute_list.get(i).y.longValue(), paillier_public_key);

                    BigIntPoint theirs = new BigIntPoint(theirx, theiry);
                    encryptedcryptroute_list.add(theirs);
                }



                EncryptedPathsComparison testing = new EncryptedPathsComparison(alice, paillier_public_key);
                testing.encryptedPathIntersection(encryptedownroute_list,encryptedcryptroute_list, paillier_public_key, encryptedzero);
                System.out.println(testing);

            }
        } catch (IOException | ClassNotFoundException | HomomorphicException e) {
            System.err.println("Error reading files" + e.getMessage());
        }
    }

    @Test
    public void test_intersections() throws Exception {
        String answers_path = new File("data/testroutine.csv").toString();
        //Parse CSV file
        try (BufferedReader br = new BufferedReader(new FileReader(answers_path))){
            String assertstring;
            String line;
            while ((line = br.readLine()) != null){
                String [] values = line.split(",");
                String ownroute = values[0];
                String cryptroute = values[1];
                String expected_result = values[2];
                System.out.println(ownroute);
                //Parsing routes
                List<BigIntPoint> ownroute_list = CleartextPathsComparison.read_all_paths(ownroute);
                List<BigIntPoint> cryptroute_list = CleartextPathsComparison.read_all_paths(cryptroute);
                //Testing for intersection
                boolean output = CleartextPathsComparison.pathIntersection(ownroute_list, cryptroute_list);
                if (output == true) {
                    assertstring = "true";
                } else if (output == false) {
                     assertstring = "false";
                } else {
                     assertstring = "error";
                }
                assertEquals(expected_result, assertstring);
            }
        }
    }

    @Test
    public void test_drone_collision() {
        Drone kemal = new Drone(2048);
        Drone allan = new Drone(2048);





        // I need the drones to have each other's public key...

        // I need to figure out how to use Homomorphic Encryption Library here...
        kemal.willCollide(allan);
    }
}