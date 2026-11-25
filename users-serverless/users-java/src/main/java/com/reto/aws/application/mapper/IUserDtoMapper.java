package com.reto.aws.application.mapper;

import com.reto.aws.application.dto.request.UserRequestDto;
import com.reto.aws.application.dto.response.UserResponseDto;
import com.reto.aws.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface IUserDtoMapper {

    @Mapping(target = "id", ignore = true)
    User toUser(UserRequestDto userRequestDto);

    UserResponseDto toResponseDto(User user);
}
