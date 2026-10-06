# 规则

**提示：可以使用`Ctrl+F`快速查找自己想要的规则**

各组的 Carpet 规则是默认 `false` 的布尔总开关，按原生方式显示并保存到 `carpet.conf`。子规则通过 `/carpet group` 管理，修改后立即以格式化 JSON 保存到世界根目录的 `carpet-rof-addition.json`，位于 `ruleGroups.<组名>`；文件中的其他配置保持不变。手动编辑 JSON 后重启服务器加载，缺省子规则使用声明默认值。

```json
{
  "requirementModifyMap": {},
  "ruleGroups": {
    "packetLimits": {
      "entitySpawnLimitPerTick": 512,
      "limitedEntityTrackingRange": 16,
      "entitySpawnLimitPerSecond": 256,
      "entityTrackingRecoveryTicks": 200,
      "particlePacketRange": 2.0
    }
  }
}
```

## 地狱门出口搜索缓存距离 (netherPortalCacheDistance)

&emsp;按入口门方块缓存出口门位置；实体距上次实际搜索位置不超过此距离，且出口区块未变化时复用结果。

&emsp; `距离按来源世界的三维欧氏距离计算.0 表示禁用；只接受有限的非负数。`

&emsp;- 类型: `double`

&emsp;- 默认值: `0.0`

&emsp;- 参考选项: `0`, `0.1`, `0.5`

&emsp;- 分类: `ROF`, `optimization`, `experimental`


## 生物生成优化 (naturalSpawningOptimizations)

控制自然生成区块缓存、异步预计算和刷怪统计化简；须同时开启总开关和相应子规则。

- 类型：`boolean`（总开关）
- 默认值：`false`；子规则默认值如下
- 分类：`ROF`、`optimization`、`experimental`

| 子规则 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `spawningChunkCache` | `boolean` | `false` | 每 40 gt 刷新自然生成候选区块列表；关闭时使用原版遍历 |
| `asyncNaturalSpawning` | `boolean` | `false` | 异步预计算自然生成候选；实体创建与数量统计仍在主线程，数据不可用或过期时回退原版 |
| `spawnStatisticSimplifyWhitelist` | `String` | `{}` | 指定刷怪统计化简的维度，如 `{minecraft:overworld,minecraft:the_end}`；`{}` 表示不化简 |

刷怪统计化简保留原有校验器，修改白名单后会更新已有世界，之后创建的世界也按白名单初始化。化简跳过统计中的区块查找与生物群系查询；只有维度内所有生物群系都没有刷怪密度限制时，结果才与原版完全一致。

使用 `/carpet group naturalSpawningOptimizations` 查看子规则。修改后立即保存到 JSON，例如：

```text
/carpet setDefault naturalSpawningOptimizations true
/carpet group naturalSpawningOptimizations asyncNaturalSpawning true
/carpet group naturalSpawningOptimizations spawnStatisticSimplifyWhitelist {minecraft:overworld}
```


## 随机刻优化 (randomTickOptimizations)

控制随机刻区块缓存与异步预计算；须同时开启总开关和相应子规则。

- 类型：`boolean`（总开关）
- 默认值：`false`；子规则默认值如下
- 分类：`ROF`、`optimization`、`experimental`

| 子规则 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `randomTickChunkCache` | `boolean` | `false` | 每 40 gt 刷新随机刻区块列表；关闭时使用原版遍历 |
| `asyncRandomTick` | `boolean` | `false` | 异步预计算下一游戏刻的随机刻候选；结果失效时回退原版 |

使用 `/carpet setDefault randomTickOptimizations true` 开启并保存总开关。使用 `/carpet group randomTickOptimizations` 查看子规则，使用 `/carpet group randomTickOptimizations asyncRandomTick true` 修改并立即保存到 JSON。

原来的五个独立规则现为上述子规则，名称和默认值保持不变。运行时仍直接读取静态字段，不解析 JSON。

## 珍珠优化 (enderPearlOptimizations)

控制珍珠 tick、加载票、高速自加载、加载等待时间、ECM 和 raycast 优化；须同时开启总开关和相应子规则。

