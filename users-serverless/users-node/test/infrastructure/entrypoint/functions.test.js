import assert from 'node:assert/strict';
import { describe, it } from 'node:test';
import { beans } from '../../../src/infrastructure/configuration/bean-configuration.js';
import { handler as deleteUser } from '../../../src/infrastructure/entrypoint/delete-user-function.js';
import { buildUpdateUserFunction, handler as updateUser } from '../../../src/infrastructure/entrypoint/update-user-function.js';

const eventFor = (id, body) => ({ pathParameters: id === undefined ? undefined : { id }, body });

describe('updateUser (PUT /users/{id})', () => {
  it('updates an existing user and returns 200', async () => {
    const response = await updateUser(eventFor('1', '{"name":"Ana G.","email":"ana.g@mail.com"}'));

    assert.equal(response.statusCode, 200);
    assert.deepEqual(JSON.parse(response.body), { id: '1', name: 'Ana G.', email: 'ana.g@mail.com' });
  });

  it('returns 404 when the user does not exist', async () => {
    const response = await updateUser(eventFor('999', '{"name":"X","email":"x@mail.com"}'));

    assert.equal(response.statusCode, 404);
  });

  it('returns 409 when the email belongs to another user', async () => {
    const response = await updateUser(eventFor('2', '{"name":"Carlos","email":"LAURA.DIAZ@mail.com"}'));

    assert.equal(response.statusCode, 409);
  });

  it('returns 400 when the body is missing', async () => {
    assert.equal((await updateUser(eventFor('1', undefined))).statusCode, 400);
  });

  it('returns 500 on an unexpected error', async (t) => {
    t.mock.method(console, 'error', () => undefined);
    const failingHandler = { updateUser: async () => Promise.reject(new TypeError('failure')) };
    const fn = buildUpdateUserFunction({ ...beans, userHandler: failingHandler });

    assert.equal((await fn(eventFor('1', '{"name":"Ana","email":"ana@mail.com"}'))).statusCode, 500);
  });
});

describe('deleteUser (DELETE /users/{id})', () => {
  it('deletes an existing user and returns 204', async () => {
    assert.equal((await deleteUser(eventFor('3'))).statusCode, 204);
  });

  it('returns 404 when the user no longer exists', async () => {
    assert.equal((await deleteUser(eventFor('3'))).statusCode, 404);
  });

  it('returns 400 when the id is missing', async () => {
    assert.equal((await deleteUser(eventFor(undefined))).statusCode, 400);
  });
});
