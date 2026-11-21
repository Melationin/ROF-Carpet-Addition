package com.carpet.rof.entity.enderPearl;

import com.carpet.rof.utils.ChunkPosHelper;
import com.carpet.rof.utils.ROFWarp;
import com.carpet.rof.world.extraWorldData.ExtraWorldDatas;
import com.carpet.rof.world.extraWorldData.extraChunkDatas.ExceedChunkMarker;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.entity.Visibility;

public class EnderPearlTickets
{
    //? if >= 1.21.9 {
    private static final TicketType TYPE = new TicketType(40L, TicketType.FLAG_SIMULATION | TicketType.FLAG_KEEP_DIMENSION_ACTIVE);
    //? } else {
    /*private static final TicketType TYPE = new TicketType(40L, false, TicketType.TicketUse.SIMULATION);
    *///?}
    private static final int LEVEL = ChunkLevel.byStatus(FullChunkStatus.ENTITY_TICKING);


    private final Long2ObjectOpenHashMap<FullChunkStatus> loadingStatuses = new Long2ObjectOpenHashMap<>();

    public enum PearlLoading
    {
        VANILLA,
        BETTER_TICKET,
        SELF_LOADING
    }

    private static boolean canReplace(ServerLevel level, Entity pearl)
    {
        for (BlockPos pos : ROFWarp.getBlockPosIt(pearl.getBoundingBox())) {
            if (!ExceedChunkMarker.mustBeAir(level, pos)) return false;
        }
        return true;
    }


    private static boolean isChunkLoaded(ServerLevel level, Entity pearl)
    {
        ChunkPos pos = pearl.chunkPosition();
        return level.getChunkSource().getChunkNow(ChunkPosHelper.x(pos), ChunkPosHelper.z(pos)) != null;
    }

    public static PearlLoading decide(ServerLevel level, Entity pearl)
    {
        if (!canReplace(level, pearl)) return PearlLoading.VANILLA;
        if (isChunkLoaded(level, pearl)) return PearlLoading.VANILLA;
        if (EnderPearlSettings.isSelfLoadingEnabled()) return PearlLoading.SELF_LOADING;
        if (EnderPearlSettings.betterEnderPearlTicket) return PearlLoading.BETTER_TICKET;
        return PearlLoading.VANILLA;
    }

    public long tryPlace(ServerLevel level, Entity pearl)
    {
        return switch (decide(level, pearl)) {
            case SELF_LOADING -> {
                level.resetEmptyTime();
                ExtraWorldDatas.fromWorld(level).forcedEntitylist.put(pearl.getUUID(), pearl);
                yield TYPE.timeout();
            }
            case BETTER_TICKET -> {
                ChunkPos pos = pearl.chunkPosition();
                level.resetEmptyTime();
                long key = ChunkPosHelper.pack(pos);
                if (!loadingStatuses.containsKey(key)) {
                    ChunkHolder holder = level.getChunkSource().chunkMap.getVisibleChunkIfPresent(key);
                    loadingStatuses.put(key, holder == null ? FullChunkStatus.INACCESSIBLE : holder.getFullStatus());
                }
                level.getChunkSource().addTicket(new Ticket(TYPE, LEVEL), pos);
                level.entityManager.updateChunkStatus(pos, Visibility.TICKING);
                yield TYPE.timeout();
            }
            case VANILLA -> 0L;
        };
    }

    private boolean hasTicket(ServerLevel level, long key)
    {
        for (Ticket ticket : level.getChunkSource().ticketStorage.getTickets(key)) {
            if (ticket.getType() == TYPE) return true;
        }
        return false;
    }

    public FullChunkStatus onLoadingStatusChange(ServerLevel level, ChunkPos pos, FullChunkStatus status)
    {
        long key = ChunkPosHelper.pack(pos);
        if (loadingStatuses.containsKey(key)) {
            loadingStatuses.put(key, status);
            if (hasTicket(level, key)) return FullChunkStatus.ENTITY_TICKING;
        }
        return status;
    }

    public void tick(ServerLevel level)
    {
        var iterator = loadingStatuses.long2ObjectEntrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            if (!hasTicket(level, entry.getLongKey())) {
                // 票已过期：按当前真实状态恢复，不再用加票前的旧快照。
                // 旧快照可能是 INACCESSIBLE，会把仍然活跃的区块降成 HIDDEN，导致珍珠被 UNLOADED_TO_CHUNK 卸载。
                ChunkHolder holder = level.getChunkSource().chunkMap.getUpdatingChunkIfPresent(entry.getLongKey());
                FullChunkStatus actual = holder == null ? FullChunkStatus.INACCESSIBLE : holder.getFullStatus();
                level.entityManager.updateChunkStatus(ChunkPosHelper.unpack(entry.getLongKey()), actual);
                iterator.remove();
            }
        }
    }
}
