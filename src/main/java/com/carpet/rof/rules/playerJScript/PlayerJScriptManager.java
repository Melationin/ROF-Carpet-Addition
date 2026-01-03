package com.carpet.rof.rules.playerJScript;

import carpet.patches.EntityPlayerMPFake;
import com.carpet.rof.js.sandbox.SafeRhino;
import com.carpet.rof.utils.RofTool;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Items;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PlayerJScriptManager {

    private final EntityPlayerMPFake playerMPFake;
    private final Set< SingleScript> scriptMap = new HashSet<SingleScript>();

    public PlayerJScriptManager(EntityPlayerMPFake playerMPFake) {
        this.playerMPFake = playerMPFake;
    }

    public void start(){
        scriptMap.clear();
        playerMPFake.getInventory().forEach(

                itemStack -> {
                    String name = itemStack.getName().getLiteralString();

                    if(itemStack.getItem().equals(Items.WRITABLE_BOOK)
                        &&name!=null
                            &&name.startsWith("#script")
                    ){
                        StringBuilder code = new StringBuilder();
                        for(var page: itemStack.getComponents().get(DataComponentTypes.WRITABLE_BOOK_CONTENT).pages()){
                            code.append(page.get(false));
                            code.append("\n");
                        }
                        if(!code.isEmpty()){
                            scriptMap.add ( new SingleScript(itemStack.getName().getString(), code.toString()));
                        }
                    }
                }
        );
    }

    public void stop(){
        scriptMap.clear();
    }

    public void tick(){
        if(playerMPFake.isRemoved()){
            return;
        }
        if( !scriptMap.isEmpty() && RofTool.getWorld_(this.playerMPFake).getTickManager().shouldTick()){
            JScriptContext scriptContext = new JScriptContext(this.playerMPFake.getEntityWorld().getServer(), this.playerMPFake);
            JsExecution.enter(scriptContext);
            for(var script: scriptMap){
                script.tick();
            }
            JsExecution.exit();
        }
    }
}
