package com.carpet.rof.mixin.enderPearl;

// Minecraft 相关导入

import com.carpet.rof.rules.enderPearl.ServerWorldAccessor;
import com.carpet.rof.utils.RofTool;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.carpet.rof.rules.enderPearl.EnderPearlSettings.*;


// Java 标准库


@Mixin(EnderPearlEntity.class)
public abstract class EnderPearlEntityMixin extends ThrownItemEntity implements ServerWorldAccessor {

    @Unique
    final double MinSpeed = enderPearlForcedTickMinSpeed;

    // 是否启用同步状态（冻结 or 物理更新）
    @Unique
    public boolean syncMode = true;

    @Unique
    private int EPTicks = 1;

    // 必须定义的构造函数，调用父类
    protected EnderPearlEntityMixin(EntityType<?extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }
    // 判断某区块是否是实体可运行的状态（ENTITY_TICKING）

    // 注入 tick() 方法的开头，覆盖默认逻辑
    @Inject(method = "tick", at = @At(value = "HEAD"),cancellable = true)
    private void EndPearlHead(CallbackInfo ci) {

        World world = RofTool.getWorld_(this);

        // 仅对服务器世界处理
        if (world instanceof ServerWorld) {

            EPTicks++;
            if (syncMode) {  //此时为同步状态
                if ((MinSpeed > 0) && (Math.abs(this.getVelocity().x) > MinSpeed || Math.abs(this.getVelocity().z) > MinSpeed)) {//大于最高速度，切换加载逻辑
                    syncMode = false; //模拟状态，不计算
                    ((ServerWorldAccessor) world).addMustTickEntity(this);
                } else {
                    return;
                }

            }else {
                if (forceEnderPearlLogger) {
                    if (this.getOwner() instanceof PlayerEntity player) {
                        player.sendMessage(Text.of("[#" + EPTicks + "] " + "Pearl's Pos:" +
                                RofTool.toString_(RofTool.getPos_(this))
                        ), true);
                    }
                }
            }
            if(
                   highEnderPearlNoChunkLoading
                    && RofTool.getPos_(this).y>RofTool.getWorld_(this)./*? <=1.21.1 {*//*getTopY() *//*?} else {*/ getTopYInclusive()  /*?}*/
                    && RofTool.getWorld_(this).getWorldBorder().contains(RofTool.getPos_(this).add(this.getVelocity()))) {
                this.applyGravity();
                this.setVelocity(this.getVelocity().multiply(0.99));
                this.setPosition(RofTool.getPos_(this).add(this.getVelocity()));
                ci.cancel();
            }
        }
    }

    //? >= 1.21.4 {
    @Inject(method = "tick",at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/thrown/ThrownItemEntity;tick()V", shift = At.Shift.AFTER),cancellable = true)
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

    @Inject(method =  "tick",at = @At(value = "RETURN"))
    private void ChunkUnloadingEnd(CallbackInfo ci){
        if (this.isRemoved()) {
            World world = RofTool.getWorld_(this);
            if (world instanceof ServerWorld) {
                ServerWorldAccessor accessor = (ServerWorldAccessor)(world);
                if (accessor.inMustTickEntity(this)) {
                    accessor.removeMustTickEntity(this);
                }
            }
        }
    }
}