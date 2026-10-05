package com.carpet.rof.utils;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
//? if >=1.21.6 {
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
//?}

import java.util.Optional;

/** Accepts CompoundTag or the ValueInput/ValueOutput passed to a @Coerce Object injection. */
public final class NBTHelper
{
    private NBTHelper() {}

    public static boolean getBooleanOr(Object input, String name, boolean defaultValue)
    {
        return getByteOr(input, name, (byte) (defaultValue ? 1 : 0)) != 0;
    }

    public static byte getByteOr(Object input, String name, byte defaultValue)
    {
        //? if >=1.21.6 {
        if (input instanceof ValueInput view) return view.getByteOr(name, defaultValue);
        //?}
        CompoundTag tag = asTag(input);
        //? if >=1.21.5 {
        return tag.getByteOr(name, defaultValue);
        //?} else {
        /*return tag.contains(name, Tag.TAG_ANY_NUMERIC) ? tag.getByte(name) : defaultValue;
        *///?}
    }

    public static short getShortOr(Object input, String name, short defaultValue)
    {
        //? if >=1.21.6 {
        if (input instanceof ValueInput view) return (short) view.getShortOr(name, defaultValue);
        //?}
        CompoundTag tag = asTag(input);
        //? if >=1.21.5 {
        return tag.getShortOr(name, defaultValue);
        //?} else {
        /*return tag.contains(name, Tag.TAG_ANY_NUMERIC) ? tag.getShort(name) : defaultValue;
        *///?}
    }

    public static Optional<Integer> getInt(Object input, String name)
    {
        //? if >=1.21.6 {
        if (input instanceof ValueInput view) return view.getInt(name);
        //?}
        CompoundTag tag = asTag(input);
        //? if >=1.21.5 {
        return tag.getInt(name);
        //?} else {
        /*return tag.contains(name, Tag.TAG_ANY_NUMERIC) ? Optional.of(tag.getInt(name)) : Optional.empty();
        *///?}
    }

    public static int getIntOr(Object input, String name, int defaultValue)
    {
        //? if >=1.21.6 {
        if (input instanceof ValueInput view) return view.getIntOr(name, defaultValue);
        //?}
        CompoundTag tag = asTag(input);
        //? if >=1.21.5 {
        return tag.getIntOr(name, defaultValue);
        //?} else {
        /*return tag.contains(name, Tag.TAG_ANY_NUMERIC) ? tag.getInt(name) : defaultValue;
        *///?}
    }

    public static Optional<Long> getLong(Object input, String name)
    {
        //? if >=1.21.6 {
        if (input instanceof ValueInput view) return view.getLong(name);
        //?}
        CompoundTag tag = asTag(input);
        //? if >=1.21.5 {
        return tag.getLong(name);
        //?} else {
        /*return tag.contains(name, Tag.TAG_ANY_NUMERIC) ? Optional.of(tag.getLong(name)) : Optional.empty();
        *///?}
    }

    public static long getLongOr(Object input, String name, long defaultValue)
    {
        //? if >=1.21.6 {
        if (input instanceof ValueInput view) return view.getLongOr(name, defaultValue);
        //?}
        CompoundTag tag = asTag(input);
        //? if >=1.21.5 {
        return tag.getLongOr(name, defaultValue);
        //?} else {
        /*return tag.contains(name, Tag.TAG_ANY_NUMERIC) ? tag.getLong(name) : defaultValue;
        *///?}
    }

    public static float getFloatOr(Object input, String name, float defaultValue)
    {
        //? if >=1.21.6 {
        if (input instanceof ValueInput view) return view.getFloatOr(name, defaultValue);
        //?}
        CompoundTag tag = asTag(input);
        //? if >=1.21.5 {
        return tag.getFloatOr(name, defaultValue);
        //?} else {
        /*return tag.contains(name, Tag.TAG_ANY_NUMERIC) ? tag.getFloat(name) : defaultValue;
        *///?}
    }

    public static double getDoubleOr(Object input, String name, double defaultValue)
    {
        //? if >=1.21.6 {
        if (input instanceof ValueInput view) return view.getDoubleOr(name, defaultValue);
        //?}
        CompoundTag tag = asTag(input);
        //? if >=1.21.5 {
        return tag.getDoubleOr(name, defaultValue);
        //?} else {
        /*return tag.contains(name, Tag.TAG_ANY_NUMERIC) ? tag.getDouble(name) : defaultValue;
        *///?}
    }

    public static Optional<String> getString(Object input, String name)
    {
        //? if >=1.21.6 {
        if (input instanceof ValueInput view) return view.getString(name);
        //?}
        CompoundTag tag = asTag(input);
        //? if >=1.21.5 {
        return tag.getString(name);
        //?} else {
        /*return tag.contains(name, Tag.TAG_STRING) ? Optional.of(tag.getString(name)) : Optional.empty();
        *///?}
    }

    public static String getStringOr(Object input, String name, String defaultValue)
    {
        //? if >=1.21.6 {
        if (input instanceof ValueInput view) return view.getStringOr(name, defaultValue);
        //?}
        CompoundTag tag = asTag(input);
        //? if >=1.21.5 {
        return tag.getStringOr(name, defaultValue);
        //?} else {
        /*return tag.contains(name, Tag.TAG_STRING) ? tag.getString(name) : defaultValue;
        *///?}
    }

