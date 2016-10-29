package eu.codetopic.anty.ev3projectsbase.slam.base.move;

public class RotateMove implements Move {

    private static final String LOG_TAG = "RotateMove";

    private final float angle;

    public RotateMove(float angle) {
        this.angle = angle;
    }

    public float getAngle() {
        return angle;
    }
}
