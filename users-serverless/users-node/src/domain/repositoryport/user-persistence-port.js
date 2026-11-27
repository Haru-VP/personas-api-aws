export class UserPersistencePort {
  async saveUser() {
    throw new Error('saveUser is not implemented');
  }

  async findById() {
    throw new Error('findById is not implemented');
  }

  async existsByEmail() {
    throw new Error('existsByEmail is not implemented');
  }

  async deleteById() {
    throw new Error('deleteById is not implemented');
  }
}
