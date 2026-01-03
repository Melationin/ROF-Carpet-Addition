package com.carpet.rof.js.api;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.util.Arrays;

public class ConsoleApi extends ScriptableObject {
        @Override
        public String getClassName() {
            return "console";
        }

        public static void jsFunction_log(Context cx,
                                          Scriptable thisObj,
                                          Object[] args,
                                          Function funObj) {
            System.out.println("[JS] " + Arrays.toString(args));
        }

        public static void jsFunction_info(Context cx,
                                           Scriptable thisObj,
                                           Object[] args,
                                           Function funObj) {
            jsFunction_log(cx,thisObj,args,funObj);
        }

        public static void jsFunction_warn(Context cx,
                                           Scriptable thisObj,
                                           Object[] args,
                                           Function funObj) {
            System.err.println("[JS WARN] " + Arrays.toString(args));
        }

        public static void jsFunction_error(Context cx,
                                            Scriptable thisObj,
                                            Object[] args,
                                            Function funObj) {
            System.err.println("[JS ERROR] " + Arrays.toString(args));
        }

}
