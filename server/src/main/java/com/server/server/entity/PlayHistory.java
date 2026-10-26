package com.server.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@TableName("play_history")
public class PlayHistory {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long musicId;
    private LocalDateTime playTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public Long getMusicId() { return musicId; }
    public void setMusicId(Long musicId) { this.musicId = musicId; }
    
    public LocalDateTime getPlayTime() { return playTime; }
    public void setPlayTime(LocalDateTime playTime) { this.playTime = playTime; }
}
