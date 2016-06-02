package com.epam.canvaschart.chart;

import android.view.View;

public interface IDataLoader {

    void getDataByXValue(Number startTimestamp, Number endTimestamp, ISubmitSeries submitSeries);

    double getStartAvailableXValue();

    double getEndAvailableXValue();

    void setProgressView(View progress);
}
