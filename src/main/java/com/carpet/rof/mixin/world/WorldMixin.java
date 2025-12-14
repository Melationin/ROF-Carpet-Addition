package com.carpet.rof.mixin.world;

import com.carpet.rof.accessor.BlockEntityAccessor;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockEntityTickInvoker;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.tick.TickManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(World.class)
public abstract class WorldMixin {
    @Shadow @Final protected List<BlockEntityTickInvoker> blockEntityTickers;


    @Shadow public abstract TickManager getTickManager();

    private long getIndex(BlockEntityTickInvoker blockEntityTickInvoker){
        if( blockEntityTickInvoker instanceof WorldChunk.WrappedBlockEntityTickInvoker wrappedBlockEntityTickInvoker){
            return getIndex( wrappedBlockEntityTickInvoker.wrapped);
        }else if(blockEntityTickInvoker instanceof WorldChunk.DirectBlockEntityTickInvoker<?>  directBlockEntityTickInvoker){
            return ((BlockEntityAccessor) directBlockEntityTickInvoker.blockEntity).getIndex();
        }
        return Long.MAX_VALUE;
    }

    @Inject(method = "tickBlockEntities",at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"))
    private void tickBlockEntities(CallbackInfo ci) {

        if(!((Object)this instanceof ServerWorld)) return;
        if(!this.getTickManager().shouldTick()) return;
        blockEntityTickers.sort((it1,it2)->{
            var a = getIndex(it1);
            var b = getIndex(it2);
            return Long.compare(a, b);
        });

        for(BlockEntityTickInvoker bti:blockEntityTickers){
          System.out.println(bti.getPos() + ", " + bti.getName()+", "+getIndex(bti));
        }

    }
}
