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

    public static class EntityPosAndVec{
        final Vec3d pos;
        final Vec3d vec;
        final int Fuse;
        public EntityPosAndVec(Vec3d pos, Vec3d vec,int Fuse) {
            this.pos = pos;
            this.vec = vec;
            this.Fuse = Fuse;
        }
        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            EntityPosAndVec that = (EntityPosAndVec) o;
            return Fuse == that.Fuse && Objects.equals(pos, that.pos) && Objects.equals(vec, that.vec);
        }

        @Override
        public int hashCode() {
            return Objects.hash(pos, vec, Fuse);
        }
    }
}
