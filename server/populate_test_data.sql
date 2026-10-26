USE music_platform;

-- 1. 插入分类数据 (Category) - 10条
INSERT IGNORE INTO `category` (name) VALUES 
('流行'), ('摇滚'), ('古典'), ('说唱'), ('爵士'), 
('民谣'), ('金属'), ('乡村'), ('电子'), ('蓝调');

-- 2. 插入用户数据 (User) - 10条 (密码均为 123456 的 BCrypt 加密串)
INSERT IGNORE INTO `user` (username, password, nickname, role) VALUES 
('user1', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.SBaH1NT', '初听者', 0),
('user2', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.SBaH1NT', '乐迷小张', 0),
('user3', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.SBaH1NT', '耳机漫游', 0),
('user4', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.SBaH1NT', '深空旋律', 0),
('user5', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.SBaH1NT', '节奏大师', 0),
('user6', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.SBaH1NT', '音符控', 0),
('user7', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.SBaH1NT', '古典灵魂', 0),
('user8', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.SBaH1NT', '摇滚之火', 0),
('user9', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.SBaH1NT', '城市民谣', 0),
('user10', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.SBaH1NT', '电子幻境', 0);

-- 3. 插入音乐数据 (Music) - 10条
INSERT IGNORE INTO `music` (title, singer, album, style, file_url, cover_url, play_count) VALUES 
('七里香', '周杰伦', '七里香', '流行', '/uploads/demo1.mp3', 'https://img1.baidu.com/it/u=3025211915,2232924403&fm=253', 100),
('Hotel California', 'Eagles', 'Hotel California', '摇滚', '/uploads/demo2.mp3', 'https://img1.baidu.com/it/u=3025211915,2232924403&fm=253', 85),
('G弦上的咏叹调', '巴赫', '经典集', '古典', '/uploads/demo3.mp3', 'https://img1.baidu.com/it/u=3025211915,2232924403&fm=253', 50),
('Lose Yourself', 'Eminem', '8 Mile', '说唱', '/uploads/demo4.mp3', 'https://img1.baidu.com/it/u=3025211915,2232924403&fm=253', 120),
('Fly Me to the Moon', 'Frank Sinatra', 'It Might as Well Be Swing', '爵士', '/uploads/demo5.mp3', 'https://img1.baidu.com/it/u=3025211915,2232924403&fm=253', 70),
('平凡之路', '朴树', '猎户星座', '民谣', '/uploads/demo6.mp3', 'https://img1.baidu.com/it/u=3025211915,2232924403&fm=253', 200),
('Master of Puppets', 'Metallica', 'Master of Puppets', '金属', '/uploads/demo7.mp3', 'https://img1.baidu.com/it/u=3025211915,2232924403&fm=253', 45),
('Take Me Home, Country Roads', 'John Denver', 'Poems, Prayers & Promises', '乡村', '/uploads/demo8.mp3', 'https://img1.baidu.com/it/u=3025211915,2232924403&fm=253', 90),
('Animals', 'Martin Garrix', 'Gold Skies', '电子', '/uploads/demo9.mp3', 'https://img1.baidu.com/it/u=3025211915,2232924403&fm=253', 110),
('The Thrill Is Gone', 'B.B. King', 'Completely Well', '蓝调', '/uploads/demo10.mp3', 'https://img1.baidu.com/it/u=3025211915,2232924403&fm=253', 30);

-- 获取一下基础ID用于后续关联 (假设当前表是空的或ID可预测)
-- 简单起见，这里假设 user 1-10 和 music 1-10

-- 4. 插入收藏数据 (Favorite) - 10条
INSERT IGNORE INTO `favorite` (user_id, music_id) VALUES 
(1, 1), (1, 2), (2, 3), (3, 4), (4, 5), 
(5, 6), (6, 7), (7, 8), (8, 9), (9, 10);

-- 5. 插入评论数据 (Comment) - 10条
INSERT IGNORE INTO `comment` (user_id, music_id, content) VALUES 
(1, 1, '周青春的歌，永远的神！'),
(2, 2, '前奏响起，浑身鸡皮疙瘩。'),
(3, 3, '平静生活的一剂良药。'),
(4, 4, '这爆发力，无可替代。'),
(5, 5, '在午后听这首歌太惬意了。'),
(6, 6, '路过山川大海，最后还是回到平凡。'),
(7, 7, '这就是重金属的魅力！'),
(8, 8, '想回家了。'),
(9, 9, '节奏感满分，抖腿停不下来。'),
(10, 10, '蓝调之王，实至名归。');

-- 6. 插入播放记录 (Play History) - 10条
INSERT IGNORE INTO `play_history` (user_id, music_id) VALUES 
(1, 1), (2, 2), (3, 3), (4, 4), (5, 5), 
(6, 6), (7, 7), (8, 8), (9, 9), (10, 10);

-- 7. 插入歌单数据 (Playlist) - 10条
INSERT IGNORE INTO `playlist` (user_id, name, description) VALUES 
(1, '我的最爱', '日常循环。'),
(2, '静谧时光', '适合看书时听。'),
(3, '运动能量', '健身房必备。'),
(4, '经典老歌', '时光的记忆。'),
(5, '睡前舒缓', '助眠音乐集合。'),
(6, '摇滚万岁', '躁起来。'),
(7, '华语流行', '流行前沿。'),
(8, '欧美金曲', '格莱美精选。'),
(9, '纯音乐赏析', '让心静下来。'),
(10, '独立乐团', '小众但也优秀。');

-- 8. 插入歌单音乐关联 (Playlist Music) - 10条 (关联前10个歌单和前10首歌)
INSERT IGNORE INTO `playlist_music` (playlist_id, music_id) VALUES 
(1, 1), (2, 3), (3, 9), (4, 2), (5, 5), 
(6, 7), (7, 6), (8, 4), (9, 3), (10, 10);
