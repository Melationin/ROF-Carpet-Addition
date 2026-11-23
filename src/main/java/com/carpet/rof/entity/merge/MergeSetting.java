package com.carpet.rof.entity.merge;

import carpet.api.settings.Rule;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.ROFRule;
import com.carpet.rof.carpet.BaseSetting;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.phys.Vec3;

import static carpet.api.settings.RuleCategory.*;

@ROFRule
public class MergeSetting extends BaseSetting
{
    public enum MergeTNTNextMode
    {
        TRUE, FALSE, SAFE, ALMOST_VANILLA, SAFE_PLUS;

        public boolean mergeDuringCountdown()
        {
            return this == TRUE || this == SAFE;
        }

        public boolean simulateMovementOnExplode()
        {
            return this == SAFE || this == ALMOST_VANILLA || this == SAFE_PLUS;
        }
    }

    @Rule(categories = {ROF, OPTIMIZATION, TNT, FEATURE})
    @QuickTranslations(
            name = "合并TNTnext",
            description = "更为激进的tnt合并方案, 可能会导致预期之外的结果。不能与其他tnt合并一起开。",
            extra = {
                    "False:关闭合并",
                    "TRUE: 旧版的爆炸处理方案，会使移动中的合并tnt发生不原版的行为",
                    "SAFE: 更安全的爆炸处理方案，让移动中的合并tnt行为与原版一致",
                    "AlmostVanilla: 更接近原版的tnt合并方案，只在tnt爆炸时通过每tick一次的提前遍历合并相邻的同点位tnt，且保持tnt tick顺序",
                    "SafePlus: 每tick提前遍历并尝试合并相邻tick顺序且满足合并条件的tnt，不跨其他实体合并",
                    "理论上，AlmostVanilla 模式不会改变 TNT 行为，因此在纯原版中不应发现相关差异。若出现与原版不一致的情况，请提交 issue。"
            })
    public static MergeTNTNextMode mergeTNTNext = MergeTNTNextMode.FALSE;

    @Rule(categories = {ROF, OPTIMIZATION, TNT, FEATURE})
    @QuickTranslations(
            name = "爆炸合并",
            description = "把合并TNT产生的多次爆炸合成一次，只对实体作用一次。仅在合并TNT和爆炸优化已启用时生效。",
            extra = {
                    "需要 mergeTNTNext 处于非 False 模式",
                    "伤害只结算一次；推力按合并次数补齐，总推力与原版一致"
            })
    public static boolean mergeExplosion = false;

    public record EntityPosAndVec(
            double posX, double posY, double posZ,
            double vecX, double vecY, double vecZ,
            int fuse)
    {
        public EntityPosAndVec(Vec3 pos, Vec3 vec, int fuse)
        {
            this(pos.x, pos.y, pos.z, vec.x, vec.y, vec.z, fuse);
        }

        public static EntityPosAndVec of(PrimedTnt tnt)
        {
            Vec3 delta = tnt.getDeltaMovement();
            return new EntityPosAndVec(tnt.getX(), tnt.getY(), tnt.getZ(), delta.x, delta.y, delta.z, tnt.getFuse());
        }
    }
}
