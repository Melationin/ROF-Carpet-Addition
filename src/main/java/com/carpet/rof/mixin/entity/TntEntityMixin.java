package com.carpet.rof.mixin.entity;

import com.carpet.rof.ROFCarpetSettings;
import com.carpet.rof.accessor.ServerWorldAccessor;
import com.carpet.rof.accessor.TntEntityAccessor;
import com.carpet.rof.utils.RofTool;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.TntEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
//? >=1.21.6 {
/*import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
*///?}

import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.HashMap;

@Mixin(TntEntity.class)
public abstract class TntEntityMixin extends Entity implements TntEntityAccessor {

    @Unique
    int mergedTNT = 1;

    @Override
    public void addMergeCount(int mergedTNTCount) {
        mergedTNT += mergedTNTCount;
    }

    @Shadow
    public abstract int getFuse();


    public TntEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/TntEntity;getFuse()I"), cancellable = true)
    private void merge(CallbackInfo ci) {
        if (ROFCarpetSettings.mergeTNTNext && this.getWorld() instanceof ServerWorld world && !this.isRemoved() && getFuse() > 1) {
            RofTool.EntityPosAndVec TntPosAndVec = new RofTool.EntityPosAndVec(this.getPos(), this.getVelocity(), this.getFuse());
            HashMap<RofTool.EntityPosAndVec, TntEntity> TntMergeMap = ((ServerWorldAccessor) world).getTNTMergeMap();
            if (TntMergeMap.containsKey(TntPosAndVec)) {
                TntEntity mainTNT = TntMergeMap.get(TntPosAndVec);
                ((TntEntityAccessor) mainTNT).addMergeCount(mergedTNT);
                this.discard();
                mergedTNT = 0;
                ci.cancel();
            } else {
                TntMergeMap.put(TntPosAndVec, (TntEntity) (Object) this);
            }
        }
    }

    @Inject(method = "explode", at = @At(value = "HEAD"))
    private void onExplode(CallbackInfo ci) {
        if (mergedTNT > 1)
            for (int i = 0; i < mergedTNT - 1; i++) {
                this.getWorld().createExplosion(this, this.getX(), this.getBodyY(0.0625),
                        this.getZ(), 4.0F, World.ExplosionSourceType.TNT);
            }

    }

    //? >=1.21.6 {
    /*@Inject(method = "writeCustomData", at = @At(value = "HEAD"))
    private void writeCustomData(WriteView view, CallbackInfo ci) {
        if (mergedTNT > 1) {
            view.putInt("mergedTNT", mergedTNT);
        }
    }

    @Inject(method = "readCustomData", at = @At(value = "HEAD"))
    private void readCustomData(ReadView view, CallbackInfo ci) {
        view.getOptionalInt("mergedTNT").ifPresent(integer -> mergedTNT = integer);
    }

    *///?} else {

    @Inject(method = "readCustomDataFromNbt",at = @At(value = "HEAD"))
    private void readCustomDataFromNbt(NbtCompound tag, CallbackInfo ci) {
        if (mergedTNT > 1) {
            tag.putInt("mergedTNT", mergedTNT);
        }
    }

    @Inject(method = "writeCustomDataToNbt",at = @At(value = "HEAD"))
    private void writeCustomDataToNbt(NbtCompound tag, CallbackInfo ci) {
        if (tag.contains("mergedTNT")) {
            mergedTNT = tag.getInt("mergedTNT");
        }
    }
    //?}
}
