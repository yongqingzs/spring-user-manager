# 生产环境 Dockerfile
FROM node:18-alpine AS builder

WORKDIR /app

COPY package*.json ./
RUN npm install --production=false
COPY . .
RUN npm run build

# 使用更小的镜像运行静态文件
FROM node:18-alpine
WORKDIR /app

RUN npm install -g serve
COPY --from=builder /app/dist ./dist

EXPOSE 3000
CMD ["serve", "-s", "dist", "-l", "3000"]
