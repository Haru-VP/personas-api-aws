package com.reto.aws.application.dto.response;

public record PersonResponseDto(
        String identificationNumber,
        String name,
        String email
) {
}
