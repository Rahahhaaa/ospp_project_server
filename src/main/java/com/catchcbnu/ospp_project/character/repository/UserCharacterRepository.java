package com.catchcbnu.ospp_project.character.repository;

import com.catchcbnu.ospp_project.character.domain.UserCharacter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserCharacterRepository extends JpaRepository<UserCharacter, Long> {

    List<UserCharacter> findByUserId(Long userId);
}