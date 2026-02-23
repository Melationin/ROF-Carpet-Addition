# 命令

**提示：可以使用`Ctrl+F`快速查找自己想要的命令**

## 区块过滤器 (commandChunkFilter)

&emsp;可以通过这个命令筛选区块，并以MCA支持的格式输出。用于删区块

### &emsp;用法:

&emsp;&emsp;- /chunkFilter load <dimension> - 加载某维度区块数据。一次只能加载一个维度的数据。默认无区块被筛选,即空区块

&emsp;&emsp;- /chunkFilter save <string> - 输出筛完的区块，输出文件将以cvs格式输出，并存储在维度的地图存档中(注意文件名必须为英文)

&emsp;&emsp;- /chunkFilter add/remove <chunkPos> - 添加或删除单区块

&emsp;&emsp;- /chunkFilter add/remove rect <from> <to> - 添加或删除矩形区域

&emsp;&emsp;- /chunkFilter add/remove inhabitedTime <value> - 玩家停留时间大于等于value的区块将会被添加或删除

&emsp;&emsp;- /chunkFilter add/remove modifyTime <value> - 修改时间小于等于value的区块将会被添加或删除. 在区块更新记录器未开启时无效，且此值应该低于等于维度minChunkLifetime

&emsp;&emsp;- /chunkFilter extend <distance> - 将过滤器里的区块向外扩展distance个区块( 以切比雪夫距离)

&emsp;&emsp;- /chunkFilter reverse - 反转现有区块

&emsp;&emsp;- /chunkFilter clear -  清空过滤器里的区块

&emsp;&emsp;- /chunkFilter close - 关闭过滤器


## 区块修改记录器命令 (commandChunkModifyCommand)

&emsp;区块修改记录器等级。在未开始区块修改记录器时无效

### &emsp;用法:

&emsp;&emsp;- /chunkModify - 显示区块修改记录器的信息

&emsp;&emsp;- /chunkModify <dimension> - 显示选定维度的信息

&emsp;&emsp;- /chunkModify [dimension] clear - 清除选定维度的区块修改记录器数据（维度默认为玩家所在维度）

&emsp;&emsp;- /chunkModifyLogger [dimension] setMinChunkLifetime <minChunkLifetime> - 设置选定维度的区块修改记录器最小区块生命周期（维度默认为玩家所在维度）

&emsp;&emsp;-  minChunkLifetime : 被记录的区块至少在创建后 minChunkLifetime tick内被修改过才会被记录


## 实体追踪 (commandEntityTracker)

&emsp;可以选择一个实体进行追踪，其坐标与位置将会实时广播给玩家。

### &emsp;用法:

&emsp;&emsp;- /entityTracker set <entity> - 开始追踪一个实体

&emsp;&emsp;- /entityTracker clear - 结束追踪


## 超高度区块标记器(ECM)命令 (commandExceedChunkMarker)

&emsp;控制ECM命令权限等级。在未开始ECM时无效

### &emsp;用法:

&emsp;&emsp;- /exceedChunkMarker - 显示ECM当前的信息

&emsp;&emsp;- /exceedChunkMarker <dimension> - 显示选定维度的信息

&emsp;&emsp;- /exceedChunkMarker [dimension] setTopY <topY> - 设置选定维度的ECM高度（维度默认为玩家所在维度）

&emsp;&emsp;- /exceedChunkMarker [dimension] clear - 清除选定维度的ECM数据（维度默认为玩家所在维度）

&emsp;&emsp;- /exceedChunkMarker [dimension] loadFromWorld - 从选定维度的世界数据中加载ECM数据（维度默认为玩家所在维度）


## 记录加载区块命令 (commandLoadedChunkFinder)

&emsp;记录一段时间内活动的连通区块，用于查找被遗忘的区块加载器

### &emsp;用法:

&emsp;&emsp;- /loadedChunkFinder - 记录当前维度1 tick内的区块加载情况

&emsp;&emsp;- /loadedChunkFinder <dimension> [tick] - 记录指定指定一段时间内的区块加载情况，tick默认为1


## 数据包监视器Plus (commandPacketLoggerPlus)

&emsp;记录各种数据包的压缩前大小。

### &emsp;用法:

&emsp;&emsp;- /packetLogger start - 开始记录数据包

&emsp;&emsp;- /packetLogger stop - 结束记录并显示数据

&emsp;&emsp;- /packetLogger - 显示当前数据（如果正在记录）


## Carpet规则搜索命令 (commandRulesSearcher)

&emsp;添加了carpet的子命令search，可以通过关键字搜索carpet规则

### &emsp;用法:

&emsp;&emsp;- /carpet search <key> [isNoDefaultValue] [category] - 搜索carpet规则，key为搜索关键词，isNoDefaultValue为是否只搜索默认值改变(默认 false)，category为规则分类


## 安全加速 (commandSafeTick)

&emsp;添加了命令tickSafe , 可以在无生存真人时自动加速

### &emsp;用法:

&emsp;&emsp;- /tickSafe rate <rate> - 设置安全加速的tick速率. 当使用tick 的其他调速指令时，自动取消安全加速的效果


## 无前缀假人召唤命令 (commandSpawnWhitedListedPlayer)

&emsp;用于召唤无前缀假人

### &emsp;用法:

&emsp;&emsp;- /player <name> spawn original - 召唤一个无前缀假人


