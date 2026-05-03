# Rules


## commandEntityID

&emsp;Command to view and control entity IDs

&emsp;- 类型: `String`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `feature`, `creative`, `command`


## commandEntityIDSet

&emsp;Command to set entity IDs

&emsp;- 类型: `String`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `feature`, `creative`, `command`


## commandExceedChunkMarker

&emsp;Controls the permission level of ECM command. Invalid when ECM is not started.

&emsp;- 类型: `String`

&emsp;- 默认值: `ops`

&emsp;- 分类: `command`, `ROF`


## commandLoadedChunkFinder

&emsp;Records connected chunks that are active during a certain period, used to find forgotten chunk loaders.

&emsp;- 类型: `String`

&emsp;- 默认值: `ops`

&emsp;- 分类: `ROF`, `command`


## commandPacketLoggerPlus

&emsp;Records the pre-compression size of various packets.

&emsp;- 类型: `String`

&emsp;- 默认值: `ops`

&emsp;- 分类: `command`, `ROF`


## commandRequirementModify

&emsp;Modify permission requirements of specified commands

&emsp;- 类型: `String`

&emsp;- 默认值: `ops`

&emsp;- 分类: `command`, `creative`


## commandRulesSearcher

&emsp;Adds a subcommand 'search' to carpet, allowing to search carpet rules by keyword

&emsp;- 类型: `String`

&emsp;- 默认值: `true`

&emsp;- 分类: `ROF`, `command`


## commandSpawnWhitedListedPlayer

&emsp;Used to summon fake players without prefix

&emsp;- 类型: `String`

&emsp;- 默认值: `ops`

&emsp;- 分类: `ROF`, `command`, `creative`


## enderPearlForcedTickMinSpeed

&emsp;Applies new loading logic to ender pearls with speed above a certain threshold, which is more stable and requires fewer loaded chunks.

&emsp; `The set value indicates the self-loading speed threshold. Set to a negative value to disable.`

&emsp; `For pearls with the new loading logic, the loading behavior differs significantly from vanilla.`

&emsp;- 类型: `double`

&emsp;- 默认值: `-1.0`

&emsp;- 参考选项: `16.0`, `-1.0`

&emsp;- 分类: `ROF`, `optimization`, `feature`


## entityIDOverflowPeriod

&emsp;Set to 0 to disable

&emsp;- 类型: `int`

&emsp;- 默认值: `0`

&emsp;- 分类: `ROF`, `feature`, `creative`


## entitySpawnPacketLimitSeconds

&emsp;When too many entities of the same type are spawned in the same second, probabilistically prevents tracking and packet sending of that entity type.

&emsp; `Set to a negative number to disable`

&emsp;- 类型: `int`

&emsp;- 默认值: `-1`

&emsp;- 参考选项: `-1`, `100`

&emsp;- 分类: `ROF`, `optimization`, `packet`


## entitySpawnPacketLimitTicks

&emsp;When too many entities of the same type are spawned in the same tick, reduces the packet sending distance for excess entities. Used for optimization of large-yield pearl cannons.

&emsp; `Set to a negative number to disable`

&emsp;- 类型: `int`

&emsp;- 默认值: `-1`

&emsp;- 参考选项: `-1`, `100`, `1000`

&emsp;- 分类: `ROF`, `optimization`, `packet`


## entitySpawnPacketLimitTicksTrackerDistance

&emsp;Sets the packet sending distance for entities that are limited

&emsp;- 类型: `int`

&emsp;- 默认值: `16`

&emsp;- 参考选项: `2`, `16`, `64`

&emsp;- 分类: `ROF`, `optimization`, `packet`


## exceedChunkMarker

&emsp;Prerequisite for raycast optimization, may cause additional storage (generally only increases save size by less than 0.1%)

&emsp; `When enabling for the first time, be sure to load once with /exceedChunkMarker`

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `experimental`


## mergeTNTNext

&emsp;A more aggressive TNT merging scheme, may cause unexpected results. Cannot be used together with other TNT merging rules.

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `optimization`, `tnt`, `feature`


## optimizeForcedEnderPearlTick

&emsp;Only available when better pearl self-loading is enabled. In most cases, prevents high-speed pearls from generating new chunks, greatly reducing save size. When ECM is not enabled, only pearls outside world height will not generate chunks.

&emsp; `Known feature: Pearls will ignore entity hitboxes that are not loaded.`

&emsp; `false - Disable optimization`

&emsp; `true - Enable optimization, and pearl behavior matches current version`

&emsp; `1_21_2- - Enable optimization, and pearl behavior matches version 1.21.2 and below`

&emsp; `1_21_2+ - Enable optimization, and pearl behavior matches version 1.21.2 and above`

&emsp;- 类型: `String`

&emsp;- 默认值: `false`

&emsp;- 参考选项: `false`, `true`, `1_21_2-`, `1_21_2+`

&emsp;- 分类: `ROF`, `optimization`, `experimental`


## optimizeRaycast

&emsp;Optimizes raycast via ECM. Ensure ECM is enabled and has been loaded from the save before turning on.

&emsp; `Known feature: Projectiles will ignore entity hitboxes at some specific positions.`

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `optimization`, `experimental`


## particlesPacketsRange

&emsp;This distance only affects forced particle packets. But most particles are forced.

&emsp; `Vanilla default is 32`

&emsp;- 类型: `double`

&emsp;- 默认值: `32.0`

&emsp;- 参考选项: `32.0`, `1.0`, `8.0`

&emsp;- 分类: `ROF`, `optimization`, `packet`


## piglinLootItemDelay

&emsp;Only items that have been present for a certain time will be picked up by piglins

&emsp;- 类型: `int`

&emsp;- 默认值: `0`

&emsp;- 参考选项: `0`, `20`

&emsp;- 分类: `ROF`, `optimization`, `feature`


## piglinStackingAISuppression

&emsp;For piglins stacked to a certain amount, suppress AI of some of them.

&emsp;- 类型: `int`

&emsp;- 默认值: `10000`

&emsp;- 参考选项: `100`, `10000`

&emsp;- 分类: `ROF`, `optimization`, `feature`


## tntPacketOptimization

&emsp;Optimizes TNT entities by removing unnecessary TNT entity packets (Fuse) and reducing packet frequency

&emsp; `May cause client display errors`

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `optimization`, `packet`


