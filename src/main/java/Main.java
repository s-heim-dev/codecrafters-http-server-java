import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        String directory = "/tmp/";

        if (args.length > 0) {
            if (args[0].equals("--directory") && args.length >= 2) {
                directory = args[1];
            }
        }

        try {
            serverSocket = new ServerSocket(4221);
            serverSocket.setReuseAddress(true);

            while(true) {
                Socket client = serverSocket.accept();
                System.out.println("accepted new connection");
                
                HttpHandlerThread thread = new HttpHandlerThread(client, directory);
                thread.start();
            }            
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
