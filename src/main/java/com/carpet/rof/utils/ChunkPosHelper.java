package com.carpet.rof.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

/*
 * 26.x 把 ChunkPos 暴露成类似 record 的类型（x() / z() / pack()），
 * 而 1.21.11 仍使用公开的 x / z 字段加 toLong()，因此这里对访问器做了包装。
 */
public final class ChunkPosHelper
{
    private ChunkPosHelper() {}

    public static int x(ChunkPos pos)
    {
        //? if >=26.1 {
        return pos.x();
        //?}else{
        /*return pos.x;
        *///?}
    }

    public static ChunkPos containing(BlockPos pos)
    {
        //? if >=26.1 {
        return ChunkPos.containing(pos);
        //?}else{
        /*return new ChunkPos(pos);
         *///?}
    }

    public static int z(ChunkPos pos)
    {
        //? if >=26.1 {
        return pos.z();
        //?}else{
        /*return pos.z;
        *///?}
    }

    public static ChunkPos unpack(long packed)
    {
        //? if >=26.1 {
        return ChunkPos.unpack(packed);
        //?}else{
        /*return new ChunkPos(packed);
        *///?}
    }

    public static long pack(ChunkPos pos)
    {
        //? if >=26.1 {
        return pos.pack();
        //?}else{
        /*return pos.toLong();
        *///?}
    }

    public static long pack(int x, int z)
    {
        //? if >=26.1 {
        return ChunkPos.pack(x, z);
        //?}else{
        /*return ChunkPos.asLong(x, z);
        *///?}
    }

    public static long pack(BlockPos pos)
    {
        //? if >=26.1 {
        return ChunkPos.pack(pos);
        //?}else{
        /*return ChunkPos.asLong(pos);
        *///?}
    }
}
