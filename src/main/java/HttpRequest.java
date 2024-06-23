import java.util.HashMap;

public class HttpRequest {
    private String method = new String();
    private String target = new String();
    private String version = new String();
    private String body = new String();
    private HashMap<String, String> headers = new HashMap<>();

    public HttpRequest(String request) {       
        String[] lines = request.split("(\r\n)");
        String[] requestLines = lines[0].split(" ");
        boolean hasBody = !request.endsWith("\r\n\r\n");
        this.method = requestLines[0];

        if (requestLines.length > 1) {
            this.target = requestLines[1];
            if (requestLines.length == 3) {
                this.version = requestLines[2];
            }
        }

        for (int i = 1; (hasBody && i < lines.length - 1) || (i < lines.length); i++) {
            String[] line = lines[i].split("(: )");
            this.headers.put(line[0], line[1]);
        }

        if (hasBody) {
            this.body = lines[lines.length - 1];
        }
    }

    public HttpRequest(String method, String target, String version, HashMap<String, String> headers) {
        this.method = method;
        this.target = target;
        this.version = version;
        this.headers = new HashMap<>(headers);
    }

    public String getMethod() {
        return this.method;
    }

    public String getTarget() {
        return this.target;
    }

    public String getVersion() {
        return this.version;
    }

    public String getBody() {
        return this.body;
    }

    public String getHeader(String key) {
        return this.headers.get(key);
    }

    public boolean hasHeader(String key) {
        return this.headers.containsKey(key);
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(String.format("%s %s %s\r\n", this.method, this.target, this.version));
        for (String key : this.headers.keySet()) {
            sb.append(String.format("%s: %s\r\n", key, this.headers.get(key)));
        }

        return sb.toString();
    }
}
