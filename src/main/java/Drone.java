import security.dgk.DGKKeyPairGenerator;
import security.dgk.DGKPrivateKey;
import security.dgk.DGKPublicKey;
import security.paillier.PaillierKeyPairGenerator;
import security.paillier.PaillierPrivateKey;
import security.paillier.PaillierPublicKey;
import security.socialistmillionaire.alice_joye;
import security.socialistmillionaire.bob_joye;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyPair;

public class Drone {

    private KeyPair paillier;
    private KeyPair dgk;
    private DGKPublicKey dgk_public_key;
    private DGKPrivateKey dgk_private_key;
    private PaillierPublicKey paillier_public_key;
    private PaillierPrivateKey paillier_private_key;

    // A drone will create its own key pair
    // Before launch, it will be provided with its path
    public Drone(int key_size) {
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

    public void runbob(int port1, int port2)
    {

        bob_joye bob = new bob_joye(paillier,dgk,null);
        //Thread pollingbob = new Thread(new BobThread(bob,port1, port2));
        //pollingbob.start();
    }

    public void runalice(InetAddress host, int port1, int port2){
        alice_joye alice = new alice_joye();
        try {
            alice.set_socket(new Socket(host, port1));
            alice.set_socket(new Socket(host, port2));
            alice.receivePublicKeys();
            //theirs path should be received here
            //Thread threadone = new Thread(new AliceThreadOne(mine, theirs, alice, port1, paillier_public_key));
            //Thread threadtwo = new Thread(new AliceThreadTwo(mine, theirs, alice, port2, paillier_public_key));
            //threadone.start();
            //threadtwo.start();
        }catch(IOException | ClassNotFoundException e){
            System.err.println(e.getMessage());
        }
    }


    // Check if this drone will collide with the other drone.
    // Return true if a collision will occur - call to EncryptedPathIntersection
    public boolean willCollide(Drone other) {

        alice_joye alice = new alice_joye();
        EncryptedPathsComparison comparison = new EncryptedPathsComparison(alice, paillier_public_key);

        return true;
    }
}
