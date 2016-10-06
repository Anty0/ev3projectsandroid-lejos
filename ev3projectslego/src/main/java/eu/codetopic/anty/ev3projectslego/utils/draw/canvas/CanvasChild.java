package eu.codetopic.anty.ev3projectslego.utils.draw.canvas;

import eu.codetopic.anty.ev3projectslego.utils.Rectangle2DInt;

class CanvasChild extends CanvasAbs {

    private Canvas parent;

    CanvasChild(Canvas parent, Rectangle2DInt drawingArea) {
        super(drawingArea);
        this.parent = parent;
    }

    @Override
    public Canvas getParent() {
        return parent;
    }

    @Override
    public void removeSelf() {
        parent.remove(this);
        parent.apply();
        parent = null;
    }

    @Override
    public void bumpSelf() {
        parent.bump(this);
        parent.apply();
    }

    @Override
    public void apply() {
        parent.apply();
    }
}
