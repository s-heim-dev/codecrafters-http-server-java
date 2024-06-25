import java.util.HashMap;

public class HttpResponse {
    private byte[] body;
    private String version;
    private HttpStatusCode code;
    private HashMap<String, String> headers;

    public HttpResponse(String version, HttpStatusCode code) {
        this.version = version;
        this.code = code;
        this.headers = new HashMap<>();
    }

    public HttpResponse(String version, HttpStatusCode code, HashMap<String, String> headers) {
        this.version = version;
        this.code = code;
        this.headers = new HashMap<>(headers);
    }

    public HttpResponse(HttpStatusCode code, HashMap<String, String> headers) {
        this("HTTP/1.1", code, headers);
    }

    public HttpResponse(HttpStatusCode code) {
        this("HTTP/1.1", code);
    }

    public HttpResponse() {
        this(HttpStatusCode.InternalServerError);
    }

    public void setStatus(HttpStatusCode code) {
        this.code = code;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public void setHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public boolean hasBody() {
        return this.body != null && this.body.length > 0;
    }

    public byte[] getBody() {
        return this.body;
    }

    public String getContentLength() {
        if (this.hasBody()) {
            return String.valueOf(this.body.length);
        }
        return null;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(String.format("%s %s %s\r\n", this.version, this.code.getCode(), this.code.toString()));
        for (String key : this.headers.keySet()) {
            sb.append(String.format("%s: %s\r\n", key, this.headers.get(key)));
        }
        sb.append("\r\n");

        return sb.toString();
    }
}
