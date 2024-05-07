import security.misc.HomomorphicException;
import security.paillier.PaillierCipher;
import security.socialistmillionaire.alice_joye;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class PathsAlice {
    private final int port;

    public PathsAlice(int port) {
        this.port = port;
    }

    public static void main(String[] args){

        PathsAlice pathsalice = new PathsAlice(9200);

        alice_joye alice = new alice_joye();
        String my_path = new File("ownroutefile.txt").toString();
        List<BigIntPoint> alice_route = CleartextPathsComparison.read_all_paths(my_path);
        List<BigIntPoint> alices_encrypted_route = new ArrayList<>();
        List<BigIntPoint> bobs_route;


        try {

            Socket socket = new Socket ("127.0.0.1", pathsalice.port);
            alice.set_socket(socket);
            alice.receivePublicKeys();
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            Object object = input.readObject();
            bobs_route = (ArrayList<BigIntPoint>) object;
            BigInteger encryptedzero = PaillierCipher.encrypt(0, alice.getPaillierPublicKey());

            for (BigIntPoint bigIntPoint : alice_route) {
                BigInteger alicex = PaillierCipher.encrypt(bigIntPoint.x.longValue(), alice.getPaillierPublicKey());
                BigInteger alicey = PaillierCipher.encrypt(bigIntPoint.y.longValue(), alice.getPaillierPublicKey());

                BigIntPoint point = new BigIntPoint(alicex, alicey);
                alices_encrypted_route.add(point);
            }

            EncryptedPathsComparison testing = new EncryptedPathsComparison(alice, alice.getPaillierPublicKey());
            List<Integer> result = testing.encryptedWhereIntersection(alices_encrypted_route,bobs_route,alice.getPaillierPublicKey(), encryptedzero);
            System.out.println(result);
        }
        catch (IOException | ClassNotFoundException | HomomorphicException e){
            e.printStackTrace();
        }
    }
}
