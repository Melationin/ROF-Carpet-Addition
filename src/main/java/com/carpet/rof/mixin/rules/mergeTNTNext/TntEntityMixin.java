package com.carpet.rof.mixin.rules.mergeTNTNext;


import com.carpet.rof.extraWorldData.ExtraWorldDatas;
import com.carpet.rof.rules.mergeTNTNext.MergeTNTNextSetting;
import com.carpet.rof.rules.mergeTNTNext.TntEntityAccessor;
import com.carpet.rof.utils.ROFWarp;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.TntEntity;

import net.minecraft.server.world.ServerWorld;
//? >=1.21.6 {
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
//?} else {
/*import net.minecraft.nbt.NbtCompound;
*///?}

import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.HashMap;

import static com.carpet.rof.rules.mergeTNTNext.MergeTNTNextSetting.mergeTNTNext;

@Mixin(TntEntity.class)
public abstract class TntEntityMixin extends Entity implements TntEntityAccessor {

    @Unique
    int rof$mergedTNTNCount = 1;

    @Override
    public void ROF$addMergeCount(int mergeCount){
        rof$mergedTNTNCount += mergeCount;
    };



    @Shadow
    public abstract int getFuse();

    public TntEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/TntEntity;getFuse()I"), cancellable = true)
    private void merge(CallbackInfo ci) {
        if (mergeTNTNext &&
                ROFWarp.getWorld_(this)  instanceof ServerWorld world
                && !this.isRemoved() && getFuse() > 2) {
            MergeTNTNextSetting.EntityPosAndVec TntPosAndVec = new MergeTNTNextSetting.EntityPosAndVec(ROFWarp.getPos_(this), this.getVelocity(), this.getFuse());
            HashMap<MergeTNTNextSetting.EntityPosAndVec, TntEntity> TntMergeMap = ExtraWorldDatas.fromWorld(world).mergeTntMap;
            if (TntMergeMap.containsKey(TntPosAndVec)) {
                TntEntity mainTNT = TntMergeMap.get(TntPosAndVec);
                ((TntEntityAccessor) mainTNT).ROF$addMergeCount(rof$mergedTNTNCount);
                this.remove(RemovalReason.DISCARDED);
                rof$mergedTNTNCount = 0;
                ci.cancel();
            } else {
                TntMergeMap.put(TntPosAndVec, (TntEntity) (Object) this);
            }
        }
    }

    @Inject(method = "explode", at = @At(value = "HEAD"), cancellable = true)
    private void onExplode(CallbackInfo ci) {
        if (rof$mergedTNTNCount > 1)
            for (int i = 0; i < rof$mergedTNTNCount - 1; i++) {
                ROFWarp.getWorld_(this)
                        .createExplosion(this, this.getX(), this.getBodyY(0.0625),
                        this.getZ(), 4.0F, World.ExplosionSourceType.TNT);
            }
        else if (rof$mergedTNTNCount == 0) {
            ci.cancel();
        }

    }

    //? >=1.21.6 {
    @Inject(method = "writeCustomData", at = @At(value = "HEAD"))
    private void writeCustomData(WriteView view, CallbackInfo ci) {
        if (rof$mergedTNTNCount > 1) {
            view.putInt("mergedTNT", rof$mergedTNTNCount);
        }
    }

    @Inject(method = "readCustomData", at = @At(value = "HEAD"))
    private void readCustomData(ReadView view, CallbackInfo ci) {
        view.getOptionalInt("mergedTNT").ifPresent(integer -> rof$mergedTNTNCount = integer);
    }

    //?} else {

    /*@Inject(method = "readCustomDataFromNbt",at = @At(value = "HEAD"))
    private void readCustomDataFromNbt(NbtCompound tag, CallbackInfo ci) {
        if (rof$mergedTNTNCount > 1) {
            tag.putInt("mergedTNT", rof$mergedTNTNCount );
        }
    }

    @Inject(method = "writeCustomDataToNbt",at = @At(value = "HEAD"))
    private void writeCustomDataToNbt(NbtCompound tag, CallbackInfo ci) {
        if (tag.contains("mergedTNT")) {
            rof$mergedTNTNCount  = tag.getInt("mergedTNT");
        }
    }
    *///?}
}
