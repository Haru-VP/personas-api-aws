import assert from 'node:assert/strict';
import { beforeEach, describe, it, mock } from 'node:test';
import { toResponseDto, toUser } from '../../../src/application/mapper/user-dto-mapper.js';
import { UserHandler } from '../../../src/application/handler/user-handler.js';
import { UserNotFoundError } from '../../../src/domain/exception/user-not-found-error.js';
import { User } from '../../../src/domain/model/user.js';

describe('UserHandler', () => {
  let servicePort;
  let handler;

  beforeEach(() => {
    servicePort = {
      updateUser: mock.fn(async (id, user) => new User({ id, name: user.name, email: user.email })),
      deleteUser: mock.fn(async () => undefined),
    };
    handler = new UserHandler(servicePort);
  });

  it('updateUser maps the request to the domain and returns the response DTO', async () => {
    const response = await handler.updateUser('1', { name: 'Ana', email: 'ana@mail.com', id: 'ignorado' });

    const [id, sentToDomain] = servicePort.updateUser.mock.calls[0].arguments;
    assert.equal(id, '1');
    assert.deepEqual(sentToDomain, new User({ id: undefined, name: 'Ana', email: 'ana@mail.com' }));
    assert.deepEqual(response, { id: '1', name: 'Ana', email: 'ana@mail.com' });
  });

  it('deleteUser delegates to the service port', async () => {
    await handler.deleteUser('1');

    assert.equal(servicePort.deleteUser.mock.calls[0].arguments[0], '1');
  });

  it('propagates domain errors', async () => {
    servicePort.deleteUser.mock.mockImplementation(async (id) => {
      throw new UserNotFoundError(id);
    });

    await assert.rejects(handler.deleteUser('999'), UserNotFoundError);
  });
});

describe('userDtoMapper', () => {
  it('toUser ignores any id sent by the client', () => {
    assert.equal(toUser({ id: 'x', name: 'Ana', email: 'ana@mail.com' }).id, undefined);
  });

  it('toResponseDto exposes only id, name and email', () => {
    const user = new User({ id: '1', name: 'Ana', email: 'ana@mail.com' });

    assert.deepEqual(Object.keys(toResponseDto(user)), ['id', 'name', 'email']);
  });
});
