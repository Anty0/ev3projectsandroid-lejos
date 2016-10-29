package eu.codetopic.anty.ev3projectsbase.slam.base.scan;

import java.io.Serializable;

public class SeekResult implements Serializable {

    private final int channel;
    private final float beaconPosAngle;
    private final float distance;

    public SeekResult(int channel, float beaconPosAngle, float distance) {
        this.channel = channel;
        this.beaconPosAngle = beaconPosAngle;
        this.distance = distance;
    }

    public int getChannel() {
        return channel;
    }

    public float getBeaconPosAngle() {
        return beaconPosAngle;
    }

    public float getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return "SeekResult{" +
                "distance=" + distance +
                ", beaconPosAngle=" + beaconPosAngle +
                ", channel=" + channel +
                '}';
    }
}
