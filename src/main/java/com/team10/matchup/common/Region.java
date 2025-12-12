package com.team10.matchup.common;

/**
 * Region presets used for match, team, and board filtering.
 * Stored as plain strings in DB for compatibility.
 */
public enum Region {
    SEOUL,
    GYEONGGI,
    INCHEON,
    BUSAN,
    DAEGU,
    DAEJEON,
    GWANGJU,
    ULSAN,
    SEJONG,
    GANGWON,
    CHUNGBUK,
    CHUNGNAM,
    JEONBUK,
    JEONNAM,
    GYEONGBUK,
    GYEONGNAM,
    JEJU;

    public static boolean isValid(String value) {
        if (value == null) return false;
        try {
            Region.valueOf(value.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
