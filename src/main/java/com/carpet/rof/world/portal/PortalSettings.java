package com.carpet.rof.world.portal;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.Rule;
import carpet.api.settings.Validator;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.ROFRule;
import com.carpet.rof.world.extraWorldData.ExtraWorldDatas;
import com.carpet.rof.carpet.BaseSetting;
import net.minecraft.commands.CommandSourceStack;

import static carpet.api.settings.RuleCategory.EXPERIMENTAL;
import static carpet.api.settings.RuleCategory.OPTIMIZATION;

@ROFRule
public class PortalSettings extends BaseSetting
{
    @Rule(categories = {ROF, OPTIMIZATION, EXPERIMENTAL}, options = {"0", "0.1", "0.5"},
            strict = false, validators = CacheDistanceValidator.class)
    @QuickTranslations(
            name = "地狱门出口搜索缓存距离",
            description = "按入口门方块缓存出口门位置；实体距上次实际搜索位置不超过此距离，且出口区块未变化时复用结果。",
            extra = {
                    "0 表示禁用；距离按来源世界的三维欧氏距离计算."
            }
    )
    public static double netherPortalCacheDistance = 0.0D;

    public static final class CacheDistanceValidator extends Validator<Double>
    {
        @Override
        public Double validate(CommandSourceStack source, CarpetRule<Double> rule, Double value, String userInput)
        {
            if (value == null || !Double.isFinite(value) || value < 0) return null;
            if (source != null && value.doubleValue() != rule.value().doubleValue()) {
                for (var level : source.getServer().getAllLevels()) {
                    ExtraWorldDatas.fromWorld(level).netherPortalCache.clear();
                }
            }
            return value;
        }

        @Override
        public String description()
        {
            return "必须是有限的非负距离；0 表示禁用";
        }
    }
}
