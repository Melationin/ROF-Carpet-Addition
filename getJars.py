import os
import shutil


def get_latest_file_in_libs(libs_path):
    """获取某个 libs 目录中最新修改的文件"""
    files = [
        os.path.join(libs_path, f)
        for f in os.listdir(libs_path)
        if os.path.isfile(os.path.join(libs_path, f))
    ]

    if not files:
        return None

    return max(files, key=os.path.getmtime)


def collect_latest_files():
    """收集每个版本中最新的构建产物"""
    latest_files = []

    if not os.path.exists("versions"):
        print("错误：versions 文件夹不存在！")
        return latest_files

    for version in os.listdir("versions"):
        libs_path = os.path.join("versions", version, "build", "libs")

        if not os.path.isdir(libs_path):
            continue

        latest = get_latest_file_in_libs(libs_path)

        if latest:
            latest_files.append(latest)
            print(f"{version} → 选中: {os.path.basename(latest)}")

    return latest_files


def clear_target_dir(target_dir):
    """清空目标目录"""
    if not os.path.exists(target_dir):
        return

    for file in os.listdir(target_dir):
        file_path = os.path.join(target_dir, file)
        if os.path.isfile(file_path):
            os.remove(file_path)


def copy_files(files):
    """复制文件到目标目录"""
    target_dir = "build\\libs"

    if not os.path.exists(target_dir):
        os.makedirs(target_dir)

    # 先清空
    clear_target_dir(target_dir)
    print(f"\n已清空目标目录: {target_dir}")

    print("\n开始复制...")
    print("-" * 50)

    copied = 0

    for source_file in files:
        file_name = os.path.basename(source_file)
        target_file = os.path.join(target_dir, file_name)

        try:
            shutil.copy2(source_file, target_file)
            print(f"复制 '{file_name}'")
            copied += 1
        except Exception as e:
            print(f"复制 '{file_name}' 时出错: {e}")

    print("-" * 50)
    print(f"复制完成: {copied} 个文件")


def main():
    latest_files = collect_latest_files()

    if not latest_files:
        print("未找到任何可复制文件")
        return

    copy_files(latest_files)


if __name__ == "__main__":
    main()