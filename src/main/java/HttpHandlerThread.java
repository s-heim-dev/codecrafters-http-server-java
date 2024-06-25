import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

public class HttpHandlerThread extends Thread {
    String basePath;
    Socket client;
    HttpRequest request;
    HttpResponse response;

    public HttpHandlerThread(Socket client, String basePath) {
        this.client = client;
        this.basePath = basePath;
    }

    public void run() {
        try {
            InputStream input = this.client.getInputStream();
            this.request = this.readHttpRequestFromString(input);
            this.response = new HttpResponse();

            this.handle();

            OutputStream output = this.client.getOutputStream();
            output.write(response.toString().getBytes());
        }
        catch (IOException ex) {
            return;
        }
    }

    private HttpRequest readHttpRequestFromString(InputStream inputStream) throws IOException {
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

        HttpRequest request = new HttpRequest(sb.toString());
        int bodyLength = 0;
        if (request.getMethod().equals("POST") && request.hasHeader("Content-Length")) {
            bodyLength = Integer.valueOf(request.getHeader("Content-Length"));
        }

        sb = new StringBuilder();
        while(bodyLength > 0) {
            c = reader.read();
            sb.append((char) c);
            bodyLength--;
        }

        request.setBody(sb.toString());

        return request;
    }

    private void handle() {
        String target = this.request.getTarget();
        String method = this.request.getMethod();

        if (target.equals("/")) {
            if (method.equals("GET")) {
                response.setStatus(HttpStatusCode.OK);
            }
            else {
                response.setStatus(HttpStatusCode.MethodNotAllowed);
            }
        }
        else if (target.startsWith("/echo")) {
            if (method.equals("GET")) {
                this.handleEcho();
            }
            else {
                response.setStatus(HttpStatusCode.MethodNotAllowed);
            }
        }
        else if (target.startsWith("/user-agent")) {
            if (method.equals("GET")) {
                this.handleUserAgent();
            }
            else {
                response.setStatus(HttpStatusCode.MethodNotAllowed);
            }
        }
        else if (target.startsWith("/files")) {
            if (method.equals("GET")) {
                this.handleGetFiles();
            }
            else if (method.equals("POST")) {

                this.handlePostFiles();
            }
            else {
                response.setStatus(HttpStatusCode.MethodNotAllowed);
            }
        }
        else {
            response.setStatus(HttpStatusCode.NotFound);
        }
    }

    private void handleEcho() {
        String arg = this.request.getTarget().replace("/echo/", "");
        String encoding = this.request.getHeader("Accept-Encoding");

        if (encoding != null && encoding.equals("gzip")) {
            response.setHeader("Content-Encoding", encoding);
        }

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

    private void handleGetFiles() {
        String path = this.basePath + this.request.getTarget().replace("/files/", "");
        File file = new File(path);

        if (!file.exists() || !file.isFile()) {
            response.setStatus(HttpStatusCode.NotFound);
            return;
        }

        String content;
        try {
            content = Files.readString(Path.of(path));
        }
        catch(IOException ex) {
            response.setStatus(HttpStatusCode.InternalServerError);
            return;
        }

        response.setBody(content);
        response.setHeader("Content-Type", "application/octet-stream");
        response.setHeader("Content-Length", String.valueOf(file.length()));
        response.setStatus(HttpStatusCode.OK);
    }

    private void handlePostFiles() {
        String path = this.basePath + this.request.getTarget().replace("/files/", "");
        String content = this.request.getBody();

        File file = new File(path);

        try {
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(path);
            BufferedWriter writer = new BufferedWriter(fileWriter);

            writer.write(content);
            writer.close();
            response.setStatus(HttpStatusCode.Created);
        }
        catch (IOException ex) {
            response.setStatus(HttpStatusCode.InternalServerError);
        }

    }
}
