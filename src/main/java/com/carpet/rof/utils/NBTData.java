package com.carpet.rof.utils;

import net.minecraft.nbt.NbtCompound;

public interface NBTData
{
    void write(NbtCompound nbt);

    void read(NbtCompound nbt);

    NbtCompound toNbt();
}
