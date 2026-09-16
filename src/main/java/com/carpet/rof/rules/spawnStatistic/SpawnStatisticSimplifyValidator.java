package com.carpet.rof.rules.spawnStatistic;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.Validator;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.Set;

public final class SpawnStatisticSimplifyValidator extends Validator<String>
{
    @Override
    public String validate(CommandSourceStack source, CarpetRule<String> rule, String newValue, String userInput)
    {
        String value = newValue == null ? "" : newValue.trim();
        if (value.length() >= 2 && value.startsWith("{") && value.endsWith("}")) {
            value = value.substring(1, value.length() - 1);
        }
        Set<ResourceKey<Level>> worlds = new HashSet<>();
        for (String entry : value.split(",")) {
            String id = entry.trim();
            if (id.isEmpty()) {
                continue;
            }
            Identifier identifier = Identifier.tryParse(id);
            if (identifier == null) {
                return null;
            }
            worlds.add(ResourceKey.create(Registries.DIMENSION, identifier));
        }
        SpawnStatisticSimplifySettings.apply(Set.copyOf(worlds));
        return newValue;
    }

    @Override
    public String description()
    {
        return "花括号包裹、逗号分隔的维度 ID 列表，如 {minecraft:overworld}；{} 表示任何世界都不化简";
    }
}
