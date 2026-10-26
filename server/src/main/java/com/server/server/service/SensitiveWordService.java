package com.server.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.server.server.entity.SensitiveWord;

public interface SensitiveWordService extends IService<SensitiveWord> {
    boolean containsSensitiveWord(String text);
}
