import java.util.HashMap;

public class HttpResponse {
    private String body;
    private String version;
    private HttpStatusCode code;
    private HashMap<String, String> headers;

    public HttpResponse(String version, HttpStatusCode code) {
        this.version = version;
        this.code = code;
        this.headers = new HashMap<>();
        this.body = new String();
    }

    public HttpResponse(String version, HttpStatusCode code, HashMap<String, String> headers) {
        this.version = version;
        this.code = code;
        this.headers = new HashMap<>(headers);
        this.body = new String();
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

    public void setBody(String body) {
        this.body = body;
    }

    public void setHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(String.format("%s %s %s\r\n", this.version, this.code.getCode(), this.code.toString()));
        for (String key : this.headers.keySet()) {
            sb.append(String.format("%s: %s\r\n", key, this.headers.get(key)));
        }
        sb.append("\r\n" + this.body);

        return sb.toString();
    }
}
