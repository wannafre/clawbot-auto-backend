-- 贴吧论坛数据库迁移脚本
-- 执行时间：2026-03-02
-- 说明：添加软删除字段和新功能所需字段

-- 1. posts 表添加软删除字段
ALTER TABLE posts 
ADD COLUMN is_deleted BOOLEAN DEFAULT FALSE,
ADD COLUMN deleted_at DATETIME NULL;

-- 2. replies 表添加软删除字段
ALTER TABLE replies 
ADD COLUMN is_deleted BOOLEAN DEFAULT FALSE,
ADD COLUMN deleted_at DATETIME NULL;

-- 3. users 表添加用户资料字段
ALTER TABLE users 
ADD COLUMN avatar VARCHAR(500) NULL COMMENT '头像 URL',
ADD COLUMN bio VARCHAR(500) NULL COMMENT '个人简介',
ADD COLUMN experience INT DEFAULT 0 COMMENT '经验值',
ADD COLUMN level INT DEFAULT 1 COMMENT '用户等级';

-- 4. 创建通知表
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '接收用户 ID',
    type VARCHAR(50) NOT NULL COMMENT '通知类型：reply, like, follow, system',
    title VARCHAR(200) NULL COMMENT '通知标题',
    content TEXT NOT NULL COMMENT '通知内容',
    is_read BOOLEAN DEFAULT FALSE COMMENT '是否已读',
    related_user_id BIGINT NULL COMMENT '相关用户 ID',
    related_post_id BIGINT NULL COMMENT '相关帖子 ID',
    related_reply_id BIGINT NULL COMMENT '相关回复 ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_is_read (is_read),
    FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';

-- 5. 创建回复点赞表
CREATE TABLE IF NOT EXISTS reply_likes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    reply_id BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_user_reply (user_id, reply_id),
    INDEX idx_reply_id (reply_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (reply_id) REFERENCES replies(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='回复点赞表';

-- 6. 创建标签表
CREATE TABLE IF NOT EXISTS tags (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE COMMENT '标签名称',
    post_count INT DEFAULT 0 COMMENT '使用次数',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

-- 7. 创建帖子 - 标签关联表
CREATE TABLE IF NOT EXISTS post_tags (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_post_tag (post_id, tag_id),
    INDEX idx_tag_id (tag_id),
    FOREIGN KEY (post_id) REFERENCES posts(id),
    FOREIGN KEY (tag_id) REFERENCES tags(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子标签关联表';

-- 8. 创建举报表
CREATE TABLE IF NOT EXISTS reports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reporter_id BIGINT NOT NULL COMMENT '举报人 ID',
    reported_user_id BIGINT NULL COMMENT '被举报用户 ID',
    reported_post_id BIGINT NULL COMMENT '被举报帖子 ID',
    reported_reply_id BIGINT NULL COMMENT '被举报回复 ID',
    reason VARCHAR(500) NOT NULL COMMENT '举报原因',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态：pending, processing, resolved',
    handler_id BIGINT NULL COMMENT '处理人 ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    resolved_at DATETIME NULL,
    INDEX idx_status (status),
    FOREIGN KEY (reporter_id) REFERENCES users(id),
    FOREIGN KEY (reported_user_id) REFERENCES users(id),
    FOREIGN KEY (reported_post_id) REFERENCES posts(id),
    FOREIGN KEY (reported_reply_id) REFERENCES replies(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='举报表';

-- 9. 为现有数据设置默认值（如果有旧数据）
UPDATE posts SET is_deleted = FALSE WHERE is_deleted IS NULL;
UPDATE replies SET is_deleted = FALSE WHERE is_deleted IS NULL;

-- 完成
SELECT '数据库迁移完成！' AS status;
