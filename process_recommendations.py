import pandas as pd
import requests
import json
import time
import urllib.parse
import sys

def main():
    print("开始处理协配流转数据...")

    # 读取Excel文件
    try:
        # 允许从命令行参数指定文件名
        if len(sys.argv) > 1:
            excel_file = sys.argv[1]
        else:
            excel_file = "协配流转.xlsx"

        df = pd.read_excel(excel_file)
        print(f"成功读取Excel文件，共有{len(df)}行数据")

        # 显示列名，帮助调试
        print(f"文件包含的列: {list(df.columns)}")
    except Exception as e:
        print(f"读取Excel文件失败: {e}")
        return

    # 统计变量
    total_rows = len(df)
    success_count = 0
    error_count = 0

    # 添加新列用于存储推荐结果
    df['H'] = ""  # 添加H列用于存储当前办理人
    df['M'] = ""  # 添加M列用于存储领导姓名
    df['Q'] = 0   # 添加Q列用于存储M列和H列的比对结果，默认值为0
    df['推荐领导账号'] = ""
    df['匹配分数'] = ""
    df['推荐理由'] = ""

    # 循环处理每一行
    for index, row in df.iterrows():
        try:
            # 提取协配标题列的值
            task_title = row['协配标题']

            if pd.isna(task_title) or task_title == "":
                print(f"第{index+1}行: 标题为空，跳过")
                error_count += 1
                continue

            print(f"处理第{index+1}行: {task_title}")

            # URL编码任务标题，避免特殊字符问题
            encoded_title = urllib.parse.quote(task_title)

            # 调用API
            api_url = f"http://localhost:8082/api/recommendation/recommend?taskTitle={encoded_title}"
            response = requests.get(api_url)

            # 检查响应状态
            if response.status_code == 200:
                # 解析JSON响应
                result = response.json()

                # 提取需要的字段
                leader_account = result.get('leaderAccount', '')
                score = result.get('score', 0)
                reason = result.get('reason', '')

                # 根据账号设置领导姓名
                leader_name = ''
                if leader_account == 'zb1':
                    leader_name = '赵斌'
                elif leader_account == 'zhangyaohua1':
                    leader_name = '张耀华'
                elif leader_account == 'xuhuiyun':
                    leader_name = '许慧云'

                # 更新DataFrame
                df.at[index, 'M'] = leader_name  # 将领导姓名保存到M列
                df.at[index, '推荐领导账号'] = leader_account
                df.at[index, '匹配分数'] = score
                df.at[index, '推荐理由'] = reason

                print(f"  成功: 推荐领导账号={leader_account}, 领导姓名={leader_name}, 分数={score}")
                success_count += 1
            else:
                print(f"  API请求失败，状态码: {response.status_code}")
                error_count += 1

            # 添加短暂延迟，避免请求过快
            time.sleep(0.1)

        except Exception as e:
            print(f"  处理第{index+1}行时出错: {e}")
            error_count += 1

    # 将当前办理人信息保存到H列
    if '当前办理人' in df.columns:
        for index, row in df.iterrows():
            person_name = str(row['当前办理人']).strip() if not pd.isna(row['当前办理人']) else ''
            df.at[index, 'H'] = person_name
        print("已将当前办理人信息保存到H列")
    else:
        print("警告: 找不到'当前办理人'列，无法填充H列")
        print(f"可用的列: {list(df.columns)}")

    # 根据规则设置Q列的值（M列和H列的比对结果）

    # 应用规则设置Q列的值（M列和H列的比对结果）
    match_count = 0
    for index, row in df.iterrows():
        # 获取H列和M列的值
        h_value = str(row['H']).strip() if not pd.isna(row['H']) else ''
        m_value = str(row['M']).strip() if not pd.isna(row['M']) else ''

        # 打印调试信息，查看前几行的值
        if index < 10:
            print(f"  行 {index+1}: H列(当前办理人)='{h_value}', M列(推荐领导姓名)='{m_value}'")

        # 应用规则 - 比较H列和M列的值
        if (h_value == '赵斌' and m_value == '赵斌') or \
           (h_value == '张耀华' and m_value == '张耀华') or \
           (h_value == '许慧云' and m_value == '许慧云'):
            df.at[index, 'Q'] = 1
            match_count += 1
            print(f"  匹配成功: 行 {index+1}, H列='{h_value}', M列='{m_value}'")

    print(f"根据规则匹配到 {match_count} 行数据，已将Q列标记为1")

    # 保存更新后的Excel文件
    try:
        output_file = "协配流转_处理结果.xlsx"
        df.to_excel(output_file, index=False)
        print(f"已将处理结果保存到 {output_file}")
    except Exception as e:
        print(f"保存Excel文件失败: {e}")

    # 输出执行结果摘要
    print("\n执行结果摘要:")
    print(f"总行数: {total_rows}")
    print(f"成功处理: {success_count}行")
    print(f"处理失败: {error_count}行")
    print(f"成功率: {success_count/total_rows*100:.2f}%")

    # 输出Q列统计
    q_ones = df['Q'].sum()
    print(f"Q列标记为1的行数: {q_ones}")
    print(f"Q列标记为1的比例: {q_ones/total_rows*100:.2f}%")

if __name__ == "__main__":
    main()
