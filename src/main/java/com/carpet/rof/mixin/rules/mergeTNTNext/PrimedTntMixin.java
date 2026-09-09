package com.carpet.rof.mixin.rules.mergeTNTNext;


import com.carpet.rof.extraWorldData.ExtraWorldDatas;
import com.carpet.rof.rules.merge.MergeSetting;
import com.carpet.rof.rules.merge.MergedEntityAccessor;
import com.carpet.rof.utils.ROFTool;
import com.carpet.rof.utils.ROFWarp;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.PrimedTnt;

import net.minecraft.server.level.ServerLevel;
//? >=1.21.6 {
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
//?} else {
/*import net.minecraft.nbt.NbtCompound;
*///?}

import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.HashMap;

import static com.carpet.rof.rules.merge.MergeSetting.mergeTNTNext;
import static com.carpet.rof.rules.merge.MergeSetting.mergeTNTOnlyNether;

@Mixin(PrimedTnt.class)
public abstract class PrimedTntMixin extends Entity implements MergedEntityAccessor
{




    @Unique
    private int rof$mergedTNTNCount = 1;

    @Override
    public void ROF$addMergeCount(int mergeCount){
        rof$mergedTNTNCount += mergeCount;
    };
   // private int mergedTNTNCount2 = 1;


    @Shadow
    public abstract int getFuse();

    public PrimedTntMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/PrimedTnt;getFuse()I"), cancellable = true)
    private void merge(CallbackInfo ci) {
        //System.out.println(mergedTNTNCount2);

        if (mergeTNTNext &&
                ROFWarp.getWorld_(this)  instanceof ServerLevel world
                && !this.isRemoved() && getFuse() > 2
        &&(!mergeTNTOnlyNether || ROFTool.isNetherWorld(world))
        ) {
            MergeSetting.EntityPosAndVec TntPosAndVec = new MergeSetting.EntityPosAndVec(ROFWarp.getPos_(this), this.getDeltaMovement(), this.getFuse());
            HashMap<MergeSetting.EntityPosAndVec, PrimedTnt> tntMergeMap = ExtraWorldDatas.fromWorld(world).mergeTntMap;
            tntMergeMap.compute(TntPosAndVec,(k,tnt)->{
                if(tnt!=null){
                    ((MergedEntityAccessor) tnt).ROF$addMergeCount(rof$mergedTNTNCount);
                    MergedEntityAccessor thisAccessor = (MergedEntityAccessor) this;
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

    @Inject(method = "explode", at = @At(value = "HEAD"), cancellable = true)
    private void onExplode(CallbackInfo ci) {
        if (rof$mergedTNTNCount > 1)
            for (int i = 0; i < rof$mergedTNTNCount - 1; i++) {
                ROFWarp.getWorld_(this)
                        .explode(this, this.getX(), this.getY(0.0625),
                        this.getZ(), 4.0F, Level.ExplosionInteraction.TNT);
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

    /*@Inject(method = "readCustomDataFromNbt",at = @At(value = "HEAD"))
    private void readCustomDataFromNbt(NbtCompound tag, CallbackInfo ci) {
        if (rof$mergedTNTNCount > 1) {
            tag.putInt("mergedTNT", rof$mergedTNTNCount );
        }
    }

    @Inject(method = "writeCustomDataToNbt",at = @At(value = "HEAD"))
    private void writeCustomDataToNbt(NbtCompound tag, CallbackInfo ci) {
        if (tag.contains("mergedTNT")) {
            rof$mergedTNTNCount  = ROFWarp.getFromNbt(tag.getInt("mergedTNT"));
        }
    }
    *///?}


}
