package com.silicontycoon.model;

/**
 * The three optional founder roles. Taking a role costs the player
 * 1 victory point (i.e. the player starts at -1) but grants a
 * permanent gameplay advantage (Strategy-pattern style behaviour
 * switch consulted by the controller and market).
 */
public enum FounderRole {
    /** Trades with the market at a 3:1 rate instead of the standard 4:1. */
    HACKER_CEO("The Hacker CEO", "معامله با بازار با نرخ ۳:۱ به‌جای ۴:۱"),
    /** Upgrading MVP to Unicorn costs 1 Cloud instead of 2. */
    TECH_GURU("The Tech Guru (CTO)", "ارتقا به Unicorn فقط ۱ زیرساخت ابری مصرف می‌کند؛ کارت مجاز پیش از مالیات ۹ عدد است"),
    /** Starts with 2 extra Capital and a higher hand-limit during crises. */
    VC_FUNDED("The VC-Funded", "شروع با ۲ سرمایه اضافه");

    public static final int ROLE_SELECTION_PENALTY = -1;
    public static final int TECH_GURU_CARD_LIMIT = 9;

    private final String englishName;
    private final String description;

    FounderRole(String englishName, String description) {
        this.englishName = englishName;
        this.description = description;
    }

    public String getEnglishName() { return englishName; }
    public String getDescription() { return description; }
}
