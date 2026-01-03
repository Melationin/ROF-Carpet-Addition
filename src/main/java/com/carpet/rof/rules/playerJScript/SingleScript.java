package com.carpet.rof.rules.playerJScript;

import com.carpet.rof.js.sandbox.SafeRhino;
import net.minecraft.text.Text;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptableObject;



public class SingleScript {
    public String name;
    public String code;
    public SafeRhino.ScriptInstance  script= null;

    public SingleScript(String name, String code) {
        this.name = name;
        this.code = code;
        script = SafeRhino.INSTANCE.create(code);
    }

    public void tick(){
       if(script!=null){
           script.run();
       }
    }

}
