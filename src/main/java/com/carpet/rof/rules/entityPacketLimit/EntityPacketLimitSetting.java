package com.carpet.rof.rules.entityPacketLimit;

import carpet.api.settings.Rule;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.RulesSetting;

import static carpet.api.settings.RuleCategory.OPTIMIZATION;
import static com.carpet.rof.BaseSetting.PACKET;
import static com.carpet.rof.BaseSetting.ROF;

@RulesSetting
public class EntityPacketLimitSetting {

    @Rule(
            categories = {ROF,OPTIMIZATION,PACKET},
            strict = false
    )
    @QuickTranslations(
            name = "实体发包限制",
            description = "限制非玩家的实体发包，以避免在带宽不足时造成玩家卡顿。设置为正值时表示玩家数据包每tick限额",
            extra = {"可能会造成客户端显示错误。"}
    )
    public static int entityPacketLimit = -1;



}
