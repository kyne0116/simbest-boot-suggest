#!/bin/bash

# 固定参数
BASE_URL="http://localhost"
PORT="12345"

echo "===== 智能领导推荐系统测试 ====="
echo "测试配置:"
echo "- 基础URL: $BASE_URL"
echo "- 端口: $PORT"

# 保存当前 Java 版本
CURRENT_JAVA_VERSION=$(jenv version-name)
echo "当前 Java 版本: $CURRENT_JAVA_VERSION"

# 切换到 JDK 17
echo "切换到 JDK 17..."
jenv local 17
echo "已切换到 Java 版本: $(jenv version-name)"

# 更新TestNG配置文件中的参数
sed -i '' "s|<parameter name=\"baseUrl\" value=\".*\"/>|<parameter name=\"baseUrl\" value=\"$BASE_URL\"/>|g" src/test/resources/testng-recommendation-641.xml
sed -i '' "s|<parameter name=\"port\" value=\".*\"/>|<parameter name=\"port\" value=\"$PORT\"/>|g" src/test/resources/testng-recommendation-641.xml

echo "已更新测试配置文件: src/test/resources/testng-recommendation-641.xml"

# 编译测试类
echo "编译测试类..."
mvn clean compile test-compile

# 使用TestNG命令行工具运行测试
echo "运行测试..."
# 创建输出目录（如果不存在）
mkdir -p test-output

# 设置自定义报告文件名
REPORT_FILE="test-output/recommendation-641-report.html"

# 运行测试并指定输出报告文件
java -cp target/test-classes:target/classes:$(mvn dependency:build-classpath -Dmdep.outputFile=/dev/stdout -q) org.testng.TestNG -d test-output/recommendation-641 src/test/resources/testng-recommendation-641.xml

# 复制自定义报告并重命名
if [ -f "test-output/custom-report.html" ]; then
    cp test-output/custom-report.html $REPORT_FILE
    echo "测试执行完成，请查看测试报告: $REPORT_FILE"
else
    echo "测试执行完成，但未找到自定义报告文件"
fi

# 恢复原来的 Java 版本
echo "恢复到原来的 Java 版本: $CURRENT_JAVA_VERSION"
jenv local $CURRENT_JAVA_VERSION
