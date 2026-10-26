package com.server.server.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.server.server.common.Result;
import com.server.server.entity.Music;

import java.util.List;

public interface MusicService extends IService<Music> {
    Page<Music> searchMusic(String keyword, String style, Integer status, int current, int size);
    List<String> getAllStyles();
    List<Music> getRecommendedMusic(Long userId, int limit);
}
