package com.server.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.server.server.common.Result;
import com.server.server.entity.Favorite;
import com.server.server.entity.Music;
import com.server.server.mapper.FavoriteMapper;
import com.server.server.mapper.MusicMapper;
import com.server.server.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    @Autowired
    private MusicMapper musicMapper;

    @Override
    public Result<String> toggleFavorite(Long userId, Long musicId) {
        Favorite favorite = this.getOne(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getMusicId, musicId));

        if (favorite != null) {
            this.removeById(favorite.getId());
            return Result.success("Unfavorited");
        } else {
            Favorite newFavorite = new Favorite();
            newFavorite.setUserId(userId);
            newFavorite.setMusicId(musicId);
            newFavorite.setCreateTime(java.time.LocalDateTime.now());
            this.save(newFavorite);
            return Result.success("Favorited");
        }
    }

    @Override
    public List<Music> getUserFavorites(Long userId) {
        List<Favorite> favorites = this.list(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId));

        return favorites.stream()
                .map(f -> musicMapper.selectById(f.getMusicId()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isFavorited(Long userId, Long musicId) {
        return this.count(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getMusicId, musicId)) > 0;
    }
}
