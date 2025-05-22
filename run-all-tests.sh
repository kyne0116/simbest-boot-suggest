#!/bin/bash

# 固定参数
BASE_URL="http://localhost"
PORT="12345"

echo "===== 智能领导推荐系统全部测试 ====="
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

# 更新所有TestNG配置文件中的参数
echo "更新所有测试配置文件..."

# 更新推荐功能测试配置
sed -i '' "s|<parameter name=\"baseUrl\" value=\".*\"/>|<parameter name=\"baseUrl\" value=\"$BASE_URL\"/>|g" src/test/resources/testng-recommendation-641.xml
sed -i '' "s|<parameter name=\"port\" value=\".*\"/>|<parameter name=\"port\" value=\"$PORT\"/>|g" src/test/resources/testng-recommendation-641.xml

# 更新职责领域测试配置
sed -i '' "s|<parameter name=\"baseUrl\" value=\".*\"/>|<parameter name=\"baseUrl\" value=\"$BASE_URL\"/>|g" src/test/resources/testng-domain-642.xml
sed -i '' "s|<parameter name=\"port\" value=\".*\"/>|<parameter name=\"port\" value=\"$PORT\"/>|g" src/test/resources/testng-domain-642.xml

# 更新组织关系测试配置
sed -i '' "s|<parameter name=\"baseUrl\" value=\".*\"/>|<parameter name=\"baseUrl\" value=\"$BASE_URL\"/>|g" src/test/resources/testng-organization-643.xml
sed -i '' "s|<parameter name=\"port\" value=\".*\"/>|<parameter name=\"port\" value=\"$PORT\"/>|g" src/test/resources/testng-organization-643.xml

# 更新批复历史测试配置
sed -i '' "s|<parameter name=\"baseUrl\" value=\".*\"/>|<parameter name=\"baseUrl\" value=\"$BASE_URL\"/>|g" src/test/resources/testng-history-644.xml
sed -i '' "s|<parameter name=\"port\" value=\".*\"/>|<parameter name=\"port\" value=\"$PORT\"/>|g" src/test/resources/testng-history-644.xml

# 更新反馈机制测试配置
sed -i '' "s|<parameter name=\"baseUrl\" value=\".*\"/>|<parameter name=\"baseUrl\" value=\"$BASE_URL\"/>|g" src/test/resources/testng-feedback-645.xml
sed -i '' "s|<parameter name=\"port\" value=\".*\"/>|<parameter name=\"port\" value=\"$PORT\"/>|g" src/test/resources/testng-feedback-645.xml

echo "已更新所有测试配置文件"

# 编译测试类
echo "编译测试类..."
mvn clean compile test-compile

# 创建合并的TestNG配置文件
echo "创建合并的TestNG配置文件..."
cat > src/test/resources/testng-all.xml << EOF
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="智能领导推荐系统全部测试套件">
  <parameter name="baseUrl" value="$BASE_URL"/>
  <parameter name="port" value="$PORT"/>
  <listeners>
    <listener class-name="com.simbest.boot.suggest.test.report.TestReportGenerator"/>
  </listeners>
  <test name="6.4.1推荐功能测试">
    <classes>
      <class name="com.simbest.boot.suggest.test.RecommendationTest641"/>
    </classes>
  </test>
  <test name="6.4.2职责领域测试">
    <classes>
      <class name="com.simbest.boot.suggest.test.DomainTest642"/>
    </classes>
  </test>
  <test name="6.4.3组织关系测试">
    <classes>
      <class name="com.simbest.boot.suggest.test.OrganizationTest643"/>
    </classes>
  </test>
  <test name="6.4.4批复历史测试">
    <classes>
      <class name="com.simbest.boot.suggest.test.HistoryTest644"/>
    </classes>
  </test>
  <test name="6.4.5反馈机制测试">
    <classes>
      <class name="com.simbest.boot.suggest.test.FeedbackTest645"/>
    </classes>
  </test>
</suite>
EOF

echo "已创建合并的TestNG配置文件: src/test/resources/testng-all.xml"

# 使用TestNG命令行工具运行测试
echo "运行所有测试..."
# 创建输出目录（如果不存在）
mkdir -p test-output

# 设置自定义报告文件名
REPORT_FILE="test-output/all-tests-report.html"

# 运行测试并指定输出报告文件
java -cp target/test-classes:target/classes:$(mvn dependency:build-classpath -Dmdep.outputFile=/dev/stdout -q) org.testng.TestNG -d test-output/all-tests src/test/resources/testng-all.xml

# 复制自定义报告并重命名
if [ -f "test-output/custom-report.html" ]; then
    cp test-output/custom-report.html $REPORT_FILE
    echo "所有测试执行完成，请查看测试报告: $REPORT_FILE"
else
    echo "所有测试执行完成，但未找到自定义报告文件"
fi

# 恢复原来的 Java 版本
echo "恢复到原来的 Java 版本: $CURRENT_JAVA_VERSION"
jenv local $CURRENT_JAVA_VERSION
