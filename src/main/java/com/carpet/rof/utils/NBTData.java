package com.carpet.rof.utils;

import net.minecraft.nbt.CompoundTag;

public interface NBTData
{
    void write(CompoundTag nbt);

    void read(CompoundTag nbt);

    CompoundTag toNbt();
}
