package com.server.server.controller;

import com.server.server.common.Result;
import com.server.server.service.PlayHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/stats")
public class StatsController {

    @Autowired
    private PlayHistoryService playHistoryService;

    @PostMapping("/record")
    public Result<String> record(@RequestParam(required = false) Long userId, @RequestParam Long musicId) {
        playHistoryService.recordPlay(userId, musicId);
        return Result.success("Recorded");
    }

    @GetMapping("/top")
    public Result<List<Map<String, Object>>> getTop(@RequestParam(defaultValue = "10") int limit) {
        return Result.success(playHistoryService.getTopMusic(limit));
    }

    @GetMapping("/active")
    public Result<Long> getActiveCount(@RequestParam(defaultValue = "1") int days) {
        return Result.success(playHistoryService.getActiveUserCount(days));
    }

    @GetMapping("/summary")
    public Result<Map<String, Object>> getSummary() {
        return Result.success(playHistoryService.getSummary());
    }

    @GetMapping("/categories")
    public Result<List<Map<String, Object>>> getCategories() {
        return Result.success(playHistoryService.getCategoryStats());
    }
}
