package com.carpet.rof.rules.explosion;

/**
 * 实体身上的暴露度缓存槽：批次号 + 在 ExplosionMergeData.exposures 里的下标。
 * 批次号和当前合并组不一致，就说明这个实体在本组里还没算过、或者算完之后动过了
 * （setPosRaw / setBoundingBox 都会把批次号清成 0）。
 */
public interface ExposureCacheAccess
{
    int rof$getExposureStamp();

    void rof$setExposureStamp(int stamp);

    int rof$getExposureIndex();

    void rof$setExposureIndex(int index);
}
