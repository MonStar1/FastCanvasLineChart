package com.epam.canvaschart;

public class GraphWidget {

    private double minX;
    private double maxX;
    private double minY;
    private double maxY;
    private int leftPadding;
    private int rightPadding;
    private int topPadding;
    private int bottomPadding;
    private double rangeX;
    private Number minRangeX;
    private Number maxRangeX;
    private Number minLeftXBorder;
    private Number maxRightXBorder;

    public double getMinX() {
        return minX;
    }

    public void setMinX(double minX) {
        this.minX = minX;
        rangeX = this.maxX - this.minX;
    }

    public double getMaxX() {
        return maxX;
    }

    public void setMaxX(double maxX) {
        this.maxX = maxX;
        rangeX = this.maxX - this.minX;
    }

    public Number getMinY() {
        return minY;
    }

    public void setMinY(double minY) {
        this.minY = minY;
    }

    public Number getMaxY() {
        return maxY;
    }

    public void setMaxY(double maxY) {
        this.maxY = maxY;
    }

    public double getXRange() {
        return rangeX;
    }

    public double getYRange() {
        return maxY - minY;
    }

    public int getLeftPadding() {
        return leftPadding;
    }

    public void setLeftPadding(int leftPadding) {
        this.leftPadding = leftPadding;
    }

    public int getRightPadding() {
        return rightPadding;
    }

    public void setRightPadding(int rightPadding) {
        this.rightPadding = rightPadding;
    }

    public int getTopPadding() {
        return topPadding;
    }

    public void setTopPadding(int topPadding) {
        this.topPadding = topPadding;
    }

    public int getBottomPadding() {
        return bottomPadding;
    }

    public void setBottomPadding(int bottomPadding) {
        this.bottomPadding = bottomPadding;
    }

    public Number getMinRangeX() {
        return minRangeX;
    }

    public void setMinRangeX(Number minRangeX) {
        this.minRangeX = minRangeX;
    }

    public Number getMaxRangeX() {
        return maxRangeX;
    }

    public void setMaxRangeX(Number maxRangeX) {
        this.maxRangeX = maxRangeX;
    }

    public Number getMinLeftXBorder() {
        return minLeftXBorder;
    }

    public void setMinLeftXBorder(Number minLeftXBorder) {
        this.minLeftXBorder = minLeftXBorder;
    }

    public Number getMaxRightXBorder() {
        return maxRightXBorder;
    }

    public void setMaxRightXBorder(Number maxRightXBorder) {
        this.maxRightXBorder = maxRightXBorder;
    }

}
