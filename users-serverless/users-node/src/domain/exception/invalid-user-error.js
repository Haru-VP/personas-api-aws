export class InvalidUserError extends Error {
  constructor(message) {
    super(message);
    this.name = 'InvalidUserError';
  }
}
