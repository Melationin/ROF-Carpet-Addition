package com.carpet.rof.mixin.rules.oec;

import com.carpet.rof.rules.oec.OecSettings;
import com.carpet.rof.rules.oec.OecStorageHolder;
import com.carpet.rof.rules.oec.OecUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntitySectionStorage;
import net.minecraft.world.level.entity.LevelEntityGetter;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin implements OecStorageHolder {
    @Shadow protected abstract LevelEntityGetter<Entity> getEntities();

    @Override
    public @Nullable EntitySectionStorage<?> rof$entitySectionStorage() {
        return this.getEntities() instanceof OecStorageHolder holder ? holder.rof$entitySectionStorage() : null;
    }

    @Inject(method = "tick(Ljava/util/function/BooleanSupplier;)V", at = @At("HEAD"))
    private void rof$rebuildPushGrids(BooleanSupplier haveTime, CallbackInfo ci) {
        if (!OecSettings.optimizedEntityCollection) return;
        OecUtil.rebuild((ServerLevel) (Object) this);
    }
}
