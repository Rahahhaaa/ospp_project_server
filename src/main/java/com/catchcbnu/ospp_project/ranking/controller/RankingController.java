package com.catchcbnu.ospp_project.ranking.controller;

import com.catchcbnu.ospp_project.ranking.dto.RankingResponse;
import com.catchcbnu.ospp_project.ranking.service.RankingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rankings")
public class RankingController {

    private final RankingService rankingService;

    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @GetMapping
    public List<RankingResponse> getRanking(
            @RequestParam(defaultValue = "TOTAL") String scope,
            @RequestParam(required = false) String college,
            @RequestParam(required = false) String department
    ) {
        return rankingService.getRanking(scope, college, department);
    }
}