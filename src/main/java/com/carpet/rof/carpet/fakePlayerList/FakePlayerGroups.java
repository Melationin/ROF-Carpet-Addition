package com.carpet.rof.carpet.fakePlayerList;

import carpet.patches.EntityPlayerMPFake;
import com.carpet.rof.mixinAccessor.FakePlayerInfoPacketAccess;
import com.mojang.authlib.GameProfile;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Action;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Entry;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class FakePlayerGroups
{
    private static final int MAX_PROFILE_NAME_LENGTH = 16;
    private static final String LABEL_PREFIX = "(players)";

    private static final EnumSet<Action> SYNC_ACTIONS = EnumSet.of(Action.UPDATE_LISTED, Action.UPDATE_DISPLAY_NAME);

    // 每个接收者各自记录已经下发给它的假人状态，避免同一状态被重复下发。
    private static final Map<UUID, Map<UUID, BotState>> ANNOUNCED = new HashMap<>();

    public record Group(String label, ServerPlayer representative, List<ServerPlayer> members)
    {
    }

    public record BotState(boolean listed, Component displayName)
    {
    }

    public static int threshold()
    {
        return FakePlayerListSettings.mergeFakePlayerTabList;
    }

    public static boolean enabled()
    {
        return threshold() >= 2;
    }

    public static List<Group> collect(final Collection<ServerPlayer> players)
    {
        Map<String, List<ServerPlayer>> byBase = new LinkedHashMap<>();
        for (ServerPlayer player : players)
        {
            if (!(player instanceof EntityPlayerMPFake))
            {
                continue;
            }
            String name = player.getGameProfile().name();
            int split = name.length();
            while (split > 0 && Character.isDigit(name.charAt(split - 1)))
            {
                split--;
            }
            if (split == 0 || split == name.length())
            {
                continue;
            }
            byBase.computeIfAbsent(name.substring(0, split), key -> new ArrayList<>()).add(player);
        }

        List<Group> groups = new ArrayList<>(byBase.size());
        int minMembers = Math.max(2, threshold());
        for (Map.Entry<String, List<ServerPlayer>> entry : byBase.entrySet())
        {
            List<ServerPlayer> members = entry.getValue();
            if (members.size() < minMembers)
            {
                continue;
            }
            String base = entry.getKey();
            members.sort(Comparator.comparingLong(member -> suffixOf(member.getGameProfile().name(), base.length())));
            groups.add(new Group(buildLabel(base, members), members.get(0), List.copyOf(members)));
        }
        return groups;
    }

    private static long suffixOf(final String name, final int baseLength)
    {
        try
        {
            return Long.parseLong(name.substring(baseLength));
        }
        catch (NumberFormatException ignored)
        {
            return Long.MAX_VALUE;
        }
    }

    private static String buildLabel(final String base, final List<ServerPlayer> members)
    {
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;
        List<Long> suffixes = new ArrayList<>(members.size());
        for (ServerPlayer member : members)
        {
            long suffix = suffixOf(member.getGameProfile().name(), base.length());
            if (suffix == Long.MAX_VALUE)
            {
                return base + "x" + members.size();
            }
            suffixes.add(suffix);
            min = Math.min(min, suffix);
            max = Math.max(max, suffix);
        }
        if (min >= 0 && max - min == members.size() - 1L)
        {
            return base + min + "-" + max;
        }
        return base + "x" + members.size();
    }

    // 合并行由组内编号最小的假人承担，其余成员隐藏；未成组的假人保持原样。
    public static Map<UUID, BotState> desiredStates(final Collection<ServerPlayer> players)
    {
        Map<UUID, BotState> states = new HashMap<>();
        for (Group group : collect(players))
        {
            Component label = Component.literal(LABEL_PREFIX + group.label())
                    .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);
            for (ServerPlayer member : group.members())
            {
                states.put(member.getUUID(), new BotState(false, null));
            }
            states.put(group.representative().getUUID(), new BotState(true, label));
        }
        for (ServerPlayer player : players)
        {
            if (player instanceof EntityPlayerMPFake)
            {
                states.putIfAbsent(player.getUUID(), new BotState(true, null));
            }
        }
        return states;
    }

    public static Entry entryOf(final ServerPlayer player, final BotState state)
    {
        String name = player.getGameProfile().name();
        String profileName = name.length() > MAX_PROFILE_NAME_LENGTH ? name.substring(0, MAX_PROFILE_NAME_LENGTH) : name;
        return new Entry(player.getUUID(), new GameProfile(player.getUUID(), profileName), state.listed(), 0,
                player.gameMode(), state.displayName(), false, 0, null);
    }

    // 组合变化只会发生在假人进出服务器时，所以这里补齐“没出现在本次包内”的假人状态。
    public static List<Entry> pendingSync(final UUID recipient, final Collection<ServerPlayer> players,
                                          final Map<UUID, BotState> states, final Set<UUID> present)
    {
        Map<UUID, BotState> announced = ANNOUNCED.computeIfAbsent(recipient, key -> new HashMap<>());
        List<Entry> entries = new ArrayList<>();
        for (ServerPlayer player : players)
        {
            if (!(player instanceof EntityPlayerMPFake))
            {
                continue;
            }
            UUID id = player.getUUID();
            BotState state = states.get(id);
            if (state == null)
            {
                continue;
            }
            if (present.contains(id))
            {
                announced.put(id, state);
                continue;
            }
            if (!state.equals(announced.get(id)))
            {
                entries.add(entryOf(player, state));
                announced.put(id, state);
            }
        }
        return entries;
    }

    public static ClientboundPlayerInfoUpdatePacket packet(final EnumSet<Action> actions, final List<Entry> entries)
    {
        ClientboundPlayerInfoUpdatePacket packet = new ClientboundPlayerInfoUpdatePacket(actions, List.of());
        ((FakePlayerInfoPacketAccess) packet).rof$setEntries(entries);
        return packet;
    }

    // 由“假人被移除”触发：重新下发全部假人的权威状态。
    public static void resync(final Collection<ServerPlayer> players)
    {
        Map<UUID, BotState> states = desiredStates(players);
        if (states.isEmpty())
        {
            return;
        }
        List<Entry> entries = new ArrayList<>(states.size());
        for (ServerPlayer player : players)
        {
            if (!(player instanceof EntityPlayerMPFake))
            {
                continue;
            }
            BotState state = states.get(player.getUUID());
            if (state != null)
            {
                entries.add(entryOf(player, state));
            }
        }
        if (entries.isEmpty())
        {
            return;
        }

        ClientboundPlayerInfoUpdatePacket packet = packet(SYNC_ACTIONS, entries);
        for (ServerPlayer player : players)
        {
            player.connection.send(packet);
        }

        ANNOUNCED.clear();
        for (ServerPlayer player : players)
        {
            ANNOUNCED.put(player.getUUID(), new HashMap<>(states));
        }
    }

    private FakePlayerGroups()
    {
    }
}
