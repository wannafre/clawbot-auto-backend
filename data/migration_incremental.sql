-- 贴吧论坛增量迁移脚本
-- 执行时间：2026-03-02 06:50
-- 说明：创建缺失的新表

-- 1. 创建通知表
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

-- 2. 创建回复点赞表
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

-- 3. 创建标签表
CREATE TABLE IF NOT EXISTS tags (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE COMMENT '标签名称',
    post_count INT DEFAULT 0 COMMENT '使用次数',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

-- 4. 创建帖子 - 标签关联表
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

-- 5. 创建举报表
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

-- 6. 为 users 表添加 experience 字段（如果不存在）
-- 检查并添加 experience 字段
SET @dbname = DATABASE();
SET @tablename = 'users';
SET @columnname = 'experience';
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE
      (table_name = @tablename)
      AND (table_schema = @dbname)
      AND (column_name = @columnname)
  ) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' INT DEFAULT 0 COMMENT \'经验值\' AFTER level')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

SELECT '✅ 数据库增量迁移完成！' AS status;
SELECT '已创建表：notifications, reply_likes, tags, post_tags, reports' AS message;
