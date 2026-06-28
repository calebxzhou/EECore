package com.endlessepoch.core.api;

import com.endlessepoch.core.api.energy.IOmegaEnergyStorage;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.BlockCapability;

/**
 * EECore capability definitions for external mod integration.
 * <p>
 * Other mods query Ω energy storage through this capability instead of hard-dependency on EECore classes.
 *
 * <pre>{@code
 * // Get a neighbor's Ω energy storage
 * var cap = level.getCapability(EECoreCapabilities.OMEGA_ENERGY, pos, side);
 * if (cap != null) cap.receivePacket(packet, false);
 * }</pre>
 */
public class EECoreCapabilities {

    /**
     * Ω energy storage capability key.
     * Any block entity implementing {@link IOmegaEnergyStorage} and registering this capability
     * becomes accessible to other mods without compile-time dependency.
     */
    public static final BlockCapability<IOmegaEnergyStorage, Direction> OMEGA_ENERGY =
            BlockCapability.create(
                    ResourceLocation.fromNamespaceAndPath("eecore", "omega_energy"),
                    IOmegaEnergyStorage.class,
                    Direction.class
            );

    private EECoreCapabilities() {}
}
