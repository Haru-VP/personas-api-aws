import assert from 'node:assert/strict';
import { describe, it } from 'node:test';
import { InvalidUserError } from '../../../src/domain/exception/invalid-user-error.js';
import { UserAlreadyExistsError } from '../../../src/domain/exception/user-already-exists-error.js';
import { UserNotFoundError } from '../../../src/domain/exception/user-not-found-error.js';
import { HttpResponseFactory } from '../../../src/infrastructure/entrypoint/http/http-response-factory.js';
import { InvalidRequestError } from '../../../src/infrastructure/exception/invalid-request-error.js';
import { ApiExceptionHandler } from '../../../src/infrastructure/exceptionhandler/api-exception-handler.js';

describe('ApiExceptionHandler', () => {
  const handler = new ApiExceptionHandler(new HttpResponseFactory());

  const cases = [
    [new InvalidUserError('inválido'), 400],
    [new InvalidRequestError('sin body'), 400],
    [new UserNotFoundError('9'), 404],
    [new UserAlreadyExistsError('ana@mail.com'), 409],
  ];

  for (const [error, expectedStatus] of cases) {
    it(`${error.name} se traduce a ${expectedStatus}`, () => {
      assert.equal(handler.toResponse(error).statusCode, expectedStatus);
    });
  }

  for (const error of [new TypeError('detalle interno'), undefined]) {
    it(`un error inesperado (${String(error)}) devuelve 500 sin exponer detalles y se registra`, (t) => {
      const logError = t.mock.method(console, 'error', () => undefined);

      const response = handler.toResponse(error);

      assert.equal(response.statusCode, 500);
      assert.equal(response.body.includes('detalle interno'), false);
      assert.equal(logError.mock.callCount(), 1);
    });
  }
});
