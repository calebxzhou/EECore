package com.endlessepoch.core.api.energy;

import com.endlessepoch.core.api.tier.VoltageTier;

import java.math.BigInteger;

/**
 * FE ↔ Ω 转换桥接器。
 * <p>
 * 推荐使用 BigInteger 版本的方法，对大数值无精度损失。
 * long 版本已废弃，仅用于兼容旧 FE API。
 */
public class EnergyBridge {

    // ============================================================
    //  BigInteger 方法（推荐）
    // ============================================================

    public static EnergyPacket feToPacket(BigInteger feAmount) {
        BigInteger omega = EnergyUnit.FE.convertToOmega(feAmount);
        OmegaValue val = OmegaValue.of(omega);
        VoltageTier tier = VoltageTier.fromVoltage(val);
        return new EnergyPacket(tier, 1, val);
    }

    public static EnergyPacket feToPacket(BigInteger feAmount, int amperage) {
        BigInteger omega = EnergyUnit.FE.convertToOmega(feAmount);
        OmegaValue val = OmegaValue.of(omega);
        VoltageTier tier = VoltageTier.fromVoltage(val);
        return new EnergyPacket(tier, amperage, val);
    }

    public static BigInteger omegaToFE(OmegaValue omega) {
        if (omega == null || omega.isZero()) return BigInteger.ZERO;
        return EnergyUnit.FE.convertFromOmega(omega.toBigInteger());
    }

    public static BigInteger feToOmega(BigInteger fe) {
        if (fe == null || fe.signum() <= 0) return BigInteger.ZERO;
        return EnergyUnit.FE.convertToOmega(fe);
    }

    // ============================================================
    //  long 方法（已废弃）
    // ============================================================

    /**
     * @deprecated 使用 {@link #feToPacket(BigInteger)} 以避免大数溢出。
     */
    @Deprecated
    public static EnergyPacket feToPacket(long feAmount) {
        long omega = EnergyUnit.FE.convertToOmega(feAmount);
        OmegaValue val = OmegaValue.of(omega);
        VoltageTier tier = VoltageTier.fromVoltage(val);
        return new EnergyPacket(tier, 1, val);
    }

    /**
     * @deprecated 使用 {@link #feToPacket(BigInteger, int)}。
     */
    @Deprecated
    public static EnergyPacket feToPacket(long feAmount, int amperage) {
        long omega = EnergyUnit.FE.convertToOmega(feAmount);
        OmegaValue val = OmegaValue.of(omega);
        VoltageTier tier = VoltageTier.fromVoltage(val);
        return new EnergyPacket(tier, amperage, val);
    }

    /**
     * @deprecated 使用 {@link #omegaToFE(OmegaValue)}（返回 BigInteger）。
     */
    @Deprecated
    public static long packetToFE(EnergyPacket packet) {
        if (packet == null || packet.isEmpty()) return 0;
        return EnergyUnit.FE.convertFromOmega(packet.getEnergy().toLong());
    }

    /**
     * @deprecated 使用 {@link #omegaToFE(OmegaValue)}（返回 BigInteger）。
     */
    @Deprecated
    public static long omegaToFELong(OmegaValue omega) {
        if (omega == null || omega.isZero()) return 0;
        return EnergyUnit.FE.convertFromOmega(omega.toLong());
    }

    /**
     * @deprecated 使用 {@link #feToOmega(BigInteger)}（返回 BigInteger）。
     */
    @Deprecated
    public static long feToOmega(long fe) {
        return EnergyUnit.FE.convertToOmega(fe);
    }
}