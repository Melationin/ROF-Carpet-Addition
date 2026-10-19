package com.carpet.rof.mixin.carpet.fakePlayerSpawn;

import com.carpet.rof.utils.ROFTool;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.util.StringUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(OldUsersConverter.class)
public abstract class OldUsersConverterMixin
{
    @Inject(method = "convertMobOwnerIfNecessary", at = @At("HEAD"), cancellable = true, require = 1)
    private static void rof$offlineUuidInDebug(MinecraftServer server, String owner, CallbackInfoReturnable<UUID> cir)
    {
        if (ROFTool.DEBUG && !StringUtil.isNullOrEmpty(owner) && owner.length() <= 16)
        {
            cir.setReturnValue(UUIDUtil.createOfflinePlayerUUID(owner));
        }
    }
}
