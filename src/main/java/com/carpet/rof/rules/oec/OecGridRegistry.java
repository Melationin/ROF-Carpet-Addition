package com.carpet.rof.rules.oec;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

/** Tracks only sections which currently own an allocated grid. All calls are on the server thread. */
public final class OecGridRegistry {
    private static final Set<OecSectionAccess> ACTIVE = Collections.newSetFromMap(new WeakHashMap<>());
    private OecGridRegistry() {}

    public static synchronized void register(OecSectionAccess section) { ACTIVE.add(section); }
    public static synchronized void unregister(OecSectionAccess section) { ACTIVE.remove(section); }
    public static synchronized int activeCount() { return ACTIVE.size(); }

    public static void releaseAll() {
        OecSectionAccess[] sections;
        synchronized (OecGridRegistry.class) {
            sections = ACTIVE.toArray(OecSectionAccess[]::new);
            ACTIVE.clear();
        }
        for (OecSectionAccess section : sections) section.rof$releaseGrid();
    }
}