- 类型：`boolean`（总开关）
- 默认值：`false`；子规则默认值如下
- 分类：`ROF`、`optimization`、`experimental`

| 子规则 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `enderPearlForcedTickMinSpeed` | `double` | `-1.0` | 高速珍珠自加载的速度阈值；负数禁用。此旧方案已不建议使用，优先使用更好的珍珠加载票 |
| `optimizedEnderPearlTick` | `boolean` | `false` | 尽量不让高速珍珠飞行生成新区块；ECM 未开启时只影响世界高度外的珍珠 |
| `betterEnderPearlTicket` | `boolean` | `false` | 使用特殊加载票；ECM 未开启时只影响世界高度外的珍珠 |
| `blockingEnderPearlLoading` | `int` | `0` | 珍珠加载等待的最大时间，单位为毫秒；0 禁用 |
| `exceedChunkMarker` | `boolean` | `false` | 超高度区块标记器（ECM），为 raycast 优化提供数据，可能增加少量存储空间 |
| `optimizedRaycast` | `boolean` | `false` | 使用 ECM 优化 raycast；要求 ECM 已启用且已从存档加载 |

高速自加载方案与原版加载逻辑有较大差异；优化珍珠 tick 时，珍珠会忽略未加载的实体碰撞箱。加载等待会堵塞主线程，以减少珍珠 tick 和世界 tick 不同步的问题。

ECM 原有命令、权限设置和世界数据格式保持不变。首次启用后，在需要使用的维度执行 `/exceedChunkMarker [dimension] loadFromWorld` 加载数据，再启用 raycast 优化，例如：

```text
/carpet setDefault enderPearlOptimizations true
/carpet group enderPearlOptimizations exceedChunkMarker true
/exceedChunkMarker minecraft:overworld loadFromWorld
/carpet group enderPearlOptimizations optimizedRaycast true
```

raycast 优化仍可能使投掷物忽略某些特定位置的实体碰撞箱；ECM 可能增加额外存储空间，原说明估计通常低于存档的 0.1%。

使用 `/carpet group enderPearlOptimizations` 查看子规则。例如 `/carpet group enderPearlOptimizations betterEnderPearlTicket true` 会修改并立即保存到 JSON。

子规则名称、默认值与校验保持不变。运行时仍直接读取静态字段。

## 生物 AI 优化 (mobAiOptimizations)

控制生物 AI 延迟、更好的 NoAI NBT 和猪灵 AI 优化；须同时开启总开关和相应子规则。

- 类型：`boolean`（总开关）
- 默认值：`false`；子规则默认值如下
- 分类：`ROF`、`optimization`、`feature`

| 子规则 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `mobAIDelayChance` | `double` | `0.0` | 白名单内生物发生 AI 延迟的概率，范围为 0 到 1；0 禁用 AI 延迟 |
| `mobAIDelayWhitelist` | `String` | `{!minecraft:drowned}` | 允许 AI 延迟的实体 ID 或类型标签列表；`{}` 表示空列表 |
| `mobAIDelayTicks` | `int` | `3` | AI 延迟持续的游戏刻数，必须为正数 |
| `betterNoAiNbt` | `boolean` | `false` | 让带 `NoBrainAI` NBT 的实体跳过 AI，同时保留被动运动 |
| `piglinLootItemDelay` | `int` | `0` | 猪灵只捡起存在时间超过此游戏刻数的掉落物 |
| `piglinStackingAISuppression` | `int` | `10000` | 堆叠猪灵 AI 抑制阈值；10000 保留原有禁用行为 |

AI 延迟的概率、白名单和持续时间保留原有校验器。白名单用花括号包裹、逗号分隔，可使用实体 ID（`minecraft:pig`）、实体类型标签（`#zombies`）和排除项（`!minecraft:drowned`、`!#undead`）。先取正选项的并集，再减去排除项；全部为排除项时表示除这些之外的全部生物。未加载的标签按空集合匹配。概率为 0 或白名单为空时，AI 延迟不生效，其他子规则仍按各自条件生效。

