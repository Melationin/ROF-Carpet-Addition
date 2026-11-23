# 规则

**提示：可以使用`Ctrl+F`快速查找自己想要的规则**

## 生物生成异步 (asyncNaturalSpawning)

&emsp;异步预计算自然生成候选；实体创建和数量统计仍在主线程执行。

&emsp; `数据不可用或结果过期时自动回退原版。`

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `optimization`, `experimental`


## 随机刻异步 (asyncRandomTick)

&emsp;异步预计算下一游戏刻的随机刻候选；结果失效时自动回退原版。

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `optimization`, `experimental`


## 更好的珍珠加载票 (betterEnderPearlTicket)

&emsp;用一种特殊的加载票替代某些情况下原有的加载票。在ECM未打开时，只对世界高度外的珍珠有效

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 参考选项: `false`, `true`

&emsp;- 分类: `ROF`, `optimization`, `experimental`


## 更好的NoAI NBT (betterNoAiNbt)

&emsp;实体带有 NoBrainAI NBT 时跳过其 AI 逻辑（Mob.serverAiStep

&emsp; `NoBrainAI:1b`

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `feature`


## 珍珠加载堵塞主线程 (blockingEnderPearlLoading)

&emsp;让珍珠的区块加载堵塞主线程，减少珍珠tick和世界tick不同步的问题。

&emsp; `此处为最大堵塞时间，设置为0表示禁用。`

&emsp;- 类型: `int`

&emsp;- 默认值: `0`

&emsp;- 参考选项: `0`, `50`, `100`, `1000`

&emsp;- 分类: `ROF`, `optimization`, `feature`


## 实体ID命令 (commandEntityID)

&emsp;查看并控制实体id的命令

&emsp;- 类型: `String`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `feature`, `creative`, `command`


## 实体ID设置命令 (commandEntityIDSet)

&emsp;设置实体id的命令

&emsp;- 类型: `String`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `feature`, `creative`, `command`


## 超高度区块标记器(ECM)命令 (commandExceedChunkMarker)

&emsp;控制ECM命令权限等级。在未开始ECM时无效

&emsp;- 类型: `String`

&emsp;- 默认值: `ops`

&emsp;- 分类: `command`, `ROF`


## 记录加载区块命令 (commandLoadedChunkFinder)

&emsp;记录一段时间内活动的连通区块，用于查找被遗忘的区块加载器

&emsp;- 类型: `String`

&emsp;- 默认值: `ops`

&emsp;- 分类: `ROF`, `command`


## 数据包监视器Plus (commandPacketLoggerPlus)

&emsp;记录各种数据包的压缩前大小。

&emsp;- 类型: `String`

&emsp;- 默认值: `ops`

&emsp;- 分类: `command`, `ROF`


## 命令权限修改命令 (commandRequirementModify)

&emsp;修改指定命令的权限要求

&emsp;- 类型: `String`

&emsp;- 默认值: `ops`

&emsp;- 分类: `command`, `creative`


## Carpet规则搜索命令 (commandRulesSearcher)

&emsp;添加了carpet的子命令search，可以通过关键字搜索carpet规则

&emsp;- 类型: `String`

&emsp;- 默认值: `true`

&emsp;- 分类: `ROF`, `command`


## 无前缀假人召唤命令 (commandSpawnWhitedListedPlayer)

&emsp;用于召唤无前缀假人

&emsp;- 类型: `String`

&emsp;- 默认值: `ops`

&emsp;- 分类: `ROF`, `command`, `creative`


## 加速逐tick耗时输出 (commandTickSprintFull)

&emsp;为 /tick sprint 添加 full 参数，加速时输出每一个游戏刻的耗时

&emsp;- 类型: `String`

&emsp;- 默认值: `ops`

&emsp;- 分类: `ROF`, `command`


## 更好的珍珠自加载 (enderPearlForcedTickMinSpeed)

&emsp;对某些情况的珍珠启用自加载，自加载期间不放任何加载票。

&emsp; `读到大于 0 的数值时按 true 处理。`

&emsp;- 类型: `String`

&emsp;- 默认值: `false`

&emsp;- 参考选项: `true`, `false`

&emsp;- 分类: `ROF`, `optimization`, `feature`


## 实体ID溢出周期 (entityIDOverflowPeriod)

&emsp;设置为0表示禁用

