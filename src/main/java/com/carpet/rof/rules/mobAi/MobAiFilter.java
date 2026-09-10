package com.carpet.rof.rules.mobAi;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.List;

// 空列表不匹配；只有排除项时匹配其余所有实体。
public final class MobAiFilter
{
    public static final MobAiFilter EMPTY = new MobAiFilter(List.of(), List.of());

    private final List<Entry> allowed;
    private final List<Entry> denied;

    public MobAiFilter(List<Entry> allowed, List<Entry> denied)
    {
        this.allowed = allowed;
        this.denied = denied;
    }

    public boolean isEmpty()
    {
        return this.allowed.isEmpty() && this.denied.isEmpty();
    }

    public boolean matches(Entity entity)
    {
        if (this.isEmpty()) {
            return false;
        }
        if (!this.allowed.isEmpty() && !matchesAny(this.allowed, entity)) {
            return false;
        }
        return !matchesAny(this.denied, entity);
    }

    private static boolean matchesAny(List<Entry> entries, Entity entity)
    {
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).matches(entity)) {
                return true;
            }
        }
        return false;
    }

    public List<Entry> allowedEntries()
    {
        return this.allowed;
    }

    public List<Entry> deniedEntries()
    {
        return this.denied;
    }

    public record Entry(EntityType<?> type, TagKey<EntityType<?>> tag)
    {
        public static Entry ofType(EntityType<?> type)
        {
            return new Entry(type, null);
        }

        public static Entry ofTag(TagKey<EntityType<?>> tag)
        {
            return new Entry(null, tag);
        }

        public boolean matches(Entity entity)
        {
            return this.type != null ? entity.getType() == this.type : entity.is(this.tag);
        }
    }
}
