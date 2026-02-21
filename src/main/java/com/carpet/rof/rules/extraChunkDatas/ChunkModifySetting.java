package com.carpet.rof.rules.extraChunkDatas;

import carpet.api.settings.Rule;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.ROFRule;

import static carpet.api.settings.RuleCategory.EXPERIMENTAL;
import static com.carpet.rof.rules.BaseSetting.ROF;

@ROFRule
public class ChunkModifySetting
{
    @Rule(
            categories = {ROF,EXPERIMENTAL}
    )
    @QuickTranslations(
            name = "区块修改记录器",
            description = "记录区块生成时间和最后保存时间，占用体积相对较大(存档体积的2%以下)"
    )
    public static boolean chunkModifyLogger = false;

    @Rule(
            categories = {ROF,EXPERIMENTAL}
    )
    @QuickTranslations(
            name = "区块修改记录器保存玩家区块",
            description = "将玩家途径区块视为已修改"
    )
    public static boolean chunkModifyLoggerPlayerChunk = true;
}
