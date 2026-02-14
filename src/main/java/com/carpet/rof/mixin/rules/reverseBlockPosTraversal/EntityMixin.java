package com.carpet.rof.mixin.rules.reverseBlockPosTraversal;

import net.minecraft.entity.Entity;

import org.spongepowered.asm.mixin.Mixin;


@Mixin(Entity.class)
public abstract class EntityMixin
{
/*
    @Shadow
    public abstract void setVelocity(Vec3d velocity);

    @Inject(method = "readData",
            at = @At(value = "TAIL"))
    private void readData(ReadView view, CallbackInfo ci)
    {
        Vec3d vec3d2 = (Vec3d) view.read("Motion", Vec3d.CODEC).orElse(Vec3d.ZERO);
        this.setVelocity(vec3d2);
    }

 */
}
