package com.carpet.rof.accessor;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.Biome;

public interface ChunkAccessor {
    RegistryEntry<Biome>[] rof$getBiomeList(int y);
}
