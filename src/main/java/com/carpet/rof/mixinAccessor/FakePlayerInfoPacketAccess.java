package com.carpet.rof.mixinAccessor;

import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;

import java.util.List;

public interface FakePlayerInfoPacketAccess
{
    void rof$setEntries(List<ClientboundPlayerInfoUpdatePacket.Entry> entries);
}
