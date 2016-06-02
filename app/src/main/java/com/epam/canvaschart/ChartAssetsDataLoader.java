package com.epam.canvaschart;

import android.content.Context;
import android.view.View;

import com.epam.canvaschart.chart.IDataLoader;
import com.epam.canvaschart.chart.ISubmitSeries;
import com.epam.canvaschart.chart.Series;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class ChartAssetsDataLoader implements IDataLoader {

    private final double POINTS_SIZE = 100000000000f + 50000f;
    private Context context;
    private View progress;

    private final Series series = new Series();
    private final AtomicInteger countLoadingInProgress = new AtomicInteger(0);

    public ChartAssetsDataLoader(Context context) {
        this.context = context;
        for (double i = 100000000000f; i < POINTS_SIZE; i += (new Random()).nextDouble()) {
            series.addPoint(i, (new Random()).nextInt(100));
        }
        series.calculatePointPositions();
    }

    @Override
    public void getDataByXValue(Number startX, Number endX, ISubmitSeries submitSeries) {
        submitSeries.submitSeries(series);
    }

    @Override
    public double getStartAvailableXValue() {
        return series.getPointsSet().first().x;
    }

    public boolean isLoadingProgress() {
        return countLoadingInProgress.get() != 0;
    }

    @Override
    public void setProgressView(View progress) {
        this.progress = progress;
    }

    @Override
    public double getEndAvailableXValue() {
        return series.getPointsSet().last().x;
    }
}
