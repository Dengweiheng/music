package com.server.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.server.server.entity.Comment;
import com.server.server.entity.User;
import com.server.server.mapper.CommentMapper;
import com.server.server.mapper.UserMapper;
import com.server.server.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<Map<String, Object>> getCommentsByMusicId(Long musicId) {
        List<Comment> comments = this.list(new LambdaQueryWrapper<Comment>()
                .eq(Comment::getMusicId, musicId)
                .orderByDesc(Comment::getCreateTime));

        return comments.stream().map(c -> {
            Map<String, Object> map = new HashMap<>();
            map.put("comment", c);
            User user = userMapper.selectById(c.getUserId());
            if (user != null) {
                map.put("nickname", user.getNickname());
                map.put("avatar", user.getAvatar());
            }
            return map;
        }).collect(Collectors.toList());
    }
}
