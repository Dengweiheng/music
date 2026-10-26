-- Create Database
CREATE DATABASE IF NOT EXISTS music_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE music_platform;

-- 1. 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `nickname` VARCHAR(50) COMMENT '昵称',
    `avatar` VARCHAR(255) COMMENT '头像',
    `role` INT DEFAULT 0 COMMENT '角色 (0: 普通用户, 1: 管理员)',
    `status` INT DEFAULT 0 COMMENT '状态 (0: 正常, 1: 禁用)',
    `last_login_time` DATETIME COMMENT '最后登录时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- 2. 音乐表
CREATE TABLE IF NOT EXISTS `music` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `title` VARCHAR(100) NOT NULL COMMENT '歌名',
    `singer` VARCHAR(50) COMMENT '歌手',
    `album` VARCHAR(50) COMMENT '专辑',
    `style` VARCHAR(50) COMMENT '风格 (如摇滚、流行等)',
    `file_url` VARCHAR(255) NOT NULL COMMENT '音乐文件地址',
    `cover_url` VARCHAR(255) COMMENT '封面图地址',
    `play_count` INT DEFAULT 0 COMMENT '播放量',
    `user_id` BIGINT COMMENT '上传用户ID',
    `status` INT DEFAULT 0 COMMENT '状态 (0: 待审核, 1: 已通过, 2: 已拒绝)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    INDEX `idx_search` (`title`, `singer`, `album`),
    INDEX `idx_style` (`style`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='音乐信息表';

-- 3. 收藏表
CREATE TABLE IF NOT EXISTS `favorite` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `music_id` BIGINT NOT NULL COMMENT '音乐ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_user_music` (`user_id`, `music_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- 4. 评论表
CREATE TABLE IF NOT EXISTS `comment` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `music_id` BIGINT NOT NULL COMMENT '音乐ID',
    `content` TEXT NOT NULL COMMENT '评论内容',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_music_id` (`music_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- 5. 播放记录表
CREATE TABLE IF NOT EXISTS `play_history` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT COMMENT '用户ID',
    `music_id` BIGINT NOT NULL COMMENT '音乐ID',
    `play_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_stat` (`music_id`, `play_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='播放历史统计表';

-- 6. 分类表
CREATE TABLE IF NOT EXISTS `category` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(50) NOT NULL UNIQUE COMMENT '分类名称',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='音乐分类表';

-- 7. 歌单表
CREATE TABLE IF NOT EXISTS `playlist` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL COMMENT '所有者ID',
    `name` VARCHAR(100) NOT NULL COMMENT '歌单名称',
    `description` VARCHAR(255) COMMENT '描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='歌单表';

-- 7. 歌单音乐关联表
CREATE TABLE IF NOT EXISTS `playlist_music` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `playlist_id` BIGINT NOT NULL,
    `music_id` BIGINT NOT NULL,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_playlist_music` (`playlist_id`, `music_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='歌单音乐关联表';

-- 8. 轮播图表
CREATE TABLE IF NOT EXISTS `banner` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `title` VARCHAR(100) COMMENT '标题',
    `image_url` VARCHAR(255) NOT NULL COMMENT '图片地址',
    `link_url` VARCHAR(255) COMMENT '跳转链接',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='轮播图表';

-- 9. 公告表
CREATE TABLE IF NOT EXISTS `notice` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `title` VARCHAR(100) NOT NULL COMMENT '标题',
    `content` TEXT NOT NULL COMMENT '内容',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

-- 10. 敏感词表
CREATE TABLE IF NOT EXISTS `sensitive_word` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `word` VARCHAR(100) NOT NULL UNIQUE COMMENT '敏感词',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='敏感词库';

-- 插入默认管理员 (123456)
INSERT INTO `user` (username, password, nickname, role, status) VALUES ('admin', '$2a$10$7R6L/tP9G.bYV7/x4y.WBeW5Z.m9P0p9v9Z9P9v9P9v9P9v9P9v9P9v9p', '系统管理员', 1, 0);

-- 插入示例轮播图
INSERT INTO `banner` (title, image_url, link_url, sort) VALUES 
('听见冬天的声音', 'https://images.unsplash.com/photo-1470225620780-dba8ba36b745', '/', 1),
('春日限定摇滚', 'https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4', '/', 2);

-- 插入示例公告
INSERT INTO `notice` (title, content) VALUES 
('系统升级公告', '我们将于今晚 2:00 进行系统维护，预计时长 2 小时。'),
('欢迎入驻 MusicBox', 'MusicBox 全新版本上线，快来探索属于你的音乐世界吧！');

-- 11. 预设敏感词
INSERT IGNORE INTO `sensitive_word` (word) VALUES 
('滥发广告'), ('恶意刷屏'), ('色情内容'), ('暴力倾向'), ('侮辱谩骂'), ('政治敏感'), ('非法链接');

-- 12. 示例音乐数据
INSERT IGNORE INTO `music` (title, singer, album, style, file_url, cover_url, status, play_count) VALUES 
('Stay With Me', 'Miki Matsubara', 'Pocket Park', '流行', 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3', 'https://images.unsplash.com/photo-1493225255756-d9584f8606e9', 1, 1200),
('Plastic Love', 'Mariya Takeuchi', 'Variety', '流行', 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3', 'https://images.unsplash.com/photo-1470225620780-dba8ba36b745', 1, 850),
('Bohemian Rhapsody', 'Queen', 'A Night at the Opera', '摇滚', 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3', 'https://images.unsplash.com/photo-1498038432885-c6f3f1b912ee', 1, 2500);
