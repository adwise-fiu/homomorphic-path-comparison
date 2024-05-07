import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertEquals;

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
        // Parse CSV file
        try (BufferedReader br = new BufferedReader(new FileReader(answers_path))) {
            String line;

            alice_joye alice = new alice_joye();
            bob_joye bob = new bob_joye(paillier, dgk);
            Thread multiply = new Thread(new BobThread(bob,9200));
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

                BigInteger encrypted_zero = PaillierCipher.encrypt(0, paillier_public_key);

                for (BigIntPoint intPoint : ownroute_list) {
                    BigInteger own_x = PaillierCipher.encrypt(intPoint.x.longValue(), paillier_public_key);
                    BigInteger own_y = PaillierCipher.encrypt(intPoint.y.longValue(), paillier_public_key);

                    BigIntPoint own = new BigIntPoint(own_x, own_y);
                    encryptedownroute_list.add(own);
                }

                for (BigIntPoint bigIntPoint : cryptroute_list) {
                    BigInteger their_x = PaillierCipher.encrypt(bigIntPoint.x.longValue(), paillier_public_key);
                    BigInteger their_y = PaillierCipher.encrypt(bigIntPoint.y.longValue(), paillier_public_key);

                    BigIntPoint theirs = new BigIntPoint(their_x, their_y);
                    encryptedcryptroute_list.add(theirs);
                }

                EncryptedPathsComparison testing = new EncryptedPathsComparison(alice, paillier_public_key);
                testing.encryptedWhereIntersection(encryptedownroute_list, encryptedcryptroute_list,
                        paillier_public_key, encrypted_zero);
                System.out.println(testing);
            }
        }
        catch (IOException | ClassNotFoundException | HomomorphicException e) {
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
                // Parsing routes
                List<BigIntPoint> ownroute_list = CleartextPathsComparison.read_all_paths(ownroute);
                List<BigIntPoint> cryptroute_list = CleartextPathsComparison.read_all_paths(cryptroute);
                // Testing for intersection
                boolean output = CleartextPathsComparison.pathIntersection(ownroute_list, cryptroute_list);
                if (output) {
                    assertstring = "true";
                }
                else {
                     assertstring = "false";
                }
                assertEquals(expected_result, assertstring);
            }
        }
    }
}