import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    static String readStringFromStream(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        StringBuilder sb = new StringBuilder();
        Reader reader = new BufferedReader(inputStreamReader);
        
        int c = 0;
        int i = 0;
        int[] lastAdded = { 0, 0, 0, 0 };
        while ((c = reader.read()) != -1) {
            sb.append((char) c);
            lastAdded[i % 4] = c;
            i += 1;

            if ((lastAdded[0] == 13 && lastAdded[1] == 10 && lastAdded[2] == 13 && lastAdded[3] == 10) 
                || (lastAdded[0] == 10 && lastAdded[1] == 13 && lastAdded[2] == 10 && lastAdded[3] == 13)) {
                break;
            }
        }

        return sb.toString();
    }



    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        try {
            serverSocket = new ServerSocket(4221);
            // Since the tester restarts your program quite often, setting SO_REUSEADDR
            // ensures that we don't run into 'Address already in use' errors
            serverSocket.setReuseAddress(true);
            clientSocket = serverSocket.accept(); // Wait for connection from client.
            System.out.println("accepted new connection");
            
            InputStream input = clientSocket.getInputStream();
            String inputString = Main.readStringFromStream(input);

            HttpRequest request = new HttpRequest(inputString);
            HttpResponse response = new HttpResponse();

            HttpHandler handler = new HttpHandler(request, response);
            handler.handle();

            OutputStream output = clientSocket.getOutputStream();
            output.write(response.toString().getBytes());
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
