package com.epam.canvaschart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;

public class Series {

    private final AtomicBoolean isPointPosCalculated = new AtomicBoolean(false);
    private final SortedSet<PointPos> pointsSet = Collections.synchronizedSortedSet(new TreeSet<PointPos>());
    private final List<PointPos> pointsList = Collections.synchronizedList(new ArrayList<PointPos>());

    public SortedSet<PointPos> getPointsSet() {
        if (!isPointPosCalculated.get()) {
            throw new IllegalStateException("Points positions didn't calculate. Call calculatePointPositions() after adding all points");
        }
        return pointsSet;
    }

    public List<PointPos> getPointsList() {
        if (!isPointPosCalculated.get()) {
            throw new IllegalStateException("Points positions didn't calculate. Call calculatePointPositions() after adding all points");
        }
        return pointsList;
    }

    public void addPoint(long x, float y) {
        isPointPosCalculated.set(false);
        PointPos pointPos = new PointPos(x, y);
        pointsSet.add(pointPos);
    }

    public void calculatePointPositions() {
        int pos = 0;
        pointsList.clear();
        for (PointPos pointPos : pointsSet) {
            pointPos.position = pos++;
            pointsList.add(pointPos);
        }
        isPointPosCalculated.set(true);
    }
}
