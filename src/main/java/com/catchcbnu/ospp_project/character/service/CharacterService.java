package com.catchcbnu.ospp_project.character.service;

import com.catchcbnu.ospp_project.character.domain.UserCharacter;
import com.catchcbnu.ospp_project.character.dto.CharacterCatchRequest;
import com.catchcbnu.ospp_project.character.dto.CharacterResponse;
import com.catchcbnu.ospp_project.character.repository.UserCharacterRepository;
import com.catchcbnu.ospp_project.exp.service.LevelEngine;
import com.catchcbnu.ospp_project.user.entity.User;
import com.catchcbnu.ospp_project.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CharacterService {

    private final UserRepository userRepository;
    private final UserCharacterRepository userCharacterRepository;
    private final LevelEngine levelEngine;

    public CharacterService(
            UserRepository userRepository,
            UserCharacterRepository userCharacterRepository,
            LevelEngine levelEngine
    ) {
        this.userRepository = userRepository;
        this.userCharacterRepository = userCharacterRepository;
        this.levelEngine = levelEngine;
    }

    @Transactional
    public CharacterResponse catchCharacter(CharacterCatchRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        UserCharacter character = new UserCharacter(
                user,
                request.characterCode(),
                request.characterName(),
                request.grade()
        );

        UserCharacter savedCharacter = userCharacterRepository.save(character);

        // 캐릭터 획득 시 경험치 30 지급
        levelEngine.applyExp(user, 30);

        return toResponse(savedCharacter);
    }

    @Transactional(readOnly = true)
    public List<CharacterResponse> getUserCharacters(Long userId) {
        return userCharacterRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private CharacterResponse toResponse(UserCharacter character) {
        return new CharacterResponse(
                character.getId(),
                character.getCharacterCode(),
                character.getCharacterName(),
                character.getGrade(),
                character.getAcquiredAt()
        );
    }
}