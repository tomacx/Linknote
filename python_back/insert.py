import pymysql
import sys

# 数据库连接配置
db_config = {
    'host': 'localhost',
    'user': 'root',
    'password': '123456',
    'database': 'linknote',
    'cursorclass': pymysql.cursors.DictCursor
}

def insert_pdf_to_db(pdf_file_path, user_id):
    try:
        # 打开 PDF 文件并读取二进制内容
        with open(pdf_file_path, 'rb') as file:
            pdf_content = file.read()

        # 连接到数据库
        connection = pymysql.connect(**db_config)
        try:
            with connection.cursor() as cursor:
                # 插入数据的 SQL 语句
                sql = "INSERT INTO knowledge_base (user_id, file_name, file_type, file_content) VALUES (%s, %s, %s, %s)"
                file_name = pdf_file_path.split('/')[-1] if '/' in pdf_file_path else pdf_file_path.split('\\')[-1]
                file_type = 'pdf'
                # 执行插入操作
                cursor.execute(sql, (user_id, file_name, file_type, pdf_content))
            # 提交事务
            connection.commit()
            print(f"成功将 {file_name} 插入到数据库中。")
        except pymysql.Error as e:
            print(f"插入数据时发生错误: {e}")
            # 回滚事务
            connection.rollback()
    except FileNotFoundError:
        print(f"未找到文件: {pdf_file_path}")
    except Exception as e:
        print(f"发生未知错误: {e}")
    finally:
        if 'connection' in locals() and connection.open:
            connection.close()

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Usage: python insert.py <pdf_file_path> <user_id>")
        sys.exit(1)
    
    pdf_file_path = sys.argv[1]
    user_id = int(sys.argv[2])
    insert_pdf_to_db(pdf_file_path, user_id)