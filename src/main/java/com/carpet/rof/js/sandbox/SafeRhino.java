package com.carpet.rof.js.sandbox;

import com.carpet.rof.js.api.CommandApi;
import com.carpet.rof.js.api.ConsoleApi;
import com.carpet.rof.rules.playerJScript.functions.TickFunction;
import org.mozilla.javascript.*;

import java.util.HashSet;
import java.util.Set;

/**
 * 真正安全的 Rhino JavaScript 沙箱（单文件实现）
 *
 * 特性：
 * 1. 使用 Rhino 原生 API（非 ScriptEngine）
 * 2. 指令计数限制，死循环可中断
 * 3. ClassShutter：彻底禁止 Java 访问
 * 4. 禁用 JIT，防止逃逸
 * 5. 单线程、无并发、无 Executor
 */
public final class SafeRhino {

    /** 最大指令数（防止死循环 / 重计算） */
    private static final int MAX_INSTRUCTIONS = 100_000;

    /** 统一 ContextFactory（指令计数在这里完成） */
    private static final ContextFactory CONTEXT_FACTORY = new SandboxContextFactory();

    private final ScriptableObject sharedGlobal;

    public static SafeRhino INSTANCE = null;

    public SafeRhino() {
        Context cx = CONTEXT_FACTORY.enterContext();
        try {
            sharedGlobal = cx.initSafeStandardObjects();

            defineApi(cx, sharedGlobal,
                    ConsoleApi.class,
            CommandApi.class

            );

            sharedGlobal.sealObject();
        } finally {
            Context.exit();
        }
    }
    /**
     * 执行 JavaScript 代码
     *
     * @param code JS 源码
     * @return 执行结果，或 null（被阻止 / 出错）
     */
    public Object execute(String code) {
        if (code == null || code.isEmpty()) {
            return null;
        }

        if (code.length() > 10_000) {
            System.err.println("[阻止] 脚本过长: " + code.length());
            return null;
        }

        return CONTEXT_FACTORY.call(cx -> {
            // 禁止任何 Java 类可见
            cx.setClassShutter(new SandboxClassShutter());

            // 禁用 JIT，确保指令计数可靠
            cx.setOptimizationLevel(-1);
            ScriptableObject scriptScope = (ScriptableObject) cx.newObject(sharedGlobal);
            scriptScope.setPrototype(sharedGlobal);
            scriptScope.setParentScope(null);

            try {
                return cx.evaluateString(scriptScope, code, "sandbox", 1, null);
            } catch (Error e) {
                // 包括：指令超限、Rhino 内部强制中断
                System.err.println("[阻止] " + e.getMessage());
                return null;
            } catch (Exception e) {
                System.err.println("[JS错误] " + e.getMessage());
                return null;
            }
        });
    }



    /**
     * 指令计数 ContextFactory
     * 真正用于中断死循环
     */
    private static final class SandboxContextFactory extends ContextFactory {

        @Override
        protected Context makeContext() {
            Context cx = super.makeContext();
            cx.setInstructionObserverThreshold(10_000);
            return cx;
        }

        @Override
        protected void observeInstructionCount(Context cx, int instructionCount) {
            Integer total = (Integer) cx.getThreadLocal("ic");
            total = (total == null) ? instructionCount : total + instructionCount;

            if (total > MAX_INSTRUCTIONS) {
                throw new Error("Script instruction limit exceeded (" + MAX_INSTRUCTIONS + ")");
            }

            cx.putThreadLocal("ic", total);
        }
    }

    /**
     * ClassShutter：彻底禁止 Java 访问
     */
    private static final class SandboxClassShutter implements ClassShutter {
        @Override
        public boolean visibleToScripts(String className) {
            return false;
        }
    }

    /**
     * 提供给 JS 的 console 对象
     */

    @SafeVarargs
    private static void defineApi(
            Context cx,
            ScriptableObject global,
            Class<? extends ScriptableObject>... apiClasses
    ) {
        for (Class<? extends ScriptableObject> apiClass : apiClasses) {
            try {
                // 1. 注册 JS 类（扫描 jsFunction_*）
                ScriptableObject.defineClass(global, apiClass);

                // 2. JS 中的对象名 = getClassName()
                ScriptableObject apiObject =
                        (ScriptableObject) cx.newObject(
                                global,
                                apiClass.getDeclaredConstructor().newInstance().getClassName()
                        );

                // 3. API 对象只读
                apiObject.sealObject();

                // 4. 挂到 global
                ScriptableObject.putProperty(
                        global,
                        apiObject.getClassName(),
                        apiObject
                );

            } catch (Exception e) {
                throw new RuntimeException(
                        "Failed to define API: " + apiClass.getName(), e
                );
            }
        }
    }

    Script compile(String code) {
        return CONTEXT_FACTORY.call(cx ->
                cx.compileString(code, "sandbox", 1, null)
        );
    }
    public static class ScriptInstance {
        protected final ScriptableObject scope;
        protected final Set<ScriptFunction> functions;
        
        ScriptInstance(ScriptableObject scope,Set<ScriptFunction> functions) {
            this.scope = scope;
            this.functions = functions;
        }
        public void run() {
            for (ScriptFunction function : functions) {
                function.run(scope);
            }
        }
    }

    public static class ScriptFunction {
        protected final Function function;
        protected ScriptFunction(Function function) {
            this.function = function;
        }

        public void run(ScriptableObject scope) {
            CONTEXT_FACTORY.call(cx -> {
                this.function.call(cx, scope, scope, ScriptRuntime.emptyArgs);
                return null;
            });
        }

    }


    public ScriptInstance create(String code) {
        return CONTEXT_FACTORY.call(cx -> {
            ScriptableObject scope = (ScriptableObject) cx.newObject(sharedGlobal);
            scope.setPrototype(sharedGlobal);
            scope.setParentScope(null);
            Script script = cx.compileString(code, "sandbox", 1, null);
            script.exec(cx, scope);

            Set<ScriptFunction> functions = new HashSet<ScriptFunction>();

            var tickFunction = TickFunction.from(scope);
            if(tickFunction != null) {
                functions.add(tickFunction);
            }

            return new ScriptInstance(scope, functions);
        });
    }
}
