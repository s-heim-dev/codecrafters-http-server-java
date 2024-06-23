import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.Socket;

public class HttpHandlerThread extends Thread {
    Socket client;
    HttpRequest request;
    HttpResponse response;
    String target;

    public HttpHandlerThread(Socket client) {
        this.client = client;
    }

    public void run() {
        String inputString;
        try {
            InputStream input = this.client.getInputStream();
            inputString = this.readStringFromStream(input);
        }
        catch (IOException ex) {
            return;
        }

        this.request = new HttpRequest(inputString);
        this.response = new HttpResponse();

        this.handle();

        try {
            OutputStream output = this.client.getOutputStream();
            output.write(response.toString().getBytes());
        }
        catch (IOException ex) {
            return;
        }
    }

    private String readStringFromStream(InputStream inputStream) throws IOException {
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

    private void handle() {
        this.target = this.request.getTarget();

        if (this.target.equals("/")) {
            response.setStatus(HttpStatusCode.OK);
        }
        else if (this.target.startsWith("/echo")) {
            this.handleEcho();
        }
        else if (this.target.startsWith("/user-agent")) {
            this.handleUserAgent();
        }
        else {
            response.setStatus(HttpStatusCode.NotFound);
        }
    }

    private void handleEcho() {
        String arg = this.target.replace("/echo/", "");

        response.setBody(arg);
        response.setHeader("Content-Type", "text/plain");
        response.setHeader("Content-Length", String.valueOf(arg.length()));
        response.setStatus(HttpStatusCode.OK);
    }

    private void handleUserAgent() {
        String agent = this.request.getHeader("User-Agent");

        response.setBody(agent);
        response.setHeader("Content-Type", "text/plain");
        response.setHeader("Content-Length", String.valueOf(agent.length()));
        response.setStatus(HttpStatusCode.OK);
    }
}
