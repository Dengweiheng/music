package com.server.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.server.common.Result;
import com.server.server.entity.Category;
import com.server.server.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // Public list without pagination
    @GetMapping("/all")
    public Result<List<Category>> getAll() {
        return Result.success(categoryService.list());
    }

    // Admin pagination list
    @GetMapping("/admin/list")
    public Result<Page<Category>> adminList(@RequestParam(defaultValue = "1") int current,
                                            @RequestParam(defaultValue = "10") int size,
                                            @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Category::getName, keyword);
        }
        wrapper.orderByDesc(Category::getId);
        return Result.success(categoryService.page(new Page<>(current, size), wrapper));
    }

    // Admin save
    @PostMapping("/admin/save")
    public Result<String> save(@RequestBody Category category) {
        // Prevent duplicate names
        long count = categoryService.count(new LambdaQueryWrapper<Category>().eq(Category::getName, category.getName()));
        if (count > 0) {
            return Result.error("分类名称已存在");
        }
        categoryService.save(category);
        return Result.success("保存成功");
    }

    // Admin update
    @PutMapping("/admin/update")
    public Result<String> update(@RequestBody Category category) {
        long count = categoryService.count(new LambdaQueryWrapper<Category>()
                .eq(Category::getName, category.getName())
                .ne(Category::getId, category.getId()));
        if (count > 0) {
            return Result.error("分类名称已存在");
        }
        categoryService.updateById(category);
        return Result.success("更新成功");
    }

    // Admin delete
    @DeleteMapping("/admin/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        categoryService.removeById(id);
        return Result.success("删除成功");
    }
}
