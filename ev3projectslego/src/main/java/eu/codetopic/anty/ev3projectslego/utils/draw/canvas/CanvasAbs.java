package eu.codetopic.anty.ev3projectslego.utils.draw.canvas;

import java.util.ArrayList;
import java.util.List;

import eu.codetopic.anty.ev3projectslego.utils.Rectangle2DInt;
import eu.codetopic.anty.ev3projectslego.utils.draw.drawer.CommonDrawer;
import eu.codetopic.anty.ev3projectslego.utils.draw.drawer.CommonDrawerImpl;
import eu.codetopic.anty.ev3projectslego.utils.draw.drawer.GraphicsDrawer;
import eu.codetopic.anty.ev3projectslego.utils.draw.drawer.GraphicsDrawerImpl;
import eu.codetopic.anty.ev3projectslego.utils.draw.drawer.TextDrawer;
import eu.codetopic.anty.ev3projectslego.utils.draw.drawer.TextDrawerImpl;

abstract class CanvasAbs implements Canvas {

    protected final Rectangle2DInt drawingArea;
    protected final byte[] displayBuf;
    private final List<Canvas> children = new ArrayList<>();
    protected GraphicsDrawer graphicsDrawer = null;
    protected TextDrawer textDrawer = null;
    protected boolean visible = true;

    public CanvasAbs(Rectangle2DInt drawingArea) {
        this.drawingArea = drawingArea;
        displayBuf = CommonDrawerImpl.generateBuffer(drawingArea
                .getHeight(), drawingArea.getWidth());
    }

    @Override
    public Canvas[] getChildren() {
        synchronized (children) {
            return children.toArray(new Canvas[children.size()]);
        }
    }

    @Override
    public Canvas createRestrictedCanvas(Rectangle2DInt restrictedArea) {
        if (!drawingArea.contains(restrictedArea))
            throw new IllegalArgumentException("restricted area bust be in canvas drawing area");
        Canvas child = new CanvasChild(this, restrictedArea);
        synchronized (children) {
            children.add(child);
        }
        return child;
    }

    @Override
    public void remove(Canvas canvas) {
        synchronized (children) {
            children.remove(canvas);
        }
    }

    @Override
    public void bump(Canvas canvas) {
        synchronized (children) {
            if (children.remove(canvas)) {
                children.add(canvas);
            }
        }
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public Rectangle2DInt getParentPosition() {
        return drawingArea;
    }

    @Override
    public byte[] getContent() {
        return displayBuf;
    }

    @Override
    public GraphicsDrawer getGraphicsDrawer() {
        if (graphicsDrawer == null) graphicsDrawer = new GraphicsDrawerImpl(
                drawingArea.getHeight(), drawingArea.getWidth(), displayBuf);
        return graphicsDrawer;
    }

    @Override
    public TextDrawer getTextDrawer() {
        if (textDrawer == null) textDrawer = new TextDrawerImpl(
                drawingArea.getHeight(), drawingArea.getWidth(), displayBuf);
        return textDrawer;
    }

    @Override
    public CommonDrawer getDrawer() {
        if (textDrawer != null) return textDrawer;
        return getGraphicsDrawer();
    }
}
