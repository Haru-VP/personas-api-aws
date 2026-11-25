const HTTP_OK = 200;
const HTTP_NO_CONTENT = 204;
const JSON_HEADERS = Object.freeze({ 'Content-Type': 'application/json; charset=utf-8' });

const jsonResponse = (statusCode, body) => ({
  statusCode,
  headers: { ...JSON_HEADERS },
  body: JSON.stringify(body),
});

export class HttpResponseFactory {
  ok(body) {
    return jsonResponse(HTTP_OK, body);
  }

  noContent() {
    return { statusCode: HTTP_NO_CONTENT };
  }

  error(statusCode, message) {
    return jsonResponse(statusCode, { message });
  }
}
