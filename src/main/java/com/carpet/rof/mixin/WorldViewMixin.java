package com.carpet.rof.mixin;


import com.carpet.rof.accessor.ChunkAccessor;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.carpet.rof.ROFCarpetSettings.getBiomeLayerCache;

@Mixin(WorldView.class)
public interface WorldViewMixin {




    @Shadow BiomeAccess getBiomeAccess();

    @Shadow int getBottomY();

     //int BiomeCacheHeight = 3;

    @Shadow @Nullable Chunk getChunk(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create);

    @Inject(method = "getBiome",at = @At(value = "HEAD"),cancellable = true)
    default void getBiome2(BlockPos pos, CallbackInfoReturnable<RegistryEntry<Biome>> cir) {

        if(!( this instanceof ServerWorld) || pos.getY() >= getBottomY() + getBiomeLayerCache+1 || pos.getY() == getBottomY() ) { return; }

        Chunk chunk = this.getChunk(pos.getX() >> 4, pos.getZ() >> 4, ChunkStatus.FULL, false);
        if(chunk != null) {
            RegistryEntry<Biome>[] arrayList = ((ChunkAccessor)chunk).rof$getBiomeList((pos.getY()-getBottomY()));
            int l = (((pos.getX())&15)<<4)+(pos.getZ())&15;
            if(arrayList[l] == null) {
                cir.setReturnValue( arrayList[l]= this.getBiomeAccess().getBiome(pos));
                System.out.println( pos.getX() + " " + pos.getY() + " " + pos.getZ());
                cir.cancel();
            }else {
                cir.setReturnValue(arrayList[l]);
                cir.cancel();
            }
        }

    }
}
