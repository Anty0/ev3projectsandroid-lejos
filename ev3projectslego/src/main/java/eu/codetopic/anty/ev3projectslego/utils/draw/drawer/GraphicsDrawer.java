package eu.codetopic.anty.ev3projectslego.utils.draw.drawer;

import lejos.hardware.lcd.Font;
import lejos.hardware.lcd.Image;

public interface GraphicsDrawer extends CommonDrawer {

    int TRANS_MIRROR = 2;
    int TRANS_MIRROR_ROT180 = 1;
    int TRANS_MIRROR_ROT270 = 4;
    int TRANS_MIRROR_ROT90 = 7;
    int TRANS_NONE = 0;
    int TRANS_ROT180 = 3;
    int TRANS_ROT270 = 6;
    int TRANS_ROT90 = 5;

    /**
     * Centering text and images horizontally
     * around the anchor point
     * <p>
     * <P>Value <code>1</code> is assigned to <code>HCENTER</code>.</P>
     */
    int HCENTER = 1;
    /**
     * Centering images vertically
     * around the anchor point.
     * <p>
     * <P>Value <code>2</code> is assigned to <code>VCENTER</code>.</P>
     */
    int VCENTER = 2;
    /**
     * Position the anchor point of text and images
     * to the left of the text or image.
     * <p>
     * <P>Value <code>4</code> is assigned to <code>LEFT</code>.</P>
     */
    int LEFT = 4;
    /**
     * Position the anchor point of text and images
     * to the right of the text or image.
     * <p>
     * <P>Value <code>8</code> is assigned to <code>RIGHT</code>.</P>
     */
    int RIGHT = 8;
    /**
     * Position the anchor point of text and images
     * above the text or image.
     * <p>
     * <P>Value <code>16</code> is assigned to <code>TOP</code>.</P>
     */
    int TOP = 16;
    /**
     * Position the anchor point of text and images
     * below the text or image.
     * <p>
     * <P>Value <code>32</code> is assigned to <code>BOTTOM</code>.</P>
     */
    int BOTTOM = 32;
    /**
     * Position the anchor point at the baseline of text.
     * <p>
     * <P>Value <code>64</code> is assigned to <code>BASELINE</code>.</P>
     */
    int BASELINE = 64;
    /**
     * Constant for the <code>SOLID</code> stroke style.
     * <p>
     * <P>Value <code>0</code> is assigned to <code>SOLID</code>.</P>
     */
    int SOLID = 0;
    /**
     * Constant for the <code>DOTTED</code> stroke style.
     * <p>
     * <P>Value <code>1</code> is assigned to <code>DOTTED</code>.</P>
     */
    int DOTTED = 1;

    /* Public color definitions NOT Standard*/
    int BLACK = 0;
    int WHITE = 0xffffff;

    /**
     * Draws the specified String using the current font and color. x and y
     * give the location of the anchor point. Additional method to allow for
     * the easy use of inverted text. In this case the area below the string
     * is drawn in the current color, before drawing the text in the "inverted"
     * color.
     * <br><b>Note</b>: This is a non standard method.
     *
     * @param str      the String to be drawn
     * @param x        the x coordinate of the anchor point
     * @param y        the y coordinate of the anchor point
     * @param anchor   the anchor point for positioning the text
     * @param inverted true to invert the text display.
     */
    void drawString(String str, int x, int y, int anchor, boolean inverted);

    /**
     * Draws the specified String using the current font and color. x and y
     * give the location of the anchor point.
     *
     * @param str    the String to be drawn
     * @param x      the x coordinate of the anchor point
     * @param y      the y coordinate of the anchor point
     * @param anchor the anchor point for positioning the text
     */
    void drawString(String str, int x, int y, int anchor);

    /**
     * Draw a substring to the graphics surface using the current color.
     *
     * @param str    the base string
     * @param offset the start of the sub string
     * @param len    the length of the sub string
     * @param x      the x coordinate of the anchor point
     * @param y      the x coordinate of the anchor point
     * @param anchor the anchor point used to position the text.
     */
    void drawSubstring(String str, int offset, int len,
                       int x, int y, int anchor);

    /**
     * Draw a single character to the graphics surface using the current color.
     *
     * @param character the character to draw
     * @param x         the x coordinate of the anchor point
     * @param y         the x coordinate of the anchor point
     * @param anchor    the anchor point used to position the text.
     */
    void drawChar(char character, int x, int y, int anchor);

    /**
     * Draw a series of characters to the graphics surface using the current color.
     *
     * @param data   the characters
     * @param offset the start of the characters to be drawn
     * @param length the length of the character string to draw
     * @param x      the x coordinate of the anchor point
     * @param y      the x coordinate of the anchor point
     * @param anchor the anchor point used to position the text.
     */
    void drawChars(char[] data, int offset, int length,
                   int x, int y, int anchor);

