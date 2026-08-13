package com.carpet.rof.utils.asyncWorldgen.spawner;

import java.util.List;

public record SpawnResult(int epoch,
                          List<SpawnCandidate> candidates)
{
}
