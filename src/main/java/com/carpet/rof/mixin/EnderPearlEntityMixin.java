package com.carpet.rof.mixin;

// Minecraft 相关导入

import com.carpet.rof.accessor.ServerWorldAccessor;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.server.world.*;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.carpet.rof.ROFCarpetSettings.*;

// Java 标准库


@Mixin(EnderPearlEntity.class)
public abstract class EnderPearlEntityMixin extends ThrownItemEntity implements  ServerWorldAccessor {



    @Unique
    final double MinSpeed = enderPearlForcedTickMinSpeed;




    // 是否启用同步状态（冻结 or 物理更新）
    @Unique
    public boolean syncMode = true;



    @Unique
    private int EPTicks = 1;

    @Unique
    private boolean ChangeSpeed = false;

    // 必须定义的构造函数，调用父类
    protected EnderPearlEntityMixin(EntityType<?extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }



    // 判断某区块是否是实体可运行的状态（ENTITY_TICKING）

    // 注入 tick() 方法的开头，覆盖默认逻辑
    @Inject(method = "tick", at = @At(value = "HEAD"),cancellable = true)
    private void EndPearlHead(CallbackInfo ci) {
        World world = this.getWorld();

         if(true){  //for debug
             if (world instanceof ServerWorld){
                 Text name = this.getStack().getCustomName();
                 if(name != null && ChangeSpeed == false){
                     this.setVelocity(new Vec3d(-1,0,-1).multiply(Double.parseDouble( name.getString())));
                     ChangeSpeed = true;
                 }
             }
         }

        // 仅对服务器世界处理
        if (world instanceof ServerWorld serverWorld) {

            EPTicks++;

            if(syncMode){  //此时为同步状态
                if((MinSpeed >0)  && (  Math.abs(this.getVelocity().x)> MinSpeed ||Math.abs(this.getVelocity().z)> MinSpeed)){//大于最高速度，切换加载逻辑
                    syncMode = false; //模拟状态，不计算
                    ((ServerWorldAccessor)world).addMustTickEntity(this);
                }
                else {
                    return;
                }
            }

            if(forceEnderPearlLogger){
                if(this.getOwner() instanceof PlayerEntity player) {
                    player.sendMessage(Text.of("[#" + EPTicks + "] " + "Pearl's Pos:" + this.getPos()), true);
                }
            }

        }
    }

    @Inject(method = "tick",at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/thrown/ThrownItemEntity;tick()V", shift = At.Shift.AFTER),cancellable = true)
    private void EndPearlBetterForce(CallbackInfo ci, @Local(ordinal = 0) int i, @Local(ordinal = 1) int j,@Local Entity entity) {
        if(!syncMode) {
            ci.cancel();
        }
    }


    @Inject(method =  "tick",at = @At(value = "RETURN"))
    private void ChunkUnloadingEnd(CallbackInfo ci){
        if (this.isRemoved()) {
            World world = this.getWorld();
            if (world instanceof ServerWorld) {
                ServerWorldAccessor accessor = (ServerWorldAccessor)((ServerWorld)world);
                if (accessor.inMustTickEntity(this)) {
                    accessor.removeMustTickEntity(this);
                }
            }
        }
    }
}