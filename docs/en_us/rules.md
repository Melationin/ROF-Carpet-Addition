# Rules


## asyncNaturalSpawning

&emsp;Precomputes natural-spawning candidates off-thread; entity creation and counting stay on the main thread

&emsp; `Falls back to vanilla when data is unavailable or stale`

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `optimization`, `experimental`


## asyncRandomTick

&emsp;Precomputes the next tick's random-tick candidates off-thread; falls back to vanilla when the result is invalid

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `optimization`, `experimental`


## betterNoAiNbt

&emsp;Skips AI logic (Mob.serverAiStep) for entities carrying the NoBrainAI NBT tag, while keeping passive motion such as gravity, fluids, entity pushing, crush damage, explosion knockback, pistons and riding. Unlike vanilla NoAI, the entity does not freeze in mid-air.

&emsp; `/data merge entity <target> {NoBrainAI:1b} to set, {NoBrainAI:0b} or /data remove entity <target> NoBrainAI to clear; the tag is saved with the entity and can also be written in /summon NBT`

&emsp; `Skipped: target selector, goal selector, navigation, sensing, brain, and movement/look/jump controls`

&emsp; `Kept: gravity and ground friction, water and bubble columns, entity pushing and crush damage, explosion knockback, pistons, riding (a player can still steer the mount), burning, item pickup, distance-based despawn`

&emsp; `Note: the AI-independent noActionTime no longer accumulates, so the entity will not despawn from 600 ticks of inactivity near a player (same as vanilla NoAI); bosses such as the Ender Dragon or Wither stop their phase progression while tagged`

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `feature`


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


## enderPearlForcedSync

&emsp;Keeps a pearl's tick in sync with the world tick despite async chunk loading delays

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 参考选项: `false`, `true`

&emsp;- 分类: `ROF`, `optimization`, `feature`


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


## entitySpawnPacketLimitSecondsRecoverTime

&emsp;Throttled entities regain vanilla tracking distance after surviving this many ticks

&emsp; `Set to a negative value to disable`

&emsp; `No effect on the per-tick limit`

&emsp;- 类型: `int`

&emsp;- 默认值: `200`

&emsp;- 参考选项: `-1`, `200`, `100`, `400`

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

&emsp; `False: no merging`

&emsp; `TRUE: old explosion handling; moving merged TNT behaves unlike vanilla`

&emsp; `SAFE: safer explosion handling; moving merged TNT behaves like vanilla`

&emsp; `AlmostVanilla: merges only when TNT explodes, keeping TNT tick order`

&emsp; `AlmostVanilla should not change TNT behaviour; report any vanilla mismatch as an issue`

&emsp;- 类型: `MergeTNTNextMode`

&emsp;- 默认值: `FALSE`

&emsp;- 分类: `ROF`, `optimization`, `tnt`, `feature`


## mobAIDelayChance

&emsp;Chance for a whitelisted mob to have its AI temporarily disabled. The roll happens once, the first time the entity is about to run AI; only when the random value is below this chance is the AI suppressed. Passive motion (gravity, fluids, entity pushing, crush damage, explosion knockback, pistons, riding) is kept.

&emsp; `0 (default) disables the feature entirely; an empty entity list also disables it. Suggested values: 0 and 0.1 (the rule does not restrict the value, anything between 0 and 1 works)`

&emsp; `The roll result is stored in the entity's MobAi NBT field, so reloading a chunk never re-rolls it; every entity is decided exactly once`

&emsp; `A suppressed entity restores its AI when the restore time elapses and is never suppressed again`

&emsp; `Never suppressed: baby mobs (LivingEntity.isBaby) and zombified piglins spawned by a nether portal random tick (they carry a portal cooldown); noActionTime still accumulates, so vanilla 600-tick idle despawn keeps working`

&emsp;- 类型: `double`

&emsp;- 默认值: `0.0`

&emsp;- 参考选项: `0`, `0.1`

&emsp;- 分类: `ROF`, `optimization`


## mobAIDelayTicks

&emsp;How many ticks a suppressed entity stays without AI before its AI is restored; must be a positive number.

&emsp; `Counted in ticks experienced by the entity and saved with it (the remaining time is exactly the MobAi NBT field)`

&emsp; `A negative remaining time means 'decided: AI is not suppressed' - that is also the state after the AI has been restored`

