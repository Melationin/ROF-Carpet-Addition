//package com.carpet.rof.mixin.block;
//
//import com.carpet.rof.accessor.BlockEntityAccessor;
//import net.minecraft.block.BlockState;
//import net.minecraft.block.entity.BlockEntity;
//import net.minecraft.block.entity.BlockEntityType;
////? > 1.21.4{
//import net.minecraft.storage.ReadView;
//import net.minecraft.storage.WriteView;
////?}
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.World;
//import org.jetbrains.annotations.Nullable;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//import org.spongepowered.asm.mixin.Unique;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//@Mixin(BlockEntity.class)
//public abstract class BlockEntityMixin implements BlockEntityAccessor {
////    @Shadow @Nullable public abstract World getWorld();
////
////    @Unique
////    private static int index = 0;
////
////    private long pindex = 0;
////
////    @Override
////    public long getIndex() {
////        return pindex;
////    }
////    @Inject(method = "writeData",at = @At(value = "HEAD"))
////    private void writeData(WriteView view, CallbackInfo ci) {
////        view.putLong("_Index", pindex);
////    }
////
////    @Inject(method = "read", at = @At(value = "HEAD"))
////    private void readData(ReadView view, CallbackInfo ci) {
////       pindex =  view.getLong("_Index",0);
////    }
////
////    @Inject(method = "setWorld",at = @At(value = "HEAD"))
////    private void setWorld(World world, CallbackInfo ci) {
////        if(pindex == 0) this.pindex =  (world.getTime()<<32) + (index++);
////    }
//}
