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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * <p>本功能完全由LLM生成</p>
 *
 * 珍珠调试工具（调试专用，与正式功能完全分离，独立包 com.carpet.rof.debug）。
 *
 * <p>触发方式：给珍珠一个调试名（实体 CustomName 或物品自定义名），名称以 {@link #NAME_PREFIX} 开头。
 * 例如用命令召唤一个带名字的珍珠：</p>
 * <pre>
 *   /summon minecraft:ender_pearl ~ ~1 ~ {CustomName:'"rof:vx=0.5,vy=0.4,vz=0.0"'}
 * </pre>
 *
 * <p>也可以给末影珍珠物品改名后投掷，投掷出的珍珠会保留物品上的自定义名。</p>
 *
 * <p>名称格式（{@code rof:} 前缀之后，分量之间用逗号或分号分隔，允许空格，键名不区分大小写）：</p>
 * <ul>
 *   <li>{@code rof:vx=1.0,vy=2.0,vz=3.0} - 按轴分别指定初速度，顺序任意，未写的轴为 0</li>
 *   <li>{@code rof:x=1.0,y=2.0,z=3.0}    - 同上，简写</li>
 *   <li>{@code rof:1.0,2.0,3.0}          - 位置形式，依次为 vx, vy, vz</li>
 *   <li>{@code rof:v=1.0,2.0,3.0}        - 同上，显式向量前缀（也接受 {@code v=(1.0,2.0,3.0)}）</li>
 *   <li>{@code rof:3.0}                  - 只给速度大小 3.0，方向取珍珠当前运动方向（没有则取抛掷者视线，再退化为 +X）</li>
 *   <li>{@code rof:speed=3.0}            - 同上</li>
 *   <li>{@code rof:}                     - 不指定速度，仅统计珍珠tick与世界tick</li>
 * </ul>
 *
 * <p>数值是 tick 开始时的 deltaMovement，与原版 Motion NBT 同义；本 tick 内原版仍会正常施加重力与阻力。
 * 解析失败时保留原速度，并在控制台和抛掷者聊天栏给出原因。</p>
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

    // 调试名称前缀，只有带此前缀名称的珍珠才会进入调试流程，不影响普通珍珠。
    public static final String NAME_PREFIX = "rof:";

    private static final double MIN_DIRECTION_LENGTH_SQR = 1.0E-8;

    /**
     * 解析结果：velocity 与 error 至多一个非 null；都为 null 表示不修改速度、仅统计tick。
     */
    public record Parsed(Vec3 velocity, String error)
    {
        static Parsed none() { return new Parsed(null, null); }
        static Parsed ok(Vec3 velocity) { return new Parsed(velocity, null); }
        static Parsed fail(String error) { return new Parsed(null, error); }
    }

    private EnderPearlDebug() {}

    // 获取珍珠的调试名：优先实体自定义名，其次物品自定义名；都没有则返回 null。
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

    // 从调试名解析初速度；失败时 velocity 为 null 且 error 非 null，调用方保持原速度。
    public static Parsed parseVelocity(String name, ThrownEnderpearl pearl)
    {
        String body = stripPrefix(name);
        if (body.isEmpty()) return Parsed.none();

        List<Double> positional = new ArrayList<>(3);
        Double vx = null;
        Double vy = null;
        Double vz = null;
        Double speed = null;
        boolean named = false;

        for (String raw : body.split("[,;]"))
        {
            String part = raw.trim();
            if (part.isEmpty()) return Parsed.fail("存在空的数值段: " + body);

            int eq = part.indexOf('=');
            if (eq < 0)
            {
                Double value = number(part);
                if (value == null) return Parsed.fail("无法解析数值: " + part);
                positional.add(value);
                continue;
            }

            named = true;
            String key = part.substring(0, eq).trim().toLowerCase(Locale.ROOT);
            String text = part.substring(eq + 1).trim();
            Double value = number(text);
            if (value == null) return Parsed.fail("无法解析 " + key + " 的数值: " + text);

            switch (key)
            {
                case "vx", "x" -> {
                    if (vx != null) return Parsed.fail("速度分量重复: " + key);
                    vx = value;
                }
                case "vy", "y" -> {
                    if (vy != null) return Parsed.fail("速度分量重复: " + key);
                    vy = value;
                }
                case "vz", "z" -> {
                    if (vz != null) return Parsed.fail("速度分量重复: " + key);
                    vz = value;
                }
                case "speed", "s", "v" -> {
                    if (speed != null) return Parsed.fail("速度大小重复: " + key);
                    speed = value;
                }
                default -> {
                    return Parsed.fail("未知的速度分量: " + key + "（可用 vx/vy/vz、x/y/z 或 speed）");
                }
            }
        }

        if (named && !positional.isEmpty()) return Parsed.fail("命名分量与位置数值不能混用");

        if (!named)
        {
            if (positional.size() == 3)
            {
                return Parsed.ok(new Vec3(positional.get(0), positional.get(1), positional.get(2)));
            }
            if (positional.size() == 1)
            {
                return Parsed.ok(direction(pearl).scale(positional.get(0)));
            }
            return Parsed.fail("需要 3 个分量（vx,vy,vz）或 1 个速度大小，实际为 " + positional.size() + " 个");
        }

        if (speed != null)
        {
            if (vx != null || vy != null || vz != null) return Parsed.fail("speed 不能与 vx/vy/vz 同时使用");
            return Parsed.ok(direction(pearl).scale(speed));
        }

        return Parsed.ok(new Vec3(orZero(vx), orZero(vy), orZero(vz)));
    }

    // 去掉 rof: 前缀、可选的向量前缀和括号。
    private static String stripPrefix(String name)
    {
        String body = name.substring(NAME_PREFIX.length()).trim()
                .replace('，', ',')
                .replace('；', ';')
                .replace('＝', '=');
        if (body.regionMatches(true, 0, "vec=", 0, 4)) body = body.substring(4).trim();
        else if (body.regionMatches(true, 0, "v=", 0, 2)) body = body.substring(2).trim();
        if (body.length() >= 2 && body.startsWith("(") && body.endsWith(")"))
        {
            body = body.substring(1, body.length() - 1).trim();
        }
        return body;
    }

    private static Double number(String text)
    {
        try
        {
            double value = Double.parseDouble(text);
            return Double.isFinite(value) ? value : null;
        }
        catch (NumberFormatException e)
        {
            return null;
        }
    }

    private static double orZero(Double value)
    {
        return value == null ? 0.0 : value;
    }

    // 速度方向：珍珠当前运动方向 > 抛掷者视线 > +X。
    private static Vec3 direction(ThrownEnderpearl pearl)
    {
        Vec3 motion = pearl.getDeltaMovement();
        if (motion.lengthSqr() > MIN_DIRECTION_LENGTH_SQR) return motion.normalize();
        Entity owner = pearl.getOwner();
        if (owner instanceof LivingEntity living) return living.getLookAngle();
        return new Vec3(1.0, 0.0, 0.0);
    }

    // 调试名解析失败：控制台 + 抛掷者聊天栏各提示一次。
    public static void reportParseFailure(ThrownEnderpearl pearl, String name, String error)
    {
        LOGGER.warn("[ROF-PearlDebug] 无法解析调试名 {}: {}", name, error);
        Entity owner = pearl.getOwner();
        if (owner instanceof ServerPlayer player)
        {
            player.sendSystemMessage(Component.literal("[ROF-PearlDebug] 无法解析调试名 " + name + ": " + error)
                    .withStyle(ChatFormatting.RED));
        }
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
        LOGGER.info("[ROF-PearlDebug] {} 落地({}) 珍珠内tick={} 真实世界tick={} 差={} gameTime {}->{} (最后珍珠tick观察到{}) 初速度[{}] 位置={}",
                name, removalReason, pearlTicks, worldTicks, delta,
                worldTickStart, worldTickEnd, worldTickAtLastPearlTick,
                fmtMotion(appliedVelocity), fmtPos(pearl));

        // 抛掷者聊天
        Component msg = Component.literal("[ROF-PearlDebug] ")
                .append(Component.literal(name).withStyle(ChatFormatting.AQUA))
                .append(Component.literal(" 落地 (" + removalReason + ")\n"))
                .append(Component.literal("  珍珠内计算的tick: " + pearlTicks + "\n").withStyle(ChatFormatting.GOLD))
                .append(Component.literal("  真实世界tick    : " + worldTicks
                        + "   (gameTime " + worldTickStart + " -> " + worldTickEnd + ")\n").withStyle(ChatFormatting.GREEN))
                .append(Component.literal("  差(delta)       : " + delta + "\n").withStyle(ChatFormatting.RED))
                .append(Component.literal("  最后珍珠tick观察到的世界tick: " + worldTickAtLastPearlTick + "\n").withStyle(ChatFormatting.GRAY))
                .append(Component.literal("  初速度          : " + fmtMotion(appliedVelocity) + "\n").withStyle(ChatFormatting.GRAY))
                .append(Component.literal("  落地位置        : " + fmtPos(pearl)).withStyle(ChatFormatting.GRAY));

        Entity owner = pearl.getOwner();
        if (owner instanceof ServerPlayer player) {
            player.sendSystemMessage(msg);
        }
    }

    private static String fmtMotion(Vec3 v)
    {
        if (v == null) return "未指定（保持原速度）";
        return String.format(Locale.ROOT, "vx=%.4f, vy=%.4f, vz=%.4f (|v|=%.4f)",
                v.x, v.y, v.z, v.length());
    }

    private static String fmtPos(ThrownEnderpearl pearl)
    {
        return String.format(Locale.ROOT, "(%.2f, %.2f, %.2f)", pearl.getX(), pearl.getY(), pearl.getZ());
    }
}
