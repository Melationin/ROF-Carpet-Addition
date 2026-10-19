package com.carpet.rof.mixin.carpet.fakePlayerTick;

import com.carpet.rof.carpet.fakePlayerTick.FakePlayerTickUtil;
//? >=26.2 {
/*import net.minecraft.advancements.predicates.entity.EntityPredicate;
 *///?}else{
import net.minecraft.advancements.criterion.EntityPredicate;
//?}
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPredicate.class)
public abstract class EntityPredicateMixin
{
    @Inject(method = "createContext", at = @At("HEAD"), cancellable = true)
    private static void rof$skipFakePlayerContext(ServerPlayer player, Entity entity, CallbackInfoReturnable<LootContext> cir)
    {
        if (FakePlayerTickUtil.isOptimized(player))
        {
            cir.setReturnValue(null);
        }
    }
}
