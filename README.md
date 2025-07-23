# 用户部门管理系统

基于 Spring Boot + React 的前后端分离用户部门管理系统。

## 项目结构
```bash
├── backend/                                 # 后端 Spring Boot 项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/
│   │   │   │       └── userdept/
│   │   │   │           └── system/
│   │   │   │               ├── aop/         # 面向切面编程相关（如日志、权限等）
│   │   │   │               ├── config/      # 配置类（如安全、MyBatis、Redis等）
│   │   │   │               ├── controller/  # 控制器（接口入口）
│   │   │   │               ├── dto/         # 数据传输对象（接口参数、返回值）
│   │   │   │               ├── entity/      # 实体类（数据库表映射）
│   │   │   │               ├── exception/   # 异常处理相关
│   │   │   │               ├── mapper/      # MyBatis 映射接口
│   │   │   │               ├── service/     # 业务服务层
│   │   │   │               ├── utils/       # 工具类与辅助方法
│   │   │   │               └── vo/          # 视图对象（前端展示用）
│   │   │   └── resources/
│   │   │       ├── application.yml           # 后端主配置文件
│   │   │       ├── logback-spring.xml        # 日志配置
│   │   │       └── db/migration/             # 数据库迁移脚本（Flyway）
│   ├── pom.xml                               # Maven 依赖配置
│   ├── Dockerfile                            # 后端生产环境 Docker 配置
│   └── Dockerfile.dev                        # 后端开发环境 Docker 配置
├── frontend/                                 # 前端 React + Vite 项目
│   ├── src/                                  # 前端源码目录
│   │   ├── components/                       # 复用的UI组件
│   │   ├── pages/                            # 路由页面（对应具体业务模块）
│   │   ├── router/                           # 路由配置与守卫
│   │   ├── services/                         # API接口与数据请求
│   │   ├── store/                            # 状态管理（如用户认证等）
│   │   ├── App.tsx                           # 应用主入口组件
│   │   └── main.tsx                          # 应用启动入口
│   ├── public/                                # 公共资源（favicon、静态文件等）
│   ├── Dockerfile                             # 前端生产环境 Docker 配置
│   ├── Dockerfile.dev                         # 前端开发环境 Docker 配置
│   ├── package.json                           # 前端依赖配置
│   ├── vite.config.ts                         # Vite 配置文件
├── nginx/                                     # Nginx 配置文件
│   ├── nginx.conf                             # 主配置
│   ├── html/                                  # 错误页等静态页面
│   └── logs/                                  # Nginx 日志
├── doc/                                       # 文档目录
│   └── *.postman_collection.json              # Postman 测试集合
├── docker-compose.yaml                        # Docker Compose 配置
└── README.md                                  # 项目说明文档
```

## 技术栈

### 后端技术栈
- **Spring Boot 3.1.5**: 后端框架
- **Spring Security**: 认证和授权
- **MyBatis-Plus**: 数据访问层
- **MySQL**: 关系型数据库
- **Redis**: 缓存和会话存储
- **JWT**: 无状态认证
- **Flyway**: 数据库版本管理
- **Swagger/OpenAPI**: API 文档生成

### 前端技术栈
- **React 18**: 前端框架
- **TypeScript**: 类型安全
- **Ant Design**: UI 组件库
- **Vite**: 构建工具和开发服务器
- **React Router**: 路由管理
- **Zustand**: 轻量级状态管理
- **Axios**: HTTP 客户端

### 基础设施
- **Docker**: 容器化部署
- **Nginx**: 反向代理和静态文件服务
- **Docker Compose**: 多容器应用管理

## 快速开始

### 环境要求
- Java 17+
- Node.js 16+
- MySQL 8.0+
- Redis 6.0+
- Docker & Docker Compose (可选)

### 使用 Docker Compose (推荐)

1. 克隆项目
```bash
git clone <repository-url>
cd spring-user-manager
```

2. 启动所有服务
```bash
docker-compose up -d
```

3. 访问应用
- 前端应用: http://localhost
- 后端 API: http://localhost:8080
- API 文档: http://localhost:8080/swagger-ui.html

### 手动部署

#### 后端部署

1. 配置数据库
```bash
# 创建数据库
CREATE DATABASE user_dept_system;
```

2. 修改配置文件
```yaml
# backend/src/main/resources/application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/user_dept_system
    username: your_username
    password: your_password
  data:
    redis:
      host: localhost
      port: 6379
```

3. 启动后端
```bash
cd backend
mvn spring-boot:run
```

#### 前端部署

1. 安装依赖
```bash
cd frontend-react
npm install
```

2. 启动开发服务器
```bash
npm run dev
```

3. 构建生产版本
```bash
npm run build
```

## 功能特性

### 用户管理
- ✅ 用户列表查看（分页、搜索）
- ✅ 用户创建、编辑、删除
- ✅ 用户状态管理（启用/禁用）
- ✅ 密码重置功能
- ✅ 用户信息验证

### 部门管理
- ✅ 部门列表查看（分页、搜索）
- ✅ 部门树形结构展示
- ✅ 部门创建、编辑、删除
- ✅ 多级部门结构支持
- ✅ 部门与用户关联管理

### 认证和授权
- ✅ JWT Token 认证
- ✅ 登录登出功能
- ✅ 路由权限保护
- ✅ 自动登出（Token 过期）
- ✅ 密码加密存储

