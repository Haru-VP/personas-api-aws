import { InvalidRequestError } from '../../exception/invalid-request-error.js';

const BODY_REQUIRED_MESSAGE = 'El cuerpo de la petición es obligatorio';
const INVALID_BODY_MESSAGE = 'El cuerpo de la petición no es un JSON válido';

const decode = (body, isBase64Encoded) => (isBase64Encoded ? Buffer.from(body, 'base64').toString('utf8') : body);

const parse = (json) => {
  try {
    return JSON.parse(json);
  } catch {
    throw new InvalidRequestError(INVALID_BODY_MESSAGE);
  }
};

const isJsonObject = (value) => value !== null && typeof value === 'object' && !Array.isArray(value);

export class HttpRequestReader {
  readBody(event) {
    const { body, isBase64Encoded } = event;
    if (typeof body !== 'string' || body.trim() === '') {
      throw new InvalidRequestError(BODY_REQUIRED_MESSAGE);
    }
    const value = parse(decode(body, isBase64Encoded));
    if (!isJsonObject(value)) {
      throw new InvalidRequestError(INVALID_BODY_MESSAGE);
    }
    return value;
  }

  readPathParameter(event, name) {
    return event.pathParameters?.[name];
  }
}
