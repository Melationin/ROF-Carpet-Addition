package com.carpet.rof.mixinAccessor;

import carpet.api.settings.Validator;

import java.util.List;

public interface ParsedRuleAccessor
{
    static ParsedRuleAccessor of(Object object)
    {
        return (ParsedRuleAccessor) object;
    }

    List<? extends Validator<?>> rof$getValidators();
}

