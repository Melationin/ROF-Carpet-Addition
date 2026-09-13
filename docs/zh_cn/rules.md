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


## 更好的NoAI NBT (betterNoAiNbt)

&emsp;实体带有 NoBrainAI NBT 时跳过其 AI 逻辑（Mob.serverAiStep），但保留重力、流体流动、实体推挤、挤压伤害、爆炸击退、活塞推动与骑乘等被动运动。与原版 NoAI 不同，实体不会悬空静止。

&emsp; `用法：/data merge entity <目标> {NoBrainAI:1b} 设置，{NoBrainAI:0b} 或 /data remove entity <目标> NoBrainAI 移除；标签随实体存档保存，也支持 /summon 时直接写入`

&emsp; `被跳过的：目标选择器、goal 选择器、寻路导航、传感器(sensing)、大脑(brain)、移动/视角/跳跃控制器`

&emsp; `保留的：重力与落地摩擦、水流/气泡柱、实体互推与挤压伤害、爆炸击退、活塞推动、骑乘（玩家仍可操控坐骑）、燃烧、捡装备、距离消失检查`

&emsp; `注意：与 AI 无关的 noActionTime 不再累加，因此在玩家附近也不会因 600 tick 无动作而消失（与原版 NoAI 行为一致）；末影龙/凋灵等 Boss 若打上该标签将停止相位推进`

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `feature`


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


## 珍珠加载强行同步 (enderPearlForcedSync)

&emsp;防止珍珠加载时因为异步区块加载延迟而导致珍珠tick和世界tick不同步

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 参考选项: `false`, `true`

&emsp;- 分类: `ROF`, `optimization`, `feature`


## 更好的高速珍珠自加载 (enderPearlForcedTickMinSpeed)

&emsp;对速度高于一定值的珍珠使用新的加载逻辑，更加稳定，需要加载的区块更少。

&emsp; `设置的值表示自加载速度阈值。设置为负值时，表示禁用。`

&emsp; `对于新加载逻辑的珍珠，其加载逻辑与原版有较大差异。`

&emsp;- 类型: `double`

&emsp;- 默认值: `-1.0`

&emsp;- 参考选项: `16.0`, `-1.0`

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


## 每秒实体生成发包限制恢复时间 (entitySpawnPacketLimitSecondsRecoverTime)

&emsp;被每秒发包限制的实体在存活时间达到该tick数后恢复原版发包距离

&emsp; `设置为负数表示禁用`

&emsp; `对每游戏刻发包限制不生效`

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


## 合并TNTnext (mergeTNTNext)

&emsp;更为激进的tnt合并方案, 可能会导致预期之外的结果。不能与其他tnt合并一起开。

&emsp; `False:关闭合并`

&emsp; `TRUE: 旧版的爆炸处理方案，会使移动中的合并tnt发生不原版的行为`

&emsp; `SAFE: 更安全的爆炸处理方案，让移动中的合并tnt行为与原版一致`

&emsp; `AlmostVanilla: 更接近原版的tnt合并方案，只在tnt爆炸时合并，并且保tnt tick顺序`

&emsp; `理论上，AlmostVanilla 模式不会改变 TNT 行为，因此在纯原版中不应发现相关差异。若出现与原版不一致的情况，请提交 issue。`

&emsp;- 类型: `MergeTNTNextMode`

&emsp;- 默认值: `FALSE`

&emsp;- 分类: `ROF`, `optimization`, `tnt`, `feature`


## 生物AI堵塞概率 (mobAiStallChance)

&emsp;白名单内的生物发生 AI 堵塞、暂时停止 AI 行为的概率。

&emsp; `概率为 0，或实体列表为空时，功能完全不生效`

&emsp; `掷骰结果随实体存档保存（实体 NBT 的 MobAi 字段）`

&emsp;- 类型: `double`

&emsp;- 默认值: `0.0`

&emsp;- 参考选项: `0`, `0.1`

&emsp;- 分类: `ROF`, `optimization`


## 生物AI堵塞时间 (mobAiStallTicks)

&emsp;生物 AI 堵塞持续的 tick 数，结束后恢复 AI，必须是正数。

&emsp; `剩余时间随实体存档保存；小于 0 表示「已判定且不关闭 AI」`

