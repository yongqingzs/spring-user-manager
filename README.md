## 组件
- 数据传输（无业务逻辑）
```txt
DTO（Data Transfer Object，数据传输对象）
用于在不同层（如前端和后端、服务与服务之间）传递数据，主要目的是封装数据，减少多次远程调用的次数。DTO 通常用于前后端或服务间的数据交互。

VO（View Object，视图对象）
用于展示层，封装页面或接口需要展示的数据。VO 通常是后端返回给前端的数据结构，更贴合前端展示需求。
```

## 项目结构
```bash
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── userdept/
│   │           └── system/
│   │               ├── UserDeptSystemApplication.java      # 应用入口
│   │               ├── config/                             # 配置类
│   │               │   ├── MybatisPlusConfig.java
│   │               │   ├── SecurityConfig.java
│   │               │   └── SystemInitializer.java
│   │               ├── controller/                         # 控制器
│   │               │   ├── AuthController.java
│   │               │   ├── DepartmentController.java
│   │               │   ├── IndexController.java
│   │               │   └── UserController.java
│   │               ├── dto/                                # 数据传输对象
│   │               │   ├── DepartmentDTO.java
│   │               │   └── UserDTO.java
│   │               ├── entity/                             # 实体类
│   │               │   ├── Department.java
│   │               │   ├── User.java
│   │               │   └── UserDepartment.java
│   │               ├── exception/                          # 异常处理
│   │               │   └── GlobalExceptionHandler.java
│   │               ├── mapper/                             # MyBatis-Plus映射器
│   │               │   ├── DepartmentMapper.java
│   │               │   ├── UserDepartmentMapper.java
│   │               │   └── UserMapper.java
│   │               ├── service/                            # 服务层接口
│   │               │   ├── AuthService.java
│   │               │   ├── DepartmentService.java
│   │               │   └── UserService.java
│   │               ├── service/impl/                       # 服务实现
│   │               │   ├── AuthServiceImpl.java
│   │               │   ├── DepartmentServiceImpl.java
│   │               │   ├── UserDetailsServiceImpl.java
│   │               │   └── UserServiceImpl.java
│   │               ├── utils/                              # 工具类
│   │               │   └── PasswordUtil.java
│   │               ├── vo/                                 # 视图对象
│   │               │   ├── PageVO.java
│   │               │   └── ResultVO.java
│   └── resources/
│       ├── application.yml                                 # 应用配置
│       ├── mapper/                                         # MyBatis XML映射文件
│       ├── static/                                         # 静态资源
│       └── templates/                                      # Thymeleaf模板
└── test/                                                   # 测试代码
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

# 构建
docker compose up --build  # 构建并启动容器
docker compose up --build -d # 构建并后台运行容器
docker compose up -d  # 后台运行容器

# 停止容器并remove
docker compose down

# 日志
docker compose logs -f  # 实时查看容器日志
docker compose logs -f web
docker compose logs -f db
```

docker
```bash
# 查看运行中的容器
docker ps

# 进入容器内部
docker exec -it flask-user-app_web_1 bash
docker exec -it flask-user-app_db_1 bash

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