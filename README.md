[![progress-banner](https://backend.codecrafters.io/progress/http-server/ebe7d7cd-5282-4e09-89ba-c5e3fac0dafd)](https://app.codecrafters.io/users/codecrafters-bot?r=2qF)

This is a HTTP-Server in Java based on the ["Build Your Own HTTP server" Challenge](https://app.codecrafters.io/courses/http-server/overview) by [codecrafters.io](https://codecrafters.io).

## Features

- End-Points:
  - `/echo/$param` (GET) - Return the `$param` in the body of the response  
  (supports returning `gzip`-compressed)
  - `/user-agent` (GET) - Returns the user agent use for the request
  - `/files/$path` (GET) - Gets the content of the file from the `$path`
  - `/files/$path` (POST) - Writes the request body into a file at `$path`
- Status-Codes: `OK`, `Created`, `Not Found`, `Method Not Allowed`, `Internal Server Errors`
- Headers: `Content-Type`, `Content-Length`, `Accept-Encoding`/`Content-Encoding` (supports only `gzip`)
- Supports concurrent connections