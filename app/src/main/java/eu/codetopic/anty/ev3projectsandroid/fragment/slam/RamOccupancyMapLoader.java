package eu.codetopic.anty.ev3projectsandroid.fragment.slam;

import java.util.HashMap;
import java.util.Map;

import eu.codetopic.anty.ev3projectsbase.slam.base.map.FoldingOccupancyMap;
import eu.codetopic.anty.ev3projectsbase.slam.base.map.OccupancyMapImpl;

public class RamOccupancyMapLoader implements FoldingOccupancyMap.OccupancyMapLoader<OccupancyMapImpl> {// this class completely looses its meaning

    private static final String LOG_TAG = "RamOccupancyMapLoader";

    private final Map<MapId, OccupancyMapImpl> maps = new HashMap<>();

    @Override
    public boolean isMapCreated(int idX, int idY) {
        return maps.containsKey(new MapId(idX, idY));
    }

    @Override
    public void saveMap(int idX, int idY, OccupancyMapImpl map) {
        maps.put(new MapId(idX, idY), map);
        /*Log.d(LOG_TAG, "createOrLoadMap: cached map for idX=" + idX + ", idY=" + idY
                    + ", " + maps.size() + " maps is cached now");*/
    }

    @Override
    public OccupancyMapImpl createOrLoadMap(int idX, int idY, int mapWidth, int mapHeight) {
        MapId mapId = new MapId(idX, idY);
        OccupancyMapImpl map = maps.get(mapId);
        if (map == null) {
            /*Log.d(LOG_TAG, "createOrLoadMap: creating map for idX=" + idX + ", idY=" + idY
                    + ", " + maps.size() + " maps is cached now");*/
            map = new OccupancyMapImpl(mapWidth, mapHeight);
        }
        return map;
    }

    private static class MapId {

        final int x, y;

        private MapId(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + y;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj) || obj instanceof MapId
                    && ((MapId) obj).x == x
                    && ((MapId) obj).y == y;
        }
    }
}
