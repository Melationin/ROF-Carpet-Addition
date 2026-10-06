# Rules

**Tip: use `Ctrl+F` to quickly find the rule you need**

Each Carpet group rule is a boolean master switch, defaulting to `false` and persisted in `carpet.conf`. Hidden members are managed through `/carpet group` and saved immediately as formatted JSON under `ruleGroups.<group>` in the world-root `carpet-rof-addition.json`, preserving other configuration entries. Restart the server after manual JSON edits; omitted members use their declared defaults.

## netherPortalCacheDistance

&emsp;Caches exit portal positions per entrance portal block while entities remain within this distance of the last actual search position and the exit chunk has not changed.

&emsp; `Distance is measured in three dimensions in the source world. 0 disables caching. Only finite nonnegative values are accepted.`

&emsp;- Type: `double`

&emsp;- Default: `0.0`

&emsp;- Options: `0`, `0.1`, `0.5`

&emsp;- Categories: `ROF`, `optimization`, `experimental`


## naturalSpawningOptimizations

Configures natural-spawning chunk caching, async precomputation and spawn-statistic simplification with a master switch; both the group and the relevant member must be enabled.

- Type: `boolean` (master switch)
- Default: `false`; member defaults are listed below
- Categories: `ROF`, `optimization`, `experimental`

| Member | Type | Default | Behavior |
| --- | --- | --- | --- |
| `spawningChunkCache` | `boolean` | `false` | Refreshes the natural-spawning candidate chunk list every 40 gt; disabled uses vanilla iteration |
| `asyncNaturalSpawning` | `boolean` | `false` | Precomputes candidates off-thread; entity creation and counting stay on the main thread, with vanilla fallback for unavailable or stale results |
| `spawnStatisticSimplifyWhitelist` | `String` | `{}` | Dimensions whose spawn statistic is simplified, such as `{minecraft:overworld,minecraft:the_end}`; `{}` simplifies nothing |

The whitelist keeps its existing validator and updates existing worlds when changed; newly created worlds also use the whitelist. Simplification skips chunk lookup and biome queries. Results match vanilla only when no biome in the dimension defines spawn costs.

Use `/carpet group naturalSpawningOptimizations` to inspect members. Changes are immediately saved to JSON, for example:

```text
/carpet setDefault naturalSpawningOptimizations true
/carpet group naturalSpawningOptimizations asyncNaturalSpawning true
/carpet group naturalSpawningOptimizations spawnStatisticSimplifyWhitelist {minecraft:overworld}
```


## randomTickOptimizations

Configures random-tick chunk caching and async precomputation with a master switch; both the group and the relevant member must be enabled.

- Type: `boolean` (master switch)
- Default: `false`; member defaults are listed below
- Categories: `ROF`, `optimization`, `experimental`

| Member | Type | Default | Behavior |
| --- | --- | --- | --- |
| `randomTickChunkCache` | `boolean` | `false` | Refreshes the random-tick chunk list every 40 gt; disabled uses vanilla iteration |
| `asyncRandomTick` | `boolean` | `false` | Precomputes the next tick's random-tick candidates off-thread, with vanilla fallback for invalid results |

Use `/carpet setDefault randomTickOptimizations true` to enable and save the master switch. Use `/carpet group randomTickOptimizations` to inspect members and `/carpet group randomTickOptimizations asyncRandomTick true` to change and immediately persist a value.

The five former standalone rules are now members of these groups, with unchanged names and defaults. Runtime logic still reads static fields directly and does not parse JSON.

## enderPearlOptimizations

Configures pearl ticking, loading tickets, high-speed self-loading, chunk-loading wait time, ECM and raycast optimization with a master switch; both the group and the relevant member must be enabled.

- Type: `boolean` (master switch)
- Default: `false`; member defaults are listed below
- Categories: `ROF`, `optimization`, `experimental`

| Member | Type | Default | Behavior |
| --- | --- | --- | --- |
| `enderPearlForcedTickMinSpeed` | `double` | `-1.0` | Speed threshold for high-speed pearl self-loading; negative disables it. This older scheme is discouraged in favor of better pearl tickets |
| `optimizedEnderPearlTick` | `boolean` | `false` | Avoids new chunk generation for high-speed flying pearls; without ECM, only affects pearls outside world height |
| `betterEnderPearlTicket` | `boolean` | `false` | Uses special loading tickets; without ECM, only affects pearls outside world height |
| `blockingEnderPearlLoading` | `int` | `0` | Maximum chunk-loading wait time in milliseconds; 0 disables it |
| `exceedChunkMarker` | `boolean` | `false` | Exceed Chunk Marker (ECM), providing data for raycast optimization and potentially adding a small amount of storage |
| `optimizedRaycast` | `boolean` | `false` | Optimizes raycast using ECM; requires ECM to be enabled and loaded from the save |

High-speed self-loading differs substantially from vanilla. Optimized pearl ticking ignores unloaded entity hitboxes. Chunk-loading waits block the main thread to reduce desynchronization between pearl ticks and world ticks.

ECM commands, permissions and world data format remain unchanged. After first enabling ECM, run `/exceedChunkMarker [dimension] loadFromWorld` in each required dimension before enabling raycast optimization, for example:

```text
/carpet setDefault enderPearlOptimizations true
/carpet group enderPearlOptimizations exceedChunkMarker true
/exceedChunkMarker minecraft:overworld loadFromWorld
/carpet group enderPearlOptimizations optimizedRaycast true
```

Raycast optimization may still make projectiles ignore entity hitboxes at some specific positions. ECM may increase storage; the original description estimates that this is typically below 0.1% of save size.

Use `/carpet group enderPearlOptimizations` to inspect members. For example, `/carpet group enderPearlOptimizations betterEnderPearlTicket true` changes the value and immediately saves it to JSON.

Member names, defaults and validation remain unchanged. Runtime code still reads static fields directly.

## mobAiOptimizations

Configures mob AI delays, better NoAI NBT and piglin AI optimizations with a master switch; both the group and the relevant member must be enabled.

- Type: `boolean` (master switch)
- Default: `false`; member defaults are listed below
- Categories: `ROF`, `optimization`, `feature`

| Member | Type | Default | Behavior |
| --- | --- | --- | --- |
| `mobAIDelayChance` | `double` | `0.0` | AI delay probability for whitelisted mobs, between 0 and 1; 0 disables AI delays |
| `mobAIDelayWhitelist` | `String` | `{!minecraft:drowned}` | Entity IDs or entity-type tags eligible for AI delays; `{}` is an empty list |
| `mobAIDelayTicks` | `int` | `3` | AI delay duration in game ticks; must be positive |
| `betterNoAiNbt` | `boolean` | `false` | Skips AI for entities with the `NoBrainAI` NBT tag while retaining passive movement |
| `piglinLootItemDelay` | `int` | `0` | Piglins only pick up items older than this many game ticks |
| `piglinStackingAISuppression` | `int` | `10000` | Stacked piglin AI suppression threshold; 10000 retains the existing disabled behavior |

The probability, whitelist and duration keep their existing validators. The whitelist is brace-wrapped and comma-separated. It accepts entity IDs (`minecraft:pig`), entity-type tags (`#zombies`) and exclusions (`!minecraft:drowned`, `!#undead`). Matching takes the union of positive entries and removes exclusions; a list consisting entirely of exclusions means everything except those entries. Unloaded tags match an empty set. Zero probability or an empty whitelist disables AI delays; other members still follow their own activation conditions.

AI delay decisions and remaining time are still stored in the entity's `MobAi` NBT field. Negative remaining time means a decision was made and AI is retained. Unmatched entities do not save this field, allowing another match after reload.

When `betterNoAiNbt` is enabled, set the tag with `/data merge entity <target> {NoBrainAI:1b}`, clear it with `{NoBrainAI:0b}` or `/data remove entity <target> NoBrainAI`, or write it in `/summon` NBT. The tag is saved with the entity. This mode skips target and goal selectors, navigation, sensing, the brain and movement/look/jump controls. It retains gravity, ground friction, fluids, entity pushing, crush damage, explosion knockback, pistons, riding, burning, item pickup and distance-based despawn checks. `noActionTime` no longer accumulates, so tagged entities do not despawn from 600 ticks of inactivity near players; tagged bosses such as the Ender Dragon and Wither stop phase progression.

Use `/carpet group mobAiOptimizations` to inspect members, for example:

```text
/carpet setDefault mobAiOptimizations true
/carpet group mobAiOptimizations mobAIDelayChance 0.5
/carpet group mobAiOptimizations mobAIDelayWhitelist {!minecraft:drowned,!minecraft:piglin}
/carpet group mobAiOptimizations betterNoAiNbt true
```

Commands provide completion and immediately save changes to JSON.

Member names, defaults and activation conditions remain unchanged. Runtime code still reads static fields and the parsed whitelist directly, without parsing JSON.

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


## entityIDOverflowPeriod

&emsp;Set to 0 to disable

&emsp;- Type: `int`

&emsp;- Default: `0`

&emsp;- Categories: `ROF`, `feature`, `creative`


## Packet limits (packetLimits)

Configures entity spawn packet limits, tracking recovery and normal particle packet range. The parent is a boolean master switch with default `false` and categories `ROF`, `optimization`, `packet`. Use `/carpet group packetLimits` to inspect and configure its hidden subrules; edits are immediately saved to JSON.

| Subrule | Default | Previous name |
|---|---|---|
| `entitySpawnLimitPerTick` | `512` | `entitySpawnPacketLimitTicks` |
| `limitedEntityTrackingRange` | `16` | `entitySpawnPacketLimitTicksTrackerDistance` |
| `entitySpawnLimitPerSecond` | `256` | `entitySpawnPacketLimitSeconds` |
| `entityTrackingRecoveryTicks` | `200` | `entitySpawnPacketLimitSecondsRecoverTime` |
| `particlePacketRange` | `2.0` | `particlesPacketsRange` |

Both spawn limits and the particle range require `packetLimits=true`. The per-tick threshold counts entities of the same type in one world and reduces the tracking range of excess entities to `limitedEntityTrackingRange` blocks. The per-second threshold probabilistically prevents tracking based on the previous one-second count for the same entity type and chunk; one second means 20 game ticks. A negative threshold disables that limit individually.

Both limits share `entityTrackingRecoveryTicks`. Once an affected entity's age reaches this many game ticks, its original tracking range is restored and player tracking is refreshed. Entities affected by both limits recover as well. Negative values disable automatic recovery; turning off the master switch still restores affected entities on the next tracker tick.

`particlePacketRange` only controls normal, non-forced particles, in blocks. With the master switch off, their vanilla range is 32 blocks. Forced particles keep the vanilla range of 512 blocks. Both range parameters must be nonnegative. The `tntPacketOptimization` member of `tntExplosionOptimizations` independently controls TNT packet optimization.

```text
/carpet group packetLimits
/carpet setDefault packetLimits true
/carpet group packetLimits entityTrackingRecoveryTicks 200
/carpet group packetLimits particlePacketRange 2
```

Disabling the `packetLimits` master switch stops limiting and restores tracking while retaining member values in JSON.

## killedTriggerLimitPerTick

&emsp;Limits KilledTrigger.trigger executions per tick with one shared budget for all entities.

&emsp; `0 means unlimited`

&emsp;- Type: `int`

&emsp;- Default: `0`

&emsp;- Options: `0`, `1`, `10`, `100`

&emsp;- Categories: `ROF`, `optimization`, `feature`


## tntExplosionOptimizations

Configures TNT merging, explosion merging, the explosion optimization threshold and TNT packet optimization with a master switch; both the group and the relevant member must be enabled.

- Type: `boolean` (master switch)
- Default: `false`; member defaults are listed below
- Categories: `ROF`, `optimization`, `tnt`, `experimental`

| Member | Type | Default | Behavior |
| --- | --- | --- | --- |
| `mergeTNTNext` | `MergeTNTNextMode` | `FALSE` | Selects a TNT merge mode; cannot be enabled with other TNT merging rules |
| `mergeExplosion` | `boolean` | `false` | Merges a merged TNT's explosions; damage is applied once and knockback is scaled by the merge count |
| `optimizedExplosion` | `int` | `0` | After this many same-tick explosions with identical coordinates and power, skips block calculation if a worst-case check proves no block can be destroyed; 0 or less disables it |
| `tntPacketOptimization` | `boolean` | `false` | Removes unnecessary TNT Fuse packets and reduces packet frequency; may cause client display errors |

`mergeExplosion` still requires TNT merging and explosion optimization to be enabled. `tntPacketOptimization` is independently controlled in this group and is unaffected by `packetLimits`.

The `mergeTNTNext` command options are `false`, `true`, `safe`, `almost_vanilla` and `safe_plus`. `true` uses the old explosion handling and may change moving TNT behavior; `safe` uses safer explosion handling; `almost_vanilla` merges only at explosion time and preserves tick order; `safe_plus` merges adjacent same-position TNTs in a once-per-tick pre-pass without rewriting entity tick order. Theoretically `almost_vanilla` preserves TNT behavior; report vanilla differences as issues.

Use `/carpet group tntExplosionOptimizations` to inspect members, for example:

```text
/carpet setDefault tntExplosionOptimizations true
/carpet group tntExplosionOptimizations mergeTNTNext almost_vanilla
/carpet group tntExplosionOptimizations optimizedExplosion 4
/carpet group tntExplosionOptimizations mergeExplosion true
```

Each change is immediately saved to JSON.

Member names, defaults and validation remain unchanged. Runtime code still reads static fields directly.

## optimizedEntityCollection

&emsp;Uses a section-local spatial index to optimize Lithium push candidate collection. Entity iteration order may change.

&emsp;- Type: `boolean`

&emsp;- Default: `false`

&emsp;- Categories: `ROF`, `optimization`, `experimental`


## optimizedEquipmentDrops

&emsp;Skips equipment drop enchantment calculations for empty mob equipment slots and context creation for enchantments without equipment drop effects.

&emsp; `Empty equipment slots no longer run equipment drop enchantment effects, which may change random number consumption for custom random enchantment effects`

&emsp;- Type: `boolean`

&emsp;- Default: `false`

&emsp;- Categories: `ROF`, `optimization`, `experimental`


## optimizedExperienceOrbMerge

&emsp;Stops the experience orb spawn merge query after the first orb matching vanilla merge conditions.

&emsp; `Preserves vanilla query order, random grouping, experience value matching, and merge logic`

&emsp;- Type: `boolean`

&emsp;- Default: `false`

&emsp;- Categories: `ROF`, `optimization`


## optimizedFakePlayerTick

&emsp;Trims client sync/advancement/stats/Waypoint per-tick logic for Carpet fake players, keeping main-hand item tick and behavior simulation

&emsp; `Only affects Carpet fake players (EntityPlayerMPFake)`

&emsp; `May affect fake player stats, advancements, scoreboard auto-sync and locator bar display`

&emsp;- Type: `boolean`

&emsp;- Default: `false`

&emsp;- Categories: `ROF`, `optimization`


