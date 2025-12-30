package com.carpet.rof.rules.enderPearl;

import com.carpet.rof.utils.RofTool;
import net.minecraft.entity.Entity;
import net.minecraft.entity.TntEntity;
import net.minecraft.world.chunk.Chunk;

import java.util.HashMap;

public interface ServerWorldAccessor {

    void addMustTickEntity(Entity entity);

    void  removeMustTickEntity(Entity entity);

    boolean  inMustTickEntity(Entity entity);
}
