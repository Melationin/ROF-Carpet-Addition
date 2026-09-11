package com.carpet.rof.rules.asyncWorldgen.spawner;

import java.util.List;

public record SpawnResult(int epoch,
                          List<SpawnCandidate> candidates)
{
}
