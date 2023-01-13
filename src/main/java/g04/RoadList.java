package g04;

import java.util.ArrayList;
import java.util.HashSet;

@SuppressWarnings("serial")
public class RoadList extends ArrayList<Road> {
    public void addRoad(Road road) {
        add(road);
    }
    public void removeRoad(Road road) {
        remove(road);
    }
}

