import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    



    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(4221);
            // Since the tester restarts your program quite often, setting SO_REUSEADDR
            // ensures that we don't run into 'Address already in use' errors
            serverSocket.setReuseAddress(true);

            while(true) {
                Socket client = serverSocket.accept();
                System.out.println("accepted new connection");
                
                HttpHandlerThread thread = new HttpHandlerThread(client);
                thread.start();
            }            
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
