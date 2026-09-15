package com.carpet.rof.mixin.rules.merge;


import com.carpet.rof.extraWorldData.ExtraWorldDatas;
import com.carpet.rof.rules.explosion.ExplosionMergeData;
import com.carpet.rof.rules.merge.EntityTickOrderAccessor;
import com.carpet.rof.rules.merge.MergeSetting;
import com.carpet.rof.rules.merge.MergedEntityAccessor;
import com.carpet.rof.utils.ROFWarp;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.gamerules.GameRules;
//? >=1.21.6 {
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
//?} else {
/*import net.minecraft.nbt.CompoundTag;
*///?}

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Comparator;
import java.util.HashMap;

import static com.carpet.rof.rules.merge.MergeSetting.mergeTNTNext;

@Mixin(PrimedTnt.class)
public abstract class PrimedTntMixin extends Entity implements MergedEntityAccessor
{




    @Unique
    private int rof$mergedTNTNCount = 1;

    @Unique
    private Vec3 rof$lastExplosionPos;

    @Override
    public void ROF$addMergeCount(int mergeCount){
        rof$mergedTNTNCount += mergeCount;
    };
   // private int mergedTNTNCount2 = 1;


    @Shadow
    public abstract int getFuse();

    @Shadow
    private boolean usedPortal;

    @Shadow
    @Final
    public static ExplosionDamageCalculator USED_PORTAL_DAMAGE_CALCULATOR;

    @Shadow
    private float explosionPower;

    public PrimedTntMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/PrimedTnt;getFuse()I"), cancellable = true)
    private void merge(CallbackInfo ci) {
        if(!(ROFWarp.getWorld_(this)  instanceof ServerLevel world))return;
        if(mergeTNTNext != MergeSetting.MergeTNTNextMode.TRUE && mergeTNTNext != MergeSetting.MergeTNTNextMode.SAFE) return;
        if (!this.isRemoved() && getFuse() >= 2) {
            MergeSetting.EntityPosAndVec TntPosAndVec = new MergeSetting.EntityPosAndVec(ROFWarp.getPos_(this), this.getDeltaMovement(), this.getFuse());
            HashMap<MergeSetting.EntityPosAndVec, PrimedTnt> tntMergeMap = ExtraWorldDatas.fromWorld(world).mergeTntMap;
            tntMergeMap.compute(TntPosAndVec,(k,tnt)->{
                if(tnt!=null){
                    ((MergedEntityAccessor) tnt).ROF$addMergeCount(rof$mergedTNTNCount);
                    MergedEntityAccessor thisAccessor = this;
                    thisAccessor.ROF$addMergeCount(rof$mergedTNTNCount);
                    this.remove(RemovalReason.DISCARDED);
                    ci.cancel();
                    return tnt;
                } else {
                    return (PrimedTnt) (Object) this;
                }
            });
        }
    }

    @Inject(method = "tick", at = @At(value = "HEAD"),
            cancellable = true)
    private void tick(CallbackInfo ci){
        if(!(ROFWarp.getWorld_(this)  instanceof ServerLevel level))return;

        if(mergeTNTNext == MergeSetting.MergeTNTNextMode.ALMOST_VANILLA){
            if(this.isRemoved()) return;
            if(this.getFuse() != 1)return;
            var list = level.getEntitiesOfClass(PrimedTnt.class,
                    this.makeBoundingBox().inflate(0.01)

                    ,tnt->{
                if(tnt ==(Object) this) return false;
                if(tnt.getFuse() != 1) return false;
                if(this.getX() != tnt.getX() || this.getY() != tnt.getY() || this.getZ() != tnt.getZ()) return false;
                if(!this.getDeltaMovement().equals(tnt.getDeltaMovement())) return false;
                if(
                        ((EntityTickOrderAccessor)(Object)(this)).rof$getTickOrder()>=((EntityTickOrderAccessor)(Object)(tnt)).rof$getTickOrder()
                ) return false;
                return true;
            });

            list.sort(Comparator.comparingLong(tnt -> ((EntityTickOrderAccessor) (Object) (tnt)).rof$getTickOrder()));
            long now = ((EntityTickOrderAccessor)(Object)(this)).rof$getTickOrder();
            int count = 1;
            for(var tnt : list){
                if(((EntityTickOrderAccessor) (Object) (tnt)).rof$getTickOrder() != now +1){
                    break;
                }else {
                    now++;
                    count++;
                    tnt.discard();
                }
            }
            rof$mergedTNTNCount = count;
        }

        if(mergeTNTNext == MergeSetting.MergeTNTNextMode.SAFE || mergeTNTNext == MergeSetting.MergeTNTNextMode.ALMOST_VANILLA || mergeTNTNext == MergeSetting.MergeTNTNextMode.SAFE_PLUS) {
            if(this.getFuse() != 1 || this.rof$mergedTNTNCount  < 2)return;
            this.handlePortal();

            double x = this.getX();
            double y = this.getY();
            double z = this.getZ();
            ExplosionMergeData data = ExtraWorldDatas.fromWorld(level).explosionMergeData;

            for (int i = 0; i < rof$mergedTNTNCount; i++) {
                var vec = this.getDeltaMovement();
                this.applyGravity();
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.applyEffectsFromBlocks();
                double x2 = this.getX();
                double y2 = this.getY();
                double z2 = this.getZ();
                this.setDeltaMovement(vec);
                this.setPos(x, y, z);
                Vec3 explosionPos = new Vec3(x2, y2, z2);
                data.explosionCount = 1;
                data.needExplosionCount = explosionPos.equals(rof$lastExplosionPos) ? rof$mergedTNTNCount - i : 1;
                rof$lastExplosionPos = explosionPos;
                explode2(x2, y2, z2);
                i += data.explosionCount - 1;
            }
            this.discard();
            ci.cancel();
        }


    }

