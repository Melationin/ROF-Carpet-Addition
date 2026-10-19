package com.carpet.rof.mixin.carpet.fakePlayerList;

import com.carpet.rof.mixinAccessor.FakePlayerInfoPacketAccess;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(ClientboundPlayerInfoUpdatePacket.class)
public abstract class ClientboundPlayerInfoUpdatePacketMixin implements FakePlayerInfoPacketAccess
{
    @Shadow
    @Final
    @Mutable
    private List<ClientboundPlayerInfoUpdatePacket.Entry> entries;

    @Override
    public void rof$setEntries(List<ClientboundPlayerInfoUpdatePacket.Entry> entries)
    {
        this.entries = entries;
    }
}