AI 延迟的判定结果与剩余时间仍保存在实体的 `MobAi` NBT 中。剩余时间为负数表示已经判定并保留 AI；未命中白名单时不写入该字段，重载后仍允许匹配。

开启 `betterNoAiNbt` 后，使用 `/data merge entity <目标> {NoBrainAI:1b}` 设置标签，使用 `{NoBrainAI:0b}` 或 `/data remove entity <目标> NoBrainAI` 移除，也可在 `/summon` 时写入。标签随实体存档保存。此模式跳过目标选择器、goal 选择器、导航、感知、大脑和移动/视角/跳跃控制器，保留重力、落地摩擦、水流/气泡柱、实体推挤、挤压伤害、爆炸击退、活塞推动、骑乘、燃烧、捡装备和距离消失检查。`noActionTime` 不再累加，因此不会因玩家附近的 600 tick 无动作而消失；带此标签的末影龙、凋灵等 Boss 会停止相位推进。

使用 `/carpet group mobAiOptimizations` 查看子规则。例如：

```text
/carpet setDefault mobAiOptimizations true
/carpet group mobAiOptimizations mobAIDelayChance 0.5
/carpet group mobAiOptimizations mobAIDelayWhitelist {!minecraft:drowned,!minecraft:piglin}
/carpet group mobAiOptimizations betterNoAiNbt true
```

命令支持补全，修改后立即保存到 JSON。

子规则名称、默认值和原有生效条件保持不变。运行时仍直接读取静态字段和已解析的白名单，不解析 JSON。

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


## 实体ID溢出周期 (entityIDOverflowPeriod)

&emsp;设置为0表示禁用

&emsp;- 类型: `int`

&emsp;- 默认值: `0`

&emsp;- 分类: `ROF`, `feature`, `creative`


## 发包限制 (packetLimits)

统一管理实体生成发包限制、追踪恢复时间和普通粒子发包范围。父规则为 boolean 总开关，默认值为 `false`，类别为 `ROF`、`optimization`、`packet`。子规则通过 `/carpet group packetLimits` 查看和配置，修改子规则后立即保存到 JSON。

| 子规则 | 默认值 | 旧名称 |
|---|---|---|
| `entitySpawnLimitPerTick` | `512` | `entitySpawnPacketLimitTicks` |
| `limitedEntityTrackingRange` | `16` | `entitySpawnPacketLimitTicksTrackerDistance` |
| `entitySpawnLimitPerSecond` | `256` | `entitySpawnPacketLimitSeconds` |
| `entityTrackingRecoveryTicks` | `200` | `entitySpawnPacketLimitSecondsRecoverTime` |
| `particlePacketRange` | `2.0` | `particlesPacketsRange` |

只有 `packetLimits=true` 时才启用实体生成限制和粒子范围限制。每 tick 阈值按同一世界的同种实体计数，超过阈值的实体使用 `limitedEntityTrackingRange` 方块的追踪范围。每秒阈值根据同一区块、同种实体上一秒的生成数量，按概率阻止追踪；一秒按 20 个游戏刻计算。这两个阈值均可用负数单独禁用。

两种限制共用 `entityTrackingRecoveryTicks`：受限实体存活时间达到该游戏刻数时，恢复限制前的追踪范围并刷新玩家追踪。同时受到两种限制的实体也会恢复。负数禁用自动恢复；关闭总开关时，下一次追踪器 tick 仍会恢复受限实体。

`particlePacketRange` 只控制普通、非强制粒子，单位为方块。关闭总开关时使用原版的 32 方块范围，强制粒子始终使用原版的 512 方块范围。两个距离参数必须非负。TNT 发包优化由 `tntExplosionOptimizations` 中的 `tntPacketOptimization` 子规则独立控制。

```text
/carpet group packetLimits
/carpet setDefault packetLimits true
/carpet group packetLimits entityTrackingRecoveryTicks 200
/carpet group packetLimits particlePacketRange 2
```

关闭 `packetLimits` 总开关会停止限制并恢复追踪，但保留 JSON 中的子规则配置。

