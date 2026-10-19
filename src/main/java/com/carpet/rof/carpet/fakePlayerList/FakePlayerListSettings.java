package com.carpet.rof.carpet.fakePlayerList;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.Rule;
import carpet.api.settings.Validator;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.ROFRule;
import com.carpet.rof.carpet.BaseSetting;
import net.minecraft.commands.CommandSourceStack;

import static carpet.api.settings.RuleCategory.FEATURE;

@ROFRule
public class FakePlayerListSettings extends BaseSetting {

    @Rule(
            categories = {ROF, FEATURE, PACKET},
            strict = false,
            options = {"0", "2", "5", "10"},
            validators = MergeThresholdValidator.class
    )
    @QuickTranslations(
            name = "假人列表合并",
            description = "把名字为「相同前缀+数字」的假人合并成一行显示在Tab玩家列表",
            extra = {
                    "0 表示不合并；设为 N（N≥2）时，同前缀满 N 只才合并",
                    "例：设为 3 时，bot_a1~bot_a2 不合并，从 bot_a1~bot_a3 起合并为 (players) bot_a1-3"
            }
    )
    public static int mergeFakePlayerTabList = 0;

    public static final class MergeThresholdValidator extends Validator<Integer>
    {
        @Override
        public Integer validate(CommandSourceStack source, CarpetRule<Integer> rule, Integer value, String userInput)
        {
            return value != null && value >= 0 ? value : null;
        }

        @Override
        public String description()
        {
            return "必须是非负整数";
        }
    }
}
