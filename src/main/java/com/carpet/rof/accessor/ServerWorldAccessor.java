package com.carpet.rof.accessor;

import com.carpet.rof.utils.RofTool;
import net.minecraft.entity.Entity;
import net.minecraft.entity.TntEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

import java.util.HashMap;

public interface ServerWorldAccessor {

    void addMustTickEntity(Entity entity);

    void  removeMustTickEntity(Entity entity);

    boolean  inMustTickEntity(Entity entity);

    HashMap<RofTool.EntityPosAndVec, TntEntity> getTNTMergeMap();

    HashMap<BlockPos, RegistryEntry<Biome>> getLowYBiomeMap();
}
