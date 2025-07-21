# 后端 API 说明文档

## 概述

本文档描述了用户部门管理系统的后端 API 接口。后端基于 Spring Boot 构建，提供 RESTful API 服务。

## 基础信息

- **基础URL**: `http://localhost:8080/api/v1`
- **认证方式**: JWT Bearer Token
- **数据格式**: JSON
- **字符编码**: UTF-8

## 认证机制

### JWT Token 认证

除了登录接口外，所有 API 接口都需要在请求头中携带 JWT Token：

```
Authorization: Bearer <your-jwt-token>
```

Token 过期后需要重新登录获取新的 Token。

## API 接口

### 1. 认证相关接口

#### 1.1 用户登录

**接口地址**: `POST /auth/login`

**请求参数**:
```json
{
  "username": "string",
  "password": "string"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### 1.2 用户注销

**接口地址**: `POST /auth/logout`

**请求头**: 需要 Authorization

**响应示例**:
```json
{
  "code": 200,
  "message": "注销成功"
}
```

### 2. 用户管理接口

#### 2.1 获取用户列表

**接口地址**: `GET /users`

**请求参数**:
- `page`: 页码 (默认: 1)
- `per_page`: 每页数量 (默认: 10)
- `query`: 搜索关键词 (可选)

**请求示例**: `GET /users?page=1&per_page=10&query=张三`

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "records": [
      {
        "id": 1,
        "username": "zhangsan",
        "realname": "张三",
        "email": "zhangsan@example.com",
        "mobile": "13800138000",
        "status": 1,
        "enabled": true,
        "createdTime": "2024-01-01T10:00:00",
        "updatedTime": "2024-01-01T10:00:00"
      }
    ],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

#### 2.2 获取用户详情

**接口地址**: `GET /users/{id}`

**路径参数**:
- `id`: 用户ID

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": 1,
    "username": "zhangsan",
    "realname": "张三",
    "email": "zhangsan@example.com",
    "mobile": "13800138000",
    "status": 1,
    "enabled": true,
    "createdTime": "2024-01-01T10:00:00",
    "updatedTime": "2024-01-01T10:00:00"
  }
}
```

#### 2.3 创建用户

**接口地址**: `POST /users`

**请求参数**:
```json
{
  "username": "string",
  "password": "string",
  "realname": "string",
  "email": "string",
  "mobile": "string"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": 1,
    "username": "zhangsan",
    "realname": "张三",
    "email": "zhangsan@example.com",
    "mobile": "13800138000",
    "status": 1,
    "enabled": true,
    "createdTime": "2024-01-01T10:00:00",
    "updatedTime": "2024-01-01T10:00:00"
  }
}
```

#### 2.4 更新用户

**接口地址**: `PUT /users/{id}`

**路径参数**:
- `id`: 用户ID

**请求参数**:
```json
{
  "realname": "string",
  "email": "string",
  "mobile": "string"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "id": 1,
    "username": "zhangsan",
    "realname": "张三修改",
    "email": "zhangsan@example.com",
    "mobile": "13800138000",
    "status": 1,
    "enabled": true,
    "createdTime": "2024-01-01T10:00:00",
    "updatedTime": "2024-01-01T11:00:00"
  }
}
```

#### 2.5 删除用户

**接口地址**: `DELETE /users/{id}`

**路径参数**:
- `id`: 用户ID

**响应示例**:
```json
{
  "code": 200,
  "message": "删除成功"
}
```

#### 2.6 更新用户状态

**接口地址**: `PATCH /users/{id}/status`

**路径参数**:
- `id`: 用户ID

**请求参数**:
```json
{
  "enabled": true
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "状态更新成功"
}
```

#### 2.7 重置用户密码

**接口地址**: `PATCH /users/{id}/reset-password`

**路径参数**:
- `id`: 用户ID

**响应示例**:
```json
{
  "code": 200,
  "message": "密码重置成功"
}
```

### 3. 部门管理接口

#### 3.1 获取部门列表

**接口地址**: `GET /departments`

**请求参数**:
- `page`: 页码 (默认: 1)
- `per_page`: 每页数量 (默认: 10)
- `search`: 搜索关键词 (可选)

**请求示例**: `GET /departments?page=1&per_page=10&search=技术部`

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "records": [
      {
        "id": 1,
        "code": "TECH",
        "name": "技术部",
        "parentCode": null,
        "description": "技术研发部门",
        "createdTime": "2024-01-01T10:00:00",
        "updatedTime": "2024-01-01T10:00:00"
      }
    ],
    "total": 10,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

