package com.carpet.rof.rules.playerJScript.functions;

import com.carpet.rof.js.sandbox.SafeRhino;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ScriptableObject;

public class TickFunction extends SafeRhino.ScriptFunction {
    int interval = 1;
    int count = 0;

    protected TickFunction(Function function) {
        super(function);
    }

    public static TickFunction from(ScriptableObject scope) {
        int interval = 1;
        Object v = ScriptableObject.getProperty(scope, "interval");
        if(v instanceof Number number) {
            int n = number.intValue();
            if( n>=1){
                interval = n;
            }
        }
        Object f = scope.get("tick", scope);
        if ((f instanceof Function function)) {
            TickFunction tickFunction = new TickFunction(function);
            tickFunction.interval = interval;
            return tickFunction;
        }
        return null;
    }
    @Override
    public void run(ScriptableObject scope) {
        count++;
        if (count >= interval) {
            count = 0;
            super.run(scope);
        }
    }
}
