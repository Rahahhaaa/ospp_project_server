package com.catchcbnu.ospp_project.character.dto;

import java.time.LocalDateTime;

public record CharacterResponse(
        Long characterId,
        String characterCode,
        String characterName,
        String grade,
        LocalDateTime acquiredAt
) {
}