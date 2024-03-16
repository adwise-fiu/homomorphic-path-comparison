import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import security.misc.HomomorphicException;
import security.socialistmillionaire.bob;


/*This had some issues with variable calls from OnSegment, might
be faster if it can be made to work.
 */
public class PreconstructedBob implements Runnable {
    private final int port;
    private static ServerSocket bob_socket = null;
    private static Socket bob_client = null;
    private final bob thisguy;

    public PreconstructedBob(bob thisguy, int port) {
        this.thisguy = thisguy;
        this.port = port;
    }

    public void run() {
        try {
            bob_socket = new ServerSocket(this.port);
            bob_client = bob_socket.accept();
            thisguy.set_socket(bob_client);
            thisguy.sendPublicKeys();

            for (int k =0; k<4; k++){
                for (int j = 0; j < 4; j++) {
                    for (int i = 0; i < 2; i++) {
                        thisguy.multiplication();
                    }

                    for (int i = 0; i < 2; i++) {
                        thisguy.Protocol2();
                    }
                }

                int count =0;
                for (int i = 0; i <12; i++) {
                    thisguy.Protocol2();
                    count++;
                    System.out.println(count);
                }
            }

        } catch (IOException | ClassNotFoundException | HomomorphicException a) {
            a.printStackTrace();
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