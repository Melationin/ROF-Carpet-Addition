package com.carpet.rof.mixin.logger.projectileEntityTraker;

import carpet.logging.Logger;
import carpet.logging.LoggerRegistry;
import com.carpet.rof.commands.EntityTrackerCommand;
import com.carpet.rof.logger.ProjectileTraker;
import com.carpet.rof.utils.ROFWarp;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.carpet.rof.commands.EntityTrackerCommand.commandEntityTracker;

@Mixin(ProjectileEntity.class)
public abstract class ProjectileEntityMixin
{

    @Inject(method = "setOwner(Lnet/minecraft/entity/Entity;)V",
            at = @At(value = "TAIL"))
    public void init(Entity owner, CallbackInfo ci)
    {

        if(owner instanceof ServerPlayerEntity){
            Logger logger = LoggerRegistry.getLogger(ProjectileTraker.NAME);
            logger.log((str,player)->{
                if(owner instanceof ServerPlayerEntity serverPlayer){
                    if(str.equals("all") || player == owner) {
                        MutableText test = Text.literal("[ProjectileTraker] ").append(((Entity)(Object)this).getName());
                        test.styled(style -> style
                                    .withClickEvent(ROFWarp.suggestCommand("/entityTracker set " + ((Entity)(Object)this).getUuid().toString()))
                                    .withHoverEvent(ROFWarp.showText(Text.literal("Owner: ").append(owner.getName())))
                        );
                        if(str.equals("selfAuto")) {
                            if(carpet.utils.CommandHelper.canUseCommand(player.getCommandSource(/*? >=1.21.2 {*/(ServerWorld)ROFWarp.getWorld_( ((Entity)(Object)this))/*?}*/),commandEntityTracker)){
                                EntityTrackerCommand.addNormalEntity(serverPlayer,((Entity)(Object)this));
                            }
                        }
                        return new MutableText[]{test};
                    }
                    return null;
                }
                return null;
            });
        }
    }
}
