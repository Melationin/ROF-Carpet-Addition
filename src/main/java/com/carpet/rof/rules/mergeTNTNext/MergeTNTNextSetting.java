package com.carpet.rof.rules.mergeTNTNext;

import carpet.api.settings.Rule;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.rules.BaseSetting;
import com.carpet.rof.annotation.ROFRule;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

import static carpet.api.settings.RuleCategory.*;

@ROFRule
public class MergeTNTNextSetting extends BaseSetting {


    public static class OnlySGU implements Rule.Condition{

        @Override
        public boolean shouldRegister()
        {
            return false;
        }
    }

    @Rule(
            categories = {ROF,OPTIMIZATION,TNT,FEATURE}
    )
    @QuickTranslations(
            name = "合并TNTnext",
            description = "更为激进的tnt合并方案, 可能会导致预期之外的结果。不能与其他tnt合并一起开。"
    )
    public static boolean mergeTNTNext = false;


    @Rule(
            categories = {ROF,OPTIMIZATION,TNT,FEATURE},
            conditions = {OnlySGU.class}
    )
    @QuickTranslations(
            name = "仅在下界合并TNT",
            description = "特化tnt合并功能。为了一些特殊的机器。"
    )
    public static boolean mergeTNTOnlyNether = false;

    public record EntityPosAndVec(
            double posX, double posY, double posZ,
            double vecX, double vecY, double vecZ,
            int fuse){
        public EntityPosAndVec(Vec3d pos, Vec3d vec, int fuse){
            this(pos.x,pos.y,pos.z,vec.x,vec.y,vec.z,fuse);
        }
    }
}
