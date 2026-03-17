# 贴吧论坛项目 - 功能规划

## 项目信息
- **名称**: 贴吧风格论坛后端
- **技术栈**: Spring Boot 3.2.0 + JPA + MySQL + JWT
- **包名**: com.tieba.forum

## 已完成功能 ✅

### 1. 用户系统
- [x] 用户注册
- [x] 用户登录（JWT）
- [x] 图形验证码
- [x] 签到系统

### 2. 内容系统
- [x] 帖子创建
- [x] 帖子列表（分页）
- [x] 帖子详情
- [x] 按板块筛选帖子
- [x] 回复帖子（楼中楼支持）
- [x] 回复列表

### 3. 互动系统
- [x] 点赞
- [x] 收藏
- [x] 关注用户/吧

## 待完成功能 📋

### 高优先级 🔴

1. **帖子管理**
   - [x] 编辑帖子
   - [x] 删除帖子（软删除）
   - [x] 帖子详情增强（作者信息、板块信息）
   - [x] 搜索帖子

2. **回复管理**
   - [x] 删除回复
   - [x] 回复点赞

3. **搜索功能**
   - [x] 帖子标题/内容搜索
   - [ ] 用户搜索
   - [ ] 板块搜索

4. **排行榜**
   - [x] 热门帖子（按浏览量）
   - [x] 用户的帖子列表
   - [ ] 活跃用户
   - [ ] 签到排行榜

### 中优先级 🟡

5. **管理功能**
   - [ ] 置顶/加精
   - [ ] 封禁用户
   - [ ] 内容审核

6. **通知系统**
   - [ ] 回复通知
   - [ ] 点赞通知
   - [ ] 系统通知

7. **用户资料**
   - [ ] 修改个人信息
   - [ ] 上传头像
   - [ ] 用户主页

### 低优先级 🟢

8. **增强功能**
   - [ ] 图片上传（帖子/回复）
   - [ ] 标签系统
   - [ ] 用户等级/经验值
   - [ ] 消息私信

## API 路由规划

### 帖子相关
```
POST   /api/posts/{id}/edit      # 编辑帖子
DELETE /api/posts/{id}           # 删除帖子
GET    /api/posts/search         # 搜索帖子
GET    /api/posts/hot            # 热门帖子
GET    /api/posts/user/{userId}  # 用户的帖子
```

### 回复相关
```
DELETE /api/replies/{id}         # 删除回复
POST   /api/replies/{id}/like    # 点赞回复
```

### 用户相关
```
GET    /api/users/{id}           # 用户信息
PUT    /api/users/profile        # 修改资料
POST   /api/users/avatar         # 上传头像
GET    /api/users/{id}/posts     # 用户的帖子
```

### 搜索相关
```
GET    /api/search/posts         # 搜索帖子
GET    /api/search/users         # 搜索用户
GET    /api/search/forums        # 搜索板块
```

### 排行榜
```
GET    /api/rank/posts           # 帖子排行
GET    /api/rank/users           # 用户排行
```

## 数据库变更

需要新增的表：
- `notification` - 通知表
- `user_report` - 举报表
- `post_tag` - 标签表
- `user_level` - 用户等级表

需要修改的表：
- `post` - 添加 deleted_at, is_deleted 字段
- `reply` - 添加 deleted_at, is_deleted 字段
- `user` - 添加 avatar, bio, experience 字段

## 下一步行动

1. 完善帖子和回复的软删除功能
2. 实现搜索功能（Elasticsearch 或 MySQL LIKE）
3. 添加排行榜接口
4. 实现通知系统
