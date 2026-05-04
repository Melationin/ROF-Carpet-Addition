package com.carpet.rof.mixin.rules.piglinRules;

import com.carpet.rof.rules.piglinRules.PiglinEntityAccessor;
import com.carpet.rof.utils.ROFWarp;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.server.level.ServerLevel;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? >=1.21.6 {
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
//?} else {
/*import net.minecraft.nbt.NbtCompound;
 *///?}

import static com.carpet.rof.rules.piglinRules.PiglinRulesSettings.piglinStackingAISuppression;

@Mixin(Piglin.class)
public abstract class PiglinMixin extends AbstractPiglin implements PiglinEntityAccessor
{

    @Unique public int nearPiglinCount = 0;

    public PiglinMixin(EntityType<? extends AbstractPiglin> entityType, Level world)
    {
        super(entityType, world);
    }

    @Override
    public int getNearPiglinCount()
    {
        return nearPiglinCount;
    }


    @Inject(method = "customServerAiStep", at = @At(value = "HEAD"))
            //? >=1.21.5 {
    private void piglinTick(ServerLevel world, CallbackInfo ci)
    {
        if ((this.tickCount + this.getId() % 801) % 400 == 0) {
            nearPiglinCount = world.getEntities(EntityType.PIGLIN,
                    new AABB(ROFWarp.getPos_(this).add(0.5, 0.5, 0.5),ROFWarp.getPos_(this).add(-0.5, -0.5, -0.5)),
                    piglin -> true).size();
        }
    }
    //?} else {
    /*private void piglinTick( CallbackInfo ci) {
        var world = this.getWorld();
        if ((this.age + this.getId() % 801) % 400 == 0) {
            nearPiglinCount = world.getEntitiesByType(EntityType.PIGLIN,
                    new Box(this.getPos().add(0.5, 0.5, 0.5), this.getPos().add(-0.5, -0.5, -0.5)),
                    piglinRules -> true
            ).size();
        }
    }
    *///?}

    //? >=1.21.6 {
    @Inject(method = "addAdditionalSaveData", at = @At(value = "HEAD"))
    private void writeCustomData(ValueOutput view, CallbackInfo ci)
    {
        if (nearPiglinCount > piglinStackingAISuppression) {
            view.putInt("nearPiglinCount", nearPiglinCount);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At(value = "HEAD"))
    private void readCustomData(ValueInput view, CallbackInfo ci)
    {
        view.getInt("nearPiglinCount").ifPresent(integer -> nearPiglinCount = integer);
    }
    //?} else {

    /*@Inject(method = "readCustomDataFromNbt",at = @At(value = "HEAD"))
    private void readCustomDataFromNbt(NbtCompound tag, CallbackInfo ci) {
        if (nearPiglinCount > piglinStackingAISuppression) {
            tag.putInt("nearPiglinCount", nearPiglinCount);
        }
    }

    @Inject(method = "writeCustomDataToNbt",at = @At(value = "HEAD"))
    private void writeCustomDataToNbt(NbtCompound tag, CallbackInfo ci) {
        if (tag.contains("nearPiglinCount")) {
            nearPiglinCount= ROFWarp.getFromNbt(tag.getInt("nearPiglinCount"));
        }
    }
    *///?}


}
