package com.server.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.server.server.common.Result;
import com.server.server.entity.Music;
import com.server.server.entity.Playlist;
import com.server.server.entity.PlaylistMusic;
import com.server.server.mapper.MusicMapper;
import com.server.server.mapper.PlaylistMapper;
import com.server.server.mapper.PlaylistMusicMapper;
import com.server.server.service.PlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlaylistServiceImpl extends ServiceImpl<PlaylistMapper, Playlist> implements PlaylistService {

    @Autowired
    private PlaylistMusicMapper playlistMusicMapper;

    @Autowired
    private MusicMapper musicMapper;

    @Override
    public Result<String> addMusicToPlaylist(Long playlistId, Long musicId) {
        Long count = playlistMusicMapper.selectCount(new LambdaQueryWrapper<PlaylistMusic>()
                .eq(PlaylistMusic::getPlaylistId, playlistId)
                .eq(PlaylistMusic::getMusicId, musicId));
        if (count > 0) {
            return Result.error("Music already in playlist");
        }
        
        PlaylistMusic pm = new PlaylistMusic();
        pm.setPlaylistId(playlistId);
        pm.setMusicId(musicId);
        playlistMusicMapper.insert(pm);
        return Result.success("Added to playlist");
    }

    @Override
    public Result<String> removeMusicFromPlaylist(Long playlistId, Long musicId) {
        playlistMusicMapper.delete(new LambdaQueryWrapper<PlaylistMusic>()
                .eq(PlaylistMusic::getPlaylistId, playlistId)
                .eq(PlaylistMusic::getMusicId, musicId));
        return Result.success("Removed from playlist");
    }

    @Override
    public List<Music> getMusicByPlaylistId(Long playlistId) {
        List<PlaylistMusic> relations = playlistMusicMapper.selectList(new LambdaQueryWrapper<PlaylistMusic>()
                .eq(PlaylistMusic::getPlaylistId, playlistId));
        
        return relations.stream()
                .map(r -> musicMapper.selectById(r.getMusicId()))
                .collect(Collectors.toList());
    }

    @Override
    public Result<String> clearPlaylist(Long playlistId) {
        playlistMusicMapper.delete(new LambdaQueryWrapper<PlaylistMusic>()
                .eq(PlaylistMusic::getPlaylistId, playlistId));
        return Result.success("Playlist cleared");
    }
}
