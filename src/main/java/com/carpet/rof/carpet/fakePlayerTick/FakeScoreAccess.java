package com.carpet.rof.carpet.fakePlayerTick;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.world.scores.ScoreAccess;

public final class FakeScoreAccess
{
    public static final ScoreAccess INSTANCE = new ScoreAccess()
    {
        @Override
        public int get()
        {
            return 0;
        }

        @Override
        public void set(int value)
        {
        }

        @Override
        public boolean locked()
        {
            return false;
        }

        @Override
        public void unlock()
        {
        }

        @Override
        public void lock()
        {
        }

        @Override
        public Component display()
        {
            return null;
        }

        @Override
        public void display(Component display)
        {
        }

        @Override
        public void numberFormatOverride(NumberFormat format)
        {
        }
    };

    private FakeScoreAccess()
    {
    }
}
