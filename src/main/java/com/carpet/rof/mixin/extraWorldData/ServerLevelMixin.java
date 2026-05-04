package com.carpet.rof.mixin.extraWorldData;



import com.carpet.rof.accessor.IExtraChunkDataAccessor;
import com.carpet.rof.extraWorldData.ExtraWorldDatas;
import com.carpet.rof.utils.ROFIO;
import com.carpet.rof.utils.ROFTool;
import com.carpet.rof.utils.singleTaskWorker.SingleTaskWorker;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
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

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin implements IExtraChunkDataAccessor
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
            at = @At(value = "TAIL"))
    void saveWorld(CallbackInfo ci)
    {
        if(exceedChunkMarker )
       ROFTool.saveNBT2Data((ServerLevel) (Object)this,"extraWorldData.dat", ROFextraWorldDatas.toNbt());
    }

    @Inject(method = "<init>",
            at = @At(value = "RETURN"))
    void loadWorld(CallbackInfo ci)
    {
        ROFextraWorldDatas = new ExtraWorldDatas();
        try {
            Path savaPath = ROFTool.getSavePath((ServerLevel) (Object) this).resolve("data").resolve("extraWorldData.dat");
            CompoundTag nbtCompound;
            if(ROFIO.isGzip(savaPath)){
                nbtCompound= NbtIo.readCompressed(savaPath, NbtAccounter.create(104857600L));
            }else {
                nbtCompound= NbtIo.read(savaPath);
            }
            if(nbtCompound == null){
                if(ROFTool.isNetherWorld((ServerLevel) (Object) this)){
                    ROFextraWorldDatas.exceedChunkMarker.topY = 128;
                }else {
                    ROFextraWorldDatas.exceedChunkMarker.topY = Integer.MAX_VALUE/2;
                }
            }else {
                ROFextraWorldDatas.read(nbtCompound);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Inject(method = "tick",
            at = @At(value = "HEAD"))
    void tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci)
    {
        if (((ServerLevel)(Object)this).tickRateManager().runsNormally()&&exceedChunkMarker) {
            this.ROFextraWorldDatas.exceedChunkMarker.update((ServerLevel) (Object) this);
        }
    }
}
