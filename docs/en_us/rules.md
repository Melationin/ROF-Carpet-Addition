# Rules

**Tip: use `Ctrl+F` to quickly find the rule you need**

## asyncNaturalSpawning

&emsp;Precomputes natural-spawning candidates off-thread; entity creation and counting stay on the main thread

&emsp; `Falls back to vanilla when data is unavailable or stale`

&emsp;- Type: `boolean`

&emsp;- Default: `false`

&emsp;- Categories: `ROF`, `optimization`, `experimental`


## asyncRandomTick

&emsp;Precomputes the next tick's random-tick candidates off-thread; falls back to vanilla when the result is invalid

&emsp;- Type: `boolean`

&emsp;- Default: `false`

&emsp;- Categories: `ROF`, `optimization`, `experimental`


## betterEnderPearlTicket

&emsp;Replaces the vanilla loading ticket with a special loading ticket in certain cases. When ECM is not enabled, it only applies to pearls outside world height.

&emsp;- Type: `boolean`

&emsp;- Default: `false`

&emsp;- Options: `false`, `true`

&emsp;- Categories: `ROF`, `optimization`, `experimental`


## betterNoAiNbt

&emsp;Skips AI logic (Mob.serverAiStep) for entities carrying the NoBrainAI NBT tag, while keeping passive motion such as gravity, fluids, entity pushing, crush damage, explosion knockback, pistons and riding. Unlike vanilla NoAI, the entity does not freeze in mid-air.

&emsp; `/data merge entity <target> {NoBrainAI:1b} to set, {NoBrainAI:0b} or /data remove entity <target> NoBrainAI to clear; the tag is saved with the entity and can also be written in /summon NBT`

&emsp; `Skipped: target selector, goal selector, navigation, sensing, brain, and movement/look/jump controls`

&emsp; `Kept: gravity and ground friction, water and bubble columns, entity pushing and crush damage, explosion knockback, pistons, riding (a player can still steer the mount), burning, item pickup, distance-based despawn`

&emsp; `Note: the AI-independent noActionTime no longer accumulates, so the entity will not despawn from 600 ticks of inactivity near a player (same as vanilla NoAI); bosses such as the Ender Dragon or Wither stop their phase progression while tagged`

&emsp;- Type: `boolean`

&emsp;- Default: `false`

&emsp;- Categories: `ROF`, `feature`


## blockingEnderPearlLoading

&emsp;Makes a pearl's chunk loading block the main thread, reducing desync between pearl ticks and world ticks.

&emsp; `The maximum blocking time in milliseconds; set to 0 to disable.`

&emsp;- Type: `int`

&emsp;- Default: `0`

&emsp;- Options: `0`, `50`, `100`, `1000`

&emsp;- Categories: `ROF`, `optimization`, `feature`


## commandEntityID

&emsp;Command to view and control entity IDs

&emsp;- Type: `String`

&emsp;- Default: `false`

&emsp;- Categories: `ROF`, `feature`, `creative`, `command`


## commandEntityIDSet

&emsp;Command to set entity IDs

&emsp;- Type: `String`

&emsp;- Default: `false`

&emsp;- Categories: `ROF`, `feature`, `creative`, `command`


## commandExceedChunkMarker

&emsp;Controls the permission level of ECM command. Invalid when ECM is not started.

&emsp;- Type: `String`

&emsp;- Default: `ops`

&emsp;- Categories: `command`, `ROF`


## commandLoadedChunkFinder

&emsp;Records connected chunks that are active during a certain period, used to find forgotten chunk loaders.

&emsp;- Type: `String`

&emsp;- Default: `ops`

&emsp;- Categories: `ROF`, `command`


## commandPacketLoggerPlus

&emsp;Records the pre-compression size of various packets.

&emsp;- Type: `String`

&emsp;- Default: `ops`

&emsp;- Categories: `command`, `ROF`


## commandRequirementModify

&emsp;Modify permission requirements of specified commands

&emsp;- Type: `String`

&emsp;- Default: `ops`

&emsp;- Categories: `command`, `creative`


## commandRulesSearcher

&emsp;Adds a subcommand 'search' to carpet, allowing to search carpet rules by keyword

&emsp;- Type: `String`

&emsp;- Default: `true`

&emsp;- Categories: `ROF`, `command`


## commandSpawnWhitedListedPlayer

&emsp;Used to summon fake players without prefix

&emsp;- Type: `String`

&emsp;- Default: `ops`

&emsp;- Categories: `ROF`, `command`, `creative`


## commandTickSprintFull

&emsp;Adds a 'full' argument to /tick sprint that prints the duration of every game tick while sprinting

&emsp;- Type: `String`

&emsp;- Default: `ops`

&emsp;- Categories: `ROF`, `command`


## enderPearlForcedTickMinSpeed

&emsp;(Deprecated - the better pearl loading ticket can replace it with better results) Applies new loading logic to ender pearls whose speed is above a certain threshold; this is more stable and requires fewer loaded chunks.

&emsp; `The set value indicates the self-loading speed threshold. Set to a negative value to disable.`

&emsp; `For pearls with the new loading logic, the loading behavior differs significantly from vanilla.`

&emsp;- Type: `double`

&emsp;- Default: `-1.0`

&emsp;- Options: `16.0`, `-1.0`

&emsp;- Categories: `ROF`, `optimization`, `feature`


## entityIDOverflowPeriod

&emsp;Set to 0 to disable

&emsp;- Type: `int`

&emsp;- Default: `0`

&emsp;- Categories: `ROF`, `feature`, `creative`


## entitySpawnPacketLimitSeconds

&emsp;When too many entities of the same type are spawned in the same second, probabilistically prevents tracking and packet sending of that entity type.

&emsp; `Set to a negative number to disable`

&emsp;- Type: `int`

&emsp;- Default: `-1`

&emsp;- Options: `-1`, `100`

&emsp;- Categories: `ROF`, `optimization`, `packet`


## entitySpawnPacketLimitSecondsRecoverTime

&emsp;Throttled entities regain vanilla tracking distance after surviving this many ticks

&emsp; `Set to a negative value to disable`

&emsp; `No effect on the per-tick limit`

&emsp;- Type: `int`

&emsp;- Default: `200`

&emsp;- Options: `-1`, `200`, `100`, `400`

&emsp;- Categories: `ROF`, `optimization`, `packet`


## entitySpawnPacketLimitTicks

&emsp;When too many entities of the same type are spawned in the same tick, reduces the packet sending distance for excess entities. Used for optimization of large-yield pearl cannons.

&emsp; `Set to a negative number to disable`

&emsp;- Type: `int`

&emsp;- Default: `-1`

&emsp;- Options: `-1`, `100`, `1000`

&emsp;- Categories: `ROF`, `optimization`, `packet`


## entitySpawnPacketLimitTicksTrackerDistance

&emsp;Sets the packet sending distance for entities that are limited

&emsp;- Type: `int`

&emsp;- Default: `16`

&emsp;- Options: `2`, `16`, `64`

&emsp;- Categories: `ROF`, `optimization`, `packet`


## exceedChunkMarker

&emsp;Prerequisite for raycast optimization, may cause additional storage (generally only increases save size by less than 0.1%)

&emsp; `When enabling for the first time, be sure to load once with /exceedChunkMarker`

&emsp;- Type: `boolean`

&emsp;- Default: `false`

&emsp;- Categories: `ROF`, `experimental`


## mergeExplosion

&emsp;Merges the multiple explosions produced by one merged TNT into a single explosion, so entities are affected only once. Only applies when TNT merging and explosion optimization are both enabled.

&emsp; `Requires mergeTNTNext to be a mode other than False`

&emsp; `Damage is applied once; knockback is scaled by the merge count, so the total push matches vanilla`

&emsp;- Type: `boolean`

&emsp;- Default: `false`

&emsp;- Categories: `ROF`, `optimization`, `tnt`, `feature`


## mergeTNTNext

&emsp;A more aggressive TNT merging scheme, may cause unexpected results. Cannot be used together with other TNT merging rules.

&emsp; `False: no merging`

&emsp; `TRUE: old explosion handling; moving merged TNT behaves unlike vanilla`

