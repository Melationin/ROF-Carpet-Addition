package com.carpet.rof.mixin.extraWorldData;



import com.carpet.rof.accessor.IExtraChunkDataAccessor;
import com.carpet.rof.extraWorldData.ExtraWorldDatas;
import com.carpet.rof.rules.extraChunkDatas.ChunkModifySetting;
import com.carpet.rof.utils.ROFIO;
import com.carpet.rof.utils.ROFTool;
import com.carpet.rof.utils.singleTaskWorker.SingleTaskWorker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.BooleanSupplier;

import static com.carpet.rof.rules.extraChunkDatas.ExceedChunkMarkerSetting.exceedChunkMarker;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin implements IExtraChunkDataAccessor
{


    @Shadow public abstract String toString();

    @Unique
    ExtraWorldDatas ROFextraWorldDatas;

    @Override
    public ExtraWorldDatas getExtraChunkDatas()
    {
        return ROFextraWorldDatas;
    }


    @Inject(method = "save",
            at = @At(value = "HEAD"))
    void saveWorld(CallbackInfo ci)
    {
        if(exceedChunkMarker || ChunkModifySetting.chunkModifyLogger)
       ROFTool.saveNBT2Data((ServerWorld) (Object)this,"extraWorldData.dat", ROFextraWorldDatas.toNbt());
       //rDEBUG("saveWorld");
    }

    @Inject(method = "<init>",
            at = @At(value = "RETURN"))
    void loadWorld(CallbackInfo ci)
    {
        ROFextraWorldDatas = new ExtraWorldDatas();
        try {
            Path savaPath = ROFTool.getSavePath((ServerWorld) (Object) this).resolve("data").resolve("extraWorldData.dat");
            NbtCompound nbtCompound;
            if(ROFIO.isGzip(savaPath)){
                nbtCompound= NbtIo.readCompressed(savaPath, NbtSizeTracker.of(104857600L));
            }else {
                nbtCompound=NbtIo.read(savaPath);
            }
            if(nbtCompound == null){
                if(ROFTool.isNetherWorld((ServerWorld) (Object) this)){
                    ROFextraWorldDatas.exceedChunkMarker.topY = 128;
                }else {
                    ROFextraWorldDatas.exceedChunkMarker.topY = Integer.MAX_VALUE/2;
                }
            }else {
                ROFextraWorldDatas.read(nbtCompound);
            }
            SingleTaskWorker.INSTANCE.addBuffer("chunkModify["+((ServerWorld)(Object)this).getDimensionEntry().getIdAsString()+"]",
                    ROFextraWorldDatas.chunkModifyData.chunkChangeBuffer);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Inject(method = "tick",
            at = @At(value = "HEAD"))
    void tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci)
    {
        if (((ServerWorld)(Object)this).getTickManager().shouldTick()&&exceedChunkMarker) {
            this.ROFextraWorldDatas.exceedChunkMarker.update((ServerWorld) (Object) this);
        }
    }
}
