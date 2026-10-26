package com.server.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.server.server.common.Result;
import com.server.server.entity.Music;
import com.server.server.mapper.MusicMapper;
import com.server.server.service.MusicService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.server.server.entity.Favorite;
import com.server.server.entity.PlayHistory;
import com.server.server.mapper.FavoriteMapper;
import com.server.server.mapper.PlayHistoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MusicServiceImpl extends ServiceImpl<MusicMapper, Music> implements MusicService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private PlayHistoryMapper playHistoryMapper;

    @Override
    public Page<Music> searchMusic(String keyword, String style, Integer status, int current, int size) {
        LambdaQueryWrapper<Music> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Music::getTitle, keyword)
                    .or().like(Music::getSinger, keyword)
                    .or().like(Music::getAlbum, keyword));
        }
        if (StringUtils.hasText(style)) {
            wrapper.eq(Music::getStyle, style);
        }
        if (status != null) {
            wrapper.eq(Music::getStatus, status);
        }
        wrapper.orderByDesc(Music::getCreateTime);
        return this.page(new Page<>(current, size), wrapper);
    }

    @Override
    public List<String> getAllStyles() {
        return this.list().stream()
                .map(Music::getStyle)
                .filter(StringUtils::hasText)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<Music> getRecommendedMusic(Long userId, int limit) {
        // Fallback to hot music if user is not logged in or has no history
        if (userId == null) {
            return getHotMusic(limit);
        }

        // 1. Get current user's interacted music IDs (Favorites + History)
        Set<Long> userMusicIds = new HashSet<>();
        favoriteMapper.selectList(new QueryWrapper<Favorite>().eq("user_id", userId))
                .forEach(f -> userMusicIds.add(f.getMusicId()));
        playHistoryMapper.selectList(new QueryWrapper<PlayHistory>().eq("user_id", userId))
                .forEach(ph -> userMusicIds.add(ph.getMusicId()));

        if (userMusicIds.isEmpty()) {
            return getHotMusic(limit);
        }

        // 2. Simple Collaborative Filtering: Find other users who interacted with these songs
        // Then find what OTHER songs they interacted with.
        
        // Find users who have interaction with these song IDs
        Set<Long> similarUserIds = new HashSet<>();
        favoriteMapper.selectList(new QueryWrapper<Favorite>().in("music_id", userMusicIds))
                .forEach(f -> { if(!f.getUserId().equals(userId)) similarUserIds.add(f.getUserId()); });
        playHistoryMapper.selectList(new QueryWrapper<PlayHistory>().in("music_id", userMusicIds))
                .forEach(ph -> { if(ph.getUserId() != null && !ph.getUserId().equals(userId)) similarUserIds.add(ph.getUserId()); });

        if (similarUserIds.isEmpty()) {
            return getHotMusic(limit);
        }

        // 3. Count frequencies of songs interacted by similar users
        Map<Long, Integer> recommendations = new HashMap<>();
        
        // From Favorites
        favoriteMapper.selectList(new QueryWrapper<Favorite>().in("user_id", similarUserIds))
                .forEach(f -> {
                    if (!userMusicIds.contains(f.getMusicId())) {
                        recommendations.put(f.getMusicId(), recommendations.getOrDefault(f.getMusicId(), 0) + 1);
                    }
                });
        
        // From History
        playHistoryMapper.selectList(new QueryWrapper<PlayHistory>().in("user_id", similarUserIds))
                .forEach(ph -> {
                    if (!userMusicIds.contains(ph.getMusicId())) {
                        recommendations.put(ph.getMusicId(), recommendations.getOrDefault(ph.getMusicId(), 0) + 1);
                    }
                });

        if (recommendations.isEmpty()) {
            return getHotMusic(limit);
        }

        // 4. Sort and return top N
        List<Long> recommendedIds = recommendations.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return this.listByIds(recommendedIds);
    }

    private List<Music> getHotMusic(int limit) {
        return this.list(new QueryWrapper<Music>()
                .eq("status", 1)
                .orderByDesc("play_count")
                .last("limit " + limit));
    }
}
