package com.carpet.rof.mixin.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PistonBlockEntity.class)
public abstract class PistonBlockEntityMixin {
    @Shadow private float progress;

    @Shadow private float lastProgress;

    @Shadow private long savedWorldTime;

    @Shadow public abstract long getSavedWorldTime();

    @Inject(method = "writeData",at = @At(value = "TAIL" ))
    public void writeData1(WriteView view, CallbackInfo ci){
       // view.putLong("savedWorldTime",savedWorldTime);
       // view.putLong("AAAA",hashCode());
        view.putFloat("progress2",progress);
        System.out.println(view.toString());
        //instance.putFloat("progress", this.progress);
    }

    @Inject(method = "readData",at = @At(value = "TAIL"))
    public void readData2(ReadView view, CallbackInfo ci){
      //savedWorldTime = view.getLong("savedWorldTime",0);
        progress = lastProgress = view.getFloat("progress2",progress);

        System.out.println(view.toString());
    }


    @Inject(method = "tick",at = @At(value = "HEAD"))
    private static void tick(World world, BlockPos pos, BlockState state, PistonBlockEntity blockEntity, CallbackInfo ci){
       // System.out.println("BEtick!!!!!time:" + world.getTime()+blockEntity.getPos().toString());
    }
}
