package com.server.server.controller;

import com.server.server.common.Result;
import com.server.server.entity.User;
import com.server.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody User user) {
        return userService.login(user.getUsername(), user.getPassword());
    }

    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        return userService.register(user);
    }

    @GetMapping("/info/{id}")
    public Result<User> getInfo(@PathVariable Long id) {
        return userService.getUserInfo(id);
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody User user) {
        return userService.updateUserInfo(user);
    }

    @GetMapping("/list")
    public Result<java.util.List<User>> getList() {
        return Result.success(userService.list());
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        userService.removeById(id);
        return Result.success("删除成功");
    }

    @PutMapping("/admin/status/{id}/{status}")
    public Result<String> updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        User user = userService.getById(id);
        if (user != null) {
            user.setStatus(status);
            userService.updateById(user);
            return Result.success("状态更新成功");
        }
        return Result.error("用户不存在");
    }

    @PutMapping("/update/password")
    public Result<String> updatePassword(@RequestBody com.server.server.dto.PasswordUpdateDto dto) {
        return userService.updatePassword(dto.getId(), dto.getOldPassword(), dto.getNewPassword());
    }

    @PostMapping("/reset-password")
    public Result<String> resetPassword(@RequestBody Map<String, String> params) {
        return userService.resetPassword(params.get("username"), params.get("nickname"), params.get("newPassword"));
    }

    @PostMapping("/upload")
    public Result<String> uploadFile(@org.springframework.web.bind.annotation.RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("File is empty");
        }
        String fileName = java.util.UUID.randomUUID() + "_" + file.getOriginalFilename();
        java.io.File dest = new java.io.File(new java.io.File("uploads/").getAbsolutePath(), fileName);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        try {
            file.transferTo(dest);
            return Result.success("/uploads/" + fileName);
        } catch (java.io.IOException e) {
            return Result.error("Upload failed: " + e.getMessage());
        }
    }
}
