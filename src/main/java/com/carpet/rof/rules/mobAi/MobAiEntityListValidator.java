package com.carpet.rof.rules.mobAi;

import com.carpet.rof.rules.ListSettingValidator;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

import java.util.List;
import java.util.Optional;

/**
 * 解析「实体 ID / #实体类型标签（可用 ! 排除）」形式的实体列表。
 *
 * <p>标签只校验名称合法，不要求已加载：实体类型标签由数据包提供，
 * 未加载的标签在匹配时就是空集合（谁都不匹配），不会误伤。
 */
public final class MobAiEntityListValidator extends ListSettingValidator<MobAiFilter.Entry> {
    @Override
    protected MobAiFilter.Entry parseEntry(String entry) {
        if (entry.startsWith("#")) {
            Identifier tagId = Identifier.tryParse(entry.substring(1));
            return tagId == null ? null : MobAiFilter.Entry.ofTag(TagKey.create(Registries.ENTITY_TYPE, tagId));
        }
        Identifier id = Identifier.tryParse(entry);
        if (id == null) {
            return null;
        }
        Optional<EntityType<?>> type = BuiltInRegistries.ENTITY_TYPE.getOptional(id);
        return type.map(MobAiFilter.Entry::ofType).orElse(null);
    }

    @Override
    protected void accept(List<MobAiFilter.Entry> allowed, List<MobAiFilter.Entry> denied) {
        MobAiSettings.mobAiFilter = new MobAiFilter(allowed, denied);
    }

    @Override
    protected String entryDescription() {
        return "实体 ID（如 minecraft:pig）或实体类型标签（如 #zombies）";
    }
}
