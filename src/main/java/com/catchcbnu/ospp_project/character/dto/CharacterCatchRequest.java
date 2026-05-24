package com.catchcbnu.ospp_project.character.dto;

public record CharacterCatchRequest(
        Long userId,
        String characterCode,
        String characterName,
        String grade
) {
}