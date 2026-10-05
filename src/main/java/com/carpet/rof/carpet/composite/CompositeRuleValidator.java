package com.carpet.rof.carpet.composite;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.InvalidRuleValueException;
import carpet.api.settings.RuleHelper;
import carpet.api.settings.Validator;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

@SuppressWarnings("unchecked")
public final class CompositeRuleValidator extends Validator<String>
{
    private CarpetRule<String> parent;
    private final Map<String, CarpetRule<Object>> members = new TreeMap<>();

    void register(CarpetRule<String> rule, Collection<CarpetRule<?>> parsed)
    {
        parent = rule;
        for (CarpetRule<?> member : parsed) {
            if (!member.categories().contains(CompositeRuleManager.PARENT_PREFIX + rule.name())) continue;
            members.put(member.name(), (CarpetRule<Object>) member);
        }
        CompositeRuleManager.of(rule.settingsManager()).register(this);
    }

    public CarpetRule<String> parent()
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
    public String validate(CommandSourceStack source, CarpetRule<String> rule, String value, String input)
    {
        try {
            JsonElement parsed = JsonParser.parseString(value);
            if (!parsed.isJsonObject()) throw new IllegalArgumentException("复合规则必须是 JSON 对象 {...}");
            JsonObject json = parsed.getAsJsonObject();
            for (var entry : json.entrySet()) {
                if (!entry.getValue().isJsonPrimitive())
                    throw new IllegalArgumentException("子规则值必须为布尔、数字或字符串");
                member(entry.getKey()).set(source, entry.getValue().getAsString());
            }
            for (CarpetRule<Object> member : members.values()) {
                if (!json.has(member.name())) member.set(source, member.defaultValue());
            }
            return encode();
        } catch (InvalidRuleValueException | IllegalArgumentException | JsonParseException e) {
            if (source != null) source.sendFailure(Component.literal(e.getMessage() == null
                    ? description() : e.getMessage()));
            return null;
        }
    }

    @Override
    public String description()
    {
        return "复合规则必须是 JSON 对象 {...}，且所有子规则值都通过校验";
    }

    public int saveMember(CommandDispatcher<CommandSourceStack> dispatcher, CommandSourceStack source,
                          String name, String input) throws CommandSyntaxException
    {
        JsonObject json = JsonParser.parseString(encode()).getAsJsonObject();
        json.addProperty(name, input);
        return dispatcher.execute(parent.settingsManager().identifier() + " setDefault " + parent.name()
                + " " + json, source);
    }

    private String encode()
    {
        JsonObject json = new JsonObject();
        boolean defaults = true;
        for (var entry : members.entrySet()) {
            Object value = entry.getValue().value();
            defaults &= value.equals(entry.getValue().defaultValue());
            if (value instanceof Boolean bool) json.add(entry.getKey(), new JsonPrimitive(bool));
            else if (value instanceof Number number) json.add(entry.getKey(), new JsonPrimitive(number));
            else json.addProperty(entry.getKey(), RuleHelper.toRuleString(value));
        }
        return defaults ? "{}" : json.toString();
    }
}