#### 3.2 获取部门树形结构

**接口地址**: `GET /departments/tree`

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": [
    {
      "id": 1,
      "code": "TECH",
      "name": "技术部",
      "parentCode": null,
      "description": "技术研发部门",
      "children": [
        {
          "id": 2,
          "code": "TECH_DEV",
          "name": "开发组",
          "parentCode": "TECH",
          "description": "软件开发组",
          "children": []
        }
      ]
    }
  ]
}
```

#### 3.3 获取部门详情

**接口地址**: `GET /departments/{id}`

**路径参数**:
- `id`: 部门ID

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": 1,
    "code": "TECH",
    "name": "技术部",
    "parentCode": null,
    "description": "技术研发部门",
    "createdTime": "2024-01-01T10:00:00",
    "updatedTime": "2024-01-01T10:00:00"
  }
}
```

#### 3.4 创建部门

**接口地址**: `POST /departments`

**请求参数**:
```json
{
  "code": "string",
  "name": "string",
  "parentCode": "string",
  "description": "string"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": 1,
    "code": "TECH",
    "name": "技术部",
    "parentCode": null,
    "description": "技术研发部门",
    "createdTime": "2024-01-01T10:00:00",
    "updatedTime": "2024-01-01T10:00:00"
  }
}
```

#### 3.5 更新部门

**接口地址**: `PUT /departments/{id}`

**路径参数**:
- `id`: 部门ID

**请求参数**:
```json
{
  "name": "string",
  "parentCode": "string",
  "description": "string"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "id": 1,
    "code": "TECH",
    "name": "技术部修改",
    "parentCode": null,
    "description": "技术研发部门",
    "createdTime": "2024-01-01T10:00:00",
    "updatedTime": "2024-01-01T11:00:00"
  }
}
```

#### 3.6 删除部门

**接口地址**: `DELETE /departments/{id}`

**路径参数**:
- `id`: 部门ID

**响应示例**:
```json
{
  "code": 200,
  "message": "删除成功"
}
```

## 错误处理

### 错误响应格式

所有错误响应都遵循统一的格式：

```json
{
  "code": 400,
  "message": "错误描述信息"
}
```

### 常见错误码

- `400`: 请求参数错误
- `401`: 未认证或认证失败
- `403`: 权限不足
- `404`: 资源不存在
- `500`: 服务器内部错误

### 具体错误示例

#### 参数验证失败
```json
{
  "code": 400,
  "message": "参数验证失败: 用户名不能为空"
}
```

#### 认证失败
```json
{
  "code": 401,
  "message": "无效的 Token"
}
```

#### 资源不存在
```json
{
  "code": 400,
  "message": "用户不存在"
}
```

#### 业务逻辑错误
```json
{
  "code": 400,
  "message": "用户名已存在，请更换用户名"
}
```

## 开发环境测试

### 使用 Postman 测试

项目中包含了 Postman 集合文件：`doc/望子成龙小学职工管理系统(Separated).postman_collection.json`

导入步骤：
1. 打开 Postman
2. 点击 Import 按钮
3. 选择项目中的 JSON 文件
4. 导入后可以直接测试所有接口

### 测试流程

1. **登录获取 Token**
   - 使用 `POST /auth/login` 接口登录
   - 获取返回的 JWT Token

2. **设置环境变量**
   - 在 Postman 中设置环境变量 `jwt`
   - 将获取的 Token 设置为该变量的值

3. **测试其他接口**
   - 其他接口会自动使用环境变量中的 Token 进行认证

### 默认测试数据

- **用户名**: `ggbond`
- **密码**: `xff123`

## 部署说明

### 开发环境

1. 启动后端服务（端口 8080）
2. 前端通过代理访问后端 API

### 生产环境

1. 后端服务部署在 8080 端口
2. 前端静态文件通过 Nginx 提供服务
3. Nginx 配置 API 代理到后端服务

### Docker 部署

使用项目根目录的 `docker-compose.yaml` 文件：

```bash
docker-compose up -d
```

## 安全注意事项

1. **JWT Token 安全**
   - Token 有效期设置合理
   - 敏感信息不存储在 Token 中
   - 生产环境使用 HTTPS

2. **API 安全**
   - 所有接口都有认证和授权检查
   - 输入参数验证
   - SQL 注入防护

3. **CORS 配置**
   - 生产环境配置具体的允许域名
   - 不使用通配符 `*`

## 版本信息

- **API 版本**: v1
- **Spring Boot 版本**: 3.1.5
- **Java 版本**: 17
- **数据库**: MySQL 8.0
- **缓存**: Redis
