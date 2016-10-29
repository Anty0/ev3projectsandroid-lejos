package eu.codetopic.anty.ev3projectsbase.slam.base.geometry;

public class Range {

    private static final String LOG_TAG = "Range";

    private final float start, end;

    public Range(float start, float end) {
        if (start > end) throw new IllegalArgumentException("start can't be bigger then end");
        this.start = start;
        this.end = end;
    }

    public float getStart() {
        return start;
    }

    public float getEnd() {
        return end;
    }

    public boolean contains(float i) {
        return i >= start && i < end;
    }
}
