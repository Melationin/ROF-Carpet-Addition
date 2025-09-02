package com.carpet.rof.mixin;

// Minecraft 相关导入

import com.carpet.rof.accessor.EnderPearlEntityAccessor;
import com.carpet.rof.accessor.ServerWorldAccessor;
import com.carpet.rof.utils.RofTool;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.server.world.*;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;

import static com.carpet.rof.ROFCarpetSettings.*;
import static java.nio.file.Files.exists;

// Java 标准库


@Mixin(EnderPearlEntity.class)
public abstract class EnderPearlEntityMixin extends ThrownItemEntity implements EnderPearlEntityAccessor, ServerWorldAccessor {

    // 定义一个自定义的 Chunk Ticket 类型，等级为 2（实体级别）
    @Unique
    private static final ChunkTicketType ENDER_PEARL_TICKET =
         new  ChunkTicketType(2,false, ChunkTicketType.Use.LOADING);
    @Unique
    final double MinSpeed = enderPearlForcedTickMinSpeed;

    @Unique
    final double ExtendLength = enderPearlRaycastLength;


    // 是否启用同步状态（冻结 or 物理更新）
    @Unique
    public boolean syncMode = true;

    //Path path = FileSystems.getDefault().getPath("logs", "access.log");

    // 模拟的实际位置和速度（不一定和实体本身一致）
    @Unique
    private Vec3d realPos = null;
    @Unique
    private Vec3d realVelocity = null;

    @Unique
    private int EPticks = 1;

    @Unique
    private boolean ChangeSpeed = false;

    // 必须定义的构造函数，调用父类
    protected EnderPearlEntityMixin(EntityType<?extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }


    @Unique
    private static boolean isEntityTickingChunk(WorldChunk chunk) {
        return (chunk != null && chunk.getLevelType() == ChunkLevelType.ENTITY_TICKING);
    }
    // 判断某区块是否是实体可运行的状态（ENTITY_TICKING）
    @Override
    public boolean getSyncMode() {
        return syncMode;
    }
    // 注入 tick() 方法的开头，覆盖默认逻辑
    @Inject(method = "tick", at = @At(value = "HEAD"),cancellable = true)
    private void ChunkLoading(CallbackInfo ci) {
        World world = this.getWorld();

         if(true){  //for debug
             if (world instanceof ServerWorld){
                 Text name = this.getStack().getCustomName();
                 if(name != null && ChangeSpeed == false){
                     this.setVelocity(new Vec3d(-1,0,-1).multiply(Double.valueOf(name.getString())));
                     ChangeSpeed = true;
                 }
             }
         }

       // ((PlayerEntity)this.getOwner()).sendMessage(Text.of("123"),true);
        // 仅对服务器世界处理
        if (world instanceof ServerWorld serverWorld) {

            EPticks++;

            if(syncMode){  //此时为同步状态
                if((MinSpeed >0)  && (  Math.abs(this.getVelocity().x)> MinSpeed ||Math.abs(this.getVelocity().z)> MinSpeed)){//大于最高速度，切换加载逻辑
                    syncMode = false; //模拟状态，不计算

                    realVelocity = this.getVelocity().add(Vec3d.ZERO);//储存速度
                    realPos = this.getPos().add(Vec3d.ZERO);//储存位置
                    this.setVelocity(0,realVelocity.getY(),0);
                    ((ServerWorldAccessor)(ServerWorld)world).addMustTickEntity(this);
                    ///System.out.println("addMustTickEntity");
                }
                else {
                    return;
                }
            }

            ServerChunkManager serverChunkManager= ((ServerWorld) world).getChunkManager();



            realPos = realPos.add(realVelocity);
            realVelocity = realVelocity.multiply(0.99F).subtract(0, this.getGravity(), 0);

            if(forceEnderPearlLogger){
                ((PlayerEntity)this.getOwner()).sendMessage(Text.of("[#"+EPticks+"] "+"Pearl's Pos:"+realPos.toString()),true);
            }


            this.setPos(realPos.getX(),realPos.getY(),realPos.getZ());
            this.setVelocity(realVelocity.normalize().multiply(Math.min(realVelocity.length(),ExtendLength)));
            ChunkPos nextChunkPos = new ChunkPos(new BlockPos((int) realPos.x, (int) realPos.y, (int) realPos.z));
            if(!(isEntityTickingChunk(serverChunkManager.getWorldChunk(nextChunkPos.x, nextChunkPos.z)))){
                if(enderPearlSkipUngeneratedRegion) {
                    String _mca = "r." + (nextChunkPos.x >> 5) + "." + (nextChunkPos.z >> 5) + ".mca";
                    Path s1 = RofTool.getSavePath(serverWorld).resolve("region");
                    if (!(exists(s1.resolve(_mca)) && (s1.resolve(_mca).toFile().length() > 1))) {
                        if (realPos.y > 128.3 && RofTool.isNetherWorld(serverWorld)) {
                            ci.cancel();
                        }
                    }
                }
            }

        }
    }


    @Inject(method =  "tick",at = @At(value = "RETURN"))
    private void ChunkUnloadingEnd(CallbackInfo ci){
        //getEntityWorld().getOtherEntities()
        if (this.isRemoved()) {
            World world = this.getWorld();
            if (world instanceof ServerWorld) {
                ServerWorldAccessor accessor = (ServerWorldAccessor)((ServerWorld)world);
                if (accessor.inMustTickEntity(this)) {
                    accessor.removeMustTickEntity(this);
                }
            }
        }
    }
}