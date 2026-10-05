package com.carpet.rof.carpet;

import carpet.api.settings.Rule;
import carpet.api.settings.Validators;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.ROFRule;
import com.carpet.rof.carpet.composite.CompositeRuleValidator;
import com.carpet.rof.utils.ROFTool;

@ROFRule
public class CompositeTestSettings extends BaseSetting
{
    @Rule(categories = {ROF}, strict = false, options = {"{}"}, validators = CompositeRuleValidator.class,
            conditions = DevelopmentOnly.class)
    @QuickTranslations(name = "复合规则数值测试", description = "仅开发环境注册，不改变游戏行为")
    public static String compositeTestNumbers = "{}";

    @Rule(categories = {"SubRule:compositeTestNumbers"})
    @QuickTranslations(name = "测试开关", description = "自动推导 true、false 选项")
    public static boolean enabled = false;

    @Rule(categories = {"SubRule:compositeTestNumbers"},
          options = {"8", "16", "32"},
          strict = false,
            validators = Validators.NonNegativeNumber.class)
    @QuickTranslations(name = "测试计数", description = "非负整数，8、16、32 作为建议选项")
    public static int count = 8;

    @Rule(categories = {ROF},
          strict = false,
          options = {"{}"}, 
          validators = CompositeRuleValidator.class,
          conditions = DevelopmentOnly.class)
    @QuickTranslations(name = "复合规则文本测试", description = "验证严格选项和字符串，不改变游戏行为")
    public static String compositeTestText = "{}";

    @Rule(categories = {"SubRule:compositeTestText"}, options = {"normal", "fast"})
    @QuickTranslations(name = "测试策略", description = "strict=true，只能选择 normal 或 fast")
    public static String policy = "normal";

    @Rule(categories = {"SubRule:compositeTestText"}, strict = false)
    @QuickTranslations(name = "测试文本", description = "允许任意文本")
    public static String label = "test";

    public static class DevelopmentOnly implements Rule.Condition
    {
        @Override
        public boolean shouldRegister()
        {
            return ROFTool.DEBUG;
        }
    }
}