### 系统特性
- ✅ 响应式 UI 设计
- ✅ 中文本地化
- ✅ 统一异常处理
- ✅ API 文档自动生成
- ✅ 数据库版本管理
- ✅ 操作日志记录

## 默认账户

- **用户名**: `ggbond`
- **密码**: `xff123`

## API 文档

详细的 API 文档请查看：
- [后端 API 说明文档](doc/backend-api.md)
- Swagger UI: http://localhost:8080/swagger-ui.html

## 开发指南

### 后端开发
- 使用 Spring Boot DevTools 实现热重载
- 遵循 RESTful API 设计规范
- 使用全局异常处理器统一错误处理
- 通过 AOP 实现请求日志记录

### 前端开发
- 使用 TypeScript 确保类型安全
- 组件化开发，提高代码复用性
- 使用 Ant Design 保持 UI 一致性
- 通过 Axios 拦截器统一处理 API 请求

### 数据库
- 使用 Flyway 进行数据库版本控制
- 所有表都包含创建时间、更新时间等审计字段
- 使用逻辑删除而非物理删除

## 部署说明

### 生产环境部署

1. **数据库配置**
   - 使用独立的 MySQL 实例
   - 配置数据库连接池
   - 定期备份数据

2. **Redis 配置**
   - 配置 Redis 持久化
   - 设置合适的内存策略

3. **应用配置**
   - 修改 JWT 密钥
   - 配置日志级别
   - 设置合适的 JVM 参数

4. **Nginx 配置**
   - 配置 HTTPS
   - 启用 gzip 压缩
   - 设置静态资源缓存

### 安全注意事项

1. **JWT 安全**
   - 生产环境使用强密钥
   - 设置合理的过期时间
   - 考虑实现 Refresh Token

2. **数据库安全**
   - 使用独立的数据库用户
   - 限制数据库用户权限
   - 启用数据库连接加密

3. **应用安全**
   - 定期更新依赖版本
   - 启用 HTTPS
   - 配置防火墙规则

## 测试

### API 测试
项目提供了 Postman 测试集合，位于 `doc/` 目录下：
1. 导入 Postman 集合文件
2. 设置环境变量（如 API 基础 URL）
3. 运行测试用例

### 前端测试
```bash
cd frontend-react
npm test  # 运行单元测试（需要添加测试）
```

## 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 许可证

本项目采用 MIT 许可证。详情请参阅 [LICENSE](LICENSE) 文件。

## 更新日志

### v2.0.0 (当前版本)
- 🎉 完成前后端分离改造
- ✨ 使用 React + TypeScript 重构前端
- 🔒 升级为 JWT 认证方式
- 📚 完善 API 文档
- 🐳 优化 Docker 部署配置

### v1.0.0 (原版本)
- 基于 Spring Boot + Thymeleaf 的单体应用
- 基础的用户和部门管理功能
- Session 认证方式

## 实践记录
- 更改 CDN 服务来源: https://unpkg.com/

- 额外注解实践
```txt
@RequiredArgsConstructor  会为final字段生成构造函数
@Slf4j 封装日志记录
```

- Nginx 反向代理
```txt
Nginx 处理静态资源，发送给前端
Nginx 将 html 相关、 API 接口代理转发给 Web 服务（ Thymeleaf 引擎渲染）
```

## 常用命令行
```bash
# 编译项目
mvn clean compile

# 打包项目
mvn package

# 运行项目
# 不建议打包，因为这几个项目都不相干
java -jar target/spring-lab-1.0-SNAPSHOT.jar

mvn exec:java -Dexec.mainClass="com.userdept.system.UserDeptSystemApplication"
```

### 构建
docker compose
```bash
# 注意：docker compose 下载镜像地址和 dockerfile 中的镜像地址不一致

# 构建并启动所有服务（包含 Nginx）
docker compose up --build  # 构建并启动容器
docker compose up --build -d # 构建并后台运行容器
docker compose up -d  # 后台运行容器

# 停止容器并remove
docker compose down

# 日志
docker compose logs -f  # 实时查看容器日志
docker compose logs -f nginx  # 查看 Nginx 日志
docker compose logs -f web    # 查看 Spring Boot 日志
docker compose logs -f db     # 查看 MySQL 日志
docker compose logs -f redis  # 查看 Redis 日志

# 重启单个服务
docker compose restart nginx
docker compose restart web
```

docker
```bash
# 查看运行中的容器
docker ps

# 进入容器内部
docker exec -it spring-user-manager_web_1 sh
docker exec -it spring-user-manager_db_1 sh
docker compose exec web bash

# 清除悬空镜像
docker image prune

```

sql
```bash
# 查看数据库挂载的物理路径
docker volume inspect spring-user-manager_mysql_data

# 进入 MySQL 容器
# mysql: sql客户端命令行工具  spring-user-manager: 数据库名称
docker exec -it spring-user-manager-db-1 mysql -uuser_dept -puser_dept_pwd user_dept_system
docker compose exec db mysql -uuser_dept -puser_dept_pwd user_dept_system

# 查看所有表
SHOW TABLES;

# 查看用户表结构
DESCRIBE user;

# 查询用户表数据
SELECT * FROM user;

# 查询部门表数据
SELECT * FROM department;

# 查询用户部门关联表
SELECT * FROM user_department;

# 删除用户
DELETE FROM user WHERE username = 'admin';

# 删除表单
DROP TABLE IF EXISTS sys_user;
```