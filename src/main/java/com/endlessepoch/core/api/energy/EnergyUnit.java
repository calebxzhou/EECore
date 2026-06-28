package com.endlessepoch.core.api.energy;

import java.math.BigInteger;

/**
 * 能量单位 - 只保留 Ω 和 FE
 * 1Ω = 2FE
 * <p>
 * BigInteger 版本的方法对大数无损，long 版本已废弃。
 */
public enum EnergyUnit {
    OMEGA("Ω", 1.0),
    FE("FE", 0.5);  // 1 FE = 0.5 Ω → 1 Ω = 2 FE

    private final String symbol;
    private final double toOmega;

    EnergyUnit(String symbol, double toOmega) {
        this.symbol = symbol;
        this.toOmega = toOmega;
    }

    public String getSymbol() { return symbol; }

    // ============================================================
    //  BigInteger 方法（推荐，对大数无损）
    // ============================================================

    /**
     * 将指定数量转换为 Ω（BigInteger，无损）。
     * 对于 FE 单位：amount / 2
     */
    public BigInteger convertToOmega(BigInteger amount) {
        if (amount == null || amount.signum() <= 0) return BigInteger.ZERO;
        if (this == FE) return amount.divide(BigInteger.TWO);
        return amount; // OMEGA 不变
    }

    /**
     * 将 Ω 转换为指定单位（BigInteger，无损）。
     * 对于 FE 单位：omega * 2
     */
    public BigInteger convertFromOmega(BigInteger omega) {
        if (omega == null || omega.signum() <= 0) return BigInteger.ZERO;
        if (this == FE) return omega.multiply(BigInteger.TWO);
        return omega; // OMEGA 不变
    }

    // ============================================================
    //  double 方法（用于近似显示）
    // ============================================================

    public double convertToOmega(double amount) {
        return amount * toOmega;
    }

    public double convertFromOmega(double omega) {
        return omega / toOmega;
    }

    // ============================================================
    //  long 方法（已废弃 - 大数会溢出或截断）
    // ============================================================

    /**
     * @deprecated 对超过 Long.MAX_VALUE 的值会溢出。请使用 {@link #convertToOmega(BigInteger)}.
     */
    @Deprecated
    public long convertToOmega(long amount) {
        return (long)(amount * toOmega);
    }

    /**
     * @deprecated 对超过 Long.MAX_VALUE/2 的值会溢出。请使用 {@link #convertFromOmega(BigInteger)}.
     */
    @Deprecated
    public long convertFromOmega(long omega) {
        return (long)(omega / toOmega);
    }
}