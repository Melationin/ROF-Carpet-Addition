package com.carpet.rof.extraWorldData;

import com.carpet.rof.utils.NBTData;

public abstract class ExtraChunkData implements NBTData
{
    protected boolean needSave = false;
    protected boolean enable = true;
    public abstract String getName();

    public boolean needSave(){
        return needSave;
    };

    public boolean enable(){
        return enable;
    };

    public void setEnable(boolean enable)
    {
        this.enable = enable;
    }

    public void setNeedSave(boolean needSave)
    {
        this.needSave = needSave;
    }
}
