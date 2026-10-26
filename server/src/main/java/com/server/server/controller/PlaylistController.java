package com.server.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.server.server.common.Result;
import com.server.server.entity.Music;
import com.server.server.entity.Playlist;
import com.server.server.service.PlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/playlist")
public class PlaylistController {

    @Autowired
    private PlaylistService playlistService;

    @PostMapping("/create")
    public Result<String> create(@RequestBody Playlist playlist) {
        playlistService.save(playlist);
        return Result.success("Playlist created");
    }

    @GetMapping("/user/{userId}")
    public Result<List<Playlist>> getUserPlaylists(@PathVariable Long userId) {
        return Result.success(playlistService.list(new LambdaQueryWrapper<Playlist>()
                .eq(Playlist::getUserId, userId)));
    }

    @PostMapping("/add")
    public Result<String> addMusic(@RequestParam Long playlistId, @RequestParam Long musicId) {
        return playlistService.addMusicToPlaylist(playlistId, musicId);
    }

    @DeleteMapping("/remove")
    public Result<String> removeMusic(@RequestParam Long playlistId, @RequestParam Long musicId) {
        return playlistService.removeMusicFromPlaylist(playlistId, musicId);
    }

    @GetMapping("/{playlistId}/music")
    public Result<List<Music>> getMusic(@PathVariable Long playlistId) {
        return Result.success(playlistService.getMusicByPlaylistId(playlistId));
    }

    @DeleteMapping("/clear/{playlistId}")
    public Result<String> clear(@PathVariable Long playlistId) {
        return playlistService.clearPlaylist(playlistId);
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        playlistService.removeById(id);
        return Result.success("Playlist deleted");
    }
}
