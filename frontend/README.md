# 用户部门管理系统 - 前端

基于 React + TypeScript + Ant Design + Vite 构建的用户部门管理系统前端应用。

## 技术栈

- **React 18**: 前端框架
- **TypeScript**: 类型安全
- **Ant Design**: UI 组件库
- **Vite**: 构建工具
- **React Router**: 路由管理
- **Zustand**: 状态管理
- **Axios**: HTTP 客户端

## 项目结构

```
src/
├── components/          # 通用组件
│   └── AppLayout.tsx   # 应用布局组件
├── pages/              # 页面组件
│   ├── LoginPage.tsx   # 登录页面
│   ├── HomePage.tsx    # 首页
│   ├── UserManagePage.tsx      # 用户管理页面
│   └── DepartmentManagePage.tsx # 部门管理页面
├── services/           # API 服务
│   ├── api.ts         # API 基础配置
│   ├── auth.ts        # 认证相关 API
│   ├── user.ts        # 用户相关 API
│   └── department.ts  # 部门相关 API
├── store/             # 状态管理
│   └── auth.ts        # 认证状态
├── router/            # 路由配置
│   └── PrivateRoute.tsx # 私有路由组件
├── App.tsx            # 应用主组件
└── main.tsx           # 应用入口
```

## 功能特性

### 认证系统
- JWT Token 认证
- 自动登录状态持久化
- 路由保护
- 自动登出（Token 过期）

### 用户管理
- 用户列表查看（分页、搜索）
- 用户创建、编辑、删除
- 用户状态管理（启用/禁用）
- 密码重置

### 部门管理
- 部门列表查看（分页、搜索）
- 部门树形结构展示
- 部门创建、编辑、删除
- 支持多级部门结构

### UI/UX 特性
- 响应式设计
- 中文本地化
- 统一的错误处理
- 友好的用户提示
- 现代化的界面设计

## 开发

### 环境要求

- Node.js >= 16
- npm 或 yarn
- 后端服务运行在 http://localhost:8080

### 安装依赖

```bash
npm install
```

### 启动开发服务器

```bash
npm run dev
```

开发服务器将运行在 http://localhost:3000

### 构建生产版本

```bash
npm run build
```

构建文件将输出到 `dist` 目录。

### 预览生产构建

```bash
npm run preview
```

## 环境配置

### 开发环境 (.env.development)
```
VITE_API_BASE_URL=http://localhost:8080/api/v1
```

### 生产环境 (.env.production)
```
VITE_API_BASE_URL=/api/v1
```

## API 集成

### 请求拦截器
- 自动添加 JWT Token 到请求头
- 统一的错误处理

### 响应拦截器
- 自动处理 401 认证失败
- 统一的错误格式处理

### 服务层设计
每个业务模块都有对应的服务文件：
- `auth.ts`: 登录、登出
- `user.ts`: 用户 CRUD 操作
- `department.ts`: 部门 CRUD 操作

## 状态管理

使用 Zustand 进行轻量级状态管理：

### 认证状态 (useAuthStore)
```typescript
interface AuthState {
  token: string | null;
  user: User | null;
  isAuthenticated: boolean;
  login: (token: string, user: User) => void;
  logout: () => void;
  setUser: (user: User) => void;
}
```

状态自动持久化到 localStorage。

## 路由设计

### 公开路由
- `/login` - 登录页面

### 私有路由（需要认证）
- `/` - 首页
- `/users` - 用户管理
- `/departments` - 部门管理

### 路由保护
使用 `PrivateRoute` 组件保护需要认证的路由，未登录用户会自动重定向到登录页面。

## 组件设计

### AppLayout
- 统一的应用布局
- 顶部导航栏
- 用户信息展示
- 退出登录功能

### 页面组件
每个页面都是独立的 React 组件，包含：
- 数据获取和状态管理
- 用户交互处理
- 错误处理
- 加载状态

## 样式和主题

- 使用 Ant Design 的设计系统
- 中文语言包配置
- 响应式布局
- 统一的颜色和字体

## 错误处理

### API 错误处理
- 网络错误自动重试
- 401 错误自动登出
- 用户友好的错误提示

### 表单验证
- 实时输入验证
- 统一的验证规则
- 清晰的错误提示

## 性能优化

### 代码分割
- 路由级别的懒加载
- 组件按需加载

### 请求优化
- 请求去重
- 合理的缓存策略
- 分页加载

### 构建优化
- Vite 快速构建
- 资源压缩
- Tree Shaking

## 部署

### Nginx 配置示例

```nginx
server {
    listen 80;
    server_name your-domain.com;
    root /path/to/dist;
    index index.html;

    # 前端路由支持
    location / {
        try_files $uri $uri/ /index.html;
    }

    # API 代理
    location /api/ {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # 静态资源缓存
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
```

### Docker 部署

可以创建 Dockerfile 进行容器化部署：

```dockerfile
# 构建阶段
FROM node:18-alpine as build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# 生产阶段
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

## 开发规范

### 代码规范
- 使用 TypeScript 严格模式
- 组件使用 React.FC 类型
- 统一的文件命名规范

### Git 提交规范
- feat: 新功能
- fix: 错误修复
- docs: 文档更新
- style: 代码格式化
- refactor: 代码重构

### 测试
虽然当前版本未包含测试，但推荐添加：
- 单元测试 (Jest + React Testing Library)
- 端到端测试 (Cypress)

## 常见问题

### Q: 登录后刷新页面需要重新登录？
A: 检查 localStorage 中是否正确保存了认证信息，确保 Zustand persist 配置正确。

### Q: API 请求 CORS 错误？
A: 确保后端配置了正确的 CORS 策略，开发环境可以使用 Vite 代理。

### Q: 构建后静态资源路径错误？
A: 检查 Vite 配置中的 base 路径和 Nginx 配置是否匹配。

## 后续优化建议

1. **国际化支持**: 添加 i18n 支持多语言
2. **主题切换**: 支持深色模式
3. **权限管理**: 细粒度的权限控制
4. **数据缓存**: 添加 React Query 进行数据缓存
5. **单测覆盖**: 添加完整的测试覆盖
6. **PWA 支持**: 支持离线访问
7. **性能监控**: 添加性能监控和错误追踪
