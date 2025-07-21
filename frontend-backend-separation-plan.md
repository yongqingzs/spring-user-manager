# 前后端分离方案

## 1. 项目现状分析

当前 `spring-user-manager` 项目采用前后端耦合的架构，主要特点如下：
*   **后端渲染**：使用 Thymeleaf 模板引擎在后端渲染 HTML 页面。
*   **静态资源**：CSS、JavaScript 等静态资源由 Spring Boot 应用通过 `src/main/resources/static/` 目录提供。
*   **Nginx 代理**：Nginx 配置为将 HTML 相关和 API 接口请求都转发给后端 Web 服务进行处理。

## 2. 前后端分离的技术栈选择

为了实现前后端分离，我们将对现有架构进行改造，并引入新的技术栈。

### 后端（Spring Boot）
*   **核心框架**：Spring Boot
*   **数据访问**：MyBatis-Plus
*   **数据库**：MySQL
*   **缓存**：Redis
*   **安全**：Spring Security (改造为基于 Token 的认证，例如 JWT)
*   **API 规范**：RESTful API
*   **文档**：Swagger/OpenAPI (继续使用，用于生成 API 文档)

### 前端（React）
*   **框架**：**React**：由 Facebook 维护，生态系统庞大，组件化开发，适合大型复杂应用。
*   **UI 组件库**：**Ant Design**：企业级 UI 设计语言和 React UI 组件库，提供高质量组件，加速开发。
*   **包管理工具**：npm 或 Yarn
*   **构建工具**：Webpack (通常集成在 Create React App 或 Vite 等工具中)
*   **状态管理**：**Zustand**：轻量级、高性能的状态管理库，API 简洁。
*   **路由**：React Router
*   **HTTP 客户端**：**Axios**：功能丰富、API 友好的 HTTP 客户端，支持拦截器。

## 3. 前后端如何交互

前后端分离后，交互将主要通过 RESTful API 进行。

*   **通信协议**：HTTP/HTTPS
*   **数据格式**：JSON (后端 API 返回 JSON 数据，前端发送 JSON 数据)
*   **认证机制**：
    *   当前项目使用基于 Session 的认证（Spring Security）。
    *   前后端分离后，推荐改为**基于 Token 的认证**，例如 JWT (JSON Web Token)。
        *   用户登录时，后端验证凭据后生成一个 JWT 并返回给前端。
        *   前端将 JWT 存储在本地（如 localStorage 或 sessionStorage）。
        *   后续请求中，前端在 HTTP 请求头（`Authorization: Bearer <token>`）中携带 JWT 发送给后端。
        *   后端验证 JWT 的有效性，从而识别用户身份并进行授权。
*   **跨域处理 (CORS)**：
    *   由于前端和后端将部署在不同的域名或端口，会产生跨域问题。
    *   后端 Spring Boot 应用需要配置 CORS 策略，允许前端应用的域名进行跨域请求。可以通过 Spring Security 配置或在 Controller 方法上使用 `@CrossOrigin` 注解。

## 4. 新的项目结构

前后端分离后，项目结构将分为两个独立的部分：后端项目和前端项目。

```
.
├── backend/                  # 后端 Spring Boot 项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/userdept/system/
│   │   │   │       ├── UserDeptSystemApplication.java
│   │   │   │       ├── config/             # 移除 Thymeleaf 相关配置，添加 CORS 配置
│   │   │   │       ├── controller/         # 所有控制器返回 JSON 数据，移除视图相关逻辑
│   │   │   │       ├── dto/
│   │   │   │       ├── entity/
│   │   │   │       ├── exception/
│   │   │   │       ├── mapper/
│   │   │   │       ├── service/
│   │   │   │       └── utils/
│   │   │   └── resources/
│   │   │       ├── application.yml         # 应用配置
│   │   │       ├── logback-spring.xml
│   │   │       ├── db/
│   │   │       └── mapper/
│   │   └── test/
│   ├── pom.xml               # 移除 Thymeleaf 依赖
│   └── Dockerfile            # 后端应用的 Dockerfile
│
├── frontend/                 # 前端应用 (React 项目)
│   ├── public/               # 静态文件，如 index.html, favicon.ico
│   ├── src/
│   │   ├── assets/           # 静态资源，如图片、字体
│   │   ├── components/       # 可复用组件
│   │   ├── pages/            # 页面组件
│   │   ├── services/         # API 调用服务
│   │   ├── store/            # 状态管理 (如 Redux/Zustand/Context API)
│   │   ├── router/           # 路由配置
│   │   ├── App.js            # 应用主组件
│   │   └── index.js          # 入口文件
│   ├── package.json          # 前端依赖管理
│   ├── .env                  # 环境变量配置 (如后端 API 地址)
│   └── Dockerfile            # 前端应用的 Dockerfile (可选，如果前端也容器化部署)
│
├── nginx/                    # Nginx 配置文件
│   ├── nginx.conf            # 调整 Nginx 配置，直接服务前端静态文件，并转发 API 请求到后端
│   └── logs/
│
├── docker-compose.yaml       # Docker Compose 配置，管理后端、前端、数据库、Redis 等服务
│
└── README.md                 # 更新 README，说明新的架构和部署方式
```

## 5. 其他需要知会的事项

*   **部署复杂性增加**：前后端分离后，部署将涉及两个独立的应用（前端和后端），可能需要独立的 CI/CD 流程。Docker Compose 可以简化本地开发和部署。
*   **安全性考虑**：
    *   **JWT 安全**：JWT 默认不加密，敏感信息不应直接放入 JWT Payload。
    *   **XSS/CSRF 防护**：前端框架通常会提供 XSS 防护。由于不再使用 Session，CSRF 攻击的风险会降低，但仍需注意其他安全漏洞。
    *   **HTTPS**：生产环境务必使用 HTTPS 加密前后端通信。
