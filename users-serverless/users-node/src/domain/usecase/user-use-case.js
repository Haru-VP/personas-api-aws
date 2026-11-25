import { InvalidUserError } from '../exception/invalid-user-error.js';
import { UserAlreadyExistsError } from '../exception/user-already-exists-error.js';
import { UserNotFoundError } from '../exception/user-not-found-error.js';
import { User } from '../model/user.js';
import { UserServicePort } from '../serviceport/user-service-port.js';

const EMAIL_PATTERN = /^[^@\s]+@[^@\s.]+(?:\.[^@\s.]+)+$/;
const NAME_REQUIRED_MESSAGE = 'El nombre es obligatorio';
const INVALID_EMAIL_MESSAGE = 'El email no es válido';
const ID_REQUIRED_MESSAGE = 'El id es obligatorio';

const isBlank = (value) => typeof value !== 'string' || value.trim() === '';

const requireId = (id) => {
  if (isBlank(id)) {
    throw new InvalidUserError(ID_REQUIRED_MESSAGE);
  }
  return id;
};

const normalizeName = (name) => {
  if (isBlank(name)) {
    throw new InvalidUserError(NAME_REQUIRED_MESSAGE);
  }
  return name.trim();
};

const normalizeEmail = (email) => {
  if (isBlank(email)) {
    throw new InvalidUserError(INVALID_EMAIL_MESSAGE);
  }
  const normalized = email.trim().toLowerCase();
  if (!EMAIL_PATTERN.test(normalized)) {
    throw new InvalidUserError(INVALID_EMAIL_MESSAGE);
  }
  return normalized;
};

export class UserUseCase extends UserServicePort {
  #userPersistencePort;

  constructor(userPersistencePort) {
    super();
    this.#userPersistencePort = userPersistencePort;
  }

  async updateUser(id, user) {
    const userId = requireId(id);
    const name = normalizeName(user.name);
    const email = normalizeEmail(user.email);
    const current = await this.#findExisting(userId);
    if (email !== current.email && (await this.#userPersistencePort.existsByEmail(email))) {
      throw new UserAlreadyExistsError(email);
    }
    return this.#userPersistencePort.saveUser(new User({ id: userId, name, email }));
  }

  async deleteUser(id) {
    const userId = requireId(id);
    await this.#findExisting(userId);
    await this.#userPersistencePort.deleteById(userId);
  }

  async #findExisting(id) {
    const user = await this.#userPersistencePort.findById(id);
    if (!user) {
      throw new UserNotFoundError(id);
    }
    return user;
  }
}
