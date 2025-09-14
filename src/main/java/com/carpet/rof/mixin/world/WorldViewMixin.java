package com.carpet.rof.mixin.world;


import com.carpet.rof.accessor.ChunkAccessor;
import com.carpet.rof.accessor.ServerWorldAccessor;
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

@Mixin(value = WorldView.class,priority = 2000)
public interface WorldViewMixin {

    @Shadow BiomeAccess getBiomeAccess();

    @Shadow int getBottomY();

     //int BiomeCacheHeight = 3;

    @Shadow @Nullable Chunk getChunk(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create);



    @Inject(method = "getBiome",at = @At(value = "HEAD"),cancellable = true)
    default void getBiome2(BlockPos pos, CallbackInfoReturnable<RegistryEntry<Biome>> cir) {

        if(pos.getY() >= getBottomY() + getBiomeLayerCache+1 || pos.getY() == getBottomY() || !(this instanceof ServerWorld)) { return; }
        Chunk chunk = ((ServerWorldAccessor)this).getNowChunk();
        if(chunk==null||chunk.getPos().x != pos.getX()>>4 || chunk.getPos().z != pos.getZ()>>4 ) {
            chunk = this.getChunk(pos.getX() >> 4, pos.getZ() >> 4, ChunkStatus.FULL, false);
        }
        if(chunk != null) {
            RegistryEntry<Biome>[] arrayList = ((ChunkAccessor)chunk).rof$getBiomeList((pos.getY()-getBottomY() -1));
            int l = (((pos.getX())&15)<<4)+(pos.getZ())&15;
            RegistryEntry<Biome> entry = arrayList[l];
            if (entry == null) {
                entry = this.getBiomeAccess().getBiome(pos);
                arrayList[l] = entry; // 缓存结果
            }
            cir.setReturnValue(entry);
            cir.cancel();
        }

    }
}
