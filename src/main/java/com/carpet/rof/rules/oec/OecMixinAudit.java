package com.carpet.rof.rules.oec;

import com.carpet.rof.rules.oec.lithium.LithiumMaskedListAccess;
import com.carpet.rof.rules.oec.lithium.LithiumPushCollector;
import com.carpet.rof.rules.oec.lithium.LithiumSectionBridge;
import net.caffeinemc.mods.lithium.common.entity.pushable.EntityPushablePredicate;
import net.caffeinemc.mods.lithium.common.util.collections.ReferenceMaskedList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.core.SectionPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.entity.EntitySection;
import net.minecraft.world.level.entity.Visibility;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Random;
import java.util.Set;

/** Additional runtime checks executed only by the existing development Mixin audit. */
public final class OecMixinAudit {
    private OecMixinAudit() {}

    public static void verify() {
        EntitySection<Entity> section = new EntitySection<>(Entity.class, Visibility.TICKING);
        if (!(section instanceof OecSectionAccess)) throw new IllegalStateException("OEC EntitySection interface was not applied");
        if (!LithiumSectionBridge.isAvailable(section)) throw new IllegalStateException("OEC Lithium section bridge is unavailable");
        if (LithiumSectionBridge.getPushableEntities(section) != null) throw new IllegalStateException("New Lithium section already has a pushable cache");
        if (!LithiumSectionBridge.startFiltering(section)) throw new IllegalStateException("Cannot initialize Lithium pushable cache");
        ReferenceMaskedList<Object> mask = LithiumSectionBridge.getPushableEntities(section);
        if (!(mask instanceof LithiumMaskedListAccess)) throw new IllegalStateException("OEC ReferenceMaskedList interface was not applied");
        verifyGridGeometry();
        verifyReentrantFrames();
        verifyLithiumCollector();
        verifyRandomGridDifferential();
        if (OecGridRegistry.activeCount() != 0) throw new IllegalStateException("OEC audit leaked an active section grid");
    }

    private static void verifyGridGeometry() {
        SectionEntityGrid grid = new SectionEntityGrid(0, 0, 0);
        DummyEntity edge = new DummyEntity(new AABB(15.5, 1.0, 1.0, 16.1, 2.0, 2.0));
        DummyEntity plane = new DummyEntity(new AABB(2.0, 1.0, 1.0, 2.0, 2.0, 2.0));
        if (!grid.add(edge) || !grid.add(plane)) throw new IllegalStateException("Cannot build OEC audit grid");
        grid.enableFineGrid();
        grid.checkInvariants();
        try (OecQueryFrame frame = OecQueryFrame.acquire()) {
            AABB outsideOwner = new AABB(16.0, 1.0, 1.0, 16.2, 2.0, 2.0);
            if (!grid.collectCandidateSlots(outsideOwner, frame) || !containsSlot(frame, 0) || !grid.intersects(0, outsideOwner)) {
                throw new IllegalStateException("OEC missed an entity extending outside its owner section");
            }
            AABB aroundPlane = new AABB(1.9, 1.0, 1.0, 2.1, 2.0, 2.0);
            if (!grid.collectCandidateSlots(aroundPlane, frame) || !containsSlot(frame, 1) || !grid.intersects(1, aroundPlane)) {
                throw new IllegalStateException("OEC missed a zero-width AABB on a cell boundary");
            }
        }
        if (!grid.remove(edge)) throw new IllegalStateException("OEC failed to remove an indexed entity");
        grid.checkInvariants();
    }

    private static void verifyReentrantFrames() {
        SectionEntityGrid grid = new SectionEntityGrid(0, 0, 0);
        if (!grid.add(new DummyEntity(new AABB(1.0, 1.0, 1.0, 2.0, 2.0, 2.0)))) {
            throw new IllegalStateException("Cannot build the OEC audit reentrancy grid");
        }
        grid.enableFineGrid();
        AABB query = new AABB(0.0, 0.0, 0.0, 8.0, 8.0, 8.0);
        try (OecQueryFrame outer = OecQueryFrame.acquire()) {
            if (!grid.collectCandidateSlots(query, outer)) throw new IllegalStateException("OEC outer query failed");
            int outerWords = outer.wordCount();
            boolean outerHasSlotZero = containsSlot(outer, 0);
            try (OecQueryFrame inner = OecQueryFrame.acquire()) {
                if (inner == outer) throw new IllegalStateException("Nested OEC query reused the outer query frame");
                if (!grid.collectCandidateSlots(query, inner)) throw new IllegalStateException("OEC inner query failed");
            }
            if (outer.wordCount() != outerWords || containsSlot(outer, 0) != outerHasSlotZero) {
                throw new IllegalStateException("Nested OEC query corrupted the outer query frame");
            }
        }
    }

    private static boolean containsSlot(OecQueryFrame frame, int slot) {
        int word = slot >>> 6;
        return word < frame.wordCount() && (frame.words()[word] & (1L << (slot & 63))) != 0L;
    }

