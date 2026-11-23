package com.carpet.rof.mixin.world.blockEntity;

import com.carpet.rof.world.blockEntity.BlockEntityTickerSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Iterator;
import java.util.List;

@Mixin(Level.class)
public abstract class LevelMixin
{
    //? if =26.1 {
    @Shadow @Final protected List<TickingBlockEntity> blockEntityTickers;

    @WrapOperation(method = "tickBlockEntities",
            at = @At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z"))
    private boolean rof$compactTickers(Iterator<?> iterator, Operation<Boolean> original,
                                       @Local(ordinal = 0) boolean tick)
    {
        if (!BlockEntityTickerSettings.optimizedBlockEntityRemoval) {
            return original.call(iterator);
        }

        Level level = (Level) (Object) this;
        var tickers = this.blockEntityTickers;
        int size = tickers.size();
        int read = 0;
        int write = 0;
        long lastTickingChunk = Long.MIN_VALUE;
        try {
            while (read < size) {
                var ticker = tickers.get(read);
                boolean removed = ticker.isRemoved();
                int index = read++;
                if (removed) continue;
                if (write != index) tickers.set(write, ticker);
                write++;

                if (!tick) continue;
                var pos = ticker.getPos();
                if (pos == null) continue;
                long chunk = ChunkPos.pack(pos);
                if (chunk != lastTickingChunk && !level.shouldTickBlocksAt(chunk)) continue;
                lastTickingChunk = chunk;
                ticker.tick();
            }
        } finally {
            if (write < read) {
                // If ticking threw, retain the unvisited suffix.
                while (read < size) tickers.set(write++, tickers.get(read++));
                tickers.subList(write, size).clear();
            }
        }
        return false;
    }
    //?}
}
