package com.carpet.rof.mixin.packetRules;

import com.carpet.rof.rules.packerRules.PacketLimiter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.carpet.rof.rules.packerRules.PacketRulesSettings.tntPacketOptimization;


//@Mixin(EntityTrackerEntry.class)
public abstract class EntityTrackerEntryMixin
{
    /*
    @Shadow
    @Final
    private Entity entity;

    @Mutable
    @Shadow @Final private Consumer<Packet<?>> watchingSender;

    @Inject(method = "syncEntityData",
            at = @At(value = "HEAD"),
            cancellable = true)
    public void syncEntityData(CallbackInfo ci)
    {
        if (tntPacketOptimization && entity instanceof TntEntity tntEntity) {
            if (tntEntity.getFuse() > 1) {
                ci.cancel();
            }
        }
    }




    private final static Map<PacketType<?>,Integer> packetTypeMap = Map.ofEntries(
           Map.entry(PlayPackets.ADD_ENTITY,58),
            Map.entry(PlayPackets.ANIMATE,4),
            Map.entry(PlayPackets.DAMAGE_EVENT,37),
            Map.entry(PlayPackets.ENTITY_POSITION_SYNC,60),
            Map.entry(PlayPackets.MOVE_ENTITY_POS,10),
            Map.entry(PlayPackets.MOVE_ENTITY_ROT,6),
            Map.entry(PlayPackets.MOVE_ENTITY_POS_ROT,12),
            Map.entry(PlayPackets.SET_ENTITY_MOTION,9)
    );



    @Inject(method = "<init>",
            at = @At(value = "TAIL"))
    public void init(ServerWorld world, Entity entity, int tickInterval, boolean alwaysUpdateVelocity, Consumer<Packet<?>> watchingSender, BiConsumer<Packet<?>, List<UUID>> filteredWatchingSender, CallbackInfo ci)
    {
        this.watchingSender = (packet) -> {
            watchingSender.accept(packet);
        };
    }

     */
}
