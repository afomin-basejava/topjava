package ru.javawebinar.topjava.util.excesswrapper;

public class Excess {
    boolean excess;

    public Excess(boolean excess) {
        this.excess = excess;
    }

    public void set(boolean excess) {
        this.excess = excess;
    }

    public boolean get() {
        return excess;
    }
}
