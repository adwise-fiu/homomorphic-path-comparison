import security.dgk.DGKKeyPairGenerator;
import security.dgk.DGKPrivateKey;
import security.dgk.DGKPublicKey;
import security.paillier.PaillierKeyPairGenerator;
import security.paillier.PaillierPrivateKey;
import security.paillier.PaillierPublicKey;

import java.security.KeyPair;

public class Drone {

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
        KeyPair paillier;
        KeyPair dgk;

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

    // Check if this drone will collide with the other drone.
    // Return true if a collision will occur
    public boolean willCollide(Drone other) {
        return true;
    }
}
