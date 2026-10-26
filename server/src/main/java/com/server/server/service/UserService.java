package com.server.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.server.server.common.Result;
import com.server.server.entity.User;

import java.util.Map;

public interface UserService extends IService<User> {
    Result<Map<String, Object>> login(String username, String password);
    Result<String> register(User user);
    Result<User> getUserInfo(Long userId);
    Result<String> updateUserInfo(User user);
    Result<String> updatePassword(Long id, String oldPassword, String newPassword);
    Result<String> resetPassword(String username, String nickname, String newPassword);
}
