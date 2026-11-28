package com.carpet.rof.mixin.entity.enderPearl;

import com.carpet.rof.mixinAccessor.PearlEntitySectionAccess;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntitySection;
import net.minecraft.world.level.entity.EntitySectionStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/*
 * 珍珠实体拦截优化：给每个新建的 EntitySection 记录它自己的 sectionKey。
 * remove 钩子需要精确的 sectionKey，不能用实体坐标推导：
 * Entity#setPosRaw -> levelCallback.onMove() 会先 remove 旧 section 再 add 新区段，
 * 此时实体坐标已经在新区段里，推导会拿到错误的 key。
 */
@Mixin(EntitySectionStorage.class)
public abstract class PearlEntitySectionStorageMixin<T extends EntityAccess> {
    @Inject(method = "createSection(J)Lnet/minecraft/world/level/entity/EntitySection;", at = @At("RETURN"))
    private void rof$rememberSectionKey(long sectionKey, CallbackInfoReturnable<EntitySection<T>> cir) {
        if (cir.getReturnValue() instanceof PearlEntitySectionAccess access) {
            access.rof$setSectionKey(sectionKey);
        }
    }
}
