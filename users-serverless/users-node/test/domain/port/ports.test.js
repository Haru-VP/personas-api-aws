import assert from 'node:assert/strict';
import { describe, it } from 'node:test';
import { UserPersistencePort } from '../../../src/domain/repositoryport/user-persistence-port.js';
import { UserServicePort } from '../../../src/domain/serviceport/user-service-port.js';

describe('Domain ports', () => {
  for (const method of ['saveUser', 'findById', 'existsByEmail', 'deleteById']) {
    it(`UserPersistencePort.${method} requires an implementation`, async () => {
      await assert.rejects(new UserPersistencePort()[method](), /is not implemented/);
    });
  }

  for (const method of ['updateUser', 'deleteUser']) {
    it(`UserServicePort.${method} requires an implementation`, async () => {
      await assert.rejects(new UserServicePort()[method](), /is not implemented/);
    });
  }
});