    // 1.21.10 still exposes the legacy GameRules API (RULE_* constants + getBoolean); 1.21.11
    // replaced them with typed GameRule values, so only this lookup is version-scoped.
    @Unique
    private static boolean rof$tntExplodes(Level level) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }
        //? if >=1.21.11 {
        return serverLevel.getGameRules().get(GameRules.TNT_EXPLODES);
        //?} else {
        /*return serverLevel.getGameRules().getBoolean(GameRules.RULE_TNT_EXPLODES);
        *///?}
    }

    @Unique
    private void explode2(double x,double y,double z) {
        if (rof$tntExplodes(this.level())) {
            this.level()
                    .explode(
                            null,
                            Explosion.getDefaultDamageSource(this.level(), this),
                            this.usedPortal?USED_PORTAL_DAMAGE_CALCULATOR:null,
                            x,
                            y+this.getBbHeight() * 0.0625,
                            z,
                            this.explosionPower,
                            false,
                            Level.ExplosionInteraction.TNT
                    );
        }
    }

    @Inject(method = "explode", at = @At(value = "HEAD"), cancellable = true)
    private void onExplode(CallbackInfo ci) {
        if (rof$mergedTNTNCount > 1) {
            if (!(ROFWarp.getWorld_(this) instanceof ServerLevel level)) return;
            ExplosionMergeData data = ExtraWorldDatas.fromWorld(level).explosionMergeData;
            for (int i = 0; i < rof$mergedTNTNCount - 1; i++) {
                data.explosionCount = 1;
                data.needExplosionCount = rof$mergedTNTNCount - i;
                level.explode(this, this.getX(), this.getY(0.0625),
                        this.getZ(), this.explosionPower, Level.ExplosionInteraction.TNT);
                if (data.explosionCount > 1) {
                    ci.cancel();
                    return;
                }
            }
            data.explosionCount = 1;
            data.needExplosionCount = 1;
        }
        else if (rof$mergedTNTNCount == 0) {
            ci.cancel();
        }

    }

    //? >= 1.21.6 {
    @Inject(method = "addAdditionalSaveData", at = @At(value = "HEAD"))
    private void writeCustomData(ValueOutput view, CallbackInfo ci) {
        if (rof$mergedTNTNCount > 1) {
            view.putInt("mergedTNT", rof$mergedTNTNCount);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At(value = "HEAD"))
    private void readCustomData(ValueInput view, CallbackInfo ci) {
        view.getInt("mergedTNT").ifPresent(integer -> rof$mergedTNTNCount = integer);
    }

    //?} else {

    /*@Inject(method = "addAdditionalSaveData", at = @At(value = "HEAD"))
    private void writeCustomData(CompoundTag tag, CallbackInfo ci) {
        if (rof$mergedTNTNCount > 1) {
            tag.putInt("mergedTNT", rof$mergedTNTNCount);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At(value = "HEAD"))
    private void readCustomData(CompoundTag tag, CallbackInfo ci) {
        rof$mergedTNTNCount = tag.getIntOr("mergedTNT", rof$mergedTNTNCount);
    }
    *///?}


}
