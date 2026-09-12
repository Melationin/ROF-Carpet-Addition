package com.carpet.rof.debug;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class ItemFillStats
{
    public static final int BUCKETS = 64;

    private ItemFillStats() {}

    public static Snapshot snapshot(MinecraftServer server)
    {
        long[] buckets = new long[BUCKETS];
        long entities = 0;
        long totalItems = 0;
        double ratioSum = 0.0;
        for (ServerLevel level : server.getAllLevels())
        {
            for (Entity entity : level.getAllEntities())
            {
                if (!(entity instanceof ItemEntity itemEntity)) continue;
                ItemStack stack = itemEntity.getItem();
                int maxStackSize = stack.getMaxStackSize();
                if (maxStackSize <= 0) continue;
                int count = Math.max(0, stack.getCount());
                double ratio = Math.min(1.0, (double) count / maxStackSize);
                buckets[Math.min(BUCKETS - 1, (int) (ratio * BUCKETS))]++;
                entities++;
                totalItems += count;
                ratioSum += ratio;
            }
        }
        return new Snapshot(buckets, entities, totalItems, entities == 0 ? 0.0 : ratioSum / entities);
    }

    public record Snapshot(long[] buckets, long entities, long totalItems, double averageRatio)
    {
        public List<String> lines()
        {
            List<String> lines = new ArrayList<>();
            lines.add(String.format(Locale.ROOT, "ROF item fill: %d item entities, %d items, avg %.2f%%",
                    entities, totalItems, averageRatio * 100.0));
            if (entities == 0) return lines;
            double maxPercent = 0.0;
            for (long count : buckets) maxPercent = Math.max(maxPercent, count * 100.0 / entities);
            for (int i = 0; i < buckets.length; i++)
            {
                long count = buckets[i];
                double percent = count * 100.0 / entities;
                lines.add(String.format(Locale.ROOT, "%6.2f%%-%6.2f%%: %6d (%5.2f%%) %s",
                        i * 100.0 / BUCKETS, (i + 1) * 100.0 / BUCKETS, count, percent, bar(percent, maxPercent)));
            }
            return lines;
        }
    }

    private static String bar(double percent, double maxPercent)
    {
        int filled = maxPercent <= 0.0 ? 0 : (int) Math.round(percent / maxPercent * 20.0);
        return "#".repeat(Math.max(0, Math.min(20, filled)));
    }
}
