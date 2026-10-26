package com.server.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.server.server.common.Result;
import com.server.server.entity.Favorite;
import com.server.server.entity.Music;

import java.util.List;

public interface FavoriteService extends IService<Favorite> {
    Result<String> toggleFavorite(Long userId, Long musicId);
    List<Music> getUserFavorites(Long userId);
    boolean isFavorited(Long userId, Long musicId);
}
