package com.carpet.rof.entity.enderPearl;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.Rule;
import carpet.api.settings.Validator;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.carpet.BaseSetting;
import com.carpet.rof.annotation.ROFRule;
import net.minecraft.commands.CommandSourceStack;

import static carpet.api.settings.RuleCategory.*;


@ROFRule
public class EnderPearlSettings extends BaseSetting {

    @Rule(
            options = { "true", "false" },
            validators = EnderPearlSelfLoadingValidator.class,
            categories = {ROF,OPTIMIZATION,FEATURE},
            strict = false
    )@QuickTranslations(
            name = "更好的珍珠自加载",
            description = "对某些情况的珍珠启用自加载，自加载期间不放任何加载票。",
            extra = {
                "读到大于 0 的数值时按 true 处理。"
            }
    )
    public static String enderPearlForcedTickMinSpeed = "false";

    public static boolean isSelfLoadingEnabled()
    {
        return "true".equalsIgnoreCase(enderPearlForcedTickMinSpeed);
    }

    @Rule(
            categories = {ROF,OPTIMIZATION,EXPERIMENTAL},
            options = {"false","true"},
            strict = true
    )
    @QuickTranslations(
            name = "优化珍珠tick",
            description = "让大多数情况下高速珍珠的飞行不生成新区块，可大幅度减少存档体积。在ECM未打开时，只会让世界高度外的珍珠不生成区块",
            extra = {"已知特性：珍珠会忽略未加载的实体碰撞箱。"}
    )
    public static boolean optimizedEnderPearlTick = false;

    @Rule(
            categories = {ROF,OPTIMIZATION,EXPERIMENTAL},
            options = {"false","true"}
    )
    @QuickTranslations(

            name = "更好的珍珠加载票",
            description = "用一种特殊的加载票替代某些情况下原有的加载票。在ECM未打开时，只对世界高度外的珍珠有效"
    )
    public static boolean betterEnderPearlTicket = false;


    @Rule(
            options = {"0", "50","100","1000"},
            categories = {ROF,OPTIMIZATION,FEATURE},
            strict = false
    )@QuickTranslations(
            name = "珍珠加载堵塞主线程",
            description = "让珍珠的区块加载堵塞主线程，减少珍珠tick和世界tick不同步的问题。",
            extra = {"此处为最大堵塞时间，设置为0表示禁用。"}
    )
    public static int blockingEnderPearlLoading = 0;

    public static final class EnderPearlSelfLoadingValidator extends Validator<String>
    {
        @Override
        public String validate(CommandSourceStack source, CarpetRule<String> rule, String newValue, String userInput)
        {
            String raw = userInput != null ? userInput : newValue;
            if (raw == null) {
                return null;
            }
            raw = raw.trim();
            if (raw.equalsIgnoreCase("true")) {
                return "true";
            }
            if (raw.equalsIgnoreCase("false")) {
                return "false";
            }
            try {
                return Double.parseDouble(raw) > 0 ? "true" : "false";
            }
            catch (NumberFormatException e) {
                return null;
            }
        }

        @Override
        public String description()
        {
            return "true / false；旧配置中大于 0 的数值按 true 处理";
        }
    }
}
