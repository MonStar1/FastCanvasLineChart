package com.epam.canvaschart;

import android.view.View;

public interface IDataLoader {

    void getDataByXValue(Number startTimestamp, Number endTimestamp, ISubmitSeries submitSeries);

    long getStartAvailableXValue();

    long getEndAvailableXValue();

    void setProgressView(View progress);
}
