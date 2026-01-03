package com.carpet.rof.rules.playerJScript;

public final class JsExecution {
    private static final ThreadLocal<JScriptContext> CTX = new ThreadLocal<>();

    public static void enter(JScriptContext ctx) {
        CTX.set(ctx);
    }

    public static void exit() {
        CTX.remove();
    }

    public static JScriptContext get() {
        JScriptContext ctx = CTX.get();
        if (ctx == null) {
            throw new IllegalStateException("No execution context");
        }
        return ctx;
    }
}