&emsp; `SAFE: safer explosion handling; moving merged TNT behaves like vanilla`

&emsp; `AlmostVanilla: merges only when TNT explodes, keeping TNT tick order`

&emsp; `AlmostVanilla should not change TNT behaviour; report any vanilla mismatch as an issue`

&emsp; `SafePlus: merges only at explosion time like AlmostVanilla, but the decision is made in a once-per-tick pre-pass over adjacent same-position TNTs, without rewriting entity tick order`

&emsp;- Type: `MergeTNTNextMode`

&emsp;- Default: `FALSE`

&emsp;- Categories: `ROF`, `optimization`, `tnt`, `feature`


## mobAIDelayChance

&emsp;Chance for a whitelisted mob to have its AI temporarily disabled. The roll happens once, the first time the entity is about to run AI; only when the random value is below this chance is the AI suppressed. Passive motion (gravity, fluids, entity pushing, crush damage, explosion knockback, pistons, riding) is kept.

&emsp; `0 (default) disables the feature entirely; an empty entity list also disables it. Suggested values: 0 and 0.1 (the rule does not restrict the value, anything between 0 and 1 works)`

&emsp; `The roll result is stored in the entity's MobAi NBT field, so reloading a chunk never re-rolls it; every entity is decided exactly once`

&emsp;- Type: `double`

&emsp;- Default: `0.0`

&emsp;- Options: `0`, `0.5`, `0.9`, `0.95`

&emsp;- Categories: `ROF`, `optimization`


## mobAIDelayTicks

&emsp;How many ticks a suppressed entity stays without AI before its AI is restored; must be a positive number.

&emsp; `Counted in ticks experienced by the entity and saved with it (the remaining time is exactly the MobAi NBT field)`

&emsp; `A negative remaining time means 'decided: AI is not suppressed' - that is also the state after the AI has been restored`

&emsp;- Type: `int`

&emsp;- Default: `3`

&emsp;- Options: `3`, `50`, `200`

&emsp;- Categories: `ROF`, `optimization`


## mobAIDelayWhitelist

