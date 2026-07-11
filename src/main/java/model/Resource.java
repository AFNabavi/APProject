package model;

/**
 * The five resource types used as the currency of the game.
 * Every card a player holds is an instance of one of these types.
 */
public enum Resource {
    CAPITAL("سرمایه", "Capital"),
    TALENT("استعداد", "Talent"),
    CLOUD("زیرساخت ابری", "Cloud"),
    PATENT("پتنت", "Patent"),
    DATA("دیتا", "Data");

    private final String persianName;
    private final String englishName;

    Resource(String persianName, String englishName) {
        this.persianName = persianName;
        this.englishName = englishName;
    }

    public String getPersianName() { return persianName; }
    public String getEnglishName() { return englishName; }
}
