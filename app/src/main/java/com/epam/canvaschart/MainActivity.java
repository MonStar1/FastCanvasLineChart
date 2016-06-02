package com.epam.canvaschart;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.epam.canvaschart.chart.GraphWidget;
import com.epam.canvaschart.chart.LineChart;

public class MainActivity extends AppCompatActivity {

    private static final long POINTS_SIZE = 100000;
    public static final int MIN_RANGE = 500;
    public static final int MAX_RANGE = 1000;
    private LineChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mChart = (LineChart) findViewById(R.id.chart);
        setupData();

        findViewById(R.id.date).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mChart.scrollTo(1000);
            }
        });
        findViewById(R.id.range_1).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mChart.setRange(MIN_RANGE);
            }
        });

        findViewById(R.id.range_2).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mChart.setRange(MAX_RANGE);
            }
        });
    }

    private void setupData() {
        ChartAssetsDataLoader chartAssetsDataLoader = new ChartAssetsDataLoader(this);
        mChart.setChartDataLoader(chartAssetsDataLoader);

        final GraphWidget graphWidget = mChart.getGraphWidget();
        graphWidget.setMinY(0);
        graphWidget.setMaxY(100);
        graphWidget.setLeftPadding(100);
        graphWidget.setTopPadding(100);
        graphWidget.setRightPadding(100);
        graphWidget.setBottomPadding(100);

        mChart.setPeriodOfDrawingPoints(10);

        mChart.setMinMaxRange(MIN_RANGE, MAX_RANGE);
        mChart.setRange(MIN_RANGE);
        mChart.setBackgroundColor(Color.BLACK);
//        mChart.setDateFormat(new SimpleDateFormat("dd MMM HH:mm:ss"));
    }

}
