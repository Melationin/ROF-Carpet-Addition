classTweaker v1 official



accessible field net/minecraft/world/entity/item/ItemEntity pickupDelay I
accessible field net/minecraft/world/entity/item/ItemEntity age I
accessible field net/minecraft/world/entity/item/ItemEntity target Ljava/util/UUID;
accessible field net/minecraft/server/level/ServerLevel blockEvents Lit/unimi/dsi/fastutil/objects/ObjectLinkedOpenHashSet;


accessible field net/minecraft/world/entity/Entity ENTITY_COUNTER Ljava/util/concurrent/atomic/AtomicInteger;
accessible field net/minecraft/world/level/chunk/LevelChunk$RebindableTickingBlockEntityWrapper ticker Lnet/minecraft/world/level/block/entity/TickingBlockEntity;
accessible class net/minecraft/server/level/ChunkMap$TrackedEntity
accessible class net/minecraft/world/level/chunk/LevelChunk$BoundTickingBlockEntity
accessible class net/minecraft/world/level/chunk/LevelChunk$RebindableTickingBlockEntityWrapper
accessible field net/minecraft/world/level/chunk/LevelChunk$BoundTickingBlockEntity blockEntity Lnet/minecraft/world/level/block/entity/BlockEntity;
accessible field net/minecraft/server/level/ServerChunkCache ticketStorage Lnet/minecraft/world/level/TicketStorage;

accessible field net/minecraft/world/entity/Entity levelCallback Lnet/minecraft/world/level/entity/EntityInLevelCallback;

# Async natural-spawn planning; shared by the 26.1 build branch.
accessible method net/minecraft/world/level/NaturalSpawner isRightDistanceToPlayerAndSpawnPoint (Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/core/BlockPos$MutableBlockPos;D)Z
accessible method net/minecraft/world/level/NaturalSpawner isValidSpawnPostitionForType (Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/MobCategory;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/world/level/biome/MobSpawnSettings$SpawnerData;Lnet/minecraft/core/BlockPos$MutableBlockPos;D)Z
accessible method net/minecraft/world/level/NaturalSpawner getMobForSpawn (Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/EntityType;)Lnet/minecraft/world/entity/Mob;
accessible method net/minecraft/world/level/NaturalSpawner isValidPositionForMob (Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Mob;D)Z
accessible method net/minecraft/world/level/NaturalSpawner$SpawnState canSpawnForCategoryLocal (Lnet/minecraft/world/entity/MobCategory;Lnet/minecraft/world/level/ChunkPos;)Z
accessible method net/minecraft/world/level/NaturalSpawner$SpawnState canSpawn (Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/chunk/ChunkAccess;)Z
accessible method net/minecraft/world/level/NaturalSpawner$SpawnState afterSpawn (Lnet/minecraft/world/entity/Mob;Lnet/minecraft/world/level/chunk/ChunkAccess;)V
accessible method net/minecraft/server/level/ChunkMap getVisibleChunkIfPresent (J)Lnet/minecraft/server/level/ChunkHolder;
accessible method net/minecraft/server/level/ChunkMap forEachBlockTickingChunk (Ljava/util/function/Consumer;)V
accessible field net/minecraft/server/level/ServerLevel entityManager Lnet/minecraft/world/level/entity/PersistentEntitySectionManager;