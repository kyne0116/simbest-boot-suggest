#!/bin/bash

echo "正在启动智能领导推荐系统测试..."

# 保存当前 Java 版本
CURRENT_JAVA_VERSION=$(jenv version-name)
echo "当前 Java 版本: $CURRENT_JAVA_VERSION"

# 切换到 JDK 17
echo "切换到 JDK 17..."
jenv local 17
echo "已切换到 Java 版本: $(jenv version-name)"

# 设置测试参数
BASE_URL="http://localhost"
PORT="58080"
GENERATE_ONLY=false
TEST_CASE_FILE="docs/6-测试用例设计.md"

# 解析命令行参数
while [[ $# -gt 0 ]]; do
  case $1 in
    -baseUrl)
      BASE_URL="$2"
      shift 2
      ;;
    -port)
      PORT="$2"
      shift 2
      ;;
    -generateOnly)
      GENERATE_ONLY=true
      shift
      ;;
    -testCaseFile)
      TEST_CASE_FILE="$2"
      shift 2
      ;;
    *)
      echo "未知参数: $1"
      shift
      ;;
  esac
done

echo "测试配置:"
echo "- 基础URL: $BASE_URL"
echo "- 端口: $PORT"
echo "- 仅生成测试代码: $GENERATE_ONLY"
echo "- 测试用例文件: $TEST_CASE_FILE"

# 执行Maven命令
if [ "$GENERATE_ONLY" = true ]; then
  mvn exec:java -Dexec.mainClass="com.simbest.boot.suggest.test.AutoTestRunner" -Dexec.args="-baseUrl $BASE_URL -port $PORT -generateOnly -testCaseFile $TEST_CASE_FILE"
else
  mvn exec:java -Dexec.mainClass="com.simbest.boot.suggest.test.AutoTestRunner" -Dexec.args="-baseUrl $BASE_URL -port $PORT -testCaseFile $TEST_CASE_FILE"
fi

echo "测试执行完成，请查看测试报告: test-output/custom-report.html"

# 恢复原来的 Java 版本
echo "恢复到原来的 Java 版本: $CURRENT_JAVA_VERSION"
jenv local $CURRENT_JAVA_VERSION
