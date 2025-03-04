package eu.codetopic.anty.ev3projectslego.utils.draw.drawer;

import lejos.hardware.lcd.Font;

public interface TextDrawer extends CommonDrawer {

    /**
     * Draw a single char on the LCD at specified x,y co-ordinate.
     *
     * @param c Character to display
     * @param x X location
     * @param y Y location
     */
    void drawChar(char c, int x, int y);

    /**
     * Display an optionally inverted string on the LCD at specified x,y co-ordinate.
     *
     * @param str      The string to be displayed
     * @param x        The x character co-ordinate to display at.
     * @param y        The y character co-ordinate to display at.
     * @param inverted if true the string is displayed inverted.
     */
    void drawString(String str, int x, int y, boolean inverted);

    /**
     * Display a string on the LCD at specified x,y co-ordinate.
     *
     * @param str The string to be displayed
     * @param x   The x character co-ordinate to display at.
     * @param y   The y character co-ordinate to display at.
     */
    void drawString(String str, int x, int y);

    /**
     * Display an int on the LCD at specified x,y co-ordinate.
     *
     * @param i The value to display.
     * @param x The x character co-ordinate to display at.
     * @param y The y character co-ordinate to display at.
     */
    void drawInt(int i, int x, int y);

    /**
     * Display an in on the LCD at x,y with leading spaces to occupy at least the number
     * of characters specified by the places parameter.
     *
     * @param i      The value to display
     * @param places number of places to use to display the value
     * @param x      The x character co-ordinate to display at.
     * @param y      The y character co-ordinate to display at.
     */
    void drawInt(int i, int places, int x, int y);

    /**
     * Clear a contiguous set of characters
     *
     * @param x the x character coordinate
     * @param y the y character coordinate
     * @param n the number of characters
     */
    void clear(int x, int y, int n);

    /**
     * Clear an LCD display row
     *
     * @param y the row to clear
     */
    void clear(int y);

    /**
     * Scrolls the screen up one text line
     */
    void scroll();

    /**
     * Get the current font
     */
    Font getFont();

    /**
     * Get the width of the screen in characters
     */
    int getTextWidth();

    /**
     * Get the height of the screen in characters
     */
    int getTextHeight();
}
