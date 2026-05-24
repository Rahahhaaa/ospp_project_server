package com.catchcbnu.ospp_project.exp.contorller;

import com.catchcbnu.ospp_project.exp.dto.ExpEventRequest;
import com.catchcbnu.ospp_project.exp.dto.ExpEventResponse;
import com.catchcbnu.ospp_project.exp.service.ExpService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exp")
public class ExpController {

    private final ExpService expService;

    public ExpController(ExpService expService) {
        this.expService = expService;
    }

    @PostMapping("/events")
    public ExpEventResponse addExp(@RequestBody ExpEventRequest request) {
        return expService.addExp(request);
    }
}