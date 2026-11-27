import { User } from '../../../../src/domain/model/user.js';
import { UserPersistencePort } from '../../../../src/domain/repositoryport/user-persistence-port.js';

const SEED_USERS = [
  new User({ id: '1', name: 'Ana Gómez', email: 'ana.gomez@mail.com' }),
  new User({ id: '2', name: 'Carlos Ruiz', email: 'carlos.ruiz@mail.com' }),
  new User({ id: '3', name: 'Laura Díaz', email: 'laura.diaz@mail.com' }),
];

export class InMemoryUserAdapter extends UserPersistencePort {
  #users = new Map(SEED_USERS.map((user) => [user.id, user]));

  async saveUser(user) {
    this.#users.set(user.id, user);
    return user;
  }

  async findById(id) {
    return this.#users.get(id) ?? null;
  }

  async existsByEmail(email) {
    return [...this.#users.values()].some((user) => user.email === email);
  }

  async deleteById(id) {
    this.#users.delete(id);
  }
}
