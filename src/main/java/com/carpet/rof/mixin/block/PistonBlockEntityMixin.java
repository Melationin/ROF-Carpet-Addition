//package com.carpet.rof.mixin.block;
//
//import net.minecraft.block.entity.PistonBlockEntity;
//import net.minecraft.storage.ReadView;
//import net.minecraft.storage.WriteView;
//import net.minecraft.util.math.Direction;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.Redirect;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//@Mixin(PistonBlockEntity.class)
//public abstract class PistonBlockEntityMixin {
//    @Shadow private float progress;
//
//    @Shadow private float lastProgress;
//
//    @Redirect(method = "writeData",at = @At(value = "INVOKE", target = "Lnet/minecraft/storage/WriteView;putFloat(Ljava/lang/String;F)V"))
//    public void writeData(WriteView instance, String s, float v){
//        //instance.putFloat("progress", this.progress);
//    }
//
//    @Inject(method = "readData", at = @At("TAIL"))
//    private void fixPistonProgress(ReadView nbt, CallbackInfo ci) {
//        // 读取后检查进度
////        float savedProgress = nbt.getFloat("progress",0);
////
////        // 如果活塞处于中途推动状态（progress 在 0-1 之间）
////        if (savedProgress > 0.0F && savedProgress < 1.0F) {
////            // 强制设置为完成状态，这样 tick() 会自动清理并还原方块
////            this.progress = 1.0F;
////            this.lastProgress = 1.0F;
////        }
//    }
//}
