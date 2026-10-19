package com.carpet.rof.mixin.carpet.fakePlayerList;

import carpet.patches.EntityPlayerMPFake;
import com.carpet.rof.carpet.fakePlayerList.FakePlayerGroups;
import com.carpet.rof.carpet.fakePlayerList.FakePlayerGroups.BotState;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Action;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Entry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Mixin(ServerCommonPacketListenerImpl.class)
public abstract class ServerCommonPacketListenerImplMixin
{
    // 玩家列表由客户端根据 listed 标志维护，合并只改这一个包的 listed 与 displayName，
    // 单参重载在所有支持版本里都存在；双参重载的监听器类型在 26.1 前后换过（PacketSendListener -> ChannelFutureListener）。
    @ModifyVariable(method = "send(Lnet/minecraft/network/protocol/Packet;)V",
                    at = @At("HEAD"),
                    argsOnly = true,
                    index = 1,
                    require = 1)
    private Packet<?> rof$mergeFakePlayerTabList(Packet<?> packet)
    {
        if (!FakePlayerGroups.enabled() || !(packet instanceof ClientboundPlayerInfoUpdatePacket update))
        {
            return packet;
        }
        if (!((Object) this instanceof ServerGamePacketListenerImpl listener) || listener.player instanceof EntityPlayerMPFake)
        {
            return packet;
        }

        EnumSet<Action> actions = update.actions();
        if (!actions.contains(Action.ADD_PLAYER)
                || !actions.contains(Action.UPDATE_LISTED)
                || !actions.contains(Action.UPDATE_DISPLAY_NAME))
        {
            return packet;
        }

        List<ServerPlayer> players = listener.player.level().getServer().getPlayerList().getPlayers();
        Map<UUID, BotState> states = FakePlayerGroups.desiredStates(players);
        if (states.isEmpty())
        {
            return packet;
        }

        List<Entry> entries = new ArrayList<>(update.entries().size() + 4);
        boolean changed = false;
        for (Entry entry : update.entries())
        {
            BotState state = states.get(entry.profileId());
            if (state != null && (entry.listed() != state.listed() || !Objects.equals(entry.displayName(), state.displayName())))
            {
                entries.add(new Entry(entry.profileId(), entry.profile(), state.listed(), entry.latency(),
                        entry.gameMode(), state.displayName(), entry.showHat(), entry.listOrder(), entry.chatSession()));
                changed = true;
            }
            else
            {
                entries.add(entry);
            }
        }

        Set<UUID> present = new HashSet<>(entries.size() + 4);
        for (Entry entry : entries)
        {
            present.add(entry.profileId());
        }

        for (Entry correction : FakePlayerGroups.pendingSync(listener.player.getUUID(), players, states, present))
        {
            entries.add(correction);
            changed = true;
        }

        return changed ? FakePlayerGroups.packet(actions, entries) : packet;
    }
}
