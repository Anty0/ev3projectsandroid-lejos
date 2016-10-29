package eu.codetopic.anty.ev3projectsbase.slam.base.map;

import eu.codetopic.anty.ev3projectsbase.slam.base.geometry.Rectangle;

public interface ByteMap {

    void set(int x, int y, byte to);

    byte get(int x, int y);

    Rectangle getBoundingRect();
}
