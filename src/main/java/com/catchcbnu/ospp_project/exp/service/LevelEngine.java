package com.catchcbnu.ospp_project.exp.service;

import com.catchcbnu.ospp_project.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class LevelEngine {

    public int requiredExpForNextLevel(int currentLevel) {
        return 100 + (currentLevel - 1) * 50;
    }

    public void applyExp(User user, int gainedExp) {
        user.addExp(gainedExp);

        while (user.getExp() >= requiredExpForNextLevel(user.getLevel())) {
            int requiredExp = requiredExpForNextLevel(user.getLevel());
            user.decreaseExp(requiredExp);
            user.levelUp();
        }
    }
}