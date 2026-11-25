import assert from 'node:assert/strict';
import { beforeEach, describe, it } from 'node:test';
import { User } from '../../../../src/domain/model/user.js';
import { InMemoryUserAdapter } from '../../../../src/infrastructure/persistence/adapter/in-memory-user-adapter.js';

describe('InMemoryUserAdapter', () => {
  let adapter;

  beforeEach(() => {
    adapter = new InMemoryUserAdapter();
  });

  it('findById devuelve un usuario precargado', async () => {
    assert.equal((await adapter.findById('1')).name, 'Ana Gómez');
  });

  it('findById devuelve null si no existe', async () => {
    assert.equal(await adapter.findById('999'), null);
  });

  it('saveUser reemplaza el usuario existente', async () => {
    const updated = new User({ id: '2', name: 'Carlos R.', email: 'carlos.r@mail.com' });

    assert.equal(await adapter.saveUser(updated), updated);
    assert.equal(await adapter.findById('2'), updated);
  });

  it('existsByEmail indica si el email está registrado', async () => {
    assert.equal(await adapter.existsByEmail('laura.diaz@mail.com'), true);
    assert.equal(await adapter.existsByEmail('nadie@mail.com'), false);
  });

  it('deleteById elimina el usuario', async () => {
    await adapter.deleteById('3');

    assert.equal(await adapter.findById('3'), null);
  });
});
