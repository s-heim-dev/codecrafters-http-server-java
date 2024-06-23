public enum HttpStatusCode {
    OK(200),
    NotFound(404),
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
            case NotFound:
                return "Not Found";
            case InternalServerError:
                return "Internal Server Error";        
            default:
                return "Error";
        }
    }
}