package eu.codetopic.anty.ev3projectslego.utils.draw.drawer;

public interface CommonDrawer {

    /**
     * Common raster operations for use with bitBlt
     */
    int ROP_CLEAR = 0x00000000;
    int ROP_AND = 0xff000000;
    int ROP_ANDREVERSE = 0xff00ff00;
    int ROP_COPY = 0x0000ff00;
    int ROP_ANDINVERTED = 0xffff0000;
    int ROP_NOOP = 0x00ff0000;
    int ROP_XOR = 0x00ffff00;
    int ROP_OR = 0xffffff00;
    int ROP_NOR = 0xffffffff;
    int ROP_EQUIV = 0x00ffffff;
    int ROP_INVERT = 0x00ff00ff;
    int ROP_ORREVERSE = 0xffff00ff;
    int ROP_COPYINVERTED = 0x0000ffff;
    int ROP_ORINVERTED = 0xff00ffff;
    int ROP_NAND = 0xff0000ff;
    int ROP_SET = 0x000000ff;

    /**
     * Clear the display.
     */
    void clear();

    /**
     * Return the width of the associated drawing surface.
     *
     * @return width of the surface
     */
    int getWidth();

    /**
     * Return the height of the associated drawing surface.
     *
     * @return height of the surface.
     */
    int getHeight();

    byte[] getContent();

    /**
     * Method to set a pixel on the screen.
     *
     * @param x     the x coordinate
     * @param y     the y coordinate
     * @param color the pixel color (0 = white, 1 = black)
     */
    void setPixel(int x, int y, int color);

    /**
     * Method to get a pixel from the screen.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the pixel color (0 = white, 1 = black)
     */
    int getPixel(int x, int y);

    /**
     * Standard two input BitBlt function with the LCD display as the
     * destination. Supports standard raster ops and
     * overlapping images. Images are held in native leJOS/Lego format.
     *
     * @param src byte array containing the source image
     * @param sw  Width of the source image
     * @param sh  Height of the source image
     * @param sx  X position to start the copy from
     * @param sy  Y Position to start the copy from
     * @param dx  X destination
     * @param dy  Y destination
     * @param w   width of the area to copy
     * @param h   height of the area to copy
     * @param rop raster operation.
     */
    void bitBlt(byte[] src, int sw, int sh, int sx, int sy, int dx, int dy, int w, int h, int rop);

    /**
     * Standard two input BitBlt function. Supports standard raster ops and
     * overlapping images. Images are held in native leJOS/Lego format.
     *
     * @param src byte array containing the source image
     * @param sw  Width of the source image
     * @param sh  Height of the source image
     * @param sx  X position to start the copy from
     * @param sy  Y Position to start the copy from
     * @param dst byte array containing the destination image
     * @param dw  Width of the destination image
     * @param dh  Height of the destination image
     * @param dx  X destination
     * @param dy  Y destination
     * @param w   width of the area to copy
     * @param h   height of the area to copy
     * @param rop raster operation.
     */
    void bitBlt(byte[] src, int sw, int sh, int sx, int sy, byte dst[], int dw, int dh, int dx, int dy, int w, int h, int rop);
}
