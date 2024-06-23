public enum HttpStatusCode {
    OK(200),
    Created(201),
    NotFound(404),
    MethodNotAllowed(405),
    InternalServerError(500);

    private int code;

    private HttpStatusCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String toString() {
        switch (this) {
            case OK:
                return "OK";
            case Created:
                return "Created";
            case NotFound:
                return "Not Found";
            case MethodNotAllowed:
                return "Method Not Allowed";
            case InternalServerError:
                return "Internal Server Error";        
            default:
                return "Error";
        }
    }
}