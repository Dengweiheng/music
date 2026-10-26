package com.server.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.server.server.entity.PlayHistory;

import java.util.List;
import java.util.Map;

public interface PlayHistoryService extends IService<PlayHistory> {
    void recordPlay(Long userId, Long musicId);
    List<Map<String, Object>> getTopMusic(int limit);
    long getActiveUserCount(int days);
    List<Map<String, Object>> getCategoryStats();
    Map<String, Object> getSummary();
}
