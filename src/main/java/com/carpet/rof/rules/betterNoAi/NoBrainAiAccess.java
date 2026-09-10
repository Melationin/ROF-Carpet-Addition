package com.carpet.rof.rules.betterNoAi;

/**
 * 由 {@code com.carpet.rof.mixin.rules.betterNoAi.MobMixin} 实现在 {@code Mob} 上，
 * 供其它规则查询实体是否带 NoBrainAI 标签（例如生物AI优化在关闭 AI 时让路，避免重复处理）。
 */
public interface NoBrainAiAccess {
    /** 该实体是否带有 NoBrainAI 标签。 */
    boolean rof$hasNoBrainAi();
}
