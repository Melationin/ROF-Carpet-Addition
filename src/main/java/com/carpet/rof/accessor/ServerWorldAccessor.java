package com.carpet.rof.accessor;

import com.carpet.rof.utils.RofTool;
import net.minecraft.entity.Entity;
import net.minecraft.entity.TntEntity;

import java.util.HashMap;

public interface ServerWorldAccessor {

    void addMustTickEntity(Entity entity);

    void  removeMustTickEntity(Entity entity);

    boolean  inMustTickEntity(Entity entity);

    HashMap<RofTool.EntityPosAndVec, TntEntity> getTNTMergeMap();
}
