#!/bin/bash

echo "正在启动智能领导推荐服务..."

# 编译项目
echo "编译项目..."
mvn clean package -DskipTests

# 启动服务
echo "启动服务..."
java -jar target/suggest.jar --server.port=12345 &

# 等待服务启动
echo "等待服务启动..."
sleep 15

# 检查服务是否正常启动
echo "检查服务是否正常启动..."
response=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:12346/suggest/hello/hi)

if [ "$response" == "200" ]; then
    echo "服务启动成功，可以通过 http://localhost:12346/suggest 访问"
else
    echo "服务可能未正常启动，HTTP状态码: $response"
    echo "请检查日志文件获取更多信息"
fi
