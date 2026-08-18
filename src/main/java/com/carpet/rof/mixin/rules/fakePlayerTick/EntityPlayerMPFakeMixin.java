package com.carpet.rof.mixin.rules.fakePlayerTick;

import carpet.patches.EntityPlayerMPFake;
import com.carpet.rof.rules.fakePlayerTick.FakePlayerTickSettings;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerMPFake.class)
public abstract class EntityPlayerMPFakeMixin
{
    @Unique
    private boolean optimizedFakePlayerTick$waypointDisabled = false;

    @Inject(method = "tick", at = @At("HEAD"))
    private void optimizedFakePlayerTick$disableWaypoint(CallbackInfo ci)
    {
        if (FakePlayerTickSettings.optimizedFakePlayerTick && !optimizedFakePlayerTick$waypointDisabled)
        {
            optimizedFakePlayerTick$waypointDisabled = true;
            LivingEntity self = (LivingEntity) (Object) this;
            AttributeInstance transmit = self.getAttribute(Attributes.WAYPOINT_TRANSMIT_RANGE);
            if (transmit != null)
            {
                transmit.setBaseValue(0.0);
            }
            AttributeInstance receive = self.getAttribute(Attributes.WAYPOINT_RECEIVE_RANGE);
            if (receive != null)
            {
                receive.setBaseValue(0.0);
            }
        }
    }
}