&emsp;- 类型: `int`

&emsp;- 默认值: `0`

&emsp;- 分类: `ROF`, `feature`, `creative`


## 每秒实体生成发包限制 (entitySpawnPacketLimitSeconds)

&emsp;在同一秒同区块生成过多的同种实体时，按概率阻止该种实体的追踪与发包。

&emsp; `设置为负数表示禁用`

&emsp;- 类型: `int`

&emsp;- 默认值: `-1`

&emsp;- 参考选项: `-1`, `100`

&emsp;- 分类: `ROF`, `optimization`, `packet`


## 实体生成发包限制恢复时间 (entitySpawnPacketLimitSecondsRecoverTime)

&emsp;被每 tick 或每秒发包限制的实体在存活时间达到该游戏刻数后，恢复限制前的追踪距离

&emsp; `设置为负数表示禁用`

&emsp; `同时受到两种限制的实体也会恢复`

&emsp;- 类型: `int`

&emsp;- 默认值: `200`

&emsp;- 参考选项: `-1`, `200`, `100`, `400`

&emsp;- 分类: `ROF`, `optimization`, `packet`


## 每游戏刻实体生成发包限制 (entitySpawnPacketLimitTicks)

&emsp;在同一tick生成过多的同种实体时，减少过多的实体的发包距离。用于大当量珍珠炮的优化

&emsp; `设置为负数表示禁用`

&emsp;- 类型: `int`

&emsp;- 默认值: `-1`

&emsp;- 参考选项: `-1`, `100`, `1000`

&emsp;- 分类: `ROF`, `optimization`, `packet`


## 每游戏刻实体生成发包限制发包距离 (entitySpawnPacketLimitTicksTrackerDistance)

&emsp;设置发包限制的实体的发包距离

&emsp;- 类型: `int`

&emsp;- 默认值: `16`

&emsp;- 参考选项: `2`, `16`, `64`

&emsp;- 分类: `ROF`, `optimization`, `packet`


## 超高度区块标记器(ECM) (exceedChunkMarker)

&emsp;Raycast优化前置，可能会造成额外的存储空间(一般只会增加存档的0.1%以下)

&emsp; `在第一次启用时，务必使用/exceedChunkMarker 加载一次`

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `experimental`


## GCA 假人生成保留载具 (gcaBotSpawnKeepVehicle)

&emsp;GCA 的 /bot load 与假人驻留通过 BotSpawnUtil.spawnBot 重新生成假人时，如果已从 RootVehicle 恢复载具，则跳过 teleportTo，避免假人下矿车

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `bugfix`


## 每游戏刻击杀进度触发次数上限 (killedTriggerLimitPerTick)

&emsp;限制每 tick 内 KilledTrigger.trigger 的执行次数

&emsp; `0表示无限制`

&emsp;- 类型: `int`

&emsp;- 默认值: `0`

&emsp;- 参考选项: `0`, `1`, `10`, `100`

&emsp;- 分类: `ROF`, `optimization`, `feature`


## 爆炸合并 (mergeExplosion)

&emsp;把合并TNT产生的多次爆炸合成一次，只对实体作用一次。仅在合并TNT和爆炸优化已启用时生效。

&emsp; `需要 mergeTNTNext 处于非 False 模式`

&emsp; `伤害只结算一次；推力按合并次数补齐，总推力与原版一致`

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `optimization`, `tnt`, `feature`


## 假人列表合并 (mergeFakePlayerTabList)

&emsp;把名字为「相同前缀+数字」的假人合并成一行显示在Tab玩家列表

&emsp; `0 表示不合并；设为 N（N≥2）时，同前缀满 N 只才合并`

&emsp; `例：设为 3 时，bot_a1~bot_a2 不合并，从 bot_a1~bot_a3 起合并为 (players) bot_a1-3`

&emsp; `合并行由编号最小的假人承担，其余组员隐藏；旁观者可以直接传送到它`

&emsp; `只改变玩家列表的显示，假人实体、追踪、命令选择器与 /player 控制均不受影响`

&emsp; `纯服务端实现，原版客户端无需安装任何东西`

&emsp;- 类型: `int`

&emsp;- 默认值: `0`

&emsp;- 参考选项: `0`, `2`, `5`, `10`

&emsp;- 分类: `ROF`, `feature`, `packet`