&emsp; `必须是正数`

&emsp;- 类型: `int`

&emsp;- 默认值: `50`

&emsp;- 参考选项: `50`, `200`

&emsp;- 分类: `ROF`, `optimization`


## 生物AI堵塞白名单 (mobAiStallWhitelist)

&emsp;可能发生 AI 堵塞的生物白名单，逗号分隔；条目可以是实体 ID（minecraft:pig）或实体类型标签（#zombies），前缀 ! 表示排除该项。列表全为排除项时表示「除这些之外的全部」；列表为空时功能不生效。

&emsp; `建议取值：!minecraft:drowned（默认，除溺尸以外的全部生物）、!minecraft:drowned,!minecraft:piglin（再排除猪灵）`

&emsp; `示例：minecraft:pig,#zombies,!#undead —— 猪与僵尸类生物，但不包括带 undead 标签的`

&emsp; `先取所有正选条目的并集，再减去所有负选条目；命中任意一个正选条目即可`

&emsp;- 类型: `String`

&emsp;- 默认值: `!minecraft:drowned`

&emsp;- 参考选项: `!minecraft:drowned`, `!minecraft:drowned,!minecraft:piglin`

&emsp;- 分类: `ROF`, `optimization`


## 优化自加载态珍珠tick (optimizeForcedEnderPearlTick)

&emsp;仅在更好的珍珠自加载启用时可用。让大多数情况下高速珍珠的飞行不生成新区块，可大幅度减少存档体积。在ECM未打开时，只会让世界高度外的珍珠不生成区块

&emsp; `已知特性：珍珠会忽略未加载的实体碰撞箱。`

&emsp; `false - 关闭优化`

&emsp; `true - 开启优化,且珍珠特性符合当前版本`

&emsp; `1_21_2- - 开启优化,且珍珠特性符合1.21.2及以下版本`

&emsp; `1_21_2+ - 开启优化,且珍珠特性符合1.21.2以上版本`

&emsp;- 类型: `String`

&emsp;- 默认值: `false`

&emsp;- 参考选项: `false`, `true`, `1_21_2-`, `1_21_2+`

&emsp;- 分类: `ROF`, `optimization`, `experimental`


## 物品合并优化 (optimizeItemMerge)

&emsp;尽量让物品达到一组，以减轻卡顿(效果不明显)

&emsp; `允许部分合并：优先把掉落物填满整组，余量留在原掉落物中`

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `optimization`, `feature`


## raycast优化 (optimizeRaycast)

&emsp;通过ECM优化raycast，开启时请保证ECM已打开且已经从存档加载过

&emsp; `已知特性：投掷物会忽略一些特定位置的实体碰撞箱。`

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `optimization`, `experimental`


## 可攀爬方块标签判断缓存 (optimizedClimbableTagCheck)

&emsp;缓存 Lithium 实体推挤判断中的 CLIMBABLE 方块标签查询，减少 LivingEntity.onClimbable 的重复标签查找。数据包重载后自动失效缓存。

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `optimization`


## 实体推挤收集优化 (optimizedEntityCollection)

&emsp;使用分区空间索引优化 Lithium 的高密度实体推挤候选收集。可能改变实体遍历顺序。

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `optimization`, `experimental`


## 爆炸优化 (optimizedExplosion)

&emsp;同一游戏刻内同一点的连续爆炸达到该次数后，先按最坏情况判断这次爆炸是否可能破坏方块；只有不可能破坏方块时才跳过方块计算，只影响实体。

&emsp; `设置为 0 或负数表示禁用`

&emsp; `只对同一游戏刻内、坐标与威力完全相同的连续爆炸生效`

&emsp;- 类型: `int`

&emsp;- 默认值: `0`

&emsp;- 参考选项: `0`, `4`, `8`, `16`

&emsp;- 分类: `ROF`, `optimization`, `experimental`


## 假人tick精简 (optimizedFakePlayerTick)

&emsp;精简Carpet假人的客户端同步/进度/统计/Waypoint等每tick逻辑，保留主手物品tick与行为模拟

&emsp; `仅对Carpet假人(EntityPlayerMPFake)生效`

&emsp; `可能影响假人的统计、进度、计分板自动同步与定位条显示`

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `optimization`


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


