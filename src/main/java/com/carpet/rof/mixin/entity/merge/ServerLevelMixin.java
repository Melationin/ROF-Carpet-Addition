package com.carpet.rof.mixin.entity.merge;

import com.carpet.rof.entity.merge.MergeSetting;
import com.carpet.rof.mixinAccessor.PrimedTntAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.entity.EntityTickList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

import static com.carpet.rof.entity.merge.MergeSetting.MergeTNTNextMode.ALMOST_VANILLA;
import static com.carpet.rof.entity.merge.MergeSetting.MergeTNTNextMode.SAFE_PLUS;
import static com.carpet.rof.entity.merge.MergeSetting.mergeTNTNext;

@Mixin(ServerLevel.class)
public class ServerLevelMixin
{
    @Shadow
    @Final
    private EntityTickList entityTickList;

    @Inject(method = "tick",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/world/level/entity/EntityTickList;forEach(Ljava/util/function/Consumer;)V"))
    private void onTick(final BooleanSupplier haveTime, final CallbackInfo ci)
    {
        if (mergeTNTNext == ALMOST_VANILLA)
        {
            this.rof$preMergeAdjacentTnt(true);
        }
        else if (mergeTNTNext == SAFE_PLUS)
        {
            this.rof$preMergeAdjacentTnt(false);
        }
    }

    @Unique
    private void rof$preMergeAdjacentTnt(boolean onlyAtExplode)
    {
        PrimedTnt[] leader = new PrimedTnt[1];
        MergeSetting.EntityPosAndVec[] leaderKey = new MergeSetting.EntityPosAndVec[1];
        this.entityTickList.forEach(entity -> {
            if (!this.rof$willTick(entity)) return;
            if (!(entity instanceof PrimedTnt tnt) || (onlyAtExplode && tnt.getFuse() != 1))
            {
                leader[0] = null;
                return;
            }
            MergeSetting.EntityPosAndVec key = MergeSetting.EntityPosAndVec.of(tnt);
            if (leader[0] != null && key.equals(leaderKey[0]))
            {
                PrimedTntAccessor.of(leader[0]).rof$addMergeCount(PrimedTntAccessor.of(tnt).rof$getMergedTNTNCount());
                tnt.discard();
            }
            else
            {
                leader[0] = tnt;
                leaderKey[0] = key;
            }
        });
    }

    @Unique
    private boolean rof$willTick(Entity entity)
    {
        ServerLevel level = (ServerLevel) (Object) this;
        return !entity.isRemoved()
                && !level.tickRateManager().isEntityFrozen(entity)
                && (entity instanceof ServerPlayer || level.isPositionEntityTicking(entity.blockPosition()));
    }
}
