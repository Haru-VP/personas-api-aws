export class UserNotFoundError extends Error {
  constructor(id) {
    super(`No existe un usuario con id ${id}`);
    this.name = 'UserNotFoundError';
  }
}
