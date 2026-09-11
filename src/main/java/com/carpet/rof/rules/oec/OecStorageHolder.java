package com.carpet.rof.rules.oec;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntitySectionStorage;
import org.jetbrains.annotations.Nullable;

/** 由 ServerLevel（转交 LevelEntityGetterAdapter）实现，用于取得带 section 索引的实体存储。 */
public interface OecStorageHolder {
    @Nullable EntitySectionStorage<?> rof$entitySectionStorage();

    static @Nullable EntitySectionStorage<?> storageOf(Level level) {
        return level instanceof OecStorageHolder holder ? holder.rof$entitySectionStorage() : null;
    }
}