## 合并TNTnext (mergeTNTNext)

&emsp;更为激进的tnt合并方案, 可能会导致预期之外的结果。不能与其他tnt合并一起开。

&emsp; `False:关闭合并`

&emsp; `TRUE: 旧版的爆炸处理方案，会使移动中的合并tnt发生不原版的行为`

&emsp; `SAFE: 更安全的爆炸处理方案，让移动中的合并tnt行为与原版一致`

&emsp; `AlmostVanilla: 更接近原版的tnt合并方案，只在tnt爆炸时通过每tick一次的提前遍历合并相邻的同点位tnt，且保持tnt tick顺序`

&emsp; `SafePlus: 每tick提前遍历并尝试合并相邻tick顺序且满足合并条件的tnt，不跨其他实体合并`

&emsp; `理论上，AlmostVanilla 模式不会改变 TNT 行为，因此在纯原版中不应发现相关差异。若出现与原版不一致的情况，请提交 issue。`

&emsp;- 类型: `MergeTNTNextMode`

&emsp;- 默认值: `FALSE`

&emsp;- 分类: `ROF`, `optimization`, `tnt`, `feature`


## 生物AI延迟概率 (mobAIDelayChance)

&emsp;白名单内的生物发生 AI 延迟、暂时停止 AI 行为的概率。

&emsp;- 类型: `double`

&emsp;- 默认值: `0.0`

&emsp;- 参考选项: `0`, `0.5`, `0.9`, `0.95`

&emsp;- 分类: `ROF`, `optimization`


## 生物AI延迟时间 (mobAIDelayTicks)

&emsp;生物 AI 延迟持续的 tick 数，结束后恢复 AI，必须是正数。

&emsp;- 类型: `int`

&emsp;- 默认值: `3`

&emsp;- 参考选项: `3`, `50`, `200`

&emsp;- 分类: `ROF`, `optimization`


## 生物AI延迟白名单 (mobAIDelayWhitelist)

&emsp;可能发生 AI 延迟的生物白名单，花括号包裹、逗号分隔；条目可以是实体 ID或实体类型标签（如#zombies），前缀 ! 排除

&emsp;- 类型: `String`

&emsp;- 默认值: `{!minecraft:drowned}`

&emsp;- 参考选项: `{}`, `{!minecraft:drowned}`, `{!minecraft:drowned,!minecraft:piglin}`

&emsp;- 分类: `ROF`, `optimization`


## 地狱门出口搜索缓存距离 (netherPortalCacheDistance)

&emsp;按入口门方块缓存出口门位置；实体距上次实际搜索位置不超过此距离，且出口区块未变化时复用结果。

&emsp; `0 表示禁用；距离按来源世界的三维欧氏距离计算.`

&emsp;- 类型: `double`

&emsp;- 默认值: `0.0`

&emsp;- 参考选项: `0`, `0.1`, `0.5`

&emsp;- 分类: `ROF`, `optimization`, `experimental`


## 方块实体移除优化 (optimizedBlockEntityRemoval)

&emsp;用读写双下标从 tick 列表中批量移除失效的方块实体 ticker，保留其余 ticker 的 tick 顺序。

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `optimization`


## 珍珠实体拦截优化 (optimizedEnderPearlEntityInterception)

&emsp;对速度大于16的末影珍珠使用3D DDA算法按EntitySection步进查找实体，并缓存含特定拦截实体（盔甲架）的EntitySection以按需弱加载未加载区块的实体

&emsp; `仅对速度 > 16 的末影珍珠生效`

&emsp; `首次开启或缓存过期时会自动从全维度实体存档中构建缓存`

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 参考选项: `false`, `true`

&emsp;- 分类: `ROF`, `optimization`, `experimental`


## 优化珍珠tick (optimizedEnderPearlTick)

&emsp;让大多数情况下高速珍珠的飞行不生成新区块，可大幅度减少存档体积。在ECM未打开时，只会让世界高度外的珍珠不生成区块

&emsp; `已知特性：珍珠会忽略未加载的实体碰撞箱。`

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 参考选项: `false`, `true`

&emsp;- 分类: `ROF`, `optimization`, `experimental`


## 实体推挤收集优化 (optimizedEntityCollection)

&emsp;使用分区空间索引优化 Lithium 的高密度实体推挤候选收集。可能改变实体遍历顺序。

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `optimization`, `experimental`


