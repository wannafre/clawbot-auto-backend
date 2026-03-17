# 贴吧论坛 API 文档

## 基础信息
- **Base URL**: `http://localhost:8080`
- **认证方式**: JWT Token (通过 `Authorization: Bearer <token>` 头传递)
- **数据格式**: JSON

---

## 认证相关

### 1. 获取验证码
```
GET /api/auth/captcha
```
**响应**:
```json
{
  "code": 200,
  "data": {
    "captchaId": "uuid-string",
    "image": "base64-encoded-image"
  }
}
```

### 2. 用户注册
```
POST /api/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123",
  "email": "test@example.com",
  "captchaId": "uuid-string",
  "captchaCode": "ABCD"
}
```

### 3. 用户登录
```
POST /api/auth/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123"
}
```
**响应**:
```json
{
  "code": 200,
  "data": {
    "token": "jwt-token-string",
    "user": {
      "id": 1,
      "username": "testuser",
      "email": "test@example.com"
    }
  }
}
```

---

## 帖子相关

### 1. 获取帖子列表
```
GET /posts?page=0&size=20
```
**响应**:
```json
{
  "code": 200,
  "data": {
    "content": [...],
    "totalElements": 100,
    "totalPages": 5,
    "number": 0,
    "size": 20
  }
}
```

### 2. 获取板块帖子
```
GET /posts/forum/{forumId}?page=0&size=20
```

### 3. 获取帖子详情
```
GET /posts/{id}
```

### 4. 创建帖子
```
POST /posts
Authorization: Bearer <token>
Content-Type: application/json

{
  "forumId": 1,
  "title": "新帖子标题",
  "content": "帖子内容..."
}
```

### 5. 编辑帖子
```
PUT /posts/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "修改后的标题",
  "content": "修改后的内容"
}
```

### 6. 删除帖子（软删除）
```
DELETE /posts/{id}
Authorization: Bearer <token>
```

### 7. 搜索帖子
```
GET /posts/search?keyword=关键词&page=0&size=20
```

### 8. 热门帖子
```
GET /posts/hot?page=0&size=20
```

### 9. 用户的帖子
```
GET /posts/user/{userId}?page=0&size=20
```

---

## 回复相关

### 1. 获取帖子回复
```
GET /posts/{id}/replies
```

### 2. 创建回复
```
POST /posts/{id}/replies
Authorization: Bearer <token>
Content-Type: application/json

{
  "content": "回复内容",
  "parentId": null  // 楼中楼时填写父回复 ID
}
```

### 3. 删除回复
```
DELETE /api/interact/reply/{replyId}
Authorization: Bearer <token>
```

### 4. 点赞回复
```
POST /api/interact/reply/{replyId}/like
Authorization: Bearer <token>
```

---

## 互动相关

### 1. 点赞/取消点赞帖子
```
POST /api/interact/post/{postId}/like
Authorization: Bearer <token>
```
**响应**:
```json
{
  "code": 200,
  "data": {
    "liked": true,
    "likeCount": 15
  }
}
```

### 2. 收藏/取消收藏帖子
```
POST /api/interact/post/{postId}/favorite
Authorization: Bearer <token>
```

### 3. 获取用户收藏
```
GET /api/interact/favorites?userId=1
```

---

## 签到相关

### 1. 签到
```
POST /api/checkin
Authorization: Bearer <token>
```

### 2. 获取签到状态
```
GET /api/checkin/status?userId=1&forumId=1
```

### 3. 签到排行榜
```
GET /api/checkin/rank?forumId=1&page=0&size=20
```

---

## 板块相关

### 1. 获取板块列表
```
GET /forums
```

### 2. 获取板块详情
```
GET /forums/{id}
```

---

## 错误响应

所有错误返回统一格式：
```json
{
  "code": 400,
  "message": "错误描述"
}
```

**常见错误码**:
- `200`: 成功
- `400`: 请求参数错误
- `401`: 未登录/Token 无效
- `403`: 无权限
- `404`: 资源不存在
- `500`: 服务器内部错误

---

## 数据模型

### Post (帖子)
```json
{
  "id": 1,
  "title": "帖子标题",
  "content": "帖子内容",
  "author": { "id": 1, "username": "作者" },
  "forum": { "id": 1, "name": "板块名" },
  "viewCount": 100,
  "replyCount": 15,
  "likeCount": 20,
  "isTop": false,
  "isGood": false,
  "isDeleted": false,
  "createdAt": "2026-03-02T06:00:00",
  "updatedAt": "2026-03-02T06:00:00"
}
```

### Reply (回复)
```json
{
  "id": 1,
  "content": "回复内容",
  "author": { "id": 1, "username": "作者" },
  "post": { "id": 1, "title": "帖子标题" },
  "parent": null,
  "floor": 1,
  "likeCount": 5,
  "isDeleted": false,
  "createdAt": "2026-03-02T06:00:00"
}
```

### User (用户)
```json
{
  "id": 1,
  "username": "用户名",
  "email": "email@example.com",
  "avatar": null,
  "bio": null,
  "experience": 0,
  "level": 1,
  "createdAt": "2026-03-02T06:00:00"
}
```

---

## 更新日志

### 2026-03-02
- ✅ 新增帖子编辑功能
- ✅ 新增帖子软删除
- ✅ 新增帖子搜索
- ✅ 新增热门帖子列表
- ✅ 新增用户帖子列表
- ✅ 新增回复删除功能
- ✅ 新增回复点赞功能
- ✅ 新增软删除字段 (is_deleted, deleted_at)
- ✅ 新增用户资料字段 (avatar, bio, experience, level)
- ✅ 新增通知表
- ✅ 新增回复点赞表
- ✅ 新增标签系统表
- ✅ 新增举报表
