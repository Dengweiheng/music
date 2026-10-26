package com.server.server.controller;

import com.server.server.common.Result;
import com.server.server.entity.Comment;
import com.server.server.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private com.server.server.service.SensitiveWordService sensitiveWordService;

    @PostMapping("/add")
    public Result<String> add(@RequestBody Comment comment) {
        if (sensitiveWordService.containsSensitiveWord(comment.getContent())) {
            return Result.error("评论内容包含敏感词，请文明用语");
        }
        comment.setCreateTime(java.time.LocalDateTime.now());
        commentService.save(comment);
        return Result.success("Comment added");
    }

    @GetMapping("/music/{musicId}")
    public Result<List<Map<String, Object>>> getByMusic(@PathVariable Long musicId) {
        return Result.success(commentService.getCommentsByMusicId(musicId));
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        commentService.removeById(id);
        return Result.success("Comment deleted");
    }
}
