# 命令

**提示：可以使用`Ctrl+F`快速查找自己想要的命令**

## 超高度区块标记器(ECM)命令 (commandExceedChunkMarker)

&emsp;控制ECM命令权限等级。在未开始ECM时无效

&emsp;&emsp;- `/exceedChunkMarker - 显示ECM当前的信息`

&emsp;&emsp;- `/exceedChunkMarker <dimension> - 显示选定维度的信息`

&emsp;&emsp;- `/exceedChunkMarker [dimension] setTopY <topY> - 设置选定维度的ECM高度（维度默认为玩家所在维度）`

&emsp;&emsp;- `/exceedChunkMarker [dimension] clear - 清除选定维度的ECM数据（维度默认为玩家所在维度）`

&emsp;&emsp;- `/exceedChunkMarker [dimension] loadFromWorld - 从选定维度的世界数据中加载ECM数据（维度默认为玩家所在维度）`


## 记录加载区块命令 (commandLoadedChunkFinder)

&emsp;记录一段时间内活动的连通区块，用于查找被遗忘的区块加载器

&emsp;&emsp;- `/loadedChunkFinder - 记录当前维度1 tick内的区块加载情况`

&emsp;&emsp;- `/loadedChunkFinder <dimension> [tick] - 记录指定指定一段时间内的区块加载情况，tick默认为1`


## 数据包监视器Plus (commandPacketLoggerPlus)

&emsp;记录各种数据包的压缩前大小。

&emsp;&emsp;- `/packetLogger start - 开始记录数据包`

&emsp;&emsp;- `/packetLogger stop - 结束记录并显示数据`

&emsp;&emsp;- `/packetLogger - 显示当前数据（如果正在记录）`


## 命令权限修改命令 (commandRequirementModify)

&emsp;修改指定命令的权限要求

&emsp;&emsp;- `/requirementModify <commandPath> <permission> set - 将指定命令的权限要求设置为指定权限`

&emsp;&emsp;- `/requirementModify <commandPath> <permission> add - 在原有权限要求的基础上添加一个权限要求`

&emsp;&emsp;- `/requirementModify <commandPath> <permission> or - 在原有权限要求的基础上添加一个或条件的权限要求`

&emsp;&emsp;- `/requirementModify <commandPath> clear - 清除指定命令的权限修改`

&emsp;&emsp;- `/requirementModify clearAll - 清除所有命令的权限修改`

&emsp;&emsp;- `/requirementModify list - 列出所有被修改权限要求的命令`

&emsp;&emsp;- `对于commandPath ,用<>表示参数：`

&emsp;&emsp;- `  - 例：/requirementModify "player <player> shadow" ops set`

&emsp;&emsp;- `    表示将"player <player> shadow"的权限要求设置为ops`


## Carpet规则搜索命令 (commandRulesSearcher)

&emsp;添加了carpet的子命令search，可以通过关键字搜索carpet规则

&emsp;&emsp;- `/carpet search <key> [isNoDefaultValue] [category] - 搜索carpet规则，key为搜索关键词，isNoDefaultValue为是否只搜索默认值改变(默认 false)，category为规则分类`


## 无前缀假人召唤命令 (commandSpawnWhitedListedPlayer)

&emsp;用于召唤无前缀假人

&emsp;&emsp;- `/player <name> spawn original - 召唤一个无前缀假人`

