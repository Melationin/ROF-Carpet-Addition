package com.carpet.rof.mixinAccessor;

import com.carpet.rof.extraWorldData.ExtraWorldDatas;

public interface ServerLevelAccessor
{
    static ServerLevelAccessor of(Object object)
    {
        return (ServerLevelAccessor) object;
    }

    boolean rof$getSpawnStatisticSimplified();

    void rof$setSpawnStatisticSimplified(boolean value);

    ExtraWorldDatas rof$getROFextraWorldDatas();
}