    private static void verifyLithiumCollector() {
        boolean previous = OecSettings.optimizedEntityCollection;
        OecSettings.optimizedEntityCollection = true;
        try {
            EntitySection<Entity> section = new EntitySection<>(Entity.class, Visibility.TICKING);
            ((OecSectionAccess) section).rof$setSectionKey(SectionPos.asLong(0, 0, 0));
            ArrayList<Entity> all = new ArrayList<>();
            for (int i = 0; i < 51; i++) {
                double x = (i & 7) * 2.0 + 0.25;
                double y = ((i >>> 3) & 7) * 2.0 + 0.25;
                DummyEntity entity = new DummyEntity(new AABB(x, y, 0.25, x + 0.5, y + 0.5, 0.75));
                all.add(entity);
                section.add(entity);
            }
            AABB query = new AABB(0.0, 0.0, 0.0, 4.0, 4.0, 1.0);
            ArrayList<Entity> expected = new ArrayList<>();
            for (Entity entity : all) if (entity.getBoundingBox().intersects(query)) expected.add(entity);
            ArrayList<Entity> actual = new ArrayList<>();
            EntityPushablePredicate<Entity> acceptAll = new EntityPushablePredicate<>() {
                @Override public boolean test(Entity entity) { return true; }
            };
            if (!LithiumPushCollector.tryCollect(section, 0L, null, query, acceptAll, actual)
                    || expected.size() != actual.size() || !actual.containsAll(expected)) {
                throw new IllegalStateException("OEC Lithium collector differs from a dense AABB scan");
            }

            Entity excluded = expected.getFirst();
            actual.clear();
            if (!LithiumPushCollector.tryCollect(section, 0L, excluded, query, acceptAll, actual)
                    || actual.contains(excluded) || actual.size() != expected.size() - 1) {
                throw new IllegalStateException("OEC Lithium collector ignored the excluded entity");
            }

            EntityPushablePredicate<Entity> rejectAll = new EntityPushablePredicate<>() {
                @Override public boolean test(Entity entity) { return false; }
            };
            actual.clear();
            AABB entireSection = new AABB(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
            if (!LithiumPushCollector.tryCollect(section, 0L, null, entireSection, rejectAll, actual)
                    || !actual.isEmpty() || LithiumSectionBridge.getPushableEntities(section) == null) {
                throw new IllegalStateException("OEC did not preserve Lithium's pushable-cache activation policy");
            }
            ((OecSectionAccess) section).rof$releaseGrid();
        } finally {
            OecSettings.optimizedEntityCollection = previous;
        }
    }

    private static void verifyRandomGridDifferential() {
        Random random = new Random(261L);
        SectionEntityGrid grid = new SectionEntityGrid(-2, 1, 3);
        ArrayList<DummyEntity> entities = new ArrayList<>();
        for (int i = 0; i < 80; i++) {
            DummyEntity entity = new DummyEntity(randomBox(random));
            entities.add(entity);
            if (!grid.add(entity)) throw new IllegalStateException("Cannot populate randomized OEC grid");
        }
        grid.enableFineGrid();

        for (int step = 0; step < 400; step++) {
            if (step % 3 == 0) {
                DummyEntity entity = entities.get(random.nextInt(entities.size()));
                AABB box = randomBox(random);
                entity.setBoundingBox(box);
                grid.updateBounds(entity, box);
            }
            if (step % 17 == 0) {
                int index = random.nextInt(entities.size());
                DummyEntity removed = entities.remove(index);
                if (!grid.remove(removed)) throw new IllegalStateException("Randomized OEC removal failed");
                DummyEntity added = new DummyEntity(randomBox(random));
                entities.add(added);
                if (!grid.add(added)) throw new IllegalStateException("Randomized OEC add failed");
            }

            AABB query = randomBox(random);
            Set<Entity> expected = Collections.newSetFromMap(new IdentityHashMap<>());
            for (Entity entity : entities) if (entity.getBoundingBox().intersects(query)) expected.add(entity);
            Set<Entity> actual = Collections.newSetFromMap(new IdentityHashMap<>());
            try (OecQueryFrame frame = OecQueryFrame.acquire()) {
                if (!grid.collectCandidateSlots(query, frame)) throw new IllegalStateException("Randomized OEC query unexpectedly failed");
                long[] words = frame.words();
                int wordCount = frame.wordCount();
                for (int w = 0; w < wordCount; w++) {
                    long word = words[w];
                    while (word != 0L) {
                        int slot = (w << 6) + Long.numberOfTrailingZeros(word);
                        word &= word - 1L;
                        if (grid.intersects(slot, query)) actual.add(grid.entity(slot));
                    }
                }
            }
            if (!expected.equals(actual)) throw new IllegalStateException("Randomized OEC query differs from dense scan at step " + step);
            if (step % 50 == 0) grid.checkInvariants();
        }
        grid.checkInvariants();
    }

    private static AABB randomBox(Random random) {
        double x = -36.0 + random.nextDouble() * 24.0;
        double y = 12.0 + random.nextDouble() * 24.0;
        double z = 44.0 + random.nextDouble() * 24.0;
        double width = random.nextInt(8) == 0 ? 0.0 : random.nextDouble() * 4.0;
        double height = random.nextInt(8) == 0 ? 0.0 : random.nextDouble() * 4.0;
        double depth = random.nextInt(8) == 0 ? 0.0 : random.nextDouble() * 4.0;
        return new AABB(x, y, z, x + width, y + height, z + depth);
    }

    private static final class DummyEntity extends Entity {
        private DummyEntity(AABB box) {
            super(EntityType.PIG, null);
            this.setBoundingBox(box);
        }
        @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {}
        @Override public boolean hurtServer(ServerLevel level, DamageSource source, float amount) { return false; }
        @Override protected void readAdditionalSaveData(ValueInput input) {}
        @Override protected void addAdditionalSaveData(ValueOutput output) {}
    }
}
