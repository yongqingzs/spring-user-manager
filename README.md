## 项目结构
```bash
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── userdept/
│   │           ├── UserDeptApplication.java          # 应用入口
│   │           ├── config/                           # 配置类
│   │           │   ├── SecurityConfig.java          
│   │           │   ├── WebConfig.java
│   │           │   └── MybatisPlusConfig.java
│   │           ├── controller/                       # 控制器 (对应Flask中的路由)
│   │           │   ├── AuthController.java
│   │           │   ├── UserController.java
│   │           │   └── DepartmentController.java
│   │           ├── service/                          # 服务层 (对应services/)
│   │           │   ├── AuthService.java
│   │           │   ├── UserService.java
│   │           │   └── DepartmentService.java
│   │           ├── mapper/                           # MyBatis-Plus映射器
│   │           │   ├── UserMapper.java
│   │           │   ├── DepartmentMapper.java
│   │           │   └── UserDepartmentMapper.java
│   │           ├── entity/                           # 实体类 (对应models/)
│   │           │   ├── User.java
│   │           │   ├── Department.java
│   │           │   └── UserDepartment.java
│   │           ├── dto/                              # 数据传输对象
│   │           │   ├── UserDTO.java
│   │           │   └── DepartmentDTO.java
│   │           ├── vo/                               # 视图对象
│   │           │   ├── UserVO.java
│   │           │   └── DepartmentVO.java
│   │           └── common/                           # 通用类
│   │               ├── exception/                    # 异常处理
│   │               │   └── GlobalExceptionHandler.java
│   │               ├── security/                     # 安全相关
│   │               │   └── PasswordUtils.java
│   │               ├── util/                         # 工具类
│   │               └── result/                       # 统一响应结构
│   │                   └── Result.java
│   └── resources/
│       ├── application.yml                           # 应用配置
│       ├── application-dev.yml                       # 开发环境配置
│       ├── application-prod.yml                      # 生产环境配置
│       ├── mapper/                                   # MyBatis XML映射文件
│       ├── static/                                   # 静态资源 (从Flask复制)
│       └── templates/                                # Thymeleaf模板 (从Flask转换)
└── test/                                             # 测试代码
```
