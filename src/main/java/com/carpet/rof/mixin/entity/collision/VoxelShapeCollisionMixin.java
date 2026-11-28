package com.carpet.rof.mixin.entity.collision;

import com.carpet.rof.entity.collision.CollisionBlockerRecorder;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/*
 * 记录原版或 Lithium 执行碰撞扫描时，是哪个形状产生了吸收性的 0.0 夹取。
 * 移动后的形状仍位于单个格子内，所以可以从形状边界反推出方块坐标。
 */
@Mixin(VoxelShape.class)
public abstract class VoxelShapeCollisionMixin {
	@Inject(
			method = "collide(Lnet/minecraft/core/Direction$Axis;Lnet/minecraft/world/phys/AABB;D)D",
			at = @At("RETURN")
	)
	private void rof$recordBlockClamp(
			Direction.Axis axis,
			AABB box,
			double dist,
			CallbackInfoReturnable<Double> cir
	) {
		if (!CollisionBlockerRecorder.isActive()) {
			return;
		}
		if (Math.abs(cir.getReturnValueD()) >= 1.0E-7) {
			return;
		}
		CollisionBlockerRecorder.record(axis, (VoxelShape) (Object) this);
	}
}