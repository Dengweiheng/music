package com.server.server.controller;

import com.server.server.common.Result;
import com.server.server.entity.Music;
import com.server.server.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorite")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @PostMapping("/toggle")
    public Result<String> toggle(@RequestParam Long userId, @RequestParam Long musicId) {
        return favoriteService.toggleFavorite(userId, musicId);
    }

    @GetMapping("/list/{userId}")
    public Result<List<Music>> list(@PathVariable Long userId) {
        return Result.success(favoriteService.getUserFavorites(userId));
    }

    @GetMapping("/check")
    public Result<Boolean> check(@RequestParam Long userId, @RequestParam Long musicId) {
        return Result.success(favoriteService.isFavorited(userId, musicId));
    }
}
