package com.luminabackend.utils.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GeneralErrorResponseDTO {
    private final String message;
}
