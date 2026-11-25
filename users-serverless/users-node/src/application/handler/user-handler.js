import { toResponseDto, toUser } from '../mapper/user-dto-mapper.js';

export class UserHandler {
  #userServicePort;

  constructor(userServicePort) {
    this.#userServicePort = userServicePort;
  }

  async updateUser(id, userRequestDto) {
    const updatedUser = await this.#userServicePort.updateUser(id, toUser(userRequestDto));
    return toResponseDto(updatedUser);
  }

  async deleteUser(id) {
    await this.#userServicePort.deleteUser(id);
  }
}
