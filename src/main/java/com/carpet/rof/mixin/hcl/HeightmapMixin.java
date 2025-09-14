package com.carpet.rof.mixin.hcl;

import com.carpet.rof.ROFCarpetSettings;
import net.minecraft.block.BlockState;
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

import static com.carpet.rof.ROFCarpetServer.NETHER_HighChunkSet;

@Mixin(Heightmap.class)
public class HeightmapMixin {
    @Shadow @Final private Predicate<BlockState> blockPredicate;

    @Shadow @Final private Chunk chunk;

    @Shadow @Final private PaletteStorage storage;

    @Inject(method = "set",at = @At(value = "HEAD"))
    public void SetMixin(int x, int z, int height, CallbackInfo ci){

        if(ROFCarpetSettings.highChunkListener && chunk instanceof WorldChunk worldChunk){
            if(     height>= NETHER_HighChunkSet.topY
                    && this.blockPredicate == Heightmap.Type.MOTION_BLOCKING.getBlockPredicate()
                    && worldChunk.getWorld()==NETHER_HighChunkSet.world
                    && NETHER_HighChunkSet.isHighChunk(chunk.getPos())
            ) {
                NETHER_HighChunkSet.add(chunk.getPos());
            }
        }
    }
}
