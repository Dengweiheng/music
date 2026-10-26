package com.server.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.server.server.common.Result;
import com.server.server.entity.Music;
import com.server.server.entity.Playlist;

import java.util.List;

public interface PlaylistService extends IService<Playlist> {
    Result<String> addMusicToPlaylist(Long playlistId, Long musicId);
    Result<String> removeMusicFromPlaylist(Long playlistId, Long musicId);
    List<Music> getMusicByPlaylistId(Long playlistId);
    Result<String> clearPlaylist(Long playlistId);
}
