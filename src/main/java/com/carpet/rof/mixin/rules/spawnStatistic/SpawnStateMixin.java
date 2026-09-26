package com.carpet.rof.mixin.rules.spawnStatistic;

import com.carpet.rof.rules.spawnStatistic.SpawnStateSimplifyAccess;
import com.carpet.rof.utils.ChunkPosHelper;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LocalMobCapCalculator;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NaturalSpawner.SpawnState.class)
public abstract class SpawnStateMixin implements SpawnStateSimplifyAccess
{
    @Shadow
    @Final
    private Object2IntOpenHashMap<MobCategory> mobCategoryCounts;
    @Shadow
    @Final
    private LocalMobCapCalculator localMobCapCalculator;
    @Unique
    private boolean rof$spawnStatisticSimplified = false;

    @Override
    public void rof$simplifySpawnStatistic(Object2IntOpenHashMap<MobCategory> categoryCounts)
    {
        this.mobCategoryCounts.putAll(categoryCounts);
        this.rof$spawnStatisticSimplified = true;
    }

    // 原版取不到刷怪密度成本时就是这个结果，化简路径直接跳过生物群系查询。
    @Inject(method = "canSpawn", at = @At("HEAD"), cancellable = true)
    //? >=26.3
    //private void rof$spawnStatisticCanSpawn(EntityType<?> type, Level level, BlockPos testPos, ChunkAccess chunk, CallbackInfoReturnable<Boolean> cir)
    //? <26.3
    private void rof$spawnStatisticCanSpawn(EntityType<?> type, BlockPos testPos, ChunkAccess chunk, CallbackInfoReturnable<Boolean> cir)
    {
        if (this.rof$spawnStatisticSimplified) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "afterSpawn", at = @At("HEAD"), cancellable = true)
    private void rof$spawnStatisticAfterSpawn(Mob mob, ChunkAccess chunk, CallbackInfo ci)
    {
        if (!this.rof$spawnStatisticSimplified) {
            return;
        }
        MobCategory category = mob.getType().getCategory();
        this.mobCategoryCounts.addTo(category, 1);
        this.localMobCapCalculator.addMob(ChunkPosHelper.containing(mob.blockPosition()), category);
        ci.cancel();
    }
}
