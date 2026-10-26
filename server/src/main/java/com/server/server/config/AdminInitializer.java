package com.server.server.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.server.server.entity.User;
import com.server.server.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        User admin = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, "admin"));
        if (admin != null) {
            // If password is the plain text '123456', encrypt it. 
            // This is a simple logic for development environment.
            if ("123456".equals(admin.getPassword())) {
                admin.setPassword(passwordEncoder.encode("123456"));
                userMapper.updateById(admin);
                System.out.println("Admin password has been encrypted.");
            }
        }
    }
}
