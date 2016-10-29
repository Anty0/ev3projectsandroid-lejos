package eu.codetopic.anty.ev3projectsbase.slam.base.move;

public class TravelMove implements Move {

    private static final String LOG_TAG = "TravelMove";

    private final float distance;

    public TravelMove(float distance) {
        this.distance = distance;
    }

    public float getDistance() {
        return distance;
    }
}
