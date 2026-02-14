package com.carpet.rof.mixin.extraWorldData.chunkChange;


import com.carpet.rof.extraWorldData.ExtraWorldDatas;
import com.carpet.rof.rules.extraChunkDatas.ChunkModifySetting;
import com.carpet.rof.utils.RofTool;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerEntityPlayerMixin extends Entity
{
    public ServerEntityPlayerMixin(EntityType<?> type, World world)
    {
        super(type, world);
    }


    @Inject(method = "tick",
            at = @At(value = "TAIL"))
    public void tick(CallbackInfo ci)
    {
        if(!ChunkModifySetting.chunkModifyLoggerPlayerChunk) return;
        var data = ExtraWorldDatas.fromWorld((ServerWorld) RofTool.getWorld_(this)).chunkModifyData;
        data.modifyChunk(this.getChunkPos().toLong(),RofTool.getWorld_(this).getTime() + data.minChunkLifetime + 10);
    }
}
