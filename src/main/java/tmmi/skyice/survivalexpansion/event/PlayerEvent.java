package tmmi.skyice.survivalexpansion.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerEvent {
    public static final Event<Dead> DEAD = EventFactory.createArrayBacked(Dead.class, callbacks -> (player) -> {
        for (Dead callback : callbacks ) {
            callback.onDead(player);
        }
    });
    @FunctionalInterface
    public interface Dead {
        void onDead(ServerPlayerEntity player);
    }
}
