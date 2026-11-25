import { User } from '../../domain/model/user.js';

export const toUser = (userRequestDto) =>
  new User({ id: undefined, name: userRequestDto.name, email: userRequestDto.email });

export const toResponseDto = (user) => ({ id: user.id, name: user.name, email: user.email });
