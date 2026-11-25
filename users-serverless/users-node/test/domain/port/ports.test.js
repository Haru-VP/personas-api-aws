import assert from 'node:assert/strict';
import { describe, it } from 'node:test';
import { UserPersistencePort } from '../../../src/domain/repositoryport/user-persistence-port.js';
import { UserServicePort } from '../../../src/domain/serviceport/user-service-port.js';

describe('Puertos del dominio', () => {
  for (const method of ['saveUser', 'findById', 'existsByEmail', 'deleteById']) {
    it(`UserPersistencePort.${method} exige implementación`, async () => {
      await assert.rejects(new UserPersistencePort()[method](), /no está implementado/);
    });
  }

  for (const method of ['updateUser', 'deleteUser']) {
    it(`UserServicePort.${method} exige implementación`, async () => {
      await assert.rejects(new UserServicePort()[method](), /no está implementado/);
    });
  }
});
