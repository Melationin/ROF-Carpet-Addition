package com.carpet.rof.rules.oec;

/** 每次 pushable 查询自增一次，用于候选去重（实体字段存它）。 */
public final class OecQueryStamp {
    private static long current = 1L;

    private OecQueryStamp() {}

    public static void next() { current++; }
    public static long current() { return current; }
}
