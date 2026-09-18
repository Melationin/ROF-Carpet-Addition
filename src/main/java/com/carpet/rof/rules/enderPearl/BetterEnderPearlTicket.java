package com.carpet.rof.rules.enderPearl;

import com.carpet.rof.extraWorldData.extraChunkDatas.ExceedChunkMarker;
import com.carpet.rof.utils.ROFWarp;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.*;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.entity.Visibility;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import static com.carpet.rof.rules.enderPearl.EnderPearlSettings.betterEnderPearlTicket;



public final class BetterEnderPearlTicket
{

    public static final TicketType TYPE = new TicketType(40L, TicketType.FLAG_SIMULATION | TicketType.FLAG_KEEP_DIMENSION_ACTIVE);

    public static final int LEVEL = ChunkLevel.byStatus(FullChunkStatus.ENTITY_TICKING);

    private BetterEnderPearlTicket() {}

    public static boolean tryReplaceTicket(ThrownEnderpearl pearl, ServerPlayer player)
    {
        if(!betterEnderPearlTicket || !(pearl.level() instanceof ServerLevel level))
        {
            return false;
        }

        Vec3 start = new Vec3(pearl.xo, pearl.yo, pearl.zo);
        Vec3 end = pearl.position();
        int startChunkX = SectionPos.blockToSectionCoord(start.x);
        int startChunkZ = SectionPos.blockToSectionCoord(start.z);
        int endChunkX = SectionPos.blockToSectionCoord(end.x);
        int endChunkZ = SectionPos.blockToSectionCoord(end.z);
        if(startChunkX == endChunkX && startChunkZ == endChunkZ)
        {
            return false;
        }

        ServerChunkCache chunkSource = level.getChunkSource();
        if(chunkSource.getChunkNow(endChunkX, endChunkZ) != null)
        {
            return false;
        }

        if(!isSafe(level, pearl, start) || !isSafe(level, pearl, end))
        {
            return false;
        }

        player.registerEnderPearl(pearl);
        level.resetEmptyTime();
        level.getChunkSource().addTicket(new Ticket(TYPE, LEVEL), pearl.chunkPosition());
        level.entityManager.updateChunkStatus(pearl.chunkPosition(), Visibility.TICKING);
        //System.out.println("尝试加载一个虚拟票");
        return true;
    }

    // ECM 未打开时只认世界高度之外；打开时按 ECM 的"必为空气"判定（世界外，或 topY 以上且所在区块不是高区块）。
    private static boolean isSafe(ServerLevel level, ThrownEnderpearl pearl, Vec3 pos)
    {
        for(BlockPos blockPos : ROFWarp.getBlockPosIt(pearl.getBoundingBox()))
        {
            if(!ExceedChunkMarker.mustBeAir(level, blockPos))
            {
                return false;
            }
        }
        return true;
    }

    public static long vanillaTicketTimer()
    {
        return TicketType.ENDER_PEARL.timeout() - 1L;
    }
}
