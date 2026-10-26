package com.server.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.server.server.entity.Comment;

import java.util.List;
import java.util.Map;

public interface CommentService extends IService<Comment> {
    List<Map<String, Object>> getCommentsByMusicId(Long musicId);
}
