package com.carpet.rof.mixin.entity.enderPearl;

// Minecraft 相关导入

import com.carpet.rof.entity.enderPearl.EnderPearlTickets;
import com.carpet.rof.world.extraWorldData.ExtraWorldDatas;
import com.carpet.rof.utils.ROFWarp;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.entity.Visibility;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.carpet.rof.entity.enderPearl.EnderPearlSettings.*;
import com.carpet.rof.utils.ChunkPosHelper;
//import static com.carpet.rof.world.extraWorldData.extraChunkDatas.ExceedChunkMarkerSetting.optimizedForcedEnderPearlTick;


// Java 标准库


@Mixin(ThrownEnderpearl.class)
public abstract class ThrownEnderpearlMixin extends ThrowableItemProjectile
{

    // 必须定义的构造函数，调用父类
    protected ThrownEnderpearlMixin(EntityType<?extends ThrowableItemProjectile> entityType, Level world) {
        super(entityType, world);
    }
    // 自加载与加载票共用一个判定（EnderPearlTickets.decide）：
    // 移动完成后按当前区块判定一次，满足条件就转入自加载并取消本 tick 剩余逻辑（不注册任何加载票）；
    // 条件不再满足时退出自加载，并补一张原版加载票。
    //? >= 1.21.4 {
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/throwableitemprojectile/ThrowableItemProjectile;tick()V", shift = At.Shift.AFTER), cancellable = true)
    private void rof$updatePearlLoading(CallbackInfo ci) {
        if (!(this.level() instanceof ServerLevel level)) {
            return;
        }
        var forcedEntitylist = ExtraWorldDatas.fromWorld(level).forcedEntitylist;
        var loading = EnderPearlTickets.decide(level, this);
        if (loading == EnderPearlTickets.PearlLoading.SELF_LOADING) {
            forcedEntitylist.put(this.getUUID(), this);
            level.resetEmptyTime();
            ci.cancel();
        }
        else if (forcedEntitylist.get(this.getUUID()) == this) {
            forcedEntitylist.put(this.getUUID(), null);
            this.setPos(ROFWarp.getPos_(this));
            if (loading == EnderPearlTickets.PearlLoading.VANILLA) {
                ServerPlayer.placeEnderPearlTicket(level, chunkPosition());
            }
        }
    }
    //?} else {
    /*@Inject(method = "tick",at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/thrown/ThrownItemEntity;tick()V", shift = At.Shift.AFTER),cancellable = true)
    private void rof$updatePearlLoading(CallbackInfo ci) {
        if (!(this.level() instanceof ServerLevel level)) {
            return;
        }
        var forcedEntitylist = ExtraWorldDatas.fromWorld(level).forcedEntitylist;
        var loading = EnderPearlTickets.decide(level, this);
        if (loading == EnderPearlTickets.PearlLoading.SELF_LOADING) {
            forcedEntitylist.put(this.getUUID(), this);
            level.resetEmptyTime();
            ci.cancel();
        }
        else if (forcedEntitylist.get(this.getUUID()) == this) {
            forcedEntitylist.put(this.getUUID(), null);
            this.setPos(ROFWarp.getPos_(this));
            if (loading == EnderPearlTickets.PearlLoading.VANILLA) {
                ServerPlayer.placeEnderPearlTicket(level, chunkPosition());
            }
        }
    }
    *///?}

    @Inject(method = "tick",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/server/level/ServerPlayer;registerAndUpdateEnderPearlTicket(Lnet/minecraft/world/entity/projectile/throwableitemprojectile/ThrownEnderpearl;)J",
                     shift = At.Shift.AFTER))
    private void EndPearlForcedSync(CallbackInfo ci)
    {
        if (blockingEnderPearlLoading >0 && this.level() instanceof ServerLevel serverLevel) {
            var chunkPos = this.chunkPosition();
            //serverLevel.getChunk(ChunkPosHelper.x(chunkPos), ChunkPosHelper.z(chunkPos), ChunkStatus.FULL, true);
            ExtraWorldDatas.fromWorld(serverLevel)
                    .enderPearlForcedSyncChunks
                    .add(ChunkPosHelper.pack(chunkPos));
            serverLevel.entityManager.updateChunkStatus( chunkPos, Visibility.TICKING);
        }
    }


}
