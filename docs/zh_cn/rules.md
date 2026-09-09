# 规则

**提示：可以使用`Ctrl+F`快速查找自己想要的规则**

## 高密度实体推挤收集优化 (optimizedEntityCollection)

&emsp;使用区段内空间索引优化 Lithium 的高密度实体推挤候选收集。可能改变实体遍历顺序。

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `optimization`

## 可攀爬方块标签判断缓存 (optimizedClimbableTagCheck)

&emsp;缓存 Lithium 实体推挤判断中的 `CLIMBABLE` 方块标签查询，减少 `LivingEntity.onClimbable` 的重复标签查找。数据包重载后自动失效缓存。

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `optimization`

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

&emsp;在同一秒生成过多的同种实体时，按概率阻止该种实体的追踪与发包。

&emsp; `设置为负数表示禁用`

&emsp;- 类型: `int`

&emsp;- 默认值: `-1`

&emsp;- 参考选项: `-1`, `100`

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

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `optimization`, `tnt`, `feature`


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


## raycast优化 (optimizeRaycast)

&emsp;通过ECM优化raycast，开启时请保证ECM已打开且已经从存档加载过

&emsp; `已知特性：投掷物会忽略一些特定位置的实体碰撞箱。`

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `optimization`, `experimental`


## 粒子包发包距离 (particlesPacketsRange)

&emsp;此距离只能影响forced的粒子发包。但大部分粒子都是forced

&emsp; `原版默认为32`

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


## tnt实体发包优化 (tntPacketOptimization)

&emsp;通过去掉不必要的tnt实体发包(Fuse)与减少发包频率，优化tnt实体

&emsp; `可能会造成客户端显示错误`

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `optimization`, `packet`


