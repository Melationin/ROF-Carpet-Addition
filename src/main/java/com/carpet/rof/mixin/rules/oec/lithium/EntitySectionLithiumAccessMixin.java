package com.carpet.rof.mixin.rules.oec.lithium;

import com.carpet.rof.rules.oec.lithium.OecLithiumSectionAccess;
import net.caffeinemc.mods.lithium.common.util.collections.ReferenceMaskedList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntitySection;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * Gives ROF direct access to the two members Lithium adds to {@link EntitySection}, so the high-density
 * collector can read the pushable-entity mask and trigger Lithium's cache warm-up without reflection.
 *
 * <p>Priority must be <b>greater than 1000</b> (Lithium's mixins use Mixin's default): a mixin can only
 * see members that an earlier-applied mixin has already merged into the target class, and a lower
 * priority value is applied earlier. Mixin states the same rule in Lithium's own source, inverted:
 * {@code WalkNodeEvaluatorMixin} carries the comment "This mixin requires a priority &lt; 1000 due to
 * fabric api using 1000 and us needing to inject before them." Applying 1100 therefore runs ROF after
 * Lithium's {@code EntitySection} mixin, which is what makes {@code pushableEntities} resolvable here.
 *
 * <p>Both members belong to Lithium, not to Minecraft, so their annotations pass {@code remap = false}
 * (<i>not</i> the mixin itself: {@link EntitySection} is a vanilla class and must stay remapped).
 */
@Mixin(value = EntitySection.class, priority = 1100)
public interface EntitySectionLithiumAccessMixin extends OecLithiumSectionAccess {
    @Override
    @Accessor(value = "pushableEntities", remap = false)
    @Nullable ReferenceMaskedList<Entity> rof$lithiumPushableEntities();

    @Override
    @Invoker(value = "startFilteringPushableEntities", remap = false)
    void rof$lithiumStartFilteringPushableEntities();
}
