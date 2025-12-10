## ROF Carpet Addition
一个为生电服务器编写的 Carpet 拓展，主要目的是为了在不影响正常生电的情况下通过修改原版的一些逻辑减少卡顿。

## 前置模组

| 名称          | 类型 | 链接                                                                                                                                                                       | 备注 |
|-------------|----|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----|
| Carpet      | 必须 | [MC百科](https://www.mcmod.cn/class/2361.html) &#124; [Modrinth](https://modrinth.com/mod/carpet) | -  |
| Fabric API  | 必须 | [MC百科](https://www.mcmod.cn/class/3124.html) &#124; [官方](https://fabricmc.net/)                                                                                          | - |

## 版本支持

| MC版本   | 当前开发状态 | 最后支持版本 |  
|--------|------|--------|
| 1.21.1 | 持续更新 | -      |
| 1.21.4 | 持续更新   | -      |
| 1.21.6 | 持续更新   | -      |
| 1.21.7 | 持续更新   | -      |


### 更好的高速珍珠自加载(enderPearlForcedTickMinSpeed)
对速度高于一定值的珍珠使用新的加载逻辑，更加稳定，需要加载的区块更少。
**注意：** 设置的值表示自加载速度阈值。设置为负值时，表示禁用。对弱加载炮可能有预料之外的影响。

- 类型：`双精度浮点数`
- 默认值：`-1.0`
- 参考选项：`16.0`，`-1.0`
- 分类：`ROF`，`OPTIMIZATION`，`FEATURE`

### 强制加载态珍珠广播(forceEnderPearlLogger)
向丢出者发送强加载态珍珠的坐标(容易刷屏)

- 类型：`布尔值`
- 默认值：`false`
- 参考选项：`true`，`false`
- 分类：`ROF`，`FEATURE`

### 高区块监听器(highChunkListener)
使用额外文件记录最高方块高于128格的区块，用于raycast优化。
**注意：** 为了减少额外开销，只有地狱启用。

- 类型：`布尔值`
- 默认值：`true`
- 参考选项：`true`，`false`
- 分类：`ROF`，`HCL`，`EXPERIMENTAL`

### 使用HCL的raycast优化(optimizeRaycastWithHCL)
使用高区块监听器优化投掷物 raycast，在原版行为基本一致的情况下优化raycast。
**注意：** 若使用锂，请关闭锂的raycast优化。


- 类型：`布尔值`
- 默认值：`false`
- 参考选项：`true`，`false`
- 分类：`ROF`，`HCL`，`OPTIMIZATION`，`EXPERIMENTAL`

### 合并TNTnext(mergeTNTNext)
更为激进的tnt合并方案, 可能会导致预期之外的结果。不能与其他tnt合并一起开。

- 类型：`布尔值`
- 默认值：`false`
- 参考选项：`true`，`false`
- 分类：`ROF`，`OPTIMIZATION`，`TNT`

### 怪物AI延迟(mobAIDelay)
可能造成预期之外的结果。可以降低大型刷怪塔的卡顿(效果不明显)

- 类型：`整数`
- 默认值：`0`
- 参考选项：`0`，`10`
- 分类：`ROF`，`OPTIMIZATION`，`FEATURE`

### 低层群系获取缓存(getBiomeLayerCache)
会增大一些内存占用，适用于y0刷怪塔(效果不明显)

- 类型：`整数`
- 默认值：`0`
- 参考选项：`0`，`3`
- 分类：`ROF`，`OPTIMIZATION`，`FEATURE`

### 优化生物生成尝试(optimizeSpawnAttempts)
提前过滤掉一些不可能的生成点，减少卡顿(效果不明显)

- 类型：`布尔值`
- 默认值：`false`
- 参考选项：`true`，`false`
- 分类：`ROF`，`OPTIMIZATION`，`FEATURE`

### 物品合并优化(optimizeItemMerge)
尽量让物品达到一组，以减轻卡顿

- 类型：`布尔值`
- 默认值：`false`
- 参考选项：`true`，`false`
- 分类：`ROF`，`OPTIMIZATION`，`FEATURE`

### 猪灵捡掉落物延迟(piglinLootItemDelay)
只有出现一定时间的掉落物才会被猪灵捡起

- 类型：`整数`
- 默认值：`0`
- 参考选项：`0`，`20`
- 分类：`ROF`，`OPTIMIZATION`，`FEATURE`

### 堆叠猪灵降智阈值(piglinMax)
堆叠到一定量的猪灵自动降智，减少卡顿（不会影响猪灵交易）

- 类型：`整数`
- 默认值：`10000`
- 参考选项：`100`，`10000`
- 分类：`ROF`，`OPTIMIZATION`，`FEATURE`

###   tnt实体发包优化(TntPacketOptimization)
减少tnt实体的发包。可能会使客户端显示异常。"

- 类型：`布尔值`
- 默认值：`false`
- 参考选项：`true`，`false`
- 分类：`ROF`，`OPTIMIZATION`，`PACKET`

