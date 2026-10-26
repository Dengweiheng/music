package com.server.server.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.server.common.Result;
import com.server.server.entity.Music;
import com.server.server.service.MusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/music")
public class MusicController {

    @Autowired
    private MusicService musicService;

    @Autowired
    private com.server.server.service.PlayHistoryService playHistoryService;

    // Relative path for storage (can be configured in yml)
    private final String uploadPath = "uploads/";

    @GetMapping("/list")
    public Result<Page<Music>> list(@RequestParam(required = false) String keyword,
                                   @RequestParam(required = false) String style,
                                   @RequestParam(required = false) Integer status,
                                   @RequestParam(defaultValue = "1") int current,
                                   @RequestParam(defaultValue = "10") int size) {
        return Result.success(musicService.searchMusic(keyword, style, status, current, size));
    }

    @GetMapping("/detail/{id}")
    public Result<Music> getDetail(@PathVariable Long id) {
        return Result.success(musicService.getById(id));
    }

    @GetMapping("/styles")
    public Result<List<String>> getStyles() {
        return Result.success(musicService.getAllStyles());
    }

    @GetMapping("/recommend")
    public Result<List<Music>> recommend(@RequestParam(required = false) Long userId,
                                        @RequestParam(defaultValue = "10") int limit) {
        return Result.success(musicService.getRecommendedMusic(userId, limit));
    }

    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("File is empty");
        }
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File dest = new File(new File(uploadPath).getAbsolutePath(), fileName);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        try {
            file.transferTo(dest);
            return Result.success("/uploads/" + fileName);
        } catch (IOException e) {
            return Result.error("Upload failed: " + e.getMessage());
        }
    }

    @PostMapping("/admin/upload")
    public Result<String> uploadFileAdmin(@RequestParam("file") MultipartFile file) {
        return upload(file);
    }

    @PostMapping("/admin/save")
    public Result<String> save(@RequestBody Music music) {
        musicService.save(music);
        return Result.success("Music saved");
    }

    @PostMapping("/user/save")
    public Result<String> userSave(@RequestBody Music music) {
        music.setStatus(0); // Always pending when user uploads
        musicService.save(music);
        return Result.success("Music submitted for audit");
    }

    @PutMapping("/admin/update")
    public Result<String> update(@RequestBody Music music) {
        musicService.updateById(music);
        return Result.success("Music updated");
    }

    @DeleteMapping("/admin/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        musicService.removeById(id);
        return Result.success("Music deleted");
    }

    @PutMapping("/admin/audit/{id}/{status}")
    public Result<String> audit(@PathVariable Long id, @PathVariable Integer status) {
        Music music = musicService.getById(id);
        if (music != null) {
            music.setStatus(status);
            musicService.updateById(music);
            return Result.success("Audited");
        }
        return Result.error("Music not found");
    }

    @PostMapping("/play/{id}")
    public Result<String> play(@PathVariable Long id, @RequestParam(required = false) Long userId) {
        playHistoryService.recordPlay(userId, id);
        return Result.success("Play recorded");
    }
}
