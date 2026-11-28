package com.carpet.rof.mixin.entity.collision;

import com.carpet.rof.entity.collision.CollisionBlockerRecorder;
import com.carpet.rof.entity.oec.OecSettings;
import com.carpet.rof.entity.collision.EntityCollisionCacheAccess;
import com.carpet.rof.entity.collision.EntityCollisionHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(value = Entity.class, priority = 1100)
public abstract class EntityCollisionCacheMixin implements EntityCollisionCacheAccess {
	@Shadow
	private Level level;

	@Shadow
	public abstract AABB getBoundingBox();

	@Shadow
	public abstract float maxUpStep();

	@Shadow
	public abstract boolean onGround();

	@Unique
	private final long[] rof$blockers = {Long.MIN_VALUE, Long.MIN_VALUE, Long.MIN_VALUE};

	@Inject(
			method = "collide(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;",
			at = @At("HEAD"),
			cancellable = true
	)
	private void rof$fastCollide(Vec3 movement, CallbackInfoReturnable<Vec3> cir) {
		if (!OecSettings.optimizedEntityCollision) {
			return;
		}
		final Level lvl = this.level;
		if (lvl == null || lvl.isDebug()) {
			return;
		}
		final Vec3 fast = EntityCollisionHelper.collide(
				(Entity) (Object) this,
				this,
				lvl,
				this.getBoundingBox(),
				movement,
				this.maxUpStep(),
				this.onGround()
		);
		if (fast != null) {
			cir.setReturnValue(fast);
			return;
		}
		CollisionBlockerRecorder.begin(this);
	}

	@Inject(
			method = "collide(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;",
			at = @At("RETURN")
	)
	private void rof$learnBlockers(Vec3 movement, CallbackInfoReturnable<Vec3> cir) {
		final EntityCollisionCacheAccess cache = CollisionBlockerRecorder.end();
		if (cache != null) {
			CollisionBlockerRecorder.flush(cache);
		}
	}

	@Override
	public long[] rof$blockerSlots() {
		return this.rof$blockers;
	}
}
