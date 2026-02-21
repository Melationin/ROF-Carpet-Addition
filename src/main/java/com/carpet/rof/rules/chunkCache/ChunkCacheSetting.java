package com.carpet.rof.rules.chunkCache;

import carpet.api.settings.Rule;
import carpet.api.settings.Validators;
import com.carpet.rof.annotation.QuickTranslations;

import static com.carpet.rof.rules.BaseSetting.ROF;
import static carpet.api.settings.RuleCategory.EXPERIMENTAL;
import static carpet.api.settings.RuleCategory.OPTIMIZATION;
public class ChunkCacheSetting
{
    @Rule(
            categories = {ROF,OPTIMIZATION,EXPERIMENTAL},
            validators = Validators.CommandLevel.class,
            strict = false
    )
    @QuickTranslations(
            name = "区块数据缓存命令",
            description = "在设置为false时，表示禁用区块数据缓存。此功能会导致显著的内存占用增加，请谨慎使用。对大型刷怪塔和世吞等有一定的优化效果",
            extra = {
                    "为了避免所有区块缓存导致内存过大，只允许一定区域的区块可以缓存",
                    "/chunkCache <dimension> <dataType> list : 列出所有要缓存的区块"
            }
    )
    public static String commandChunkCacheCommand = "false";

    @Rule(
            categories = {ROF,OPTIMIZATION,EXPERIMENTAL},
            validators = Validators.CommandLevel.class,
            strict = false
    )
    @QuickTranslations(
            name = "区块数据缓存命令",
            description = "在设置为false时，表示禁用区块数据缓存。此功能会导致显著的内存占用增加，请谨慎使用。对大型刷怪塔和世吞等有一定的优化效果",
            extra = {
                    "为了避免所有区块缓存导致内存过大，只允许一定区域的区块可以缓存",
                    "/chunkCache <dimension> <dataType> list : 列出所有要缓存的区块"
            }
    )
    public static int chunkCacheMaxCount = 17*17*5;

}