## 每游戏刻击杀进度触发次数上限 (killedTriggerLimitPerTick)

&emsp;限制每 tick 内 KilledTrigger.trigger 的执行次数，所有实体共用一个额度。

&emsp; `0 表示不限制.`

&emsp;- 类型: `int`

&emsp;- 默认值: `0`

&emsp;- 参考选项: `0`, `1`, `10`, `100`

&emsp;- 分类: `ROF`, `optimization`, `feature`


## TNT 与爆炸优化 (tntExplosionOptimizations)

控制 TNT 合并模式、爆炸合并、爆炸优化阈值和 TNT 发包优化；须同时开启总开关和相应子规则。

- 类型：`boolean`（总开关）
- 默认值：`false`；子规则默认值如下
- 分类：`ROF`、`optimization`、`tnt`、`experimental`

| 子规则 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `mergeTNTNext` | `MergeTNTNextMode` | `FALSE` | 选择 TNT 合并模式，不能与其他 TNT 合并规则同时开启 |
| `mergeExplosion` | `boolean` | `false` | 合并 TNT 产生的多次爆炸，伤害只结算一次；推力按合并次数补齐 |
| `optimizedExplosion` | `int` | `0` | 同一 tick、坐标和威力完全相同的连续爆炸达到此次数后，若最坏情况判断确认不可能破坏方块，则跳过方块计算；0 或负数禁用 |
| `tntPacketOptimization` | `boolean` | `false` | 去掉不必要的 TNT Fuse 包并减少发包频率，可能造成客户端显示错误 |

`mergeExplosion` 仍要求 TNT 合并与爆炸优化已启用，保留原有生效条件。`tntPacketOptimization` 在本组中独立控制，不受 `packetLimits` 控制。

`mergeTNTNext` 的命令选项为 `false`、`true`、`safe`、`almost_vanilla`、`safe_plus`。`true` 使用旧爆炸处理，移动 TNT 可能出现非原版行为；`safe` 使用更安全的爆炸处理；`almost_vanilla` 只在爆炸时合并并保留 tick 顺序；`safe_plus` 在每 tick 的提前遍历中只合并相邻的同点位 TNT，不改写实体 tick 顺序。理论上 `almost_vanilla` 不改变 TNT 行为，如发现原版差异请提交 issue。

使用 `/carpet group tntExplosionOptimizations` 查看子规则，例如：

```text
/carpet setDefault tntExplosionOptimizations true
/carpet group tntExplosionOptimizations mergeTNTNext almost_vanilla
/carpet group tntExplosionOptimizations optimizedExplosion 4
/carpet group tntExplosionOptimizations mergeExplosion true
```

每次修改都立即保存到 JSON。

子规则名称、默认值与校验保持不变。运行时仍直接读取静态字段。

## 实体推挤收集优化 (optimizedEntityCollection)

&emsp;使用分区空间索引优化 Lithium 的高密度实体推挤候选收集。可能改变实体遍历顺序。

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `optimization`, `experimental`


## 装备掉落优化 (optimizedEquipmentDrops)

&emsp;跳过生物死亡时空装备槽的掉落附魔计算，以及没有装备掉落效果的附魔上下文创建。

&emsp; `空装备槽不再执行装备掉落附魔效果，可能改变自定义随机附魔效果的随机数消耗顺序`

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `optimization`, `experimental`


## 经验球生成合并查询优化 (optimizedExperienceOrbMerge)

&emsp;生成经验球时找到第一个符合原版合并条件的经验球就停止查询。

&emsp; `保留原版查询顺序、随机分组、经验值匹配和合并逻辑`

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `optimization`


## 假人tick精简 (optimizedFakePlayerTick)

&emsp;精简Carpet假人的客户端同步/进度/统计/Waypoint等每tick逻辑，保留主手物品tick与行为模拟

&emsp; `仅对Carpet假人(EntityPlayerMPFake)生效`

&emsp; `可能影响假人的统计、进度、计分板自动同步与定位条显示`

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `optimization`


