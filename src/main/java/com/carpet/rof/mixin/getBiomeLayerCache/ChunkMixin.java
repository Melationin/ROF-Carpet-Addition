package com.carpet.rof.mixin.getBiomeLayerCache;

import com.carpet.rof.rules.getBiomeLayerCache.ChunkAccessor;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;

@Mixin(Chunk.class)
public class ChunkMixin implements ChunkAccessor{

    @Unique
    final ArrayList<RegistryEntry<Biome>[]> biomeArray = new ArrayList<>();

    @SuppressWarnings("unchecked")
    @Override
    public RegistryEntry<Biome>[] rof$getBiomeList(int y) {
        while (y >= biomeArray.size()) {
            biomeArray.add(new RegistryEntry[256]);
        }
        return  biomeArray.get(y);
    }


}
