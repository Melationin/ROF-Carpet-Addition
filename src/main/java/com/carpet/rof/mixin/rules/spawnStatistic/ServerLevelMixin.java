package com.carpet.rof.mixin.rules.spawnStatistic;

import com.carpet.rof.annotation.PublicField;
import com.carpet.rof.mixinAccessor.ServerLevelAccessor;
import com.carpet.rof.rules.spawnStatistic.SpawnStatisticSimplifySettings;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin implements ServerLevelAccessor
{
    @PublicField
    @Unique
    private boolean spawnStatisticSimplified = false;

    @Override
    public boolean rof$getSpawnStatisticSimplified()
    {
        return this.spawnStatisticSimplified;
    }

    @Override
    public void rof$setSpawnStatisticSimplified(boolean value)
    {
        this.spawnStatisticSimplified = value;
    }


    @Inject(method = "<init>",
            at = @At("TAIL"))
    private void rof$initSpawnStatisticSimplified(CallbackInfo ci)
    {
        this.spawnStatisticSimplified = SpawnStatisticSimplifySettings.isSimplified((ServerLevel) (Object) this);
    }
}
