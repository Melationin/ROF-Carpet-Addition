package com.carpet.rof.carpet.composite;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.SettingsManager;
import com.carpet.rof.mixinAccessor.ParsedRuleAccessor;
import com.carpet.rof.mixinAccessor.SettingsManagerAccessor;
import com.carpet.rof.utils.ROFConfig;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@SuppressWarnings("unchecked")
public final class CompositeRuleManager
{
    public static final String PARENT_PREFIX = "SubRule:";

    private final Map<String, CompositeRuleValidator> groups = new TreeMap<>();
    private final List<CarpetRule<?>> pending = new ArrayList<>();

    public CompositeRuleManager() {}

    public static CompositeRuleManager of(SettingsManager manager)
    {
        return SettingsManagerAccessor.of(manager).rof$getCompositeRules();
    }

    public boolean register(CarpetRule<?> rule)
    {
        boolean member = rule.categories().stream().anyMatch(category -> category.startsWith(PARENT_PREFIX));
        if (!member && ParsedRuleAccessor.of(rule).rof$getValidators().stream()
                .noneMatch(CompositeRuleValidator.class::isInstance)) return false;
        pending.add(rule);
        return member;
    }

    public void finishRegistration()
    {
        try {
            for (CarpetRule<?> rule : pending) {
                if (rule.categories().stream().anyMatch(category -> category.startsWith(PARENT_PREFIX))) continue;
                for (var validator : ParsedRuleAccessor.of(rule).rof$getValidators()) {
                    if (validator instanceof CompositeRuleValidator composite)
                        composite.register((CarpetRule<Boolean>) rule, pending);
                }
            }
        } finally {
            pending.clear();
        }
    }

    void register(CompositeRuleValidator validator)
    {
        groups.put(validator.parent().name(), validator);
    }

    public List<CompositeRuleValidator> all()
    {
        return List.copyOf(groups.values());
    }

    public CompositeRuleValidator get(String name)
    {
        return groups.get(name);
    }

    public void load(ROFConfig config)
    {
        JsonObject json = config.get("ruleGroups", new JsonObject());
        for (var entry : groups.entrySet()) {
            var value = json.get(entry.getKey());
            entry.getValue().load(value != null && value.isJsonObject() ? value.getAsJsonObject() : new JsonObject());
        }
        save(config);
    }

    public boolean save(ROFConfig config)
    {
        // 子规则仅保存到 JSON；Carpet 配置只保存各组的布尔总开关，不迁移旧的 JSON 规则值。
        JsonObject json = config.get("ruleGroups", new JsonObject()).deepCopy();
        for (var entry : groups.entrySet()) {
            var value = json.get(entry.getKey());
            JsonObject members = value != null && value.isJsonObject() ? value.getAsJsonObject() : new JsonObject();
            entry.getValue().write(members);
            json.add(entry.getKey(), members);
        }
        config.set("ruleGroups", json);
        return config.save();
    }
}
