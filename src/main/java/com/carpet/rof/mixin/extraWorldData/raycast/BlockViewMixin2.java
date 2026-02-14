package com.carpet.rof.mixin.extraWorldData.raycast;

import com.carpet.rof.extraWorldData.extraChunkDatas.ExceedChunkMarker;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.carpet.rof.rules.extraChunkDatas.ExceedChunkMarkerSetting.optimizeRaycast;

@Mixin(BlockView.class)
public interface BlockViewMixin2 {
    @Inject(method = "method_17743",at = @At(value = "HEAD"),cancellable = true)
    default void method_177432(RaycastContext innerContext, BlockPos pos, CallbackInfoReturnable<BlockHitResult> cir){

        if((Object)this instanceof ServerWorld serverWorld){
            if(serverWorld.isOutOfHeightLimit(pos)){
                cir.setReturnValue(null);
            }
            if (optimizeRaycast
                    && ExceedChunkMarker.isMustAir(serverWorld,pos)) {
                cir.setReturnValue(null);
            }
        }
    }
}
