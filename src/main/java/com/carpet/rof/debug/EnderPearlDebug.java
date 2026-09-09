package com.carpet.rof.debug;

import com.carpet.rof.utils.ROFTool;
import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

import java.util.Locale;

/**
 * <p>本功能完全由LLM生成</p>
 *
 * 珍珠调试工具（调试专用，与正式功能完全分离，独立包 com.carpet.rof.debug）。
 *
 * <p>触发方式：给珍珠一个调试名（实体 CustomName 或物品自定义名），名称以 {@link #NAME_PREFIX} 开头。
 * 例如用命令召唤一个带名字的珍珠：</p>
 * <pre>
 *   /summon minecraft:ender_pearl ~ ~1 ~ {CustomName:'"rof:4.0,1.0,0.0"'}
 * </pre>
 *
 * <p>名称格式（{@code rof:} 前缀之后）：</p>
 * <ul>
 *   <li>{@code rof:3.0}          - 初速度大小为 3.0，方向取珍珠当前运动方向（没有则取抛掷者视线，再退化为 +X）</li>
 *   <li>{@code rof:speed=3.0}    - 同上</li>
 *   <li>{@code rof:1.0,2.0,3.0}  - 直接指定初速度向量 (vx, vy, vz)</li>
 *   <li>{@code rof:v=1.0,2.0,3.0}- 同上</li>
 * </ul>
 *
 * <p>行为：调试珍珠会在它自己的 tick() 内计数珍珠tick，并记录世界 gameTime（真实世界tick）；
 * 珍珠落地（被移除）时输出"珍珠内计算的tick"与"真实的世界tick"的对比。</p>
 */
public final class EnderPearlDebug
{
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * 总开关：默认跟随 ROFTool.DEBUG（仅开发环境或 -Drof.debug=true 时开启），
     * 生产环境自动关闭。也可在运行时手动置为 true/false 覆盖。
     */
    public static boolean ENABLED = ROFTool.DEBUG;

    /** 调试名称前缀，只有带此前缀名称的珍珠才会进入调试流程，不影响普通珍珠。 */
    public static final String NAME_PREFIX = "rof:";

    private EnderPearlDebug() {}

    /**
     * 获取珍珠的调试名：优先实体自定义名，其次物品自定义名；都没有则返回 null。
     */
    public static String getDebugName(ThrownEnderpearl pearl)
    {
        Component custom = pearl.getCustomName();
        if (custom != null) return custom.getString();
        ItemStack item = pearl.getItem();
        if (item != null) {
            Component itemName = item.getCustomName();
            if (itemName != null) return itemName.getString();
        }
        return null;
    }

    public static boolean isDebugName(String name)
    {
        return name != null && name.startsWith(NAME_PREFIX);
    }

    /**
     * 从调试名解析初速度；无法解析时返回 null（调用方保持原速度并记日志）。
     */
    public static Vec3 parseVelocity(String name, ThrownEnderpearl pearl)
    {
        String body = name.substring(NAME_PREFIX.length()).trim();
        if (body.isEmpty()) return null;

        // 向量形式：rof:1.0,2.0,3.0 或 rof:v=1.0,2.0,3.0
        if (body.startsWith("v=")) body = body.substring(2).trim();
        String[] parts = body.split(",");
        if (parts.length == 3) {
            try {
                return new Vec3(
                        Double.parseDouble(parts[0].trim()),
                        Double.parseDouble(parts[1].trim()),
                        Double.parseDouble(parts[2].trim()));
            } catch (NumberFormatException e) {
                return null;
            }
        }

        // 速度大小形式：rof:3.0 或 rof:speed=3.0
        if (body.startsWith("speed=")) body = body.substring("speed=".length()).trim();
        try {
            double speed = Double.parseDouble(body);
            return direction(pearl).scale(speed);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** 速度方向：珍珠当前运动方向 > 抛掷者视线 > +X。 */
    private static Vec3 direction(ThrownEnderpearl pearl)
    {
        Vec3 motion = pearl.getDeltaMovement();
        if (motion.lengthSqr() > 1.0E-8) return motion.normalize();
        Entity owner = pearl.getOwner();
        if (owner instanceof LivingEntity living) return living.getLookAngle();
        return new Vec3(1.0, 0.0, 0.0);
    }

    /**
     * 珍珠落地（被移除）时输出对比结果：珍珠内计算的tick 与 真实的世界tick。
     *
     * @param pearlTicks               珍珠内计算的tick数（不含生成tick，即实际飞行tick数）
     * @param worldTickStart           开始记录时的世界 gameTime（第一个珍珠tick）
     * @param worldTickEnd             珍珠被移除时的世界 gameTime
     * @param worldTickAtLastPearlTick 最后一次珍珠tick时观察到的世界 gameTime
     * @param removalReason            移除原因（DISCARDED 等）
     * @param appliedVelocity          应用过的初速度（未应用则为 null）
     */
    public static void reportLanding(ThrownEnderpearl pearl, String name,
                                     int pearlTicks,
                                     long worldTickStart, long worldTickEnd,
                                     long worldTickAtLastPearlTick,
                                     String removalReason, Vec3 appliedVelocity)
    {
        long worldTicks = worldTickEnd - worldTickStart;
        long delta = pearlTicks - worldTicks;

        // 服务端控制台
        LOGGER.info("[ROF-PearlDebug] {} 落地({}) 珍珠内tick={} 真实世界tick={} 差={} gameTime {}->{} (最后珍珠tick观察到{}) 初速度={} 位置={}",
                name, removalReason, pearlTicks, worldTicks, delta,
                worldTickStart, worldTickEnd, worldTickAtLastPearlTick,
                fmtVec(appliedVelocity), fmtPos(pearl));

        // 抛掷者聊天
        Component msg = Component.literal("[ROF-PearlDebug] ")
                .append(Component.literal(name).withStyle(ChatFormatting.AQUA))
                .append(Component.literal(" 落地 (" + removalReason + ")\n"))
                .append(Component.literal("  珍珠内计算的tick: " + pearlTicks + "\n").withStyle(ChatFormatting.GOLD))
                .append(Component.literal("  真实世界tick    : " + worldTicks
                        + "   (gameTime " + worldTickStart + " -> " + worldTickEnd + ")\n").withStyle(ChatFormatting.GREEN))
                .append(Component.literal("  差(delta)       : " + delta + "\n").withStyle(ChatFormatting.RED))
                .append(Component.literal("  最后珍珠tick观察到的世界tick: " + worldTickAtLastPearlTick + "\n").withStyle(ChatFormatting.GRAY))
                .append(Component.literal("  初速度          : " + fmtVec(appliedVelocity) + "\n").withStyle(ChatFormatting.GRAY))
                .append(Component.literal("  落地位置        : " + fmtPos(pearl)).withStyle(ChatFormatting.GRAY));

        Entity owner = pearl.getOwner();
        if (owner instanceof ServerPlayer player) {
            player.sendSystemMessage(msg);
        }
    }

    private static String fmtVec(Vec3 v)
    {
        if (v == null) return "null";
        return String.format(Locale.ROOT, "(%.4f, %.4f, %.4f)", v.x, v.y, v.z);
    }

    private static String fmtPos(ThrownEnderpearl pearl)
    {
        return String.format(Locale.ROOT, "(%.2f, %.2f, %.2f)", pearl.getX(), pearl.getY(), pearl.getZ());
    }
}