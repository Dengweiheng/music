package com.server.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.server.server.common.Result;
import com.server.server.entity.Banner;
import com.server.server.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/banner")
public class BannerController {

    @Autowired
    private BannerService bannerService;

    @GetMapping("/list")
    public Result<List<Banner>> list() {
        return Result.success(bannerService.list(new LambdaQueryWrapper<Banner>().orderByAsc(Banner::getSort)));
    }

    @PostMapping("/admin/save")
    public Result<String> save(@RequestBody Banner banner) {
        bannerService.save(banner);
        return Result.success("Saved");
    }

    @PutMapping("/admin/update")
    public Result<String> update(@RequestBody Banner banner) {
        bannerService.updateById(banner);
        return Result.success("Updated");
    }

    @DeleteMapping("/admin/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        bannerService.removeById(id);
        return Result.success("Deleted");
    }
}
