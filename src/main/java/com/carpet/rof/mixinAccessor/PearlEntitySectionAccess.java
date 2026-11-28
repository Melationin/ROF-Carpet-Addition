package com.carpet.rof.mixinAccessor;

/*
 * 珍珠实体拦截优化专用：让 EntitySection 记住自己所属的 sectionKey。
 * 由 mixin.entity.enderPearl.PearlEntitySectionStorageMixin 在
 * EntitySectionStorage.createSection 时写入。
 */
public interface PearlEntitySectionAccess {
    void rof$setSectionKey(long sectionKey);
}
