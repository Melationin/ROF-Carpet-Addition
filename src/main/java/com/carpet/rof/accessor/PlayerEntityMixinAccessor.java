package com.carpet.rof.accessor;

import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public interface PlayerEntityMixinAccessor {
    Vec3d getSuvPos();
    Vec2f getSuvRotationVec();
    String getSuvWorld();


    void setSuvPos(Vec3d pos);
    void setSuvRotationVec(Vec2f rot);

}
