package com.carpet.rof.mixin.entity.merge;

import com.carpet.rof.annotation.PublicField;
import com.carpet.rof.entity.merge.MergeSetting;
import com.carpet.rof.mixinAccessor.PrimedTntAccessor;
import com.carpet.rof.utils.NBTHelper;
import com.carpet.rof.world.explosion.ExplosionMergeData;
import com.carpet.rof.world.extraWorldData.ExtraWorldDatas;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.carpet.rof.entity.merge.MergeSetting.mergeTNTNext;

@Mixin(PrimedTnt.class)
public abstract class PrimedTntMixin extends Entity implements PrimedTntAccessor
{
    @PublicField
    @Unique
    private int mergedTNTNCount = 1;

    @Shadow
    public abstract int getFuse();

    @Shadow
    private boolean usedPortal;

    @Shadow
    @Final
    public static ExplosionDamageCalculator USED_PORTAL_DAMAGE_CALCULATOR;

    @Shadow
    private float explosionPower;

    public PrimedTntMixin(EntityType<?> type, Level world)
    {
        super(type, world);
    }

    @Override
    public void rof$addMergeCount(int mergeCount)
    {
        this.mergedTNTNCount += mergeCount;
    }

    @Override
    public int rof$getMergedTNTNCount()
    {
        return this.mergedTNTNCount;
    }

    @Override
    public void rof$setMergedTNTNCount(int value)
    {
        this.mergedTNTNCount = value;
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/PrimedTnt;getFuse()I"), cancellable = true)
    private void merge(CallbackInfo ci)
    {
        if (!mergeTNTNext.mergeDuringCountdown() || this.isRemoved() || this.getFuse() < 2) return;
        if (!(this.level() instanceof ServerLevel world)) return;

        PrimedTnt self = (PrimedTnt) (Object) this;
        PrimedTnt leader = ExtraWorldDatas.fromWorld(world).mergeTntMap.putIfAbsent(MergeSetting.EntityPosAndVec.of(self), self);
        if (leader != null)
        {
            PrimedTntAccessor.of(leader).rof$addMergeCount(this.mergedTNTNCount);
            this.discard();
            ci.cancel();
        }
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void tick(CallbackInfo ci)
    {
        if (!mergeTNTNext.simulateMovementOnExplode() || this.isRemoved() || this.getFuse() != 1 || this.mergedTNTNCount < 2) return;
        if (!(this.level() instanceof ServerLevel level)) return;

        this.rof$explodeMergedWithMovement(level);
        ci.cancel();
    }

    @Unique
    private void rof$explodeMergedWithMovement(ServerLevel level)
    {
        this.handlePortal();

        double originX = this.getX();
        double originY = this.getY();
        double originZ = this.getZ();
        ExplosionMergeData data = ExtraWorldDatas.fromWorld(level).explosionMergeData;
        Vec3 lastExplosionPos = null;

        for (int i = 0; i < this.mergedTNTNCount; i++)
        {
            Vec3 velocity = this.getDeltaMovement();
            this.applyGravity();
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.applyEffectsFromBlocks();
            Vec3 explosionPos = new Vec3(this.getX(), this.getY(), this.getZ());
            this.setDeltaMovement(velocity);
            this.setPos(originX, originY, originZ);

            data.requestMerge(explosionPos.equals(lastExplosionPos) ? this.mergedTNTNCount - i : 1);
            lastExplosionPos = explosionPos;
            this.explode2(explosionPos.x, explosionPos.y, explosionPos.z);
            i += data.mergedCount() - 1;
        }
        this.discard();
    }

    // 1.21.10 仍暴露旧版 GameRules API（RULE_* 常量 + getBoolean）；1.21.11
    // 换成了类型化的 GameRule 值，因此只有这里的一处查询需要按版本区分。
    @Unique
    private static boolean rof$tntExplodes(Level level)
    {
        if (!(level instanceof ServerLevel serverLevel))
        {
            return false;
        }
        //? if >=1.21.11 {
        return serverLevel.getGameRules().get(GameRules.TNT_EXPLODES);
        //?} else {
        /*return serverLevel.getGameRules().getBoolean(GameRules.RULE_TNT_EXPLODES);
        *///?}
    }

    @Unique
    private void explode2(double x, double y, double z)
    {
        if (!rof$tntExplodes(this.level())) return;
        this.level().explode(
                null,
                Explosion.getDefaultDamageSource(this.level(), this),
                this.usedPortal ? USED_PORTAL_DAMAGE_CALCULATOR : null,
                x,
                y + this.getBbHeight() * 0.0625,
                z,
                this.explosionPower,
                false,
                Level.ExplosionInteraction.TNT);
    }

    @Inject(method = "explode", at = @At("HEAD"), cancellable = true)
    private void onExplode(CallbackInfo ci)
    {
        if (this.mergedTNTNCount <= 0)
        {
            ci.cancel();
            return;
        }
        if (this.mergedTNTNCount == 1 || !(this.level() instanceof ServerLevel level)) return;

        ExplosionMergeData data = ExtraWorldDatas.fromWorld(level).explosionMergeData;
        for (int i = 0; i < this.mergedTNTNCount - 1; i++)
        {
            data.requestMerge(this.mergedTNTNCount - i);
            level.explode(this, this.getX(), this.getY(0.0625), this.getZ(), this.explosionPower, Level.ExplosionInteraction.TNT);
            if (data.mergedCount() > 1)
            {
                ci.cancel();
                return;
            }
        }
        data.resetMerge();
    }

    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    private void writeCustomData(@Coerce Object output, CallbackInfo ci)
    {
        if (this.mergedTNTNCount > 1)
        {
            NBTHelper.putInt(output, "mergedTNT", this.mergedTNTNCount);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    private void readCustomData(@Coerce Object input, CallbackInfo ci)
    {
        this.mergedTNTNCount = NBTHelper.getIntOr(input, "mergedTNT", this.mergedTNTNCount);
    }
}
