import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import security.misc.HomomorphicException;
import security.socialistmillionaire.bob;


public class BobOperations implements Runnable {
    private final int port;
    private static ServerSocket bob_socket = null;
    private static Socket bob_client = null;
    private final bob thisguy;

    public BobOperations(bob thisguy, int port) {
        this.thisguy = thisguy;
        this.port = port;
    }

    public void run() {
        try {
            bob_socket = new ServerSocket(this.port);
            bob_client = bob_socket.accept();
            thisguy.set_socket(bob_client);
            thisguy.sendPublicKeys();


           while (true) {

               int var = 3;
               var = thisguy.readInt();

               if (var == 1) {
                   thisguy.multiplication();
               }

               if (var == 2) {
                   thisguy.Protocol2();
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