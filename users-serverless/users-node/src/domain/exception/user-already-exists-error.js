export class UserAlreadyExistsError extends Error {
  constructor(email) {
    super(`Ya existe un usuario con email ${email}`);
    this.name = 'UserAlreadyExistsError';
  }
}
