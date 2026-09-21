package com.carpet.rof.rules.spawnStatistic;

import carpet.CarpetServer;
import carpet.api.settings.Rule;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.ROFRule;
import com.carpet.rof.mixinAccessor.ServerLevelAccessor;
import com.carpet.rof.rules.BaseSetting;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.Set;

import static carpet.api.settings.RuleCategory.OPTIMIZATION;

@ROFRule
public class SpawnStatisticSimplifySettings extends BaseSetting
{
    @Rule(
            categories = {ROF, OPTIMIZATION},
            validators = SpawnStatisticSimplifyValidator.class, strict = false,
            options = {"{}", "{minecraft:overworld}", "{minecraft:overworld,minecraft:the_end}"}
    )
    @QuickTranslations(
            name = "刷怪统计化简白名单",
            description = "刷怪统计被化简的世界白名单，花括号包裹、逗号分隔的维度 ID；{} 表示任何世界都不化简。",
            extra = {
                    "化简会跳过刷怪统计中的区块查找与生物群系查询",
                    "只有世界中任何生物群系都没有刷怪密度限制时，化简的结果才与原版完全一致"
            })
    public static String spawnStatisticSimplifyWhitelist = "{}";

    private static Set<ResourceKey<Level>> simplifiedWorlds = Set.of();

    // 规则在世界创建之前就被读取，所以已有的世界在这里刷新，之后创建的世界在构造器中初始化。
    static void apply(Set<ResourceKey<Level>> worlds)
    {
        simplifiedWorlds = worlds;
        MinecraftServer server = CarpetServer.minecraft_server;
        if (server == null) {
            return;
        }
        for (ServerLevel world : server.getAllLevels()) {
            ServerLevelAccessor.of(world).rof$setSpawnStatisticSimplified(isSimplified(world));
        }
    }

    public static boolean isSimplified(Level world)
    {
        return simplifiedWorlds.contains(world.dimension());
    }
}
