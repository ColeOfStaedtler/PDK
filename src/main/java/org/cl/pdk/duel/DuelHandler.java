package org.cl.pdk.duel;

import com.google.common.collect.ImmutableMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.cl.pdk.duel.annotation.StateHandler;
import org.cl.pdk.listener.Listener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class DuelHandler<T extends Settings> implements Listener {
    private final ImmutableMap<DuelState, Method> stateHandlers;
    private final Map<UUID, Duel<T>> playerDuels;

    public DuelHandler() {
        this.stateHandlers = buildStateHandlers();
        this.playerDuels = new HashMap<>();
    }

    private ImmutableMap<DuelState, Method> buildStateHandlers() {
        ImmutableMap.Builder<DuelState, Method> builder = new ImmutableMap.Builder<>();

        for (Method method : getClass().getDeclaredMethods()) {
            StateHandler annotation = method.getAnnotation(StateHandler.class);

            if (annotation == null) {
                continue;
            }

            builder.put(annotation.state(), method);
        }

        return builder.build();
    }

    protected void advance(Duel<T> duel, DuelState state) {
        Method handler = stateHandlers.get(state);

        if (handler == null) {
            return;
        }

        DuelState next = duel.state().next();

        if (next == null) {
            return;
        }

        duel.state(next);

        try {
            handler.invoke(this, duel);
        } catch (IllegalAccessException | InvocationTargetException e) {
            Bukkit.getLogger().severe("Failed to invoke state handler!");
            e.printStackTrace();
        }
    }

    public void register(Duel<T> duel) {
        if (duel.state() != DuelState.WAITING) {
            throw new IllegalStateException("Duel must be " + DuelState.WAITING + " in order to be valid for registration!");
        }

        for (Player player : duel.players()) {
            playerDuels.put(player.getUniqueId(), duel);
        }
    }

    public Duel<T> getDuel(Player player) {
        return playerDuels.get(player.getUniqueId());
    }

    public boolean isInDuel(Player player) {
        return getDuel(player) != null;
    }
}
