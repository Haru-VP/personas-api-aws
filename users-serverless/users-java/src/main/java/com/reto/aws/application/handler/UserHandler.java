package com.reto.aws.application.handler;

import com.reto.aws.application.dto.request.UserRequestDto;
import com.reto.aws.application.dto.response.UserResponseDto;
import com.reto.aws.application.mapper.IUserDtoMapper;
import com.reto.aws.domain.model.User;
import com.reto.aws.domain.serviceport.IUserServicePort;

public class UserHandler {

    private final IUserServicePort userServicePort;
    private final IUserDtoMapper userDtoMapper;

    public UserHandler(IUserServicePort userServicePort, IUserDtoMapper userDtoMapper) {
        this.userServicePort = userServicePort;
        this.userDtoMapper = userDtoMapper;
    }

    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        User user = userDtoMapper.toUser(userRequestDto);
        return userDtoMapper.toResponseDto(userServicePort.createUser(user));
    }

    public UserResponseDto getUserById(String id) {
        return userDtoMapper.toResponseDto(userServicePort.getUserById(id));
    }
}
