package eu.codetopic.anty.ev3projectslego.utils.draw.canvas;

import eu.codetopic.anty.ev3projectslego.utils.Rectangle2DInt;
import eu.codetopic.anty.ev3projectslego.utils.draw.drawer.CommonDrawer;
import eu.codetopic.anty.ev3projectslego.utils.draw.drawer.GraphicsDrawer;
import eu.codetopic.anty.ev3projectslego.utils.draw.drawer.TextDrawer;
import lejos.hardware.lcd.GraphicsLCD;

public interface Canvas {

    static Canvas obtain(GraphicsLCD graphicsLCD, boolean drawRectangleAroundChildren) {
        return new CanvasBase(graphicsLCD, drawRectangleAroundChildren);
    }

    static Canvas obtain(boolean drawRectangleAroundChildren) {
        return new CanvasBase(drawRectangleAroundChildren);
    }

    Canvas getParent();

    Canvas[] getChildren();

    Canvas createRestrictedCanvas(Rectangle2DInt restrictedArea);

    void remove(Canvas canvas);

    void removeSelf();

    void bump(Canvas canvas);

    void bumpSelf();

    boolean isVisible();

    void setVisible(boolean visible);

    Rectangle2DInt getParentPosition();

    byte[] getContent();

    GraphicsDrawer getGraphicsDrawer();

    TextDrawer getTextDrawer();

    CommonDrawer getDrawer();

    void apply();
}
