package com.carpet.rof.mixin.world.blockEntityIterationOptimization;

import com.carpet.rof.world.blockEntityIterationOptimization.BlockEntityIterationOptimizationSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Level.class)
public abstract class LevelMixin
{
    @Shadow
    @Final
    protected List<TickingBlockEntity> blockEntityTickers;

    @Shadow
    @Final
    private List<TickingBlockEntity> pendingBlockEntityTickers;

    @Shadow
    private boolean tickingBlockEntities;

    @Shadow
    public abstract boolean shouldTickBlocksAt(BlockPos pos);

    @Shadow
    public abstract TickRateManager tickRateManager();

    @Inject(method = "tickBlockEntities", at = @At("HEAD"), cancellable = true)
    private void rof$optimizeBlockEntityIteration(CallbackInfo ci)
    {
        if (!BlockEntityIterationOptimizationSettings.blockEntityIterationOptimization)
        {
            return;
        }

        ci.cancel();

        this.tickingBlockEntities = true;

        if (!this.pendingBlockEntityTickers.isEmpty())
        {
            this.blockEntityTickers.addAll(this.pendingBlockEntityTickers);
            this.pendingBlockEntityTickers.clear();
        }

        int write = 0;
        int size = this.blockEntityTickers.size();

        for (int read = 0; read < size; read++)
        {
            TickingBlockEntity ticker = this.blockEntityTickers.get(read);

            if (ticker.isRemoved())
            {
                continue;
            }

            if (write != read)
            {
                this.blockEntityTickers.set(write, ticker);
            }

            write++;

            if (this.tickRateManager().runsNormally() && this.shouldTickBlocksAt(ticker.getPos()))
            {
                ticker.tick();
            }
        }

        if (write != size)
        {
            this.blockEntityTickers.subList(write, size).clear();
        }

        this.tickingBlockEntities = false;
    }
}
