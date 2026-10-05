package com.carpet.rof.mixinAccessor;

import net.minecraft.world.level.entity.EntitySectionStorage;
import org.jetbrains.annotations.Nullable;

// 由 ServerLevel（转交 LevelEntityGetterAdapter）实现，用于取得带 section 索引的实体存储。
public interface OecStorageHolder {
    @Nullable EntitySectionStorage<?> rof$entitySectionStorage();
}
