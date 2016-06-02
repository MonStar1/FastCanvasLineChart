package com.epam.canvaschart;

public class PointPos implements Comparable<PointPos> {

    public PointPos(long x, float y) {
        this.x = x;
        this.y = y;
    }

    public long x;
    public float y;
    public int position;

    @Override
    public boolean equals(Object o) {
        PointPos obj = (PointPos) o;
        return obj.x == this.x;
    }

    @Override
    public int compareTo(PointPos pointPos) {
        if (x < pointPos.x) {
            return -1;
        } else if (x > pointPos.x) {
            return 1;
        }
        return 0;
    }
}
