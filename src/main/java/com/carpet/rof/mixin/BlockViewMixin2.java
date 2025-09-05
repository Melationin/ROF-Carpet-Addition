package com.carpet.rof.mixin;

import com.carpet.rof.ROFCarpetSettings;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.carpet.rof.ROFCarpetServer.NETHER_HighChunkSet;

@Mixin(BlockView.class)
public interface BlockViewMixin2 {
   @Inject(method = "method_17743",at = @At(value = "HEAD"),cancellable = true)
    default void method_177432(RaycastContext innerContext, BlockPos pos, CallbackInfoReturnable<BlockHitResult> cir){
       if(ROFCarpetSettings.optimizeRaycastWithHCL
           &&pos.getY()>= NETHER_HighChunkSet.topY-1
               && (Object)this ==  NETHER_HighChunkSet.world
               && NETHER_HighChunkSet.isHighChunk(new ChunkPos(pos))){
               cir.setReturnValue(null);
       }
   }

}
