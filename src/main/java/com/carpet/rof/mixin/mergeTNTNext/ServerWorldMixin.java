package com.carpet.rof.mixin.mergeTNTNext;

import com.carpet.rof.rules.mergeTNTNext.ServerWorldAccessor;
import com.carpet.rof.utils.RofTool;
import net.minecraft.entity.TntEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

@Mixin(ServerWorld.class)
public class ServerWorldMixin implements ServerWorldAccessor {
    @Unique
    final HashMap<RofTool.EntityPosAndVec, TntEntity> TNTMergeMap = new HashMap<>();

    @Inject(method = "tick",at = @At(value = "HEAD"))
    void clearTNTMergeMap(CallbackInfo ci){
        TNTMergeMap.clear();
    }

    @Override
    public HashMap<RofTool.EntityPosAndVec, TntEntity> getTNTMergeMap(){
        return TNTMergeMap;
    }

}
