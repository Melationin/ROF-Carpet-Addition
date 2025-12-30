package com.carpet.rof.rules.mergeTNTNext;

import com.carpet.rof.utils.RofTool;
import net.minecraft.entity.TntEntity;

import java.util.HashMap;

public interface ServerWorldAccessor {
    HashMap<RofTool.EntityPosAndVec, TntEntity> getTNTMergeMap();
}
