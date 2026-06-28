package com.endlessepoch.core.api.energy;

import com.endlessepoch.core.api.tier.VoltageTier;
import net.neoforged.bus.api.Event;

/**
 * Fired when energy is received or extracted (notification-only, not cancellable).
 * Other mods can listen for this event to implement statistics, logging, chain reactions, etc.
 *
 * <pre>{@code
 * // 在其他 MOD 中监听
 * @SubscribeEvent
 * static void onEnergyTransfer(EnergyTransferEvent event) {
 *     if (event.getPhase() == EnergyTransferEvent.Phase.RECEIVE) {
 *         LOGGER.info("收到 {} Ω", event.getAccepted());
 *     }
 * }
 * }</pre>
 */
public class EnergyTransferEvent extends Event {

    public enum Phase {
        RECEIVE,
        EXTRACT
    }

    private final Phase phase;
    private final IOmegaEnergyStorage storage;
    private final EnergyPacket packet;
    private final OmegaValue accepted;

    public EnergyTransferEvent(Phase phase, IOmegaEnergyStorage storage, EnergyPacket packet, OmegaValue accepted) {
        this.phase = phase;
        this.storage = storage;
        this.packet = packet;
        this.accepted = accepted;
    }

    public Phase getPhase() { return phase; }
    public IOmegaEnergyStorage getStorage() { return storage; }
    public EnergyPacket getPacket() { return packet; }
    public OmegaValue getAccepted() { return accepted; }
    public VoltageTier getTier() { return packet != null ? packet.getTier() : null; }
}
