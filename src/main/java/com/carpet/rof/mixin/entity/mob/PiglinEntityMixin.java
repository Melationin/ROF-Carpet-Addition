package com.carpet.rof.mixin.entity.mob;

import com.carpet.rof.accessor.PiglinEntityAccessor;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PiglinEntity.class)
public abstract class PiglinEntityMixin extends AbstractPiglinEntity implements PiglinEntityAccessor {

    public PiglinEntityMixin(EntityType<? extends AbstractPiglinEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    public int NearPiglinCount  = 0;

    @Override
    public int getNearPiglinCount(){
        return NearPiglinCount;
    }



    @Inject(method = "mobTick",at = @At(value = "HEAD"))
    //? >=1.21.4 {
    private void piglinTick(ServerWorld world, CallbackInfo ci) {
        if((this.age + this.getId()%801)%400==0 ){
            NearPiglinCount = world.getEntitiesByType(EntityType.PIGLIN,
                    new Box(this.getPos().add(0.5, 0.5, 0.5), this.getPos().add(-0.5, -0.5, -0.5)),
                    piglin -> true
            ).size();
        }
    }
    //?} else {
    /*private void piglinTick( CallbackInfo ci) {
        var world = this.getWorld();
        if ((this.age + this.getId() % 801) % 400 == 0) {
            NearPiglinCount = world.getEntitiesByType(EntityType.PIGLIN,
                    new Box(this.getPos().add(0.5, 0.5, 0.5), this.getPos().add(-0.5, -0.5, -0.5)),
                    piglin -> true
            ).size();
        }
    }
    *///?}


}
