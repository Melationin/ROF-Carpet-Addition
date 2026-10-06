package com.carpet.rof.carpet.composite;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.InvalidRuleValueException;
import carpet.api.settings.RuleHelper;
import carpet.api.settings.Validator;
import com.carpet.rof.utils.ROFConfig;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

@SuppressWarnings("unchecked")
public final class CompositeRuleValidator extends Validator<Boolean>
{
    private CarpetRule<Boolean> parent;
    private final Map<String, CarpetRule<Object>> members = new TreeMap<>();

    void register(CarpetRule<Boolean> rule, Collection<CarpetRule<?>> parsed)
    {
        parent = rule;
        for (CarpetRule<?> member : parsed) {
            if (!member.categories().contains(CompositeRuleManager.PARENT_PREFIX + rule.name())) continue;
            members.put(member.name(), (CarpetRule<Object>) member);
        }
        CompositeRuleManager.of(rule.settingsManager()).register(this);
    }

    public CarpetRule<Boolean> parent()
    {
        return parent;
    }

    public Collection<String> memberNames()
    {
        return members.keySet();
    }

    public CarpetRule<?> member(String name)
    {
        var rule = members.get(name);
        if (rule == null) throw new IllegalArgumentException("未知子规则: " + name);
        return rule;
    }

    @Override
    public Boolean validate(CommandSourceStack source, CarpetRule<Boolean> rule, Boolean value, String input)
    {
        return value;
    }

    void load(JsonObject json)
    {
        for (CarpetRule<Object> member : members.values()) {
            try {
                member.set(null, member.defaultValue());
                if (json.has(member.name())) {
                    if (!json.get(member.name()).isJsonPrimitive())
                        throw new IllegalArgumentException("子规则值必须为布尔、数字或字符串");
                    member.set(null, json.get(member.name()).getAsString());
                }
            } catch (InvalidRuleValueException | IllegalArgumentException | IllegalStateException e) {
                LogUtils.getLogger().warn("[ROFConfig] Invalid subrule {}.{}", parent.name(), member.name(), e);
            }
        }
    }

    public int saveMember(CommandSourceStack source, String name, String input)
    {
        if (ROFConfig.INSTANCE == null) {
            source.sendFailure(Component.literal("世界配置尚未加载"));
            return 0;
        }
        try {
            member(name).set(source, input);
        } catch (InvalidRuleValueException e) {
            e.notifySource(name, source);
            return 0;
        }
        if (!CompositeRuleManager.of(parent.settingsManager()).save(ROFConfig.INSTANCE)) {
            source.sendFailure(Component.literal("子规则已修改，但保存 carpet-rof-addition.json 失败，请查看服务器日志"));
            return 0;
        }
        source.sendSuccess(() -> Component.literal(parent.name() + "." + name + " = "
                + RuleHelper.toRuleString(member(name).value()) + "（已保存）"), false);
        return 1;
    }

    void write(JsonObject json)
    {
        for (var entry : members.entrySet()) {
            Object value = entry.getValue().value();
            if (value instanceof Boolean bool) json.addProperty(entry.getKey(), bool);
            else if (value instanceof Number number) json.addProperty(entry.getKey(), number);
            else json.addProperty(entry.getKey(), RuleHelper.toRuleString(value));
        }
    }
}
