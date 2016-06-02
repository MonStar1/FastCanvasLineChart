package com.epam.canvaschart;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.List;
import java.util.SortedSet;

public class LineChart extends View implements View.OnTouchListener, ISubmitSeries {

    private static final int ZERO = 0;
    public static final TypeEvaluator<Double> DOUBLE_EVALUATOR = new TypeEvaluator<Double>() {

        @Override
        public Double evaluate(float v, Double start, Double end) {
            return start + v * (end - start);
        }
    };
    public static final int MAX_VISIBLE_POINTS = 100;
    private ValueAnimator valueAnimator;
    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetector mGestureDetector;

    private GraphWidget graphWidget;
    private Paint linePaint;
    private Paint textWhiteTopPaint;
    private Paint pointPaint;
    private Paint gridPaint;
    private Paint paddingPaint;
    private Paint textWhiteLeftPaint;
    private Path path = new Path();
    private double heightScale;
    private double widthScale;
    private PointPos startPointPos = new PointPos(ZERO, ZERO);
    private PointPos endPointPos = new PointPos(ZERO, ZERO);
    private boolean mIsScaleActive = false;//hardcode
    private int periodOfDrawingPoints = 1; //default value
    private int backgroundColor = Color.BLACK;
    private DateFormat dateFormat;
    private IDataLoader chartDataLoader;
    private Series series;
    private View secondProgress;

    public LineChart(Context context) {
        super(context);
        init(context);
    }

    public LineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LineChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    public void submitSeries(Series series) {
        this.series = series;
        postInvalidate();
    }

    private void init(Context context) {
        if (isInEditMode()) {
            return;
        }
        setOnTouchListener(this);
        backgroundColor = context.getResources().getColor(android.R.color.holo_blue_bright);
        mScaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureListener());
        mGestureDetector = new GestureDetector(getContext(), new ScrollGestureListener());

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(Color.WHITE);
        linePaint.setDither(true);
        linePaint.setStrokeJoin(Paint.Join.ROUND);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        linePaint.setPathEffect(new CornerPathEffect(20));
        linePaint.setStrokeWidth(3);

        textWhiteTopPaint = new Paint();
        textWhiteTopPaint.setColor(Color.LTGRAY);
        textWhiteTopPaint.setTextSize(30);
        textWhiteTopPaint.setTextAlign(Paint.Align.CENTER);

        textWhiteLeftPaint = new Paint();
        textWhiteLeftPaint.setColor(Color.LTGRAY);
        textWhiteLeftPaint.setTextSize(30);

        pointPaint = new Paint();
        pointPaint.setColor(Color.RED);
        pointPaint.setStrokeWidth(3);

        gridPaint = new Paint();
        gridPaint.setColor(Color.DKGRAY);
        gridPaint.setStrokeWidth(2);

        paddingPaint = new Paint();
        paddingPaint.setColor(backgroundColor);

        graphWidget = new GraphWidget();
    }

