package com.team10.matchup.common;

/**
 * Football position categories.
 */
public enum PositionCategory {
    FW, MF, DF, GK;

    public static boolean isValid(String value) {
        if (value == null) return false;
        try {
            PositionCategory.valueOf(value.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
