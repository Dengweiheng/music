package com.server.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.server.server.common.Result;
import com.server.server.entity.User;
import com.server.server.mapper.UserMapper;
import com.server.server.service.UserService;
import com.server.server.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public Result<Map<String, Object>> login(String username, String password) {
        User user = this.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            return Result.error("用户不存在");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return Result.error("密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 1) {
            return Result.error("账号已被禁用，请联系管理员");
        }

        String token = jwtUtils.createToken(user.getId(), user.getUsername(), user.getRole());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", user);

        return Result.success(data);
    }

    @Override
    public Result<String> register(User user) {
        User existing = this.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, user.getUsername()));
        if (existing != null) {
            return Result.error("用户名已存在");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(0); // Default to regular user
        this.save(user);
        return Result.success("注册成功");
    }

    @Override
    public Result<User> getUserInfo(Long userId) {
        User user = this.getById(userId);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    @Override
    public Result<String> updateUserInfo(User user) {
        this.updateById(user);
        return Result.success("更新成功");
    }

    @Override
    public Result<String> updatePassword(Long id, String oldPassword, String newPassword) {
        User user = this.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return Result.error("原密码错误");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        this.updateById(user);
        return Result.success("密码修改成功");
    }

    @Override
    public Result<String> resetPassword(String username, String nickname, String newPassword) {
        User user = this.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            return Result.error("用户不存在");
        }
        if (nickname == null || !nickname.equals(user.getNickname())) {
            return Result.error("验证信息不匹配(昵称不对)");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        this.updateById(user);
        return Result.success("重置成功");
    }
}
