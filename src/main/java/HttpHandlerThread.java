import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPOutputStream;

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

            if (this.response.hasBody()) {
                output.write(this.response.getBody());
            }  
            output.close();
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

    private byte[] compress(String input) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        GZIPOutputStream gzipStream = new GZIPOutputStream(byteStream);
        OutputStreamWriter output = new OutputStreamWriter(gzipStream);
        output.write(input);
        output.close();

        return byteStream.toByteArray();
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
        String body = this.request.getTarget().replace("/echo/", "");
        String encodings = this.request.getHeader("Accept-Encoding");

        if (encodings != null) {
            List<String> encodingsAsList = new ArrayList<>(Arrays.asList(encodings.split(", ")));
            if (encodingsAsList.contains("gzip")) {
                try {
                    response.setBody(this.compress(body));
                    response.setHeader("Content-Length", response.getContentLength());
                }
                catch (IOException ex) {
                    response.setStatus(HttpStatusCode.InternalServerError);
                    return;
                }
                response.setHeader("Content-Encoding", "gzip");
            }
        }
        else {
            response.setBody(body.getBytes());
            response.setHeader("Content-Length", response.getContentLength());
        }

        response.setHeader("Content-Type", "text/plain");
        response.setStatus(HttpStatusCode.OK);
    }

    private void handleUserAgent() {
        String agent = this.request.getHeader("User-Agent");

        response.setBody(agent.getBytes());
        response.setHeader("Content-Type", "text/plain");
        response.setHeader("Content-Length", response.getContentLength());
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

        response.setBody(content.getBytes());
        response.setHeader("Content-Type", "application/octet-stream");
        response.setHeader("Content-Length", response.getContentLength());
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
