package com.carpet.rof.mixin.extraWorldData;



import com.carpet.rof.extraWorldData.ExtraWorldDatas;
import com.carpet.rof.extraWorldData.extraChunkDatas.ExceedChunkMarker;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.PaletteStorage;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

import static com.carpet.rof.rules.extraChunkDatas.ExceedChunkMarkerSetting.exceedChunkMarker;


@Mixin(Heightmap.class)
public class HeightmapMixin {
    @Shadow @Final private Predicate<BlockState> blockPredicate;

    @Shadow @Final private Chunk chunk;

    @Shadow @Final private PaletteStorage storage;

    @Inject(method = "set",at = @At(value = "HEAD"))
    public void SetMixin(int x, int z, int height, CallbackInfo ci){

        if(
                exceedChunkMarker && chunk instanceof WorldChunk worldChunk && worldChunk.getWorld() instanceof ServerWorld serverWorld){
            ExceedChunkMarker heightExceedingChunk = ExtraWorldDatas.fromWorld(serverWorld).exceedChunkMarker;
            if(
                    height>=heightExceedingChunk.topY+1
                    && this.blockPredicate == Heightmap.Type.MOTION_BLOCKING.getBlockPredicate()
                    && heightExceedingChunk.isNotHighChunk(chunk.getPos().x,chunk.getPos().z)
            ) {
                heightExceedingChunk.addChunk(chunk.getPos().toLong());;
            }
        }
    }
}
