package eu.codetopic.anty.ev3projectslego.utils.draw;

import eu.codetopic.anty.ev3projectslego.utils.draw.drawer.GraphicsDrawer;
import lejos.hardware.lcd.Font;

public final class ButtonDrawer {

    private static final String LOG_TAG = "ButtonDrawer";
    private static final int W_PADDING_WHITE = 9;
    private static final int W_PADDING_BLACK = 5;
    private static final int H_PADDING = 7;
    private static final int DIAGONAL_SIZE = 8;
    private static final int ARC_DIAM = 6;

    private ButtonDrawer() {
    }

    public static int calculateButtonHeight(Font font) {
        return 2 * H_PADDING + font.getHeight() + 2;
    }

    public static int calculateButtonWidth(Font font, String text, boolean invert) {
        return 2 * (invert ? W_PADDING_BLACK : W_PADDING_WHITE)
                + font.stringWidth(text) + 2;
    }

    public static void drawLeftButton(GraphicsDrawer drawer, String text) {
        drawButton(drawer, text, 0, drawer.getHeight() - calculateButtonHeight(drawer.getFont()), false);
    }

    public static void drawCenterButton(GraphicsDrawer drawer, String text) {
        drawButton(drawer, text,
                drawer.getWidth() / 2 - calculateButtonWidth(drawer.getFont(), text, true) / 2,
                drawer.getHeight() - calculateButtonHeight(drawer.getFont()), true);
    }

    public static void drawRightButton(GraphicsDrawer drawer, String text) {
        drawButton(drawer, text,
                drawer.getWidth() - calculateButtonWidth(drawer.getFont(), text, false),
                drawer.getHeight() - calculateButtonHeight(drawer.getFont()), false);
    }

    public static void drawButton(GraphicsDrawer drawer, String text, int x, int y, boolean invert) {
        int textWidth = drawer.getFont().stringWidth(text);
        int height = 2 * H_PADDING + drawer.getFont().getHeight();

        if (invert) {
            int width = 2 * W_PADDING_BLACK + textWidth;

            drawer.fillRect(x, y, width, height);
            drawer.drawString(text, x + W_PADDING_BLACK, y + H_PADDING, 0, true);
        } else {
            int width = 2 * W_PADDING_WHITE + textWidth;

            int xr = x + width;
            int yd = y + height;

            drawer.drawString(text, x + W_PADDING_WHITE, y + H_PADDING, 0);
            drawer.drawLine(x, y, xr, y); // top line
            drawer.drawLine(x, y, x, yd - ARC_DIAM / 2); // left line
            drawer.drawLine(xr, y, xr, yd - DIAGONAL_SIZE); // right line
            drawer.drawLine(x + ARC_DIAM / 2, yd, xr - DIAGONAL_SIZE, yd); // bottom line
            drawer.drawLine(xr - DIAGONAL_SIZE, yd, xr, yd - DIAGONAL_SIZE); // diagonal
            drawer.drawArc(x, yd - ARC_DIAM, ARC_DIAM, ARC_DIAM, 180, 90);
        }
    }

}
