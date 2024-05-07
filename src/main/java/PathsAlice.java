import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import security.misc.HomomorphicException;
import security.paillier.PaillierCipher;
import security.socialistmillionaire.alice_joye;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class PathsAlice {
    private static final Logger logger = LogManager.getLogger(PathsAlice.class);

    private final int port;

    public PathsAlice(int port) {
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

        // Execute the program
        PathsAlice pathsalice = new PathsAlice(port);
        alice_joye alice = new alice_joye();
        String my_path = new File(input_file).toString();
        List<BigIntPoint> alice_route = CleartextPathsComparison.read_all_paths(my_path);
        List<BigIntPoint> alices_encrypted_route = new ArrayList<>();
        List<BigIntPoint> bobs_route = new ArrayList<>();

        try {
            Socket socket = new Socket (ip_address, pathsalice.port);
            alice.set_socket(socket);
            alice.receivePublicKeys();
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            Object object = input.readObject();

            if (object instanceof List<?>) {
                for (Object element : (List<?>) object) {
                    if (element instanceof BigIntPoint) {
                        bobs_route.add((BigIntPoint) element);
                    }
                }
            }
            BigInteger encrypted_zero = PaillierCipher.encrypt(0, alice.getPaillierPublicKey());

            for (BigIntPoint bigIntPoint : alice_route) {
                BigInteger alice_x = PaillierCipher.encrypt(bigIntPoint.x.longValue(), alice.getPaillierPublicKey());
                BigInteger alice_y = PaillierCipher.encrypt(bigIntPoint.y.longValue(), alice.getPaillierPublicKey());

                BigIntPoint point = new BigIntPoint(alice_x, alice_y);
                alices_encrypted_route.add(point);
            }

            EncryptedPathsComparison testing = new EncryptedPathsComparison(alice);
            List<Integer> result = testing.encryptedWhereIntersection(alices_encrypted_route, bobs_route,
                    alice.getPaillierPublicKey(), encrypted_zero);
            System.out.println(result);
        }
        catch (IOException | ClassNotFoundException | HomomorphicException e){
            logger.fatal(e.getStackTrace());
        }
    }
}
