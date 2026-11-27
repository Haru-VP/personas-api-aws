import { InvalidUserError } from '../../domain/exception/invalid-user-error.js';
import { UserAlreadyExistsError } from '../../domain/exception/user-already-exists-error.js';
import { UserNotFoundError } from '../../domain/exception/user-not-found-error.js';
import { InvalidRequestError } from '../exception/invalid-request-error.js';

const HTTP_BAD_REQUEST = 400;
const HTTP_NOT_FOUND = 404;
const HTTP_CONFLICT = 409;
const HTTP_INTERNAL_ERROR = 500;
const INTERNAL_ERROR_MESSAGE = 'Error interno del servidor';

const STATUS_BY_ERROR = new Map([
  [InvalidUserError, HTTP_BAD_REQUEST],
  [InvalidRequestError, HTTP_BAD_REQUEST],
  [UserNotFoundError, HTTP_NOT_FOUND],
  [UserAlreadyExistsError, HTTP_CONFLICT],
]);

export class ApiExceptionHandler {
  #responseFactory;

  constructor(responseFactory) {
    this.#responseFactory = responseFactory;
  }

  toResponse(error) {
    const statusCode = STATUS_BY_ERROR.get(error?.constructor);
    if (statusCode) {
      return this.#responseFactory.error(statusCode, error.message);
    }
    console.error('Unexpected error', error);
    return this.#responseFactory.error(HTTP_INTERNAL_ERROR, INTERNAL_ERROR_MESSAGE);
  }
}
