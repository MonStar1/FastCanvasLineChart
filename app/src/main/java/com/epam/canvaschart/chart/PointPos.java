package com.epam.canvaschart.chart;

public class PointPos implements Comparable<PointPos> {

    public PointPos(double x, float y) {
        this.x = x;
        this.y = y;
    }

    public double x;
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
