package com.carpet.rof.rules.merge;

import carpet.api.settings.Rule;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.rules.BaseSetting;
import com.carpet.rof.annotation.ROFRule;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import static carpet.api.settings.RuleCategory.*;

@ROFRule
public class MergeSetting extends BaseSetting {


    public static class OnlySGU implements Rule.Condition{

        @Override
        public boolean shouldRegister()
        {
            return false;
        }
    }

    public enum MergeTNTNextMode
    {
        TRUE, FALSE, SAFE, ALMOST_VANILLA
    }

    @Rule(
            categories = {ROF,OPTIMIZATION,TNT,FEATURE}
    )
    @QuickTranslations(
            name = "合并TNTnext",
            description = "更为激进的tnt合并方案, 可能会导致预期之外的结果。不能与其他tnt合并一起开。",
            extra = {"False:关闭合并",
                    "TRUE: 旧版的爆炸处理方案，会使移动中的合并tnt发生不原版的行为",
                    "SAFE: 更安全的爆炸处理方案，让移动中的合并tnt行为与原版一致",
                    "AlmostVanilla: 更接近原版的tnt合并方案，只在tnt爆炸时合并，并且保tnt tick顺序",
                    "理论上，AlmostVanilla 模式不会改变 TNT 行为，因此在纯原版中不应发现相关差异。若出现与原版不一致的情况，请提交 issue。"
            }
    )
    public static MergeTNTNextMode mergeTNTNext = MergeTNTNextMode.FALSE;

/*
    @Rule(
            categories = {ROF,OPTIMIZATION,FEATURE}
    )
    @QuickTranslations(
            name = "合并下落方块",
            description = "尝试合并下落的方块"
    )
    public static boolean mergeFallingBlock = false;
*/
    @Rule(
            categories = {ROF,OPTIMIZATION,FEATURE}
    )
    @QuickTranslations(
            name = "物品合并优化",
            description = "尽量让物品达到一组，以减轻卡顿(效果不明显)",
            extra = {"允许部分合并：优先把掉落物填满整组，余量留在原掉落物中"}
    )
    public static boolean optimizeItemMerge = false;

    @Rule(
            categories = {ROF,OPTIMIZATION,TNT,FEATURE},
            conditions = {OnlySGU.class}
    )
    @QuickTranslations(
            name = "仅在下界合并TNT",
            description = "特化tnt合并功能。为了一些特殊的机器。"
    )
    public static boolean mergeTNTOnlyNether = false;

    /** 规则开启时放宽原版"装满才合并"的限制：同物品同组件、两边都未满即可合并，余量由原版 merge 留在对方。 */
    public static boolean canPartialMerge(ItemStack selfStack, ItemStack otherStack)
    {
        return selfStack.getCount() < selfStack.getMaxStackSize()
                && otherStack.getCount() < otherStack.getMaxStackSize()
                && ItemStack.isSameItemSameComponents(selfStack, otherStack);
    }

    public record EntityPosAndVec(
            double posX, double posY, double posZ,
            double vecX, double vecY, double vecZ,
            int fuse){
        public EntityPosAndVec(Vec3 pos, Vec3 vec, int fuse){
            this(pos.x,pos.y,pos.z,vec.x,vec.y,vec.z,fuse);
        }
    }
}
