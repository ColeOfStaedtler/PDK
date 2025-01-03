package org.cl.pdk.duel;

import org.bukkit.entity.Player;
import org.cl.pdk.duel.arena.Arena;

import java.util.Set;

public abstract class Duel<T extends Settings> {
    private final Arena<T> arena;
    private DuelState state;

    public Duel(Arena<T> arena) {
        this.arena = arena;
        this.state = DuelState.WAITING;
    }

    void state(DuelState state) {
        this.state = state;
    }

    public DuelState state() {
        return state;
    }

    public Arena<T> arena() {
        return arena;
    }

    public abstract Set<Player> players();
}
