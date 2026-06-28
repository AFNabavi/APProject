package view;

public class ViewConstants {
    public static final int PRIMARY_STAGE_WIDTH = 1024;
    public static final int PRIMARY_STAGE_HEIGHT = 1024;
    
    public static final int TOP_BAR_WIDTH = PRIMARY_STAGE_WIDTH;
    public static final int TOP_BAR_HEIGHT = 112;
    public static final int TOP_BAR_BUTTONS_SIDE = 50;
    public static final int TOP_BAR_BUTTONS_PADDING = (TOP_BAR_HEIGHT - TOP_BAR_BUTTONS_SIDE) / 2; // = 36

    public static final int PLAYER_PANEL_WIDTH = 262;
    public static final int PLAYER_PANEL_HEIGHT = 700;

    public static final int MARKET_PANEL_WIDTH = 162;
    public static final int MARKET_PANEL_HEIGHT = PLAYER_PANEL_HEIGHT;

    public static final int BOARD_SIDE = (PRIMARY_STAGE_WIDTH - PLAYER_PANEL_WIDTH - MARKET_PANEL_WIDTH); // = 600
    public static final int CELL_SIDE = BOARD_SIDE / 5; // = 120
    public static final int EDGE_THICK = 10;
    public static final int PARTNERSHIP_THICK = 20;
    public static final int VERTEX_RADIUS = 7;
    public static final int MVP_RAIDUS = 11;
    public static final int UNICORN_RAIDUS = 15;
    public static final int BUILDING_BORDER_THICK = 2;
    
    public static final int VERTEX_HELPER_WIDTH = BOARD_SIDE;
    public static final int VERTEX_HELPER_HEIGHT = (PLAYER_PANEL_HEIGHT - BOARD_SIDE); // = 112

    public static final int BOTTOM_BAR_WIDTH = PRIMARY_STAGE_WIDTH;
    public static final int BOTTOM_BAR_HEIGHT = 212;
}
