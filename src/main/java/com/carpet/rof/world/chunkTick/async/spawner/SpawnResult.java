package com.carpet.rof.world.chunkTick.async.spawner;

import java.util.List;

public record SpawnResult(int epoch,
                          List<SpawnCandidate> candidates)
{
}
