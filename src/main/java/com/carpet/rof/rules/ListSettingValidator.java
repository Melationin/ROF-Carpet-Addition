package com.carpet.rof.rules;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.Validator;
import net.minecraft.commands.CommandSourceStack;

import java.util.ArrayList;
import java.util.List;


public abstract class ListSettingValidator<E> extends Validator<String> {

    public static final String SEPARATOR = ",";

    public static final char NEGATE_PREFIX = '!';

    protected abstract E parseEntry(String entry);

    protected abstract void accept(List<E> allowed, List<E> denied);

    protected abstract String entryDescription();

    @Override
    public String validate(CommandSourceStack source, CarpetRule<String> rule, String newValue, String userInput) {
        List<E> allowed = new ArrayList<>();
        List<E> denied = new ArrayList<>();
        String value = newValue == null ? "" : newValue;
        for (String raw : value.split(SEPARATOR)) {
            String token = raw.trim();
            if (token.isEmpty()) {
                continue;
            }
            boolean negated = token.charAt(0) == NEGATE_PREFIX;
            String entry = negated ? token.substring(1).trim() : token;
            if (entry.isEmpty()) {
                return null;
            }
            E parsed = this.parseEntry(entry);
            if (parsed == null) {
                return null;
            }
            (negated ? denied : allowed).add(parsed);
        }
        this.accept(List.copyOf(allowed), List.copyOf(denied));
        return newValue;
    }

    @Override
    public String description() {
        return "逗号分隔的条目列表，条目为" + this.entryDescription() + "；前缀 " + NEGATE_PREFIX + " 表示排除该项。";
    }
}
