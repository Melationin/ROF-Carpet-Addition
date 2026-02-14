package com.carpet.rof.mixin.logger.enderPearlLogger;

import carpet.logging.Logger;
import carpet.logging.LoggerRegistry;
import com.carpet.rof.utils.ROFWarp;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderPearlEntity.class)
public abstract class EnderPearlMixin extends ThrownItemEntity
{
    public EnderPearlMixin(EntityType<? extends ThrownItemEntity> entityType, World world)
    {
        super(entityType, world);
    }
    //? >1.21.1 {
    @Inject(method = "<init>(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;)V",
            at = @At(value = "TAIL"))
    public void init(World world, LivingEntity livingEntity, ItemStack stack, CallbackInfo ci)
    //?} else {
    /*@Inject(method = "<init>(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;)V",
            at = @At(value = "TAIL"))
    public void init(World world, LivingEntity livingEntity,CallbackInfo ci)
    *///?}
    {
        if(livingEntity instanceof ServerPlayerEntity){
            Logger logger = LoggerRegistry.getLogger("enderPearl");
            logger.log((str,player)->{
                if(livingEntity instanceof ServerPlayerEntity){
                    if(str.equals("all") || player == livingEntity) {
                        MutableText test = Text.literal("新的末影珍珠被丢出");
                        test.styled(style -> style
                                    .withClickEvent(ROFWarp.suggestCommand("/entityTracker set " + this.getUuid().toString()))
                                    .withHoverEvent(ROFWarp.showText(Text.literal("Owner: ").append(livingEntity.getName())))
                        );
                        return new MutableText[]{test};
                    }
                    return null;
                }
                return null;
            });
        }
    }
}
