import assert from 'node:assert/strict';
import { beforeEach, describe, it, mock } from 'node:test';
import { InvalidUserError } from '../../../src/domain/exception/invalid-user-error.js';
import { UserAlreadyExistsError } from '../../../src/domain/exception/user-already-exists-error.js';
import { UserNotFoundError } from '../../../src/domain/exception/user-not-found-error.js';
import { User } from '../../../src/domain/model/user.js';
import { UserUseCase } from '../../../src/domain/usecase/user-use-case.js';

const EXISTING_USER = new User({ id: '1', name: 'Ana', email: 'ana@mail.com' });

describe('UserUseCase', () => {
  let persistencePort;
  let useCase;

  beforeEach(() => {
    persistencePort = {
      findById: mock.fn(async (id) => (id === EXISTING_USER.id ? EXISTING_USER : null)),
      existsByEmail: mock.fn(async () => false),
      saveUser: mock.fn(async (user) => user),
      deleteById: mock.fn(async () => undefined),
    };
    useCase = new UserUseCase(persistencePort);
  });

  describe('updateUser', () => {
    it('normaliza los datos y guarda el usuario', async () => {
      const result = await useCase.updateUser('1', { name: ' Ana María ', email: ' Ana.Maria@Mail.com ' });

      assert.deepEqual(result, new User({ id: '1', name: 'Ana María', email: 'ana.maria@mail.com' }));
      assert.equal(persistencePort.existsByEmail.mock.calls[0].arguments[0], 'ana.maria@mail.com');
      assert.equal(persistencePort.saveUser.mock.callCount(), 1);
    });

    it('no valida duplicado cuando el email no cambia', async () => {
      await useCase.updateUser('1', { name: 'Ana', email: 'ANA@mail.com' });

      assert.equal(persistencePort.existsByEmail.mock.callCount(), 0);
      assert.equal(persistencePort.saveUser.mock.callCount(), 1);
    });

    it('lanza error si el nuevo email ya pertenece a otro usuario', async () => {
      persistencePort.existsByEmail.mock.mockImplementation(async () => true);

      await assert.rejects(
        useCase.updateUser('1', { name: 'Ana', email: 'carlos@mail.com' }),
        UserAlreadyExistsError,
      );
      assert.equal(persistencePort.saveUser.mock.callCount(), 0);
    });

    it('lanza error si el usuario no existe', async () => {
      await assert.rejects(useCase.updateUser('999', { name: 'Ana', email: 'ana@mail.com' }), UserNotFoundError);
      assert.equal(persistencePort.saveUser.mock.callCount(), 0);
    });

    for (const id of [undefined, null, '', '   ', 7]) {
      it(`lanza error si el id es inválido (${String(id)})`, async () => {
        await assert.rejects(useCase.updateUser(id, { name: 'Ana', email: 'ana@mail.com' }), InvalidUserError);
        assert.equal(persistencePort.findById.mock.callCount(), 0);
      });
    }

    for (const name of [undefined, null, '', '   ', 42]) {
      it(`lanza error si el nombre es inválido (${String(name)})`, async () => {
        await assert.rejects(useCase.updateUser('1', { name, email: 'ana@mail.com' }), InvalidUserError);
        assert.equal(persistencePort.findById.mock.callCount(), 0);
      });
    }

    for (const email of [undefined, '', 'ana-mail.com', 'ana@mail', 'ana@@mail.com', 'ana @mail.com', 'ana@mail..com']) {
      it(`lanza error si el email es inválido (${String(email)})`, async () => {
        await assert.rejects(useCase.updateUser('1', { name: 'Ana', email }), InvalidUserError);
        assert.equal(persistencePort.findById.mock.callCount(), 0);
      });
    }
  });

  describe('deleteUser', () => {
    it('elimina el usuario existente', async () => {
      await useCase.deleteUser('1');

      assert.equal(persistencePort.deleteById.mock.calls[0].arguments[0], '1');
    });

    it('lanza error si el usuario no existe', async () => {
      await assert.rejects(useCase.deleteUser('999'), UserNotFoundError);
      assert.equal(persistencePort.deleteById.mock.callCount(), 0);
    });

    it('lanza error si el id está vacío', async () => {
      await assert.rejects(useCase.deleteUser(' '), InvalidUserError);
      assert.equal(persistencePort.findById.mock.callCount(), 0);
    });
  });
});
