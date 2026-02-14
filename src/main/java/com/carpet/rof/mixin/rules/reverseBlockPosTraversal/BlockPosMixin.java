package com.carpet.rof.mixin.rules.reverseBlockPosTraversal;

import com.google.common.collect.AbstractIterator;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.carpet.rof.rules.netherPortalFix.reverseBlockPosTraversal.reverseBlockPosTraversal;
import static net.minecraft.util.math.BlockPos.iterate;
import static net.minecraft.util.math.BlockPos.ofFloored;

@Mixin(BlockPos.class)
public class BlockPosMixin
{
    @Inject(method = "iterate(IIIIII)Ljava/lang/Iterable;",
            at = @At(value = "HEAD"),
            cancellable = true)
    private static void s(int startX, int startY, int startZ, int endX, int endY, int endZ, CallbackInfoReturnable<Iterable<BlockPos>> cir)
    {
        if (!reverseBlockPosTraversal)
            return;
        int i = endX - startX + 1;
        int j = endY - startY + 1;
        int k = endZ - startZ + 1;
        int l = i * j * k;
        cir.setReturnValue(() -> new AbstractIterator<BlockPos>()
        {
            private final BlockPos.Mutable pos = new BlockPos.Mutable();
            private int index;

            protected BlockPos computeNext()
            {
                if (this.index == l) {
                    return this.endOfData();
                } else {
                    int ix = this.index % i;
                    int jx = this.index / i;
                    int kx = jx % j;   // Y 偏移
                    int lx = jx / j;   // Z 偏移
                    this.index++;
                    return this.pos.set(startX + ix, endY - kx, startZ + lx);
                }
            }
        });
    }

}