*   **前端路由**：前端应用将负责所有路由，通常采用 HTML5 History 模式或 Hash 模式。Nginx 配置需要配合前端路由，确保刷新页面时能正确加载前端应用。
*   **错误处理**：前后端都需要有完善的错误处理机制。后端返回清晰的错误码和错误信息，前端根据错误信息进行用户提示。
*   **开发流程**：前后端团队可以并行开发，提高开发效率。前端可以通过 Mock 数据进行独立开发。
*   **性能优化**：前端可以利用 CDN 加速静态资源加载，后端可以专注于 API 响应速度。
*   **日志和监控**：需要为前端和后端分别设置日志和监控系统，以便于问题排查和性能分析。

## 6. 执行步骤 (Roadmap)

以下是实现前后端分离的建议执行步骤：

### 阶段一：后端 API 化改造

1.  **改造 Controller 层**：
    *   将所有返回 `ModelAndView` 或视图名称的 `@Controller` 类改为 `@RestController`。
    *   确保所有 API 接口都返回 JSON 数据（使用 `@ResponseBody` 或直接在 `@RestController` 中返回对象）。
    *   修改 `IndexController.java` 和 `AuthController.java` 等，使其不再处理页面跳转，而是返回 JSON 响应或重定向到前端应用的登录/主页。
2.  **实现基于 Token 的认证**：
    *   修改 `backend/src/main/java/com/userdept/system/config/SecurityConfig.java`，禁用 CSRF 防护（如果使用 JWT），并配置 Spring Security 以支持 JWT 认证。
    *   引入 JWT 相关的库（如 `jjwt`）。
    *   在 `AuthService` 或新的认证服务中实现 JWT 的生成、解析和验证逻辑。
    *   修改登录接口，使其在认证成功后返回 JWT。
3.  **配置 CORS**：
    *   在 `backend/src/main/java/com/userdept/system/config/SecurityConfig.java` 中添加 CORS 配置，允许前端应用的域名访问后端 API。
    *   或者在需要跨域访问的 Controller 方法或类上使用 `@CrossOrigin` 注解。
4.  **测试后端 API**：
    *   使用 Postman 或其他 API 测试工具，确保所有后端 API 都能正常工作并返回预期的 JSON 数据。
    *   特别测试认证和授权流程。

### 阶段二：前端 React 应用开发

1.  **创建 React 项目**：
    *   在项目根目录（与 `backend` 同级）下创建 `frontend` 目录。
    *   使用 Create React App (CRA) 或 Vite 初始化一个新的 React 项目：
        ```bash
        # 使用 CRA
        npx create-react-app frontend --template typescript # 如果使用 TypeScript
        # 或者
        npx create-react-app frontend # 如果使用 JavaScript

        # 使用 Vite (更推荐，更快更轻量)
        npm create vite@latest frontend -- --template react-ts # 如果使用 TypeScript
        # 或者
        npm create vite@latest frontend -- --template react # 如果使用 JavaScript
        ```
    *   进入 `frontend` 目录并安装依赖：`cd frontend && npm install` 或 `yarn install`。
2.  **设计前端路由**：
    *   安装 `react-router-dom`：`npm install react-router-dom`。
    *   在 `frontend/src/router/` 目录下定义前端路由，映射到不同的页面组件。
3.  **开发页面和组件**：
    *   根据原有的 Thymeleaf 页面，在 React 中重新构建对应的页面组件（如登录页、用户列表页、部门管理页等）。
    *   将公共部分抽象为可复用组件。
4.  **集成 API 调用**：
    *   安装 HTTP 客户端库，如 Axios：`npm install axios`。
    *   在 `frontend/src/services/` 目录下创建服务文件，封装对后端 API 的调用。
    *   配置 Axios 拦截器，自动在请求头中携带 JWT。
5.  **实现认证和状态管理**：
    *   使用 React Context API、Redux 或 Zustand 等管理用户认证状态和全局数据。
    *   实现登录、登出、注册等功能，与后端 API 对接。
6.  **前端构建**：
    *   在开发完成后，构建前端应用：`npm run build` 或 `yarn build`。这将生成一个包含静态文件的 `build` 或 `dist` 目录。

### 阶段三：部署与集成

1.  **更新 Nginx 配置**：
    *   修改 `nginx/nginx.conf`，使其直接服务 `frontend` 构建后的静态文件。
    *   配置 `/api/` 路径的请求转发到后端 Spring Boot 服务。
    *   确保 Nginx 配置支持前端路由的 HTML5 History 模式（`try_files $uri $uri/ /index.html;`）。
2.  **更新 Docker Compose 配置**：
    *   修改 `docker-compose.yaml`，添加前端服务的定义。
    *   确保后端服务和前端服务能够通过内部网络互相访问（例如，前端通过 `http://backend-service-name:8080/api` 访问后端）。
    *   将 Nginx 配置为前端和后端服务的入口。
3.  **测试部署**：
    *   使用 Docker Compose 启动所有服务，验证前后端是否能正常协同工作。
    *   进行全面的功能测试和集成测试。

### 阶段四：后续优化与维护

1.  **持续集成/持续部署 (CI/CD)**：为前后端分别设置自动化 CI/CD 流程。
2.  **性能优化**：对前端进行代码分割、图片优化等，对后端进行性能调优。
3.  **日志和监控**：完善前后端的日志记录和监控系统。
4.  **错误处理**：细化前后端的错误处理逻辑和用户提示。
5.  **安全性增强**：定期进行安全审计，更新依赖，防范新的安全威胁。
