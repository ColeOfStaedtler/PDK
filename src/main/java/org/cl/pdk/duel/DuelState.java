package org.cl.pdk.duel;

public enum DuelState {
    OVER(null),
    ENDING(OVER),
    LIVE(ENDING),
    STARTING(LIVE),
    PREPARING(STARTING),
    WAITING(PREPARING);

    private final DuelState next;

    DuelState(DuelState next) {
        this.next = next;
    }

    public DuelState next() {
        return next;
    }
}
