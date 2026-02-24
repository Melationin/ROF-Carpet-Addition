import urllib.request, json
from typing import Optional
import requests
import re


def get_carpet_core_version(mc_version: str) -> str:
    """
    通过 Modrinth API 获取指定 Minecraft 版本的 carpet_core_version
    """
    url = "https://api.modrinth.com/v2/project/carpet/version"
    params = {
        "game_versions": f'["{mc_version}"]',
        "loaders": '["fabric"]'
    }
    headers = {
        "User-Agent": "carpet-version-fetcher/1.0"
    }

    response = requests.get(url, params=params, headers=headers)
    response.raise_for_status()

    versions = response.json()
    if not versions:
        raise ValueError(f"未找到 Minecraft {mc_version} 对应的 Carpet 版本")

    # 取最新版本（列表第一个）
    latest = versions[0]
    version_number = latest["version_number"]  # 例如 "1.4.176"

    return version_number


def get_yarn(mc_ver):
    url = f"https://meta.fabricmc.net/v2/versions/yarn/{mc_ver}"
    with urllib.request.urlopen(url) as r:
        data = json.loads(r.read())
    if data:
        return data[0]["version"]
    return None

def get_loader(mc_ver):
    url = f"https://meta.fabricmc.net/v2/versions/loader/{mc_ver}"
    with urllib.request.urlopen(url) as r:
        data = json.loads(r.read())
    if data:
        return data[0]["loader"]["version"]
    return None

def get_fabric_api(mc_ver):
    # Modrinth API
    url = f"https://api.modrinth.com/v2/project/fabric-api/version?game_versions=%5B%22{mc_ver}%22%5D&loaders=%5B%22fabric%22%5D"
    try:
        req = urllib.request.Request(url, headers={"User-Agent": "Mozilla/5.0"})
        with urllib.request.urlopen(req) as r:
            data = json.loads(r.read())
        if data:
            return data[0]["version_number"]
    except:
        pass
    return None

versions = ["1.21","1.21.2", "1.21.3", "1.21.5", "1.21.8", "1.21.9"]
for v in versions:
    yarn = get_yarn(v)
    loader = get_loader(v)
    print(f"--- {v} ---")
    print(f"yarn_mappings =  {yarn}")
    print(f"loader_version= {loader}")
    print(f"carpet_core_version={get_carpet_core_version(v)}")

