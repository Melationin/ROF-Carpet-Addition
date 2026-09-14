package com.carpet.rof.mixin.extraWorldData;



import com.carpet.rof.extraWorldData.ExtraWorldDatas;
import com.carpet.rof.extraWorldData.extraChunkDatas.ExceedChunkMarker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.BitStorage;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

import static com.carpet.rof.rules.extraChunkDatas.ExceedChunkMarkerSetting.exceedChunkMarker;
import com.carpet.rof.utils.ChunkPosHelper;


@Mixin(Heightmap.class)
public class HeightmapMixin
{
    @Shadow @Final private Predicate<BlockState> isOpaque;

    @Shadow @Final private ChunkAccess chunk;

    @Shadow @Final private BitStorage data;

    @Inject(method = "setHeight", at = @At(value = "HEAD"))
    public void SetMixin(int x, int z, int height, CallbackInfo ci){

        if(
                exceedChunkMarker && chunk instanceof LevelChunk worldChunk && worldChunk.getLevel() instanceof ServerLevel serverWorld){
            ExceedChunkMarker heightExceedingChunk = ExtraWorldDatas.fromWorld(serverWorld).exceedChunkMarker;
            if(
                    height>=heightExceedingChunk.topY+1
                    && this.isOpaque == Heightmap.Types.MOTION_BLOCKING.isOpaque()
                    && heightExceedingChunk.isNotHighChunk(ChunkPosHelper.x(chunk.getPos()),ChunkPosHelper.z(chunk.getPos()))
            ) {
                heightExceedingChunk.addChunk(ChunkPosHelper.pack(chunk.getPos()));;
            }
        }
    }
}