&emsp; `It is also the maximum suppression duration; suggested values are 50 (default, 2.5 seconds) and 200 (10 seconds)`

&emsp;- 类型: `int`

&emsp;- 默认值: `50`

&emsp;- 参考选项: `50`, `200`

&emsp;- 分类: `ROF`, `optimization`


## mobAIDelayWhitelist

&emsp;Comma-separated whitelist of mobs eligible for AI delays. Entries are entity ids (minecraft:pig) or entity type tags (#zombies); prefix an entry with ! to exclude it. A list with only exclusions means 'everything except those'; an empty list disables the feature.

&emsp; `Example: minecraft:pig,#zombies,!#undead - pigs and zombie-like mobs, but never undead ones. Suggested values: !minecraft:drowned (default, every mob except drowned) and !minecraft:drowned,!minecraft:piglin`

&emsp; `Matching: union of all positive entries, minus every negative entry; hitting any positive entry is enough`

&emsp; `Tags are data-pack tags matched by name; a tag that is not loaded behaves as an empty set (matches nothing)`

&emsp; `Invalid entity ids are rejected and the previous value is kept`

&emsp;- 类型: `String`

&emsp;- 默认值: `!minecraft:drowned`

&emsp;- 参考选项: `!minecraft:drowned`, `!minecraft:drowned,!minecraft:piglin`

&emsp;- 分类: `ROF`, `optimization`


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


## optimizeItemMerge

&emsp;Fills item stacks to a full stack to reduce lag (minor effect)

&emsp; `Partial merging: drops fill up to a full stack first, the remainder stays in the original drop`

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `optimization`, `feature`


## optimizeRaycast

&emsp;Optimizes raycast via ECM. Ensure ECM is enabled and has been loaded from the save before turning on.

&emsp; `Known feature: Projectiles will ignore entity hitboxes at some specific positions.`

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `optimization`, `experimental`


## optimizedClimbableTagCheck

&emsp;Caches the CLIMBABLE block-tag lookup used by Lithium entity pushability checks, reducing repeated tag lookups in LivingEntity.onClimbable. The cache is invalidated after data-pack reloads.

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `optimization`


## optimizedEntityCollection

&emsp;Uses a section-local spatial index to optimize Lithium push candidate collection. Entity iteration order may change.

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `optimization`, `experimental`


## optimizedExplosion

&emsp;Once this many explosions hit the same point within one game tick, the block calculation is skipped when a worst-case check proves no block can be destroyed; only entities are affected

&emsp; `0 or less disables it`

&emsp; `Only same-tick explosions with identical coordinates and power`

&emsp; `State resets at every world tick start and on any block change`

&emsp; `Air-only explosions are optimized too: vanilla lists the air positions along each ray, and skipping them changes no block`

&emsp; `Skipped explosions consume no raycast random numbers, spawn fewer particles, and report an empty block list to the scarpet explosion event`

&emsp;- 类型: `int`

&emsp;- 默认值: `0`

&emsp;- 参考选项: `0`, `4`, `8`, `16`

&emsp;- 分类: `ROF`, `optimization`, `experimental`


## optimizedFakePlayerTick

&emsp;Trims client sync/advancement/stats/Waypoint per-tick logic for Carpet fake players, keeping main-hand item tick and behavior simulation

&emsp; `Only affects Carpet fake players (EntityPlayerMPFake)`

&emsp; `May affect fake player stats, advancements, scoreboard auto-sync and locator bar display`

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `optimization`


## particlesPacketsRange

&emsp;This distance only affects forced particle packets. But most particles are forced.

&emsp; `Vanilla default is 32`

&emsp; `Forced particle packets use a fixed distance of 512, unaffected by this rule`

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


## randomTickChunkCache

&emsp;Reuses the random-tick chunk list, refreshed every 40 gt; when disabled, vanilla iteration is used

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `optimization`, `experimental`


## spawningChunkCache

&emsp;Reuses the natural-spawning candidate chunk list, refreshed every 40 gt; when disabled, vanilla iteration is used

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `optimization`, `experimental`


## tntPacketOptimization

&emsp;Optimizes TNT entities by removing unnecessary TNT entity packets (Fuse) and reducing packet frequency

&emsp; `May cause client display errors`

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `ROF`, `optimization`, `packet`


