package com.carpet.rof.rules.autoFreeze;

import carpet.api.settings.Rule;
import carpet.api.settings.Validators;
import com.carpet.rof.event.ROFEvents;
import com.carpet.rof.rules.BaseSetting;
import com.carpet.rof.annotation.ROFRule;
import com.carpet.rof.utils.ROFWarp;
import net.minecraft.util.Util;


import static carpet.api.settings.RuleCategory.COMMAND;
import static carpet.api.settings.RuleCategory.FEATURE;
import static com.carpet.rof.utils.ROFTextTool.text;

@ROFRule
public class AutoFreezeSettings extends BaseSetting
{

    @Rule(categories = {ROF, FEATURE},
          strict = false,
          options = {"-1", "500", "1000"})
    public static int highLagFreezeLimit = -1;


    @Rule(categories = {ROF, FEATURE},
          strict = false,
          options = {"3", "5", "10"})
    public static int highLagFreezeTickLimit = 3;

    @Rule(categories = {ROF, COMMAND},
          strict = false,
          validators = {Validators.CommandLevel.class})
    public static String commandUnfreeze = "false";

    static {
        ROFEvents.ServerStart.register(server -> {
            ROFEvents.putData("autoFreeze.nanoTime", server, new long[1]);
            ROFEvents.putData("autoFreeze.highLogTick", server, new long[1]);
            long[] highLogTick = (long[])ROFEvents.getData("autoFreeze.nanoTime", server,new long[1]);
            highLogTick[0] = 0;
        });
        ROFEvents.ServerTickBegin.register(server -> {
            long[] nanoTime = (long[])ROFEvents.getData("autoFreeze.nanoTime", server,new long[1]);
            nanoTime[0] = Util.getMeasuringTimeNano();
        });
        ROFEvents.ServerTickEnd.register(server -> {
            long[] last =(long[])ROFEvents.getData("autoFreeze.nanoTime", server,new long[1]);
            if(last == null) return;
            long nanoTime = Util.getMeasuringTimeNano() - last[0];
            if (server.getTickManager().shouldTick()) {
                long[] highLogTick = (long[])ROFEvents.getData("autoFreeze.nanoTime", server,new long[1]);
                if (nanoTime / 1000000.0 > highLagFreezeLimit && highLagFreezeLimit > 0) {
                    if (highLogTick[0] > highLagFreezeTickLimit) {
                        server.getTickManager().setFrozen(true);
                        server.getPlayerManager().getPlayerList().forEach(player ->
                        {
                            player.sendMessage(text("{&c因为游戏剧烈卡顿，现已暂停}",
                                            style -> style.withHoverEvent(ROFWarp.showText(text("过去 1 tick 用时大于"+nanoTime / 1000000.0 +"毫秒")))
                                    )
                            );
                        });
                        highLogTick[0] = 0;
                    }
                } else {
                    highLogTick[0] = 0;
                }
            }
        });
    }
}
