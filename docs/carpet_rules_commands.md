# Carpet 命令规则汇总

> 来源: `@Rule` + `@QuickTranslations` + `zh_cn.json` (仅基于当前工程中的规则字段)

| 规则ID | 名称 | 描述 | 额外 |
| --- | --- | --- | --- |
| `commandSpawnWhitedListedPlayer` | 真人召唤命令 | 添加真人召唤命令 | `/playerReal <realPlayer> spawn` |
| `commandEntityTracker` | 实体追踪 | 可以选择一个实体进行追踪，其坐标与位置将会实时广播给玩家。 | - |
| `commandLoadedChunkFinder` | 记录加载区块命令 | 记录一段时间内活动的连通区块，用于查找被遗忘的区块加载器 | - |
| `commandSafeTick` | 安全加速 | 添加了tick的子命令safe rate , 可以在无生存真人时自动加速 | - |
| `commandRulesSearcher` | Carpet规则搜索命令 | 添加了carpet的子命令search，可以通过关键字搜索carpet规则 | - |
| `commandPacketLoggerPlus` | 数据包监视器Plus | 可以记录各种数据包的发包数量与压缩前大小。同时，也为发包限制的前置。 | - |
| `commandChunkFilter` | 区块过滤器 | 可以通过这个命令筛选区块，并以MCA支持的格式输出。用于删区块 | `/chunkFilter load [dimension] : 加载某维度区块数据。一次只能加载一个维度的数据。默认无区块被筛选,即空区块`<br>`/chunkFilter save [string>: 输出筛完的区块，输出文件将以cvs格式输出，并存储在维度的地图存档中`<br>`/chunkFilter add/remove [chunkPos] : 添加或删除单区块`<br>`/chunkFilter add/remove rect [from] [to] : 添加或删除矩形区域`<br>`/chunkFilter add/remove inhabitedTime [value]: 玩家停留时间大于等于value的区块将会被添加或删除`<br>`/chunkFilter add/remove modifyTime [value]: 修改时间小于等于value的区块将会被添加或删除. 在区块更新记录器未开启时无效，且此值应该低于等于维度minChunkLifetime`<br>`/chunkFilter extend [distance]: 将过滤器里的区块向外扩展distance个区块( 以切比雪夫距离)`<br>`/chunkFilter reverse: 反转现有区块`<br>`/chunkFilter clear: 清空过滤器里的区块`<br>`/chunkFilter close: 关闭过滤器` |
| `commandExceedChunkMarker` | 超高度区块标记器(ECM)命令 | 控制ECM命令权限等级。在未开始ECM时无效 | - |
| `commandChunkModifyCommand` | 区块修改记录器命令 | 区块修改记录器等级。在未开始区块修改记录器时无效 | - |
| `commandUnfreeze` | 未提供 | 未提供 | - |
| `commandChunkCacheCommand` | 区块数据缓存命令 | 在设置为false时，表示禁用区块数据缓存。此功能会导致显著的内存占用增加，请谨慎使用。对大型刷怪塔和世吞等有一定的优化效果 | 为了避免所有区块缓存导致内存过大，只允许一定区域的区块可以缓存<br>`/chunkCache <dimension> <dataType> list : 列出所有要缓存的区块` |

