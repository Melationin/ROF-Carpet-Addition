package com.carpet.rof.mixin.rules.enderPearl;

// Minecraft 相关导入

import com.carpet.rof.extraWorldData.ExtraWorldDatas;
import com.carpet.rof.rules.enderPearl.BetterEnderPearlTicket;
import com.carpet.rof.utils.ROFWarp;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.entity.Visibility;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.carpet.rof.rules.enderPearl.EnderPearlSettings.*;
import com.carpet.rof.utils.ChunkPosHelper;
//import static com.carpet.rof.rules.extraChunkDatas.ExceedChunkMarkerSetting.optimizedForcedEnderPearlTick;


// Java 标准库


@Mixin(ThrownEnderpearl.class)
public abstract class ThrownEnderpearlMixin extends ThrowableItemProjectile
{


    @Shadow
    public abstract @Nullable Entity getOwner();

    @Unique
    final double MinSpeed = enderPearlForcedTickMinSpeed;

    // 是否启用同步状态（冻结 or 物理更新）
    @Unique
    public boolean syncMode = true;

    @Unique
    private int EPTicks = 1;

    // 必须定义的构造函数，调用父类
    protected ThrownEnderpearlMixin(EntityType<?extends ThrowableItemProjectile> entityType, Level world) {
        super(entityType, world);
    }
    // 判断某区块是否是实体可运行的状态（ENTITY_TICKING）

    // 注入 tick() 方法的开头，覆盖默认逻辑
    @Inject(method = "tick", at = @At(value = "HEAD"), cancellable = true)
    private void EndPearlHead(CallbackInfo ci) {

        Level world = ROFWarp.getWorld_(this);
        if (world instanceof ServerLevel serverWorld) {
            var forcedEntitylist = ExtraWorldDatas.fromWorld(serverWorld ).forcedEntitylist;
            EPTicks++;
            if (syncMode) {
                if ((MinSpeed > 0) && (Math.abs(this.getDeltaMovement().x) > MinSpeed || Math.abs(this.getDeltaMovement().z) > MinSpeed)) {//大于最高速度，切换加载逻辑
                    syncMode = false;
                    forcedEntitylist.put(this.getUUID(), this);
                }
            }
            else {
                //? >=1.21.2 {

                if ((Math.abs(this.getDeltaMovement().x) <= MinSpeed && Math.abs(
                        this.getDeltaMovement().z) <= MinSpeed)) {
                    forcedEntitylist.put(this.getUUID(), null);
                    this.setPos(ROFWarp.getPos_(this));
                    ServerPlayer.placeEnderPearlTicket(serverWorld, chunkPosition());
                    syncMode = true;
                }
                //?}
            }
        }
    }

    //? >= 1.21.4 {
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/throwableitemprojectile/ThrowableItemProjectile;tick()V", shift = At.Shift.AFTER), cancellable = true)
    private void EndPearlBetterForce(CallbackInfo ci) {
        if(!syncMode) {
            ci.cancel();
        }
    }
    //?} else {
    /*@Inject(method = "tick",at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/thrown/ThrownItemEntity;tick()V", shift = At.Shift.AFTER),cancellable = true)
    private void EndPearlBetterForce(CallbackInfo ci) {
        if(!syncMode) {
            ci.cancel();
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



    @Inject(method = "tick", at = @At(value = "RETURN"))
    private void ChunkUnloadingEnd(CallbackInfo ci){
        if (this.isRemoved()) {
            Level world = ROFWarp.getWorld_(this);
            if (world instanceof ServerLevel world1) {
                var forcedEntitylist = ExtraWorldDatas.fromWorld(world1).forcedEntitylist;
                forcedEntitylist.put(this.getUUID(),null);
            }
        }
    }



    @WrapOperation(method = "tick",
                   at = @At(value = "INVOKE",
                            target = "Lnet/minecraft/server/level/ServerPlayer;registerAndUpdateEnderPearlTicket(Lnet/minecraft/world/entity/projectile/throwableitemprojectile/ThrownEnderpearl;)J"))
    private long rof$betterEnderPearlTicket(ServerPlayer player, ThrownEnderpearl pearl, Operation<Long> original)
    {
        if(BetterEnderPearlTicket.tryReplaceTicket(pearl, player))
        {
            return BetterEnderPearlTicket.vanillaTicketTimer();
        }
        return original.call(player, pearl);
    }
}