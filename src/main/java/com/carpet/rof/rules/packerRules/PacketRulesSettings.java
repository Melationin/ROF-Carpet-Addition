package com.carpet.rof.rules.packerRules;

import carpet.api.settings.Rule;
import com.carpet.rof.BaseSetting;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.RulesSetting;

import static carpet.api.settings.RuleCategory.OPTIMIZATION;

@RulesSetting
public class PacketRulesSettings extends BaseSetting {

    @Rule(
            categories = {ROF,OPTIMIZATION,PACKET},
            strict = false
    )
    @QuickTranslations(
            name = "tnt实体发包优化",
            description = "通过去掉不必要的tnt实体发包(Fuse)与减少发包频率，优化tnt实体",
            extra = {"可能会造成客户端显示错误"}
    )
    public static boolean tntPacketOptimization;


}
