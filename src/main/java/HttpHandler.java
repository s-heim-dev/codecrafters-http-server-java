public class HttpHandler {
    HttpRequest request;
    HttpResponse response;
    String target;

    public HttpHandler(HttpRequest request, HttpResponse response) {
        this.request = request;
        this.response = response;
        this.target = request.getTarget();
    }

    public void handle() {
        if (this.target.equals("/")) {
            response.setStatus(HttpStatusCode.OK);
        }
        else if (this.target.startsWith("/echo")) {
            this.handleEcho();
        }
        else if (this.target.startsWith("/user-agent")) {
            this.handlerUserAgent();
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

    private void handlerUserAgent() {
        String agent = this.request.getHeader("User-Agent");

        response.setBody(agent);
        response.setHeader("Content-Type", "text/plain");
        response.setHeader("Content-Length", String.valueOf(agent.length()));
        response.setStatus(HttpStatusCode.OK);
    }
}
