package com.carpet.rof.mixin.carpet;

import carpet.api.settings.Validator;
import com.carpet.rof.mixinAccessor.ParsedRuleAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(targets = "carpet.settings.ParsedRule", remap = false)
public class ParsedRuleMixin implements ParsedRuleAccessor
{
    @Shadow
    @Final
    public List<Validator<Object>> realValidators;

    @Override
    public List<? extends Validator<?>> rof$getValidators()
    {
        return realValidators;
    }
}
