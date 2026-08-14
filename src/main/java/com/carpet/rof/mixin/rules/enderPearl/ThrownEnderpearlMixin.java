package com.carpet.rof.mixin.rules.enderPearl;

// Minecraft 相关导入

import com.carpet.rof.extraWorldData.ExtraWorldDatas;
import com.carpet.rof.extraWorldData.extraChunkDatas.ExceedChunkMarker;
import com.carpet.rof.utils.ROFWarp;
import com.carpet.rof.utils.ROFTool;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.entity.Visibility;
import net.minecraft.world.phys.HitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.carpet.rof.rules.enderPearl.EnderPearlSettings.*;
//import static com.carpet.rof.rules.extraChunkDatas.ExceedChunkMarkerSetting.optimizeForcedEnderPearlTick;


// Java 标准库


@Mixin(ThrownEnderpearl.class)
public abstract class ThrownEnderpearlMixin extends ThrowableItemProjectile
{

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
            }else {

                //? >=1.21.2 {

                if ((Math.abs(this.getDeltaMovement().x) <= MinSpeed && Math.abs(this.getDeltaMovement().z) <= MinSpeed)) {
                    forcedEntitylist.put(this.getUUID(), null);
                    ChunkPos chunkPos = chunkPosition();
                    this.setPos(ROFWarp.getPos_(this));
                    ServerPlayer.placeEnderPearlTicket(serverWorld, chunkPosition());
                    syncMode = true;
                    return;
                }
                //?}
                if(!optimizeForcedEnderPearlTick.equals("false")){
                    boolean  canSkip = true;
                    for (BlockPos blockPos : ROFWarp.getBlockPosIt(this.getBoundingBox())) {
                        if(!ExceedChunkMarker.mustBeAir((ServerLevel)ROFWarp.getWorld_(this) ,blockPos)
                        ){
                            canSkip = false;
                            break;
                        }
                    }

                    boolean over1_21_2 = false;
                    if(optimizeForcedEnderPearlTick.equals("1_21_2-")){
                        over1_21_2 = false;
                    }else if(optimizeForcedEnderPearlTick.equals("1_21_2+")){
                        over1_21_2 = true;
                    }else {
                        //? > 1.21.2 {
                        over1_21_2 = true;
                        //?} else {
                        /*over1_21_2 = false;
                        *///?}
                    }

                    if(canSkip) {
                        if(over1_21_2) {
                            this.applyGravity();
                            this.setDeltaMovement(this.getDeltaMovement().scale(0.99));
                            HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
                            Vec3 vec3d;
                            if (hitResult.getType() != HitResult.Type.MISS) {
                                vec3d = hitResult.getLocation();
                            } else {
                                vec3d = ROFWarp.getPos_(this).add(this.getDeltaMovement());
                            }

                            this.setPos(vec3d);
                            this.updateRotation();

                            if (hitResult.getType() != HitResult.Type.MISS && this.isAlive()) {
                                this.hitTargetOrDeflectSelf(hitResult);
                            }

                            if (this.isRemoved()) {
                                forcedEntitylist.remove(this);
                            }
                            ci.cancel();
                        }else {
                            HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
                            if (hitResult.getType() != HitResult.Type.MISS) {
                                this.hitTargetOrDeflectSelf(hitResult);
                            }
                            Vec3 vec3d = this.getDeltaMovement();
                            double d = this.getX() + vec3d.x;
                            double e = this.getY() + vec3d.y;
                            double f = this.getZ() + vec3d.z;
                            this.updateRotation();
                            float h= 0.99F;;
                            this.setDeltaMovement(vec3d.scale((double)h));
                            this.applyGravity();
                            this.setPos(d, e, f);
                            if (this.isRemoved()) {
                                forcedEntitylist.remove(this);
                            }
                            ci.cancel();
                        }
                    }
                }
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
        if (enderPearlForcedSync && this.level() instanceof ServerLevel serverLevel) {
            var chunkPos = this.chunkPosition();
            serverLevel.getChunk(chunkPos.x(), chunkPos.z(), ChunkStatus.FULL, true);
            ExtraWorldDatas.fromWorld(serverLevel)
                    .enderPearlForcedSyncChunks
                    .add(chunkPos.pack());
            //serverLevel.entityManager.updateChunkStatus(chunkPos, Visibility.TICKING);
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
}