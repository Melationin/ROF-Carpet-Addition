# 规则

**提示：可以使用`Ctrl+F`快速查找自己想要的规则**

## 区块修改记录器 (chunkModifyLogger)

&emsp;记录区块生成时间和最后保存时间，占用体积相对较大(存档体积的2%以下)

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `experimental`


## 区块修改记录器保存玩家区块 (chunkModifyLoggerPlayerChunk)

&emsp;将玩家途径区块视为已修改

&emsp;- 类型: `boolean`

&emsp;- 默认值: `true`

&emsp;- 分类: `ROF`, `experimental`


## 区块过滤器 (commandChunkFilter)

&emsp;可以通过这个命令筛选区块，并以MCA支持的格式输出。用于删区块

&emsp;- 类型: `String`

&emsp;- 默认值: `ops`

&emsp;- 分类: `ROF`, `command`


## 区块修改记录器命令 (commandChunkModifyCommand)

&emsp;区块修改记录器等级。在未开始区块修改记录器时无效

&emsp;- 类型: `String`

&emsp;- 默认值: `ops`

&emsp;- 分类: `command`, `ROF`


## 实体追踪 (commandEntityTracker)

&emsp;可以选择一个实体进行追踪，其坐标与位置将会实时广播给玩家。

&emsp;- 类型: `String`

&emsp;- 默认值: `true`

&emsp;- 分类: `command`, `ROF`


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


## 安全加速 (commandSafeTick)

&emsp;添加了命令tickSafe , 可以在无生存真人时自动加速

&emsp;- 类型: `String`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `command`


## 无前缀假人召唤命令 (commandSpawnWhitedListedPlayer)

&emsp;用于召唤无前缀假人

&emsp;- 类型: `String`

&emsp;- 默认值: `ops`

&emsp;- 分类: `ROF`, `command`, `creative`


## 更好的高速珍珠自加载 (enderPearlForcedTickMinSpeed)

&emsp;对速度高于一定值的珍珠使用新的加载逻辑，更加稳定，需要加载的区块更少。

&emsp; `设置的值表示自加载速度阈值。设置为负值时，表示禁用。`

&emsp; `对弱加载炮可能有预料之外的影响`

&emsp;- 类型: `double`

&emsp;- 默认值: `-1.0`

&emsp;- 参考选项: `16.0`, `-1.0`

&emsp;- 分类: `ROF`, `optimization`, `feature`


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


## ~~强制加载态珍珠广播 (forceEnderPearlLogger)~~

&emsp;向丢出者发送强加载态珍珠的坐标(容易刷屏)

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `feature`

> *本规则已删除 相关功能由 log enderPearl 提供*

## ~~高珍珠不加载区块 (highEnderPearlNoChunkLoading)~~

&emsp;在开启更好的珍珠加载的情况下，超过世界高度的珍珠不会加载区块，但会加载自身

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `optimization`, `feature`
> *本规则已删除 相关功能合并至[ 优化自加载态珍珠tick](#optimizeForcedEnderPearlTick)*

## 高卡自动暂停阈值 (highLagFreezeLimit)

&emsp;设置正数表示启用。当连续几tick用时超过此阈值时，自动暂停。

&emsp;- 类型: `int`

&emsp;- 默认值: `-1`

&emsp;- 参考选项: `-1`, `500`, `1000`

&emsp;- 分类: `ROF`, `feature`


## 高卡自动暂停tick阈值 (highLagFreezeTickLimit)

&emsp;设置正数表示启用。当连续几tick用时超过此阈值时，自动暂停。

&emsp;- 类型: `int`

&emsp;- 默认值: `3`

&emsp;- 参考选项: `3`, `5`, `10`

&emsp;- 分类: `ROF`, `feature`


## 合并TNTnext (mergeTNTNext)

&emsp;更为激进的tnt合并方案, 可能会导致预期之外的结果。不能与其他tnt合并一起开。

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `optimization`, `tnt`

<h2 id="optimizeForcedEnderPearlTick"></h2>

## 优化自加载态珍珠tick (optimizeForcedEnderPearlTick)

&emsp;仅在更好的珍珠自加载启用时可用。让大多数情况下高速珍珠的飞行不生成新区块，可大幅度减少存档体积。在ECM未打开时，只会让世界高度外的珍珠不生成区块。





&emsp; `"false - 关闭优化",`

&emsp; `"true - 开启优化,且珍珠特性符合当前版本",`

&emsp; `"1_21_2- - 开启优化,且珍珠特性符合1.21.2及以下版本",`

&emsp; `"1_21_2+ - 开启优化,且珍珠特性符合1.21.2以上版本"`

&emsp; `已知特性：珍珠会忽略未加载的实体碰撞箱。`

&emsp;- 类型: `String`

&emsp;- 默认值: `false`

&emsp;- 参考选项: `true`, `false`, `1_21_2-`, `1_21_2+`

&emsp;- 分类: `ROF`, `optimization`, `experimental`


## 物品合并优化 (optimizeItemMerge)

&emsp;尽量让物品达到一组，以减轻卡顿

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `optimization`, `feature`


## raycast优化 (optimizeRaycast)

&emsp;通过ECM优化raycast，开启时请保证ECM已打开且已经从存档加载过

&emsp; `已知特性：投掷物会忽略一些特定位置的实体碰撞箱。`

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `optimization`, `experimental`


## 猪灵捡掉落物延迟 (piglinLootItemDelay)

&emsp;只有出现一定时间的掉落物才会被猪灵捡起

&emsp;- 类型: `int`

&emsp;- 默认值: `0`

&emsp;- 参考选项: `0`, `20`

&emsp;- 分类: `ROF`, `optimization`, `feature`


## 堆叠猪灵AI抑制 (piglinStackingAISuppression)

&emsp;对于堆叠到一定量的猪灵，抑制其中部分猪灵的ai。

&emsp;对于一组堆叠的猪灵中，规则保证概率上有设置值数量的猪灵表现正常。

&emsp;- 类型: `int`

&emsp;- 默认值: `10000`

&emsp;- 参考选项: `100`, `10000`

&emsp;- 分类: `ROF`, `optimization`, `feature`


## BlockPos Y轴遍历反向 (reverseBlockPosTraversal)

&emsp;让BlockPos Y轴遍历反向，让实体传送地狱门优先传送靠上的地狱门

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `feature`


## tnt实体发包优化 (tntPacketOptimization)

&emsp;通过去掉不必要的tnt实体发包(Fuse)，优化tnt实体

&emsp; `可能会造成客户端显示错误`

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `optimization`, `packet`


