package com.carpet.rof.mixin.entity.enderPearl;

import com.carpet.rof.entity.enderPearl.EnderPearlSettings;
import com.carpet.rof.entity.enderPearl.PearlEntitySectionCache;
import com.carpet.rof.mixinAccessor.PearlEntitySectionAccess;
import com.carpet.rof.world.extraWorldData.ExtraWorldDatas;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntitySection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/*
 * 珍珠实体拦截优化：增量维护“含拦截实体（盔甲架）的 EntitySection”缓存。
 * sectionKey 由 PearlEntitySectionStorageMixin 在创建 section 时写入。
 * 实体遍历使用 EntitySection#getEntities()（public Stream），
 * 实体存储通过 access widener 暴露的 ServerLevel#entityManager.sectionStorage 取得。
 */
@Mixin(EntitySection.class)
public abstract class PearlEntitySectionMixin<T extends EntityAccess> implements PearlEntitySectionAccess {
    @Unique
    private long rof$sectionKey = Long.MIN_VALUE;

    @Override
    public void rof$setSectionKey(long sectionKey) {
        this.rof$sectionKey = sectionKey;
    }

    @Inject(method = "add(Lnet/minecraft/world/level/entity/EntityAccess;)V", at = @At("RETURN"))
    private void rof$onEntityAdded(T entityLike, CallbackInfo ci) {
        if (!EnderPearlSettings.optimizedEnderPearlEntityInterception
                || this.rof$sectionKey == Long.MIN_VALUE
                || !(entityLike instanceof Entity entity)
                || !PearlEntitySectionCache.isTargetEntity(entity)
                || !(entity.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        ExtraWorldDatas.fromWorld(serverLevel).pearlEntitySectionCache.addSection(this.rof$sectionKey);
    }

    @Inject(method = "remove(Lnet/minecraft/world/level/entity/EntityAccess;)Z", at = @At("RETURN"))
    private void rof$onEntityRemoved(T entityLike, CallbackInfoReturnable<Boolean> cir) {
        // UNLOADED_TO_CHUNK 表示实体随区块卸载，存档里仍然存在，不能从缓存中移除。
        if (!Boolean.TRUE.equals(cir.getReturnValue())
                || !EnderPearlSettings.optimizedEnderPearlEntityInterception
                || this.rof$sectionKey == Long.MIN_VALUE
                || !(entityLike instanceof ArmorStand armorStand)
                || !(armorStand.level() instanceof ServerLevel serverLevel)
                || armorStand.getRemovalReason() == Entity.RemovalReason.UNLOADED_TO_CHUNK) {
            return;
        }
        EntitySection<?> section = (EntitySection<?>) (Object) this;
        if (!PearlEntitySectionCache.hasTargetEntity(section)) {
            ExtraWorldDatas.fromWorld(serverLevel).pearlEntitySectionCache.removeSection(this.rof$sectionKey);
        }
    }
}
