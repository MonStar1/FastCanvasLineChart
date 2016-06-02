package com.epam.canvaschart;

import android.content.Context;
import android.view.View;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class ChartAssetsDataLoader implements IDataLoader {

    private final long POINTS_SIZE = 50000;
    private Context context;
    private View progress;

    private final Series series = new Series();
    private final AtomicInteger countLoadingInProgress = new AtomicInteger(0);

    public ChartAssetsDataLoader(Context context) {
        this.context = context;
        for (long i = 0; i < POINTS_SIZE; i += (new Random()).nextInt(5)) {
            series.addPoint(i, (new Random()).nextInt(100));
        }
        series.calculatePointPositions();
    }

    @Override
    public void getDataByXValue(Number startX, Number endX, ISubmitSeries submitSeries) {
        submitSeries.submitSeries(series);
    }

    @Override
    public long getStartAvailableXValue() {
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
    public long getEndAvailableXValue() {
        return series.getPointsSet().last().x;
    }
}