    public static Optional<int[]> getIntArray(Object input, String name)
    {
        //? if >=1.21.6 {
        if (input instanceof ValueInput view) return view.getIntArray(name);
        //?}
        CompoundTag tag = asTag(input);
        //? if >=1.21.5 {
        return tag.getIntArray(name);
        //?} else {
        /*return tag.contains(name, Tag.TAG_INT_ARRAY) ? Optional.of(tag.getIntArray(name)) : Optional.empty();
        *///?}
    }

    public static void putBoolean(Object output, String name, boolean value)
    {
        //? if >=1.21.6 {
        if (output instanceof ValueOutput view) {
            view.putBoolean(name, value);
            return;
        }
        //?}
        asTag(output).putBoolean(name, value);
    }

    public static void putByte(Object output, String name, byte value)
    {
        //? if >=1.21.6 {
        if (output instanceof ValueOutput view) {
            view.putByte(name, value);
            return;
        }
        //?}
        asTag(output).putByte(name, value);
    }

    public static void putShort(Object output, String name, short value)
    {
        //? if >=1.21.6 {
        if (output instanceof ValueOutput view) {
            view.putShort(name, value);
            return;
        }
        //?}
        asTag(output).putShort(name, value);
    }

    public static void putInt(Object output, String name, int value)
    {
        //? if >=1.21.6 {
        if (output instanceof ValueOutput view) {
            view.putInt(name, value);
            return;
        }
        //?}
        asTag(output).putInt(name, value);
    }

    public static void putLong(Object output, String name, long value)
    {
        //? if >=1.21.6 {
        if (output instanceof ValueOutput view) {
            view.putLong(name, value);
            return;
        }
        //?}
        asTag(output).putLong(name, value);
    }

    public static void putFloat(Object output, String name, float value)
    {
        //? if >=1.21.6 {
        if (output instanceof ValueOutput view) {
            view.putFloat(name, value);
            return;
        }
        //?}
        asTag(output).putFloat(name, value);
    }

    public static void putDouble(Object output, String name, double value)
    {
        //? if >=1.21.6 {
        if (output instanceof ValueOutput view) {
            view.putDouble(name, value);
            return;
        }
        //?}
        asTag(output).putDouble(name, value);
    }

    public static void putString(Object output, String name, String value)
    {
        //? if >=1.21.6 {
        if (output instanceof ValueOutput view) {
            view.putString(name, value);
            return;
        }
        //?}
        asTag(output).putString(name, value);
    }

    public static void putIntArray(Object output, String name, int[] value)
    {
        //? if >=1.21.6 {
        if (output instanceof ValueOutput view) {
            view.putIntArray(name, value);
            return;
        }
        //?}
        asTag(output).putIntArray(name, value);
    }

    public static <T> Optional<T> read(Object input, String name, Codec<T> codec)
    {
        //? if >=1.21.6 {
        if (input instanceof ValueInput view) return view.read(name, codec);
        //?}
        Tag value = asTag(input).get(name);
        return value == null ? Optional.empty() : codec.parse(NbtOps.INSTANCE, value).result();
    }

    public static <T> void store(Object output, String name, Codec<T> codec, T value)
    {
        //? if >=1.21.6 {
        if (output instanceof ValueOutput view) {
            view.store(name, codec, value);
            return;
        }
        //?}
        CompoundTag tag = asTag(output);
        //? if >=1.21.5 {
        tag.store(name, codec, value);
        //?} else {
        /*tag.put(name, codec.encodeStart(NbtOps.INSTANCE, value).getOrThrow());
        *///?}
    }

    public static Optional<Object> readChild(Object input, String name)
    {
        //? if >=1.21.6 {
        if (input instanceof ValueInput view) return view.child(name).map(child -> (Object) child);
        //?}
        Tag value = asTag(input).get(name);
        return value instanceof CompoundTag child ? Optional.of(child) : Optional.empty();
    }

    public static Object readChildOrEmpty(Object input, String name)
    {
        //? if >=1.21.6 {
        if (input instanceof ValueInput view) return view.childOrEmpty(name);
        //?}
        return readChild(input, name).orElseGet(CompoundTag::new);
    }

    /** Creates a new child, replacing any existing value at this name. */
    public static Object writeChild(Object output, String name)
    {
        //? if >=1.21.6 {
        if (output instanceof ValueOutput view) return view.child(name);
        //?}
        CompoundTag tag = asTag(output);
        CompoundTag child = new CompoundTag();
        tag.put(name, child);
        return child;
    }

    public static void discard(Object output, String name)
    {
        //? if >=1.21.6 {
        if (output instanceof ValueOutput view) {
            view.discard(name);
            return;
        }
        //?}
        asTag(output).remove(name);
    }

    private static CompoundTag asTag(Object value)
    {
        if (value instanceof CompoundTag tag) return tag;
        throw new IllegalArgumentException("Unsupported NBT input/output: " + (value == null ? "null" : value.getClass().getName()));
    }
}
