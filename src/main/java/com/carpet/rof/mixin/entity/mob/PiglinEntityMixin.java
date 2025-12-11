package com.carpet.rof.mixin.entity.mob;

import com.carpet.rof.ROFCarpetSettings;
import com.carpet.rof.accessor.PiglinEntityAccessor;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;

import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? >=1.21.6 {
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
//?}
@Mixin(PiglinEntity.class)
public abstract class PiglinEntityMixin extends AbstractPiglinEntity implements PiglinEntityAccessor {

    public PiglinEntityMixin(EntityType<? extends AbstractPiglinEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    public int nearPiglinCount  = 0;

    @Override
    public int getNearPiglinCount(){
        return nearPiglinCount;
    }



    @Inject(method = "mobTick",at = @At(value = "HEAD"))
    //? >=1.21.4 {
    private void piglinTick(ServerWorld world, CallbackInfo ci) {
        if((this.age + this.getId()%801)%400==0 ){
            nearPiglinCount = world.getEntitiesByType(EntityType.PIGLIN,
                    new Box(this.
                            /*? >=1.21.10 {*/  /*getEntityPos() *//*?} else {*/  getPos() /*?}*/

                            .add(0.5, 0.5, 0.5), this.
                            /*? >=1.21.10 {*/  /*getEntityPos() *//*?} else {*/  getPos() /*?}*/

                            .add(-0.5, -0.5, -0.5)),
                    piglin -> true
            ).size();
        }
    }
    //?} else {
    /*private void piglinTick( CallbackInfo ci) {
        var world = this.getWorld();
        if ((this.age + this.getId() % 801) % 400 == 0) {
            nearPiglinCount = world.getEntitiesByType(EntityType.PIGLIN,
                    new Box(this.getPos().add(0.5, 0.5, 0.5), this.getPos().add(-0.5, -0.5, -0.5)),
                    piglin -> true
            ).size();
        }
    }
    *///?}

    //? >=1.21.6 {
    @Inject(method = "writeCustomData", at = @At(value = "HEAD"))
    private void writeCustomData(WriteView view, CallbackInfo ci) {
        if ( nearPiglinCount > ROFCarpetSettings.piglinMax) {
            view.putInt("nearPiglinCount", nearPiglinCount);
        }
    }

    @Inject(method = "readCustomData", at = @At(value = "HEAD"))
    private void readCustomData(ReadView view, CallbackInfo ci) {
        view.getOptionalInt("nearPiglinCount").ifPresent(integer -> nearPiglinCount = integer);
    }
    //?} else {

    /*@Inject(method = "readCustomDataFromNbt",at = @At(value = "HEAD"))
    private void readCustomDataFromNbt(NbtCompound tag, CallbackInfo ci) {
        if (nearPiglinCount > ROFCarpetSettings.piglinMax) {
            tag.putInt("nearPiglinCount", nearPiglinCount);
        }
    }

    @Inject(method = "writeCustomDataToNbt",at = @At(value = "HEAD"))
    private void writeCustomDataToNbt(NbtCompound tag, CallbackInfo ci) {
        if (tag.contains("nearPiglinCount")) {
            nearPiglinCount= tag.getInt("nearPiglinCount");
        }
    }
    *///?}


}
