package com.carpet.rof.mixin.carpet;

import carpet.CarpetServer;
import carpet.api.settings.SettingsManager;
import carpet.utils.Messenger;
import com.carpet.rof.ROFCarpetServer;
import com.carpet.rof.ROFEntry;
import net.minecraft.commands.CommandSourceStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SettingsManager.class)
public class SettingsManagerSetting
{
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