## 装备掉落优化 (optimizedEquipmentDrops)

&emsp;跳过生物死亡时空装备槽的掉落附魔计算，以及没有装备掉落效果的附魔上下文创建。

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `optimization`, `experimental`


## 经验球生成合并查询优化 (optimizedExperienceOrbMerge)

&emsp;生成经验球时找到第一个符合原版合并条件的经验球就停止查询。

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `optimization`


## 爆炸优化 (optimizedExplosion)

&emsp;同一游戏刻内同一点的连续爆炸达到该次数后，先按最坏情况判断这次爆炸是否可能破坏方块；只有不可能破坏方块时才跳过方块计算，只影响实体。

&emsp; `设置为 0 或负数表示禁用`

&emsp; `只对同一游戏刻内、坐标与威力完全相同的连续爆炸生效`

&emsp;- 类型: `int`

&emsp;- 默认值: `0`

&emsp;- 参考选项: `0`, `4`, `8`, `16`

&emsp;- 分类: `ROF`, `optimization`, `experimental`


## 假人tick精简 (optimizedFakePlayerTick)

&emsp;精简Carpet假人的进度/统计/计分板/容器同步/实体追踪/Waypoint等开销，保留行为模拟与掉落

&emsp; `仅对Carpet假人(EntityPlayerMPFake)生效`

&emsp; `假人不再产生成就进度、统计与计分板数据，也不再被登记为实体的观察者`

&emsp; `不影响 /player 控制、方块破坏、生物掉落与经验`

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `optimization`


## raycast优化 (optimizedRaycast)

&emsp;通过ECM优化raycast，开启时请保证ECM已打开且已经从存档加载过

&emsp; `已知特性：投掷物会忽略一些特定位置的实体碰撞箱。`

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `optimization`, `experimental`


## 粒子包发包距离 (particlesPacketsRange)

&emsp;此距离只能影响非forced的粒子发包。大部分粒子都是非forced

&emsp; `原版默认为32`

&emsp; `force的粒子发包距离固定为512，不受此规则影响`

&emsp;- 类型: `double`

&emsp;- 默认值: `32.0`

&emsp;- 参考选项: `32.0`, `1.0`, `8.0`

&emsp;- 分类: `ROF`, `optimization`, `packet`


## 猪灵捡掉落物延迟 (piglinLootItemDelay)

&emsp;只有出现一定时间的掉落物才会被猪灵捡起

&emsp;- 类型: `int`

&emsp;- 默认值: `0`

&emsp;- 参考选项: `0`, `20`

&emsp;- 分类: `ROF`, `optimization`, `feature`


## 堆叠猪灵AI抑制 (piglinStackingAISuppression)

&emsp;对于堆叠到一定量的猪灵，抑制其中部分猪灵的ai。

&emsp;- 类型: `int`

&emsp;- 默认值: `10000`

&emsp;- 参考选项: `100`, `10000`

&emsp;- 分类: `ROF`, `optimization`, `feature`


## 随机刻区块延迟缓存 (randomTickChunkCache)

&emsp;每40gt刷新一次随机刻区块列表。关闭时完全使用原版遍历。

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `optimization`, `experimental`


## 刷怪统计化简白名单 (spawnStatisticSimplifyWhitelist)

&emsp;刷怪统计被化简的世界白名单，花括号包裹、逗号分隔的维度 ID；{} 表示任何世界都不化简。

&emsp; `化简会跳过刷怪统计中的区块查找与生物群系查询`

&emsp; `只有世界中任何生物群系都没有刷怪密度限制时，化简的结果才与原版完全一致`

&emsp;- 类型: `String`

&emsp;- 默认值: `{}`

&emsp;- 参考选项: `{}`, `{minecraft:overworld}`, `{minecraft:overworld,minecraft:the_end}`

&emsp;- 分类: `ROF`, `optimization`


## 生物生成区块延迟缓存 (spawningChunkCache)

&emsp;每40gt刷新一次自然生成候选区块列表。关闭时完全使用原版遍历。

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `optimization`, `experimental`


## tnt实体发包优化 (tntPacketOptimization)

&emsp;通过去掉不必要的tnt实体发包(Fuse)与减少发包频率，优化tnt实体

&emsp; `可能会造成客户端显示错误`

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `optimization`, `packet`