    /**
     * Return the current stroke style.
     *
     * @return current style.
     */
    int getStrokeStyle();

    /**
     * Set the stroke style to be used for drawing operations.
     *
     * @param style new style.
     */
    void setStrokeStyle(int style);

    /**
     * Draw the specified image to the graphics surface, using the supplied rop.
     * <br><b>Note</b>: This is a non standard method.
     * Added because without it, it is very
     * hard to invert/manipulate an image, or screen region
     *
     * @param src    image to draw (may be null for ops that do not require input.
     * @param sx     x offset in the source
     * @param sy     y offset in the source
     * @param w      width of area to draw
     * @param h      height of area to draw.
     * @param x      destination
     * @param y      destination
     * @param anchor location of the anchor point
     * @param rop    drawing operation.
     * @see Image
     */
    void drawRegionRop(Image src, int sx, int sy, int w, int h, int x, int y, int anchor, int rop);

    /**
     * Draw the specified region of the source image to the graphics surface
     * after applying the requested transformation, use the supplied rop.
     * <br>NOTE: When calculating the anchor point this method assumes that
     * a transformed version of the source width/height should be used.
     *
     * @param src       The source image
     * @param sx        x coordinate of the region
     * @param sy        y coordinate of the region
     * @param w         width of the region
     * @param h         height of the region
     * @param transform the required transform
     * @param x         x coordinate of the anchor point
     * @param y         y coordinate of the anchor point
     * @param anchor    type of anchor
     * @param rop       raster operation used to draw the output.
     */
    void drawRegionRop(Image src, int sx, int sy, int w, int h, int transform, int x, int y, int anchor, int rop);

    /**
     * Draw the specified region of the supplied image to the graphics surface.
     * NOTE: Transforms are not currently supported.
     *
     * @param src       image to draw (may be null for ops that do not require input.
     * @param sx        x offset to the region
     * @param sy        y offset to the region
     * @param w         width of the region
     * @param h         height of the region
     * @param transform
     * @param x         destination
     * @param y         destination
     * @param anchor    location of the anchor point
     * @see Image
     */
    void drawRegion(Image src,
                    int sx, int sy,
                    int w, int h,
                    int transform,
                    int x, int y,
                    int anchor);

    /**
     * Draw the specified image to the graphics surface, using the supplied rop.
     *
     * @param src    image to draw (may be null for ops that do not require input.
     * @param x      destination
     * @param y      destination
     * @param anchor location of the anchor point
     * @see Image
     */
    void drawImage(Image src, int x, int y, int anchor);

    /**
     * Draw a line between the specified points, using the current color and style.
     *
     * @param x0 x start point
     * @param y0 y start point
     * @param x1 x end point
     * @param y1 y end point
     */
    void drawLine(int x0, int y0, int x1, int y1);

    /**
     * Draw an arc, using the current color and style.
     *
     * @param x
     * @param y
     * @param width
     * @param height
     * @param startAngle
     * @param arcAngle
     */
    void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle);

    /**
     * Draw a filled arc, using the current color.
     *
     * @param x
     * @param y
     * @param width
     * @param height
     * @param startAngle
     * @param arcAngle
     */
    void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle);

    /**
     * Draw a rounded rectangle.
     *
     * @param x
     * @param y
     * @param width
     * @param height
     * @param arcWidth
     * @param arcHeight
     */
    void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight);

    /**
     * Draw a rectangle using the current color and style.
     *
     * @param x
     * @param y
     * @param width
     * @param height
     */
    void drawRect(int x, int y, int width, int height);

    /**
     * Draw a filled rectangle using the current color.
     *
     * @param x
     * @param y
     * @param w
     * @param h
     */
    void fillRect(int x, int y, int w, int h);

    /**
     * Copy one rectangular area of the drawing surface to another.
     *
     * @param sx     Source x
     * @param sy     Source y
     * @param w      Source width
     * @param h      Source height
     * @param x      Destination x
     * @param y      Destination y
     * @param anchor location of the anchor point of the destination.
     */
    void copyArea(int sx, int sy,
                  int w, int h,
                  int x, int y, int anchor);

    /**
     * Return the currently selected font object.
     *
     * @return Current font.
     */
    Font getFont();

    /**
     * Set the current font
     *
     * @param f the font
     */
    void setFont(Font f);

    /**
     * Return the current rgb color.
     *
     * @return current color.
     */
    int getColor();

    /**
     * Set the current drawing color. The value is in the format 0x00RRGGBB.
     * NOTE. Currently only black and white is supported. any non black color
     * is treated as white!
     *
     * @param rgb new color.
     */
    void setColor(int rgb);

    /**
     * Sets the current color to the specified RGB values.
     *
     * @param red   the red component
     * @param green the green component
     * @param blue  the blue
     * @throws IllegalArgumentException if any of the color components
     *                                  are outside of range <code>0-255</code>
     */
    void setColor(int red, int green, int blue);
}