//    public void setSeries(Series series) {
//        this.series = series;
//        pointsSet = series.getPointsSet();
//        pointsList = series.getPointsList();
//        setMinMaxBorders(pointsSet.first().x, pointsSet.last().x);
//    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isInEditMode()) {
            return;
        }
        long TEMP_millis = System.currentTimeMillis();

        final int graphWidth = getGraphWidth();
        final int graphHeight = getGraphHeight();
        if (graphWidth < 1 || graphHeight < 1) {
            return;
        }
        widthScale = graphWidth / graphWidget.getXRange();
        heightScale = graphHeight / graphWidget.getYRange();

        canvas.drawColor(backgroundColor);

        drawGrid(canvas);
        Log.d("logoff", "Time draw GRID: " + (System.currentTimeMillis() - TEMP_millis));
        TEMP_millis = System.currentTimeMillis();
        drawChart(canvas);
        Log.d("logoff", "Time draw CHART: " + (System.currentTimeMillis() - TEMP_millis));

    }

    private void drawGrid(Canvas canvas) {
        canvas.save();
        canvas.translate(graphWidget.getLeftPadding(), graphWidget.getTopPadding());

        double gridXSize = graphWidget.getXRange() / 6;
        double gridYSize = graphWidget.getYRange() / 6;

        Number startX = graphWidget.getMinX();
        Number endX = graphWidget.getMaxX();
        Number startY = graphWidget.getMinY();
        Number endY = graphWidget.getMaxY();
        if (startX.longValue() == 0 && endX.longValue() == 0 && startY.longValue() == 0 && endY.longValue() == 0) {
            return;
        }
        DecimalFormat decimalFormat = new DecimalFormat("#");

        int counter = 0;
        for (double x = startX.doubleValue(); x <= endX.doubleValue(); x += gridXSize) {
            final float pointX = getPointX(x);
            canvas.drawLine(pointX, getPointY(startY), pointX, getPointY(endY), gridPaint);

            if (counter++ % 3 == 0) {
                canvas.save();
                canvas.translate(0, -graphWidget.getTopPadding());

                final Long value = Double.valueOf(x).longValue();
                String strValue = value.toString();
                if (dateFormat != null) {
                    strValue = dateFormat.format(value);
                }
                canvas.drawText(strValue, pointX, textWhiteTopPaint.getTextSize(), textWhiteTopPaint);

                canvas.restore();
            }
        }

        for (double y = startY.doubleValue(); y <= endY.doubleValue(); y += gridYSize) {
            final float pointY = getPointY(y);
            canvas.drawLine(getPointX(startX), pointY, getPointX(endX), pointY, gridPaint);

            canvas.save();
            canvas.translate(-graphWidget.getLeftPadding(), 0);

            canvas.drawText(decimalFormat.format(Double.valueOf(y)), 0, pointY, textWhiteLeftPaint);

            canvas.restore();
        }

        canvas.restore();
    }

    private void drawChart(Canvas canvas) {
        canvas.save();
        canvas.translate(graphWidget.getLeftPadding(), graphWidget.getTopPadding());
        canvas.clipRect(new Rect(0, 0, getGraphWidth(), getGraphHeight()));

        startPointPos.x = (long) (graphWidget.getMinX() - graphWidget.getXRange() / 2); //to draw 1/2 of screen size behind left border
        endPointPos.x = (long) (graphWidget.getMaxX() + graphWidget.getXRange() / 2); //to draw 1/2 of screen size behind right border

        if (chartDataLoader == null) {
            return;
        }

        chartDataLoader.getDataByXValue(startPointPos.x, endPointPos.x, this);

        if (series == null) {
            return;
        }
        synchronized (series) {
            SortedSet<PointPos> pointsSet = series.getPointsSet();
            SortedSet<PointPos> subSet = pointsSet.subSet(startPointPos, endPointPos);

            if (subSet.size() <= 1) {
                return;
            }

            final PointPos firstPointPos = subSet.first();
            final PointPos lastPointPos = subSet.last();

            int counter = periodOfDrawingPoints - firstPointPos.position % periodOfDrawingPoints;

            boolean firstPointFlag = true;

            List<PointPos> pointsList = series.getPointsList();
            List<PointPos> subList = pointsList.subList(firstPointPos.position, lastPointPos.position);
            for (int i = counter; i < subList.size(); i += periodOfDrawingPoints) {
                PointPos pp = subList.get(i);
                float x = getPointX(pp);
                float y = getPointY(pp);

                canvas.drawPoint(x, y, pointPaint);
                if (firstPointFlag) {
                    firstPointFlag = false;
                    path.moveTo(x, y);
                } else {
                    path.lineTo(x, y);
                }
            }
        }

        canvas.drawPath(path, linePaint);
        path.reset();

        canvas.restore();
    }

    private int getGraphWidth() {
        return getWidth() - graphWidget.getLeftPadding() - graphWidget.getRightPadding();
    }

    private int getGraphHeight() {
        return getHeight() - graphWidget.getTopPadding() - graphWidget.getBottomPadding();
    }

    public void setPeriodOfDrawingPoints(int periodOfDrawingPoints) {
        this.periodOfDrawingPoints = periodOfDrawingPoints;
    }

    public void setChartDataLoader(IDataLoader chartDataLoader) {
        this.chartDataLoader = chartDataLoader;
        initDataLoader(chartDataLoader);
    }

    private void initDataLoader(IDataLoader chartDataLoader) {
        chartDataLoader.setProgressView(secondProgress);
        final long startAvailableTimestamp = chartDataLoader.getStartAvailableXValue();
        final long endAvailableTimestamp = chartDataLoader.getEndAvailableXValue();
        setMinMaxBorders(startAvailableTimestamp, endAvailableTimestamp);
        scrollToXValue(startAvailableTimestamp);
    }

    public void setSecondProgress(View secondProgress) {
        this.secondProgress = secondProgress;
        if (chartDataLoader != null) {
            chartDataLoader.setProgressView(secondProgress);
        }
    }

    public void setDateFormat(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    /**
     * Left and right border for scrolling
     *
     * @param min
     * @param max
     */
    private void setMinMaxBorders(Number min, Number max) {
        setMinLeftBorder(min);
        setMaxRightBorder(max);
    }

    private void setMinLeftBorder(Number pointX) {
        final Number maxRightDomain = graphWidget.getMinLeftXBorder();
        if (maxRightDomain != null && maxRightDomain.doubleValue() < pointX.doubleValue()) {
            Log.d(LineChart.class.getName(), "Left domain can't be more than right domain. Left: " + pointX + " - Right: " + maxRightDomain);
        }
        graphWidget.setMinLeftXBorder(pointX);
    }

    private void setMaxRightBorder(Number pointX) {
        final Number minLeftDomain = graphWidget.getMinLeftXBorder();
        if (minLeftDomain != null && minLeftDomain.doubleValue() > pointX.doubleValue()) {
            Log.d(LineChart.class.getName(), "Right domain can't be less than left domain. Left: " + minLeftDomain + " - Right: " + pointX);
        }
        graphWidget.setMaxRightXBorder(pointX);
    }

    /**
     * Range from left side to right side for zooming
     *
     * @param minRange
     * @param maxRange
     */
    public void setMinMaxRange(Number minRange, Number maxRange) {
        setMinRange(minRange);
        setMaxRange(maxRange);
    }

    public void setMinRange(Number minRange) {
        if (minRange != null && minRange.doubleValue() < 0) {
            Log.d(LineChart.class.getName(), "Range can't be less than 0. Range: " + minRange);
        }
        Number maxRange = graphWidget.getMaxRangeX();
        if (minRange != null && maxRange != null && minRange.doubleValue() >= maxRange.doubleValue()) {
            Log.d(LineChart.class.getName(), "Min range need to be less than max range. Max range: " + maxRange + " - Min range: " + minRange);
        }
        graphWidget.setMinRangeX(minRange);
    }

    public void setMaxRange(Number maxRange) {
        final double minLeftDomain = graphWidget.getMinLeftXBorder().doubleValue();
        double maxRightDomain = graphWidget.getMaxRightXBorder().doubleValue();
        if (maxRange.doubleValue() > maxRightDomain - minLeftDomain) {
            Log.d(LineChart.class.getName(), "Max range can't be more than (maxRight - minLeft)=" + (maxRightDomain - minLeftDomain) + " Max range: " + maxRange);
        }
        final double minRangeX = graphWidget.getMinRangeX().doubleValue();
        if (maxRange.doubleValue() <= minRangeX) {
            Log.d(LineChart.class.getName(), "Min range need to be less than max range. Max range: " + maxRange + " - Min range: " + minRangeX);
        }
        graphWidget.setMaxRangeX(maxRange);
    }

    private float getPointX(Number valueX) {
        return (float) ((valueX.doubleValue() - graphWidget.getMinX()) * widthScale);
    }

    private float getPointY(Number valueY) {
        return (float) ((graphWidget.getMaxY().doubleValue() - valueY.doubleValue()) * heightScale);
    }

    private float getPointX(PointPos point) {
        return getPointX(point.x);
    }

    private float getPointY(PointPos point) {
        return getPointY(point.y);
    }

    public GraphWidget getGraphWidget() {
        return graphWidget;
    }

    public void setCurrentRange(Number range) {
        ValueAnimator valueAnimator = ValueAnimator.ofObject(DOUBLE_EVALUATOR, graphWidget.getXRange(), range.doubleValue());
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setDuration(300);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                final double animatedRange = (double) valueAnimator.getAnimatedValue();
                setRange(animatedRange);
            }
        });
        valueAnimator.start();
    }

    public void setRange(Number range) {
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        double selectedRange = graphWidget.getXRange();
        double domainMidPoint = graphWidget.getMinX() + selectedRange / 2;
        double minRange = graphWidget.getMinRangeX().doubleValue();
        double maxRange = graphWidget.getMaxRangeX().doubleValue();

        final double rangeD = range.doubleValue();
        if (rangeD < minRange) {
            Log.w(LineChart.class.getName(), "Range can't be less than min range. Min range: " + minRange + " - Range: " + range);
            return;
        } else if (rangeD > maxRange) {
            Log.w(LineChart.class.getName(), "Range can't be more than max range. Max range: " + maxRange + " - Range: " + range);
            return;
        } else {
            selectedRange = rangeD;
        }

        final double halfRange = selectedRange / 2;
        double minX = domainMidPoint - halfRange;
        double maxX = domainMidPoint + halfRange;

        graphWidget.setMinX(minX);
        graphWidget.setMaxX(maxX);

        clampToDomainBorder(minX, maxX, false);

        invalidate();
    }

    private void clampToDomainBorder(double newMinX, double newMaxX, boolean isZoom) {
        double selectedRange = graphWidget.getXRange();
        double localSelectedRange = selectedRange;
        if (isZoom) {
            localSelectedRange = newMaxX - newMinX;
        }
        final double halfRange = selectedRange / 2;

        final double mMinLeftDomain = graphWidget.getMinLeftXBorder().doubleValue();
        final double mMaxRightDomain = graphWidget.getMaxRightXBorder().doubleValue();

        double mMinLeftDomainWithHalfRange = mMinLeftDomain - halfRange;
        double mMaxRightDomainWithHalfRange = mMaxRightDomain + halfRange;

        double minX = graphWidget.getMinX();
        double maxX = graphWidget.getMaxX();

        if (newMinX < mMinLeftDomainWithHalfRange) {
            minX = mMinLeftDomainWithHalfRange;
            double difference = 0;
            if (isZoom) {
                difference = mMinLeftDomainWithHalfRange - newMinX; //calculate difference if try to scale near the border
            }
            maxX = minX + localSelectedRange + difference;

        } else if (newMaxX > mMaxRightDomainWithHalfRange) {
            maxX = mMaxRightDomainWithHalfRange;
            double difference = 0;
            if (isZoom) {
                difference = newMaxX - mMaxRightDomainWithHalfRange; //calculate difference if try to scale near the border
            }
            minX = maxX - localSelectedRange - difference;
        }
        graphWidget.setMinX(minX);
        graphWidget.setMaxX(maxX);
    }

    public void scrollTo(double pointX) {
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        scrollToXValue(pointX);
    }

    /**
     * @param pointX - point in center of screen
     */
    private void scrollToXValue(double pointX) {
        final double halfRange = graphWidget.getXRange() / 2;
        final double minLeftXBorder = graphWidget.getMinLeftXBorder().doubleValue();
        if (pointX < minLeftXBorder - halfRange) {
            Log.w(LineChart.class.getName(), "Can't scroll outside left border. Target point: " + pointX + " - Left border: " + minLeftXBorder);
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
        } else {
            final double maxRightXBorder = graphWidget.getMaxRightXBorder().doubleValue();
            if (pointX > maxRightXBorder + halfRange) {
                Log.w(LineChart.class.getName(), "Can't scroll outside right border. Target point: " + pointX + " - Right border: " + maxRightXBorder);
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
            }
        }

        clampToDomainBoundsScroll(pointX - graphWidget.getMinX() - halfRange);

        invalidate();
    }

    private void clampToDomainBoundsScroll(double offset) {
        double newMinX = graphWidget.getMinX() + offset;
        double newMaxX = graphWidget.getMaxX() + offset;

        final double halfRange = graphWidget.getXRange() / 2;

        double mMinLeftDomainWithHalfRange = graphWidget.getMinLeftXBorder().doubleValue() - halfRange;
        double mMaxRightDomainWithHalfRange = graphWidget.getMaxRightXBorder().doubleValue() + halfRange;

        if (newMinX >= mMinLeftDomainWithHalfRange && newMaxX <= mMaxRightDomainWithHalfRange) {
            graphWidget.setMinX(newMinX);
            graphWidget.setMaxX(newMaxX);
        } else {
            clampToDomainBorder(newMinX, newMaxX, false);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
//        TODO: scale does not support
        if (BuildConfig.DEBUG) {
            mScaleGestureDetector.onTouchEvent(motionEvent);
        }
        if (!mIsScaleActive) {
            mGestureDetector.onTouchEvent(motionEvent);
        }
        return true;
    }

    private class ScrollGestureListener extends GestureDetector.SimpleOnGestureListener {

        private double mSelectedRange;

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            scroll(distanceX);
            return true;
        }

        private void scroll(float distance) {
            mSelectedRange = graphWidget.getXRange();
            double step = mSelectedRange / getGraphWidth();
            double offset = distance * step;

            clampToDomainBoundsScroll(offset);

            invalidate();
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (Math.abs(velocityX) < 500) {
                return true;
            }
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            final double halfRange = mSelectedRange / 2;
            final double to = graphWidget.getMinX() - velocityX * mSelectedRange / 750 + halfRange;
            valueAnimator = ValueAnimator.ofObject(DOUBLE_EVALUATOR, graphWidget.getMinX() + halfRange, to);
            valueAnimator.setDuration(Math.abs((long) (velocityX / 2.5)));
            valueAnimator.setInterpolator(new DecelerateInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    final double animatedValue = (double) valueAnimator.getAnimatedValue();
                    scrollToXValue(animatedValue);
                }
            });
            valueAnimator.start();
            return true;
        }

    }

    private class ScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        private double mSelectedRange;
        private double mScaleFactor;
        private double mBeginSpan;
        private double mDomainMidPoint;
        private double oldMinX;
        private double oldMaxX;

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            mIsScaleActive = true;
            mSelectedRange = graphWidget.getXRange();

            mDomainMidPoint = graphWidget.getMinX() + mSelectedRange / 2;
            mBeginSpan = detector.getCurrentSpan();

            return super.onScaleBegin(detector);
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor = mBeginSpan / detector.getCurrentSpan();
            mScaleFactor = Math.max(0.1d, Math.min(2d, mScaleFactor));

            zoom(mScaleFactor);

            return true;
        }

        private void zoom(double scale) {
            double offset = mSelectedRange * scale / 2;

            double minX = graphWidget.getMinX();
            double maxX = graphWidget.getMaxX();
            oldMinX = minX;
            oldMaxX = maxX;

            minX = mDomainMidPoint - offset;
            maxX = mDomainMidPoint + offset;

            graphWidget.setMinX(minX); // -50 = 50 - 100
            graphWidget.setMaxX(maxX); // 150 = 50 + 100

            clampToDomainBoundsZoom();
            clampToDomainBorder(minX, maxX, true);

            invalidate();
        }

        private void clampToDomainBoundsZoom() {
            double minX = graphWidget.getMinX();
            double maxX = graphWidget.getMaxX();
            double range = Math.abs(minX - maxX);
            if (range > graphWidget.getMaxRangeX().doubleValue()) {
                minX = oldMinX;
                maxX = oldMaxX;
            } else if (range < graphWidget.getMinRangeX().doubleValue()) {
                minX = oldMinX;
                maxX = oldMaxX;
            }
            graphWidget.setMinX(minX);
            graphWidget.setMaxX(maxX);
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            super.onScaleEnd(detector);
            mIsScaleActive = false;
//          reInitDomain();
        }

    }
}
