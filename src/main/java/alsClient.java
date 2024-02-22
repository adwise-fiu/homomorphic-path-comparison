import java.io.*;
import java.net.*;

public class alsClient {
    // initialize socket and input output streams
    private Socket socket = null;
    //private BufferedReader input = null;
    private DataOutputStream out = null;

    private BufferedReader input = null;

    private String string = null;


    // constructor to put ip address and port
    public alsClient(String address, int port) {
        // establish a connection

        try {
            socket = new Socket(address, port);
            System.out.println("Connected");

            // Make this read a test file
            input = new BufferedReader(new InputStreamReader(System.in));

            // sends output to the socket
            out = new DataOutputStream(
                    socket.getOutputStream());
        } catch (UnknownHostException u) {
            System.out.println(u);
            return;
        } catch (IOException i) {
            System.out.println(i);
            return;
        }

        try {
        FileReader reader = new FileReader("C:\\Users\\Allan\\IdeaProjects\\Drone Homomorphic Project\\cryptroutefile.txt");
        BufferedReader breader = new BufferedReader(reader);
        string = breader.readLine();
        out.writeUTF(string);

        } catch (java.io.IOException e) {
            System.out.println(e);
        }


        // keep reading until "Over" is input
       /* while (!line.equals("Over")) {
            try {
                line = input.readLine();
                out.writeUTF(line);
            } catch (IOException i) {
                System.out.println(i);
            }
        }*/

        // close the connection
        try {
            input.close();
            out.close();
            socket.close();
        } catch (IOException i) {
            System.out.println(i);
        }
    }
    //This is here for testing, this method will be called in a Drone
    public static void main (String[] arg){
        new alsClient("127.0.0.1", 2536);
    }
}