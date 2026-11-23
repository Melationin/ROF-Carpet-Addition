package com.carpet.rof.entity.enderPearl;

import com.carpet.rof.mixinAccessor.OecSectionAccess;
import com.carpet.rof.mixinAccessor.OecSectionStorageAccess;
import com.carpet.rof.mixinAccessor.OecStorageHolder;
import com.carpet.rof.utils.ChunkPosHelper;
import com.carpet.rof.utils.ROFIO;
import com.carpet.rof.world.extraWorldData.ExtraChunkData;
import com.carpet.rof.world.extraWorldData.ExtraWorldDatas;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.entity.EntitySection;
import net.minecraft.world.level.entity.EntitySectionStorage;
import net.minecraft.world.level.entity.Visibility;

import java.util.concurrent.TimeUnit;

public class PearlEntitySectionCache extends ExtraChunkData
{
    private final String name = "pearlEntitySectionCache";
    private final LongOpenHashSet targetSections = new LongOpenHashSet();
    private long lastUpdatedGameTime = -1L;
    private final Long2LongOpenHashMap tempTrackedChunks = new Long2LongOpenHashMap();

    public PearlEntitySectionCache()
    {
        this.needSave = true;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    public boolean isInitialized()
    {
        return this.lastUpdatedGameTime >= 0L;
    }

    public boolean contains(long sectionKey)
    {
        return this.targetSections.contains(sectionKey);
    }

    public void addSection(long sectionKey)
    {
        if (sectionKey != Long.MIN_VALUE) {
            this.targetSections.add(sectionKey);
        }
    }

    public void removeSection(long sectionKey)
    {
        this.targetSections.remove(sectionKey);
    }

    public static boolean isTargetEntity(Entity entity)
    {
        return entity instanceof ArmorStand && !entity.isRemoved();
    }

    public static boolean isTargetEntityId(String id)
    {
        return "minecraft:armor_stand".equals(id) || "armor_stand".equals(id);
    }

    public static boolean hasTargetEntity(EntitySection<?> section)
    {
        return hasTargetEntity((OecSectionAccess) section);
    }

    public static boolean hasTargetEntity(OecSectionAccess section)
    {
        for (Entity entity : section.rof$entities()) {
            if (isTargetEntity(entity)) {
                return true;
            }
        }
        return false;
    }

    public boolean isCacheValid(ServerLevel level)
    {
        return this.lastUpdatedGameTime >= 0L
                && Math.abs(level.getGameTime() - this.lastUpdatedGameTime) <= 1L;
    }

    public static void ensureCachesValid(MinecraftServer server)
    {
        for (ServerLevel level : server.getAllLevels()) {
            PearlEntitySectionCache cache = ExtraWorldDatas.fromWorld(level).pearlEntitySectionCache;
            if (!cache.isCacheValid(level)) {
                cache.rebuildFromWorld(level);
            }
        }
    }

    public void syncGameTime(ServerLevel level)
    {
        if (EnderPearlSettings.optimizedEnderPearlEntityInterception && this.isCacheValid(level)) {
            this.lastUpdatedGameTime = level.getGameTime();
        }
    }

    public void onTick(ServerLevel level)
    {
        if (!EnderPearlSettings.optimizedEnderPearlEntityInterception) {
            this.lastUpdatedGameTime = -1L;
            if (!this.tempTrackedChunks.isEmpty()) {
                for (Long2LongMap.Entry entry : this.tempTrackedChunks.long2LongEntrySet()) {
                    restoreChunkEntityStatus(level, entry.getLongKey());
                }
                this.tempTrackedChunks.clear();
            }
            return;
        }

        if (!this.isCacheValid(level)) {
            ensureCachesValid(level.getServer());
        }

        long gameTime = level.getGameTime();
        this.lastUpdatedGameTime = gameTime;

        if (!this.tempTrackedChunks.isEmpty()) {
            ObjectIterator<Long2LongMap.Entry> it = this.tempTrackedChunks.long2LongEntrySet().fastIterator();
            while (it.hasNext()) {
                Long2LongMap.Entry entry = it.next();
                if (gameTime >= entry.getLongValue()) {
                    restoreChunkEntityStatus(level, entry.getLongKey());
                    it.remove();
                }
            }
        }
    }

    private static void restoreChunkEntityStatus(ServerLevel level, long chunkKey)
    {
        ChunkHolder holder = level.getChunkSource().chunkMap.getUpdatingChunkIfPresent(chunkKey);
        FullChunkStatus actual = holder == null ? FullChunkStatus.INACCESSIBLE : holder.getFullStatus();
        level.entityManager.updateChunkStatus(ChunkPosHelper.unpack(chunkKey), actual);
    }

    public void rebuildFromWorld(ServerLevel level)
    {
        this.targetSections.clear();

        ROFIO.forEachExistingEntityChunk(
                level,
                chunkPos -> !level.areEntitiesLoaded(ChunkPosHelper.pack(chunkPos)),
                (chunkPos, chunkData) -> chunkData.getList("Entities").ifPresent(this::scanEntityList)
        );

        EntitySectionStorage<?> storage = ((OecStorageHolder) level).rof$entitySectionStorage();
        if (storage != null) {
            for (LongIterator it = ((OecSectionStorageAccess) storage).rof$sectionIds().iterator(); it.hasNext(); ) {
                long sectionKey = it.nextLong();
                EntitySection<?> section = storage.getSection(sectionKey);
                if (section != null && hasTargetEntity(section)) {
                    this.targetSections.add(sectionKey);
                }
            }
        }

        this.lastUpdatedGameTime = level.getGameTime();
    }

    private void scanEntityList(ListTag entities)
    {
        for (int i = 0; i < entities.size(); i++) {
            entities.getCompound(i).ifPresent(entityTag -> {
                String id = entityTag.getString("id").orElse("");
                if (isTargetEntityId(id)) {
                    entityTag.getList("Pos").ifPresent(pos -> {
                        if (pos.size() >= 3) {
                            double x = pos.getDouble(0).orElse(Double.NaN);
                            double y = pos.getDouble(1).orElse(Double.NaN);
                            double z = pos.getDouble(2).orElse(Double.NaN);
                            if (Double.isFinite(x) && Double.isFinite(y) && Double.isFinite(z)) {
                                BlockPos bp = BlockPos.containing(x, y, z);
                                this.targetSections.add(SectionPos.asLong(bp));
                            }
                        }
                    });
                }
                entityTag.getList("Passengers").ifPresent(this::scanEntityList);
            });
        }
    }

    public boolean ensureTargetSectionLoaded(ServerLevel level, long sectionKey)
    {
        if (!this.targetSections.contains(sectionKey)) {
            return false;
        }
        int cx = SectionPos.x(sectionKey);
        int cz = SectionPos.z(sectionKey);
        ChunkPos chunkPos = new ChunkPos(cx, cz);
        long chunkKey = ChunkPosHelper.pack(chunkPos);

        EntitySectionStorage<?> storage = ((OecStorageHolder) level).rof$entitySectionStorage();
        EntitySection<?> existingSection = storage != null ? storage.getSection(sectionKey) : null;
        boolean loaded = level.areEntitiesLoaded(chunkKey);
        boolean accessible = existingSection != null && existingSection.getStatus().isAccessible();

        if (!loaded || !accessible) {
            ChunkHolder holder = level.getChunkSource().chunkMap.getUpdatingChunkIfPresent(chunkKey);
            FullChunkStatus actual = holder == null ? FullChunkStatus.INACCESSIBLE : holder.getFullStatus();
            if (!Visibility.fromFullChunkStatus(actual).isAccessible() && !level.entityManager.isTicking(chunkPos)) {
                this.tempTrackedChunks.put(chunkKey, level.getGameTime() + 2L);
                level.entityManager.updateChunkStatus(chunkPos, Visibility.TRACKED);
            }
            if (!loaded) {
                long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(50L);
                level.getServer().managedBlock(() -> {
                    level.entityManager.processPendingLoads();
                    return level.areEntitiesLoaded(chunkKey) || System.nanoTime() >= deadline;
                });
                level.entityManager.processPendingLoads();
            }
        } else if (this.tempTrackedChunks.containsKey(chunkKey)) {
            this.tempTrackedChunks.put(chunkKey, level.getGameTime() + 2L);
        }

        if (storage != null && level.areEntitiesLoaded(chunkKey)) {
            EntitySection<?> section = storage.getSection(sectionKey);
            if (section == null || !hasTargetEntity(section)) {
                this.targetSections.remove(sectionKey);
            }
        }
        return true;
    }

    @Override
    public void write(CompoundTag nbt)
    {
        nbt.putLongArray("sections", this.targetSections.toLongArray());
        nbt.putLong("lastUpdatedGameTime", this.lastUpdatedGameTime);
    }

    @Override
    public void read(CompoundTag nbt)
    {
        this.targetSections.clear();
        for (long s : nbt.getLongArray("sections").orElse(new long[0])) {
            this.targetSections.add(s);
        }
        this.lastUpdatedGameTime = nbt.getLong("lastUpdatedGameTime").orElse(-1L);
    }

    @Override
    public CompoundTag toNbt()
    {
        CompoundTag nbt = new CompoundTag();
        this.write(nbt);
        return nbt;
    }
}
