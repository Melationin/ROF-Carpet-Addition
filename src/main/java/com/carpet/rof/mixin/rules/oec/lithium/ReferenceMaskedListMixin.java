package com.carpet.rof.mixin.rules.oec.lithium;

import com.carpet.rof.rules.oec.lithium.LithiumMaskedListAccess;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.caffeinemc.mods.lithium.common.util.collections.ReferenceMaskedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.BitSet;

@Mixin(value = ReferenceMaskedList.class, remap = false)
public abstract class ReferenceMaskedListMixin implements LithiumMaskedListAccess {
    @Shadow @Final private ReferenceArrayList<Object> allElements;
    @Shadow @Final private BitSet visibleMask;
    @Shadow @Final private Reference2IntOpenHashMap<Object> element2Index;

    @Override
    public boolean rof$isVisible(Object entity) {
        int index = this.element2Index.getInt(entity);
        return index >= 0 && index < this.allElements.size() && this.allElements.get(index) == entity && this.visibleMask.get(index);
    }
}
