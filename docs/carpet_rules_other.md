# Carpet 其他规则汇总

> 来源: `@Rule` + `@QuickTranslations` + `zh_cn.json` (仅基于当前工程中的规则字段)

| 规则ID | 名称 | 描述 | 额外 |
| --- | --- | --- | --- |
| `highLagFreezeLimit` | 高卡自动暂停阈值 | 设置正数表示启用。当连续几tick用时超过此阈值时，自动暂停。 | - |
| `highLagFreezeTickLimit` | 高卡自动暂停tick阈值 | 设置正数表示启用。当连续几tick用时超过此阈值时，自���暂停。 | - |
| `enderPearlForcedTickMinSpeed` | 更好的高速珍珠自加载 | 对速度高于一定值的珍珠使用新的加载逻辑，更加稳定，需要加载的区块更少。 | 设置的值表示自加载速度阈值。设置为负值时，表示禁用。<br>对弱加载炮可能有预料之外的影响 |
| `forceEnderPearlLogger` | 强制加载态珍珠广播 | 向丢出者发送强加载态珍珠的坐标(容易刷屏) | - |
| `highEnderPearlNoChunkLoading` | 高珍珠不加载区块 | 在开启更好的珍珠加载的情况下，超过世界高度的珍珠不会加载区块，但会加载自身 | - |
| `chunkModifyLogger` | 区块修改记录器 | 记录区块生成时间和最后保存时间，占用体积相对较大(存档体积的2%以下) | - |
| `chunkModifyLoggerPlayerChunk` | 区块修改记录器保存玩家区块 | 将玩家途径区块视为已修改 | - |
| `exceedChunkMarker` | 超高度区块标记器(ECM) | Raycast优化前置，可能会造成额外的存储空间(一般只会增加存档的0.1%以下) | 在第一次启用时，务必使用/exceedChunkMarker 加载一次 |
| `optimizeRaycast` | raycast优化 | 通过ECM优化raycast，开启时请保证ECM已打开且已经从存档加载过 | 已知特性：投掷物会忽略一些特定位置的实体碰撞箱。 |
| `optimizeForcedEnderPearlTick` | 优化自加载��珍珠tick | 仅在更好的珍珠自加载与ECM启用时可用。让大多数情况下高速珍珠的飞行不生成新区块，可大幅度减少存档体积。开启时请保证ECM已打开且已经从存档加载过 | 已知特性：珍珠会忽略未加载的实体碰撞箱。 |
| `getBiomeLayerCache` | 低层群系获取缓存 | 会增大一些内存占用，适用于y0刷怪塔(效果不明显) | - |
| `mergeTNTNext` | 合并TNTnext | 更为激进的tnt合并方案, 可能会导致预期之外的结果。不能与其他tnt合并一起开。 | - |
| `reverseBlockPosTraversal` | BlockPos Y轴遍历反向 | 让BlockPos Y轴遍历反向，让实体传送地狱门优先传送靠上的地狱门 | - |
| `optimizeItemMerge` | 物品合并优化 | 尽量让物品达到一组，以减轻卡顿 | - |
| `tntPacketOptimization` | tnt实体发包优化 | 通过去掉不必要的tnt实体发包(Fuse)与减少发包频率，优化tnt实体 | 可能会造成客户端显示错误 |
| `entitySpawnPacketLimitTicks` | 每游戏刻实体生成发包限制 | 在同一tick生成过多的同种实体时，减少过多的实体的发包距离。用于大当量珍珠炮的优化 | 设置为负数表示禁用 |
| `entitySpawnPacketLimitTicksTrackerDistance` | 每游戏刻实体生成发包限制发包距离 | 设置发包限制的实体的发��距离 | - |
| `entitySpawnPacketLimitSeconds` | 每秒实体生成发包限制 | 在同一秒生成过多的同种实体时，按概率阻止该种实体的追踪与发包。 | 设置为负数表示禁用 |
| `piglinLootItemDelay` | 猪灵捡掉落物延迟 | 只有出现一定时间的掉落物才会被猪灵捡起 | - |
| `piglinMax` | 堆叠猪灵降智阈值 | 堆叠到一定量的猪灵自动降智，减少卡顿（不会影响猪灵交易） | - |
| `chunkCacheMaxCount` | 区块数据缓存命令 | 在设置为false时，表示禁用区块数据缓存。此功能会导致显著的内存占用增加，请谨慎使用。对大型刷怪塔和世吞等有一定的优化效果 | 为了避免所有区块缓存导致内存过大，只允许一定区域的区块可以缓存<br>`/chunkCache <dimension> <dataType> list : 列出所有要缓存的区块` |

