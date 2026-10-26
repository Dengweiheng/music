package com.server.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.server.server.common.Result;
import com.server.server.entity.Notice;
import com.server.server.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @GetMapping("/list")
    public Result<List<Notice>> list() {
        return Result.success(noticeService.list(new LambdaQueryWrapper<Notice>().orderByDesc(Notice::getCreateTime)));
    }

    @PostMapping("/admin/save")
    public Result<String> save(@RequestBody Notice notice) {
        noticeService.save(notice);
        return Result.success("Saved");
    }

    @PutMapping("/admin/update")
    public Result<String> update(@RequestBody Notice notice) {
        noticeService.updateById(notice);
        return Result.success("Updated");
    }

    @DeleteMapping("/admin/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        noticeService.removeById(id);
        return Result.success("Deleted");
    }
}
