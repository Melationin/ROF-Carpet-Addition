package com.carpet.rof.entity.enderPearl;

import com.carpet.rof.utils.ChunkPosHelper;
import com.carpet.rof.utils.ROFWarp;
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

    public long tryPlace(ServerLevel level, Entity pearl)
    {
        if (!EnderPearlSettings.betterEnderPearlTicket) return 0;
        for (BlockPos pos : ROFWarp.getBlockPosIt(pearl.getBoundingBox())) {
            if (!ExceedChunkMarker.mustBeAir(level, pos)) return 0;
        }
        ChunkPos pos = pearl.chunkPosition();
        level.resetEmptyTime();
        long key = ChunkPosHelper.pack(pos);
        if (!loadingStatuses.containsKey(key)) {
            ChunkHolder holder = level.getChunkSource().chunkMap.getVisibleChunkIfPresent(key);
            loadingStatuses.put(key, holder == null ? FullChunkStatus.INACCESSIBLE : holder.getFullStatus());
        }
        level.getChunkSource().addTicket(new Ticket(TYPE, LEVEL), pos);
        level.entityManager.updateChunkStatus(pos, Visibility.TICKING);
        return TYPE.timeout();
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
                // Simulation tickets expire without a loading-status callback. Release our override.
                level.entityManager.updateChunkStatus(ChunkPosHelper.unpack(entry.getLongKey()), entry.getValue());
                iterator.remove();
            }
        }
    }
}
