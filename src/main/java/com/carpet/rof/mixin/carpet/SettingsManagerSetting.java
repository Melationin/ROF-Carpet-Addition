package com.carpet.rof.mixin.carpet;

import carpet.CarpetServer;
import carpet.api.settings.CarpetRule;
import carpet.api.settings.SettingsManager;
import carpet.utils.Messenger;
import com.carpet.rof.ROFEntry;
import com.carpet.rof.annotation.PublicField;
import com.carpet.rof.carpet.composite.CompositeRuleManager;
import com.carpet.rof.mixinAccessor.SettingsManagerAccessor;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.commands.CommandSourceStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(SettingsManager.class)
public abstract class SettingsManagerSetting implements SettingsManagerAccessor
{
    @PublicField(mutable = false)
    @Unique
    private final CompositeRuleManager compositeRules = new CompositeRuleManager();


    @Override
    public CompositeRuleManager rof$getCompositeRules()
    {
        return compositeRules;
    }

    @WrapOperation(method = "parseSettingsClass", at = @At(value = "INVOKE",
            target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"), remap = false)
    private Object rof$registerRule(Map<?, ?> rules, Object name, Object rule, Operation<Object> original)
    {
        if (compositeRules.register((CarpetRule<?>) rule)) return null;
        return original.call(rules, name, rule);
    }

    @Inject(method = "parseSettingsClass", at = @At("RETURN"), remap = false)
    private void rof$registerCompositeRules(Class<?> settingsClass, CallbackInfo ci)
    {
        compositeRules.finishRegistration();
    }

    @Inject(
            method = "listAllSettings",
            at = @At(
                    value = "INVOKE",
                    target = "Lcarpet/api/settings/SettingsManager;getCategories()Ljava/lang/Iterable;"
            ),
            remap = false
    )
    private void listAllSettings(CommandSourceStack source, CallbackInfoReturnable<Integer> cir) {
        SettingsManager manager = (SettingsManager) (Object) this;
        if (CarpetServer.settingsManager == manager) {
            Messenger.m(source, "g " + ROFEntry.FANCY_NAME + " Version: " + ROFEntry.MOD_VER);
        }
    }
}