&emsp;Whitelist of mobs eligible for AI delays, wrapped in braces and comma-separated. Entries are entity ids (minecraft:pig) or entity type tags (#zombies); prefix an entry with ! to exclude it. A list with only exclusions means 'everything except those'; {} disables the feature.

&emsp; `Example: {minecraft:pig,#zombies,!#undead} - pigs and zombie-like mobs, but never undead ones. Suggested values: {!minecraft:drowned} (default, every mob except drowned) and {!minecraft:drowned,!minecraft:piglin}`

&emsp; `Matching: union of all positive entries, minus every negative entry; hitting any positive entry is enough`

&emsp; `Tags are data-pack tags matched by name; a tag that is not loaded behaves as an empty set (matches nothing)`

&emsp;- Type: `String`

&emsp;- Default: `{!minecraft:drowned}`

&emsp;- Options: `{}`, `{!minecraft:drowned}`, `{!minecraft:drowned,!minecraft:piglin}`

&emsp;- Categories: `ROF`, `optimization`


## optimizedClimbableTagCheck

&emsp;Caches the CLIMBABLE block-tag lookup used by Lithium entity pushability checks, reducing repeated tag lookups in LivingEntity.onClimbable.

&emsp;- Type: `boolean`

&emsp;- Default: `false`

&emsp;- Categories: `ROF`, `optimization`


## optimizedEnderPearlTick

&emsp;In most cases, stops flying high-speed pearls from generating new chunks, which can greatly reduce save size. When ECM is not enabled, only pearls outside world height will not generate chunks.

&emsp; `Known feature: Pearls will ignore entity hitboxes that are not loaded.`

&emsp;- Type: `boolean`

&emsp;- Default: `false`

&emsp;- Options: `false`, `true`

&emsp;- Categories: `ROF`, `optimization`, `experimental`


## optimizedEntityCollection

&emsp;Uses a section-local spatial index to optimize Lithium push candidate collection. Entity iteration order may change.

&emsp;- Type: `boolean`

&emsp;- Default: `false`

&emsp;- Categories: `ROF`, `optimization`, `experimental`


## optimizedExplosion

&emsp;Once this many explosions hit the same point within one game tick, the block calculation is skipped when a worst-case check proves no block can be destroyed; only entities are affected

&emsp; `0 or less disables it`

&emsp; `Only same-tick explosions with identical coordinates and power`

&emsp;- Type: `int`

&emsp;- Default: `0`

&emsp;- Options: `0`, `4`, `8`, `16`

&emsp;- Categories: `ROF`, `optimization`, `experimental`


## optimizedFakePlayerTick

&emsp;Trims client sync/advancement/stats/Waypoint per-tick logic for Carpet fake players, keeping main-hand item tick and behavior simulation

&emsp; `Only affects Carpet fake players (EntityPlayerMPFake)`

&emsp; `May affect fake player stats, advancements, scoreboard auto-sync and locator bar display`

&emsp;- Type: `boolean`

&emsp;- Default: `false`

&emsp;- Categories: `ROF`, `optimization`


## optimizedItemMerge

&emsp;Fills item stacks to a full stack to reduce lag (minor effect)

&emsp; `Partial merging: drops fill up to a full stack first, the remainder stays in the original drop`

&emsp;- Type: `boolean`

&emsp;- Default: `false`

&emsp;- Categories: `ROF`, `optimization`, `feature`


## optimizedRaycast

&emsp;Optimizes raycast via ECM. Ensure ECM is enabled and has been loaded from the save before turning on.

&emsp; `Known feature: Projectiles will ignore entity hitboxes at some specific positions.`

&emsp;- Type: `boolean`

&emsp;- Default: `false`

&emsp;- Categories: `ROF`, `optimization`, `experimental`


## particlesPacketsRange

&emsp;This distance only affects forced particle packets. But most particles are forced.

&emsp; `Vanilla default is 32`

&emsp; `Forced particle packets use a fixed distance of 512, unaffected by this rule`

&emsp;- Type: `double`

&emsp;- Default: `32.0`

&emsp;- Options: `32.0`, `1.0`, `8.0`

&emsp;- Categories: `ROF`, `optimization`, `packet`


## piglinLootItemDelay

&emsp;Only items that have been present for a certain time will be picked up by piglins

&emsp;- Type: `int`

&emsp;- Default: `0`

&emsp;- Options: `0`, `20`

&emsp;- Categories: `ROF`, `optimization`, `feature`


## piglinStackingAISuppression

&emsp;For piglins stacked to a certain amount, suppress AI of some of them.

&emsp;- Type: `int`

&emsp;- Default: `10000`

&emsp;- Options: `100`, `10000`

&emsp;- Categories: `ROF`, `optimization`, `feature`


## randomTickChunkCache

&emsp;Reuses the random-tick chunk list, refreshed every 40 gt; when disabled, vanilla iteration is used

&emsp;- Type: `boolean`

&emsp;- Default: `false`

&emsp;- Categories: `ROF`, `optimization`, `experimental`


## spawnStatisticSimplifyWhitelist

&emsp;Whitelist of dimensions whose spawn statistic is simplified, wrapped in braces and comma-separated; {} simplifies nothing

&emsp; `Simplification skips the chunk lookup and the biome query inside the spawn statistic`

&emsp; `The result stays identical to vanilla only when no biome of the world defines spawn costs`

&emsp;- Type: `String`

&emsp;- Default: `{}`

&emsp;- Options: `{}`, `{minecraft:overworld}`, `{minecraft:overworld,minecraft:the_end}`

&emsp;- Categories: `ROF`, `optimization`


## spawningChunkCache

&emsp;Reuses the natural-spawning candidate chunk list, refreshed every 40 gt; when disabled, vanilla iteration is used

&emsp;- Type: `boolean`

&emsp;- Default: `false`

&emsp;- Categories: `ROF`, `optimization`, `experimental`


## tntPacketOptimization

&emsp;Optimizes TNT entities by removing unnecessary TNT entity packets (Fuse) and reducing packet frequency

&emsp; `May cause client display errors`

&emsp;- Type: `boolean`

&emsp;- Default: `false`

&emsp;- Categories: `ROF`, `optimization`, `packet`


