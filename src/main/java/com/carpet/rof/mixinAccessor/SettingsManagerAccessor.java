package com.carpet.rof.mixinAccessor;

import com.carpet.rof.carpet.composite.CompositeRuleManager;

public interface SettingsManagerAccessor
{
    static SettingsManagerAccessor of(Object object)
    {
        return (SettingsManagerAccessor) object;
    }

    CompositeRuleManager rof$getCompositeRules();

}
