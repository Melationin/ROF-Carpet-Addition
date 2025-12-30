package com.carpet.rof.mixin.mergeTNTNext;


import com.carpet.rof.rules.mergeTNTNext.ServerWorldAccessor;
import com.carpet.rof.rules.mergeTNTNext.TntEntityAccessor;
import com.carpet.rof.utils.RofTool;
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
    int mergedTNTNCount = 1;

    @Override
    public void addMergeCount(int mergedTNTCount) {
        mergedTNTNCount  += mergedTNTCount;
    }

    @Shadow
    public abstract int getFuse();


    public TntEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/TntEntity;getFuse()I"), cancellable = true)
    private void merge(CallbackInfo ci) {
        if (mergeTNTNext &&
                RofTool.getWorld_(this)  instanceof ServerWorld world

                && !this.isRemoved() && getFuse() > 2) {
            RofTool.EntityPosAndVec TntPosAndVec = new RofTool.EntityPosAndVec(RofTool.getPos_(this), this.getVelocity(), this.getFuse());
            HashMap<RofTool.EntityPosAndVec, TntEntity> TntMergeMap = ((ServerWorldAccessor) world).getTNTMergeMap();
            if (TntMergeMap.containsKey(TntPosAndVec)) {
                TntEntity mainTNT = TntMergeMap.get(TntPosAndVec);
                ((TntEntityAccessor) mainTNT).addMergeCount(mergedTNTNCount );
                this.remove(RemovalReason.DISCARDED);
                //this.discard();
                mergedTNTNCount  = 0;
                ci.cancel();
            } else {
                TntMergeMap.put(TntPosAndVec, (TntEntity) (Object) this);
            }
        }
    }

    @Inject(method = "explode", at = @At(value = "HEAD"), cancellable = true)
    private void onExplode(CallbackInfo ci) {
        if (mergedTNTNCount  > 1)
            for (int i = 0; i < mergedTNTNCount  - 1; i++) {
                RofTool.getWorld_(this)
                        .createExplosion(this, this.getX(), this.getBodyY(0.0625),
                        this.getZ(), 4.0F, World.ExplosionSourceType.TNT);
            }
        else if (mergedTNTNCount == 0) {
            ci.cancel();
        }

    }

    //? >=1.21.6 {
    @Inject(method = "writeCustomData", at = @At(value = "HEAD"))
    private void writeCustomData(WriteView view, CallbackInfo ci) {
        if (mergedTNTNCount  > 1) {
            view.putInt("mergedTNT", mergedTNTNCount );
        }
    }

    @Inject(method = "readCustomData", at = @At(value = "HEAD"))
    private void readCustomData(ReadView view, CallbackInfo ci) {
        view.getOptionalInt("mergedTNT").ifPresent(integer -> mergedTNTNCount  = integer);
    }

    //?} else {

    /*@Inject(method = "readCustomDataFromNbt",at = @At(value = "HEAD"))
    private void readCustomDataFromNbt(NbtCompound tag, CallbackInfo ci) {
        if (mergedTNTNCount > 1) {
            tag.putInt("mergedTNT", mergedTNTNCount );
        }
    }

    @Inject(method = "writeCustomDataToNbt",at = @At(value = "HEAD"))
    private void writeCustomDataToNbt(NbtCompound tag, CallbackInfo ci) {
        if (tag.contains("mergedTNT")) {
            mergedTNTNCount  = tag.getInt("mergedTNT");
        }
    }
    *///?}
}
