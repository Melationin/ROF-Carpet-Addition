package com.carpet.rof.utils;

import net.minecraft.nbt.NbtCompound;

public interface NBTData
{

    public void write(NbtCompound nbt);

    public void read(NbtCompound nbt);

    public NbtCompound toNbt();

}
