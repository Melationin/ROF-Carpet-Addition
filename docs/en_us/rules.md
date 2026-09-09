# Rules

## High-density entity push collection optimization (optimizedEntityCollection)

&emsp;Uses a section-local spatial index to optimize Lithium push candidate collection. Entity iteration order may change.

&emsp;- Type: `boolean`

&emsp;- Default: `false`

&emsp;- Categories: `ROF`, `optimization`

## Climbable block tag check cache (optimizedClimbableTagCheck)

&emsp;Caches the `CLIMBABLE` block-tag lookup used by Lithium entity pushability checks, reducing repeated tag lookups in `LivingEntity.onClimbable`. The cache is invalidated after data-pack reloads.

&emsp;- Type: `boolean`

&emsp;- Default: `false`

&emsp;- Categories: `ROF`, `optimization`


## commandEntityID

&emsp;Command to view and control entity IDs

&emsp;- type: `String`

&emsp;- default: `false`

&emsp;- categories: `ROF`, `feature`, `creative`, `command`


## commandEntityIDSet

&emsp;Command to set entity IDs

&emsp;- type: `String`

&emsp;- default: `false`

&emsp;- categories: `ROF`, `feature`, `creative`, `command`


## commandExceedChunkMarker

&emsp;Controls the permission level of ECM command. Invalid when ECM is not started.

&emsp;- type: `String`

&emsp;- default: `ops`

&emsp;- categories: `command`, `ROF`


## commandLoadedChunkFinder

&emsp;Records connected chunks that are active during a certain period, used to find forgotten chunk loaders.

&emsp;- type: `String`

&emsp;- default: `ops`

&emsp;- categories: `ROF`, `command`


## commandPacketLoggerPlus

&emsp;Records the pre-compression size of various packets.

&emsp;- type: `String`

&emsp;- default: `ops`

&emsp;- categories: `command`, `ROF`


## commandRequirementModify

&emsp;Modify permission requirements of specified commands

&emsp;- type: `String`

&emsp;- default: `ops`

&emsp;- categories: `command`, `creative`


## commandRulesSearcher

&emsp;Adds a subcommand 'search' to carpet, allowing to search carpet rules by keyword

&emsp;- type: `String`

&emsp;- default: `true`

&emsp;- categories: `ROF`, `command`


## commandSpawnWhitedListedPlayer

&emsp;Used to summon fake players without prefix

&emsp;- type: `String`

&emsp;- default: `ops`

&emsp;- categories: `ROF`, `command`, `creative`


## enderPearlForcedTickMinSpeed

&emsp;Applies new loading logic to ender pearls with speed above a certain threshold, which is more stable and requires fewer loaded chunks.

&emsp; `The set value indicates the self-loading speed threshold. Set to a negative value to disable.`

&emsp; `For pearls with the new loading logic, the loading behavior differs significantly from vanilla.`

&emsp;- type: `double`

&emsp;- default: `-1.0`

&emsp;- options: `16.0`, `-1.0`

&emsp;- categories: `ROF`, `optimization`, `feature`


## entityIDOverflowPeriod

&emsp;Set to 0 to disable

&emsp;- type: `int`

&emsp;- default: `0`

&emsp;- categories: `ROF`, `feature`, `creative`


## entitySpawnPacketLimitSeconds

&emsp;When too many entities of the same type are spawned in the same second, probabilistically prevents tracking and packet sending of that entity type.

&emsp; `Set to a negative number to disable`

&emsp;- type: `int`

&emsp;- default: `-1`

&emsp;- options: `-1`, `100`

&emsp;- categories: `ROF`, `optimization`, `packet`


## entitySpawnPacketLimitTicks

&emsp;When too many entities of the same type are spawned in the same tick, reduces the packet sending distance for excess entities. Used for optimization of large-yield pearl cannons.

&emsp; `Set to a negative number to disable`

&emsp;- type: `int`

&emsp;- default: `-1`

&emsp;- options: `-1`, `100`, `1000`

&emsp;- categories: `ROF`, `optimization`, `packet`


## entitySpawnPacketLimitTicksTrackerDistance

&emsp;Sets the packet sending distance for entities that are limited

&emsp;- type: `int`

&emsp;- default: `16`

&emsp;- options: `2`, `16`, `64`

&emsp;- categories: `ROF`, `optimization`, `packet`


## exceedChunkMarker

&emsp;Prerequisite for raycast optimization, may cause additional storage (generally only increases save size by less than 0.1%)

&emsp; `When enabling for the first time, be sure to load once with /exceedChunkMarker`

&emsp;- type: `boolean`

&emsp;- default: `false`

&emsp;- categories: `ROF`, `experimental`


## mergeTNTNext

&emsp;A more aggressive TNT merging scheme, may cause unexpected results. Cannot be used together with other TNT merging rules.

&emsp;- type: `boolean`

&emsp;- default: `false`

&emsp;- categories: `ROF`, `optimization`, `tnt`, `feature`


## optimizeForcedEnderPearlTick

&emsp;Only available when better pearl self-loading is enabled. In most cases, prevents high-speed pearls from generating new chunks, greatly reducing save size. When ECM is not enabled, only pearls outside world height will not generate chunks.

&emsp; `Known feature: Pearls will ignore entity hitboxes that are not loaded.`

&emsp; `false - Disable optimization`

&emsp; `true - Enable optimization, and pearl behavior matches current version`

&emsp; `1_21_2- - Enable optimization, and pearl behavior matches version 1.21.2 and below`

&emsp; `1_21_2+ - Enable optimization, and pearl behavior matches version 1.21.2 and above`

&emsp;- type: `String`

&emsp;- default: `false`

&emsp;- options: `false`, `true`, `1_21_2-`, `1_21_2+`

&emsp;- categories: `ROF`, `optimization`, `experimental`


## optimizeRaycast

&emsp;Optimizes raycast via ECM. Ensure ECM is enabled and has been loaded from the save before turning on.

&emsp; `Known feature: Projectiles will ignore entity hitboxes at some specific positions.`

&emsp;- type: `boolean`

&emsp;- default: `false`

&emsp;- categories: `ROF`, `optimization`, `experimental`


## particlesPacketsRange

&emsp;This distance only affects forced particle packets. But most particles are forced.

&emsp; `Vanilla default is 32`

&emsp;- type: `double`

&emsp;- default: `32.0`

&emsp;- options: `32.0`, `1.0`, `8.0`

&emsp;- categories: `ROF`, `optimization`, `packet`


## piglinLootItemDelay

&emsp;Only items that have been present for a certain time will be picked up by piglins

&emsp;- type: `int`

&emsp;- default: `0`

&emsp;- options: `0`, `20`

&emsp;- categories: `ROF`, `optimization`, `feature`


## piglinStackingAISuppression

&emsp;For piglins stacked to a certain amount, suppress AI of some of them.

&emsp;- type: `int`

&emsp;- default: `10000`

&emsp;- options: `100`, `10000`

&emsp;- categories: `ROF`, `optimization`, `feature`


## tntPacketOptimization

&emsp;Optimizes TNT entities by removing unnecessary TNT entity packets (Fuse) and reducing packet frequency

&emsp; `May cause client display errors`

&emsp;- type: `boolean`

&emsp;- default: `false`

&emsp;- categories: `ROF`, `optimization`, `packet`


