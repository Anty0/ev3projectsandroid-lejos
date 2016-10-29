package eu.codetopic.anty.ev3projectsbase.slam.base;

import eu.codetopic.anty.ev3projectsbase.slam.base.map.OccupancyMap;
import eu.codetopic.anty.ev3projectsbase.slam.base.map.RectangleMap;
import eu.codetopic.anty.ev3projectsbase.slam.base.map.TileBasedMap;

public interface SlamMap extends TileBasedMap, OccupancyMap, RectangleMap {
}
