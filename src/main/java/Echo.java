public class Echo {
    static HttpResponse handle(HttpRequest request, HttpResponse response) {
        String target = request.getTarget();
        target = target.replace("/echo/", "");

        response.setBody(target);
        response.setHeader("Content-Type", "text/plain");
        response.setHeader("Content-Length", String.valueOf(target.length()));

        return response;
    }
}
