package com.server.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.server.server.entity.Music;
import com.server.server.entity.PlayHistory;
import com.server.server.entity.User;
import com.server.server.mapper.MusicMapper;
import com.server.server.mapper.PlayHistoryMapper;
import com.server.server.mapper.UserMapper;
import com.server.server.service.PlayHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PlayHistoryServiceImpl extends ServiceImpl<PlayHistoryMapper, PlayHistory> implements PlayHistoryService {

    @Autowired
    private MusicMapper musicMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public void recordPlay(Long userId, Long musicId) {
        PlayHistory history = new PlayHistory();
        history.setUserId(userId);
        history.setMusicId(musicId);
        history.setPlayTime(LocalDateTime.now());
        this.save(history);
        
        // Also update total play count in music table
        Music music = musicMapper.selectById(musicId);
        if (music != null) {
            music.setPlayCount(music.getPlayCount() + 1);
            musicMapper.updateById(music);
        }
    }

    @Override
    public List<Map<String, Object>> getTopMusic(int limit) {
        // Simple implementation: sort music by play_count
        List<Music> list = musicMapper.selectList(new QueryWrapper<Music>()
                .orderByDesc("play_count")
                .last("limit " + limit));
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Music m : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("music", m);
            result.add(map);
        }
        return result;
    }

    @Override
    public long getActiveUserCount(int days) {
        LocalDateTime startTime = LocalDateTime.now().minusDays(days);
        return this.list(new LambdaQueryWrapper<PlayHistory>()
                .ge(PlayHistory::getPlayTime, startTime)
                .isNotNull(PlayHistory::getUserId))
                .stream()
                .map(PlayHistory::getUserId)
                .distinct()
                .count();
    }

    @Override
    public List<Map<String, Object>> getCategoryStats() {
        QueryWrapper<Music> qw = new QueryWrapper<>();
        qw.select("style as name", "count(*) as value");
        qw.groupBy("style");
        return musicMapper.selectMaps(qw);
    }

    @Override
    public Map<String, Object> getSummary() {
        Map<String, Object> summary = new HashMap<>();

        // 1. Total Plays
        QueryWrapper<Music> musicQw = new QueryWrapper<>();
        musicQw.select("sum(play_count) as totalPlays");
        List<Map<String, Object>> resultList = musicMapper.selectMaps(musicQw);
        Map<String, Object> totalPlaysMap = (resultList != null && !resultList.isEmpty()) ? resultList.get(0) : null;
        Object totalPlaysObj = totalPlaysMap != null ? totalPlaysMap.get("totalPlays") : 0;
        summary.put("totalPlays", totalPlaysObj != null ? totalPlaysObj : 0);

        // 2. New Users (Last 7 Days)
        LocalDateTime lastWeek = LocalDateTime.now().minusDays(7);
        Long newUsers = userMapper.selectCount(new QueryWrapper<User>().gt("create_time", lastWeek));
        summary.put("newUsers", newUsers);

        // 3. Online Works
        Long onlineWorks = musicMapper.selectCount(null);
        summary.put("onlineWorks", onlineWorks);

        // 4. Activation Rate (Active users in 7 days / Total users)
        long activeUsers = getActiveUserCount(7);
        Long totalUsers = userMapper.selectCount(null);
        summary.put("activeUsers", activeUsers);
        summary.put("totalUsers", totalUsers);
        double activationRate = totalUsers > 0 ? (double) activeUsers / totalUsers : 0;
        summary.put("activationRate", Math.round(activationRate * 1000) / 10.0); // e.g., 64.2

        // 5. User Roles distribution
        Long regularUsers = userMapper.selectCount(new QueryWrapper<User>().eq("role", 0));
        Long adminUsers = userMapper.selectCount(new QueryWrapper<User>().eq("role", 1));
        summary.put("roleUser", regularUsers);
        summary.put("roleAdmin", adminUsers);

        // 6. Trend Data (Plays for last 7 days)
        List<Map<String, Object>> trend = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDateTime start = LocalDateTime.now().minusDays(i).withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime end = start.plusDays(1);
            Long dailyPlays = this.count(new LambdaQueryWrapper<PlayHistory>()
                    .ge(PlayHistory::getPlayTime, start)
                    .lt(PlayHistory::getPlayTime, end));
            Map<String, Object> dayStat = new HashMap<>();
            dayStat.put("date", start.getDayOfWeek().name().substring(0, 3));
            dayStat.put("count", dailyPlays);
            trend.add(dayStat);
        }
        summary.put("trend", trend);

        // 7. User Growth Data (Last 7 days)
        List<Map<String, Object>> userGrowth = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDateTime start = LocalDateTime.now().minusDays(i).withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime end = start.plusDays(1);
            Long dailyNewUsers = userMapper.selectCount(new QueryWrapper<User>()
                    .ge("create_time", start)
                    .lt("create_time", end));
            Map<String, Object> dayStat = new HashMap<>();
            dayStat.put("date", start.getDayOfWeek().name().substring(0, 3));
            dayStat.put("count", dailyNewUsers);
            userGrowth.add(dayStat);
        }
        summary.put("userGrowth", userGrowth);

        return summary;
    }
}
