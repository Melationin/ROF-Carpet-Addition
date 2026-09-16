package com.carpet.rof.mixin.rules.spawnStatistic;

import com.carpet.rof.rules.spawnStatistic.SpawnStatisticSimplifyAccess;
import com.carpet.rof.rules.spawnStatistic.SpawnStatisticSimplifySettings;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin implements SpawnStatisticSimplifyAccess
{
    @Unique
    private boolean rof$spawnStatisticSimplified = false;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void rof$initSpawnStatisticSimplified(CallbackInfo ci)
    {
        this.rof$spawnStatisticSimplified = SpawnStatisticSimplifySettings.isSimplified((ServerLevel)(Object)this);
    }

    @Override
    public boolean rof$isSpawnStatisticSimplified()
    {
        return this.rof$spawnStatisticSimplified;
    }

    @Override
    public void rof$setSpawnStatisticSimplified(boolean simplified)
    {
        this.rof$spawnStatisticSimplified = simplified;
    }
}
