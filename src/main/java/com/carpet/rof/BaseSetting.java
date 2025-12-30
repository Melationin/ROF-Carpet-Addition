package com.carpet.rof;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.Rule;
import carpet.api.settings.Validator;
import net.minecraft.server.command.ServerCommandSource;

import static carpet.api.settings.RuleCategory.*;

/**
 * Here is your example Settings class you can plug to use carpetmod /carpet settings command
 */
@SuppressWarnings("CanBeFinal")
@RulesSetting
public class BaseSetting
{

    public static final String ROF = "RofROF";
    public static final String HCL = "RofHCL";
    public static final String PACKET = "Packet";


}
