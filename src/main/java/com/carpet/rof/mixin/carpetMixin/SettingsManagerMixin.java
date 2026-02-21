package com.carpet.rof.mixin.carpetMixin;

import carpet.api.settings.SettingsManager;
import com.carpet.rof.utils.ROFCommandHelper;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SettingsManager.class)
public abstract class SettingsManagerMixin
{
    @Shadow public abstract String identifier();

    @Inject(method = "registerCommand", at = @At(value = "TAIL"))
    public void s(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandBuildContext, CallbackInfo ci){
        var root = dispatcher.getRoot();
        var node =  root.getChild(this.identifier()).getChild("rule");

        ROFCommandHelper<ServerCommandSource> helper = new ROFCommandHelper<ServerCommandSource>(node);
    }
}
