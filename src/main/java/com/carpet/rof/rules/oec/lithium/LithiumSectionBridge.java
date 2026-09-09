package com.carpet.rof.rules.oec.lithium;

import com.mojang.logging.LogUtils;
import net.caffeinemc.mods.lithium.common.util.collections.ReferenceMaskedList;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/** Resolves Lithium's private section cache once, after Mixin transformation. */
public final class LithiumSectionBridge {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static volatile Access access;

    private LithiumSectionBridge() {}

    public static boolean isAvailable(Object section) {
        return access(section).available;
    }

    @SuppressWarnings("unchecked")
    public static @Nullable ReferenceMaskedList<Object> getPushableEntities(Object section) {
        Access resolved = access(section);
        if (!resolved.available) return null;
        try {
            return (ReferenceMaskedList<Object>) resolved.maskGetter.invoke(section);
        } catch (Throwable throwable) {
            resolved.fail("Failed to read Lithium pushable entity cache", throwable);
            return null;
        }
    }

    public static boolean startFiltering(Object section) {
        Access resolved = access(section);
        if (!resolved.available) return false;
        try {
            resolved.startFiltering.invoke(section);
            return true;
        } catch (Throwable throwable) {
            resolved.fail("Failed to start Lithium pushable entity cache", throwable);
            return false;
        }
    }

    private static Access access(Object section) {
        Access current = access;
        if (current != null) return current;
        synchronized (LithiumSectionBridge.class) {
            current = access;
            if (current == null) access = current = resolve(section.getClass());
        }
        return current;
    }

    private static Access resolve(Class<?> sectionClass) {
        try {
            Field maskField = null;
            for (Field field : sectionClass.getDeclaredFields()) {
                if (ReferenceMaskedList.class.isAssignableFrom(field.getType())) {
                    if (maskField != null) throw new IllegalStateException("Multiple ReferenceMaskedList fields on EntitySection");
                    maskField = field;
                }
            }
            if (maskField == null) throw new NoSuchFieldException("Lithium pushableEntities field");

            Method startMethod = null;
            for (Method method : sectionClass.getDeclaredMethods()) {
                if (method.getParameterCount() == 0 && method.getReturnType() == void.class
                        && method.getName().endsWith("startFilteringPushableEntities")) {
                    if (startMethod != null) throw new IllegalStateException("Multiple Lithium cache initializer methods");
                    startMethod = method;
                }
            }
            if (startMethod == null) throw new NoSuchMethodException("Lithium startFilteringPushableEntities");

            MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(sectionClass, MethodHandles.lookup());
            Access resolved = new Access(lookup.unreflectGetter(maskField), lookup.unreflect(startMethod));
            LOGGER.info("ROF high-density entity collection connected to Lithium 0.24.7");
            return resolved;
        } catch (Throwable throwable) {
            LOGGER.error("ROF high-density entity collection cannot access the required Lithium 0.24.7 state; the rule will fall back to Lithium", throwable);
            return new Access();
        }
    }

    private static final class Access {
        private volatile boolean available;
        private final MethodHandle maskGetter;
        private final MethodHandle startFiltering;
        private boolean failureLogged;

        private Access(MethodHandle maskGetter, MethodHandle startFiltering) {
            this.available = true;
            this.maskGetter = maskGetter;
            this.startFiltering = startFiltering;
        }

        private Access() {
            this.available = false;
            this.maskGetter = null;
            this.startFiltering = null;
        }

        private synchronized void fail(String message, Throwable throwable) {
            this.available = false;
            if (!this.failureLogged) {
                this.failureLogged = true;
                LOGGER.error(message + "; disabling the optimized collector", throwable);
            }
        }
    }
}
