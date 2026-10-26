package com.server.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.server.server.entity.SensitiveWord;
import com.server.server.mapper.SensitiveWordMapper;
import com.server.server.service.SensitiveWordService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SensitiveWordServiceImpl extends ServiceImpl<SensitiveWordMapper, SensitiveWord> implements SensitiveWordService {

    @Override
    public boolean containsSensitiveWord(String text) {
        if (text == null || text.trim().isEmpty()) return false;
        List<SensitiveWord> words = this.list();
        for (SensitiveWord sw : words) {
            if (text.contains(sw.getWord())) {
                return true;
            }
        }
        return false;
    }
}
