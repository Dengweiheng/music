package com.server.server.controller;

import com.server.server.common.Result;
import com.server.server.entity.SensitiveWord;
import com.server.server.service.SensitiveWordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sensitive")
public class SensitiveWordController {

    @Autowired
    private SensitiveWordService sensitiveWordService;

    @GetMapping("/admin/list")
    public Result<List<SensitiveWord>> list() {
        return Result.success(sensitiveWordService.list());
    }

    @PostMapping("/admin/save")
    public Result<String> save(@RequestBody SensitiveWord sw) {
        sensitiveWordService.save(sw);
        return Result.success("Sensitive word added");
    }

    @DeleteMapping("/admin/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        sensitiveWordService.removeById(id);
        return Result.success("Sensitive word removed");
    }
}
