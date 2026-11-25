import assert from 'node:assert/strict';
import { describe, it } from 'node:test';
import { HttpRequestReader } from '../../../../src/infrastructure/entrypoint/http/http-request-reader.js';
import { HttpResponseFactory } from '../../../../src/infrastructure/entrypoint/http/http-response-factory.js';
import { InvalidRequestError } from '../../../../src/infrastructure/exception/invalid-request-error.js';

const VALID_JSON = '{"name":"Ana","email":"ana@mail.com"}';

describe('HttpRequestReader', () => {
  const reader = new HttpRequestReader();

  it('readBody devuelve el objeto del JSON', () => {
    assert.deepEqual(reader.readBody({ body: VALID_JSON }), { name: 'Ana', email: 'ana@mail.com' });
  });

  it('readBody decodifica un body en base64', () => {
    const event = { body: Buffer.from(VALID_JSON).toString('base64'), isBase64Encoded: true };

    assert.deepEqual(reader.readBody(event), { name: 'Ana', email: 'ana@mail.com' });
  });

  for (const body of [undefined, null, '', '   ', 'null', '[1,2]', '"texto"', '42', '{no es json']) {
    it(`readBody rechaza el body ${String(body)}`, () => {
      assert.throws(() => reader.readBody({ body }), InvalidRequestError);
    });
  }

  it('readPathParameter devuelve el valor o undefined', () => {
    assert.equal(reader.readPathParameter({ pathParameters: { id: '1' } }, 'id'), '1');
    assert.equal(reader.readPathParameter({}, 'id'), undefined);
  });
});

describe('HttpResponseFactory', () => {
  const factory = new HttpResponseFactory();

  it('ok devuelve 200 con JSON', () => {
    assert.deepEqual(factory.ok({ id: '1' }), {
      statusCode: 200,
      headers: { 'Content-Type': 'application/json; charset=utf-8' },
      body: '{"id":"1"}',
    });
  });

  it('noContent devuelve 204 sin body', () => {
    assert.deepEqual(factory.noContent(), { statusCode: 204 });
  });

  it('error devuelve el código y el mensaje', () => {
    const response = factory.error(404, 'No existe');

    assert.equal(response.statusCode, 404);
    assert.equal(response.body, '{"message":"No existe"}');
  });
});
