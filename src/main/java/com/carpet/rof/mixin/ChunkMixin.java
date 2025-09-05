package com.carpet.rof.mixin;

import com.carpet.rof.accessor.ChunkAccessor;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import static com.carpet.rof.ROFCarpetSettings.getBiomeLayerCache;

@Mixin(Chunk.class)
public class ChunkMixin implements ChunkAccessor{


    @Unique
    final ArrayList<RegistryEntry<Biome>[]> biomeArray = new ArrayList<>();
   // final [] biomeArray = new RegistryEntry[getBiomeLayerCache][256];

    @SuppressWarnings("unchecked")
    @Override
    public RegistryEntry<Biome>[] rof$getBiomeList(int y) {
        while (y >= biomeArray.size()) {
            biomeArray.add(new RegistryEntry[256]);
        }
        return  biomeArray.get(y);
    }
}
