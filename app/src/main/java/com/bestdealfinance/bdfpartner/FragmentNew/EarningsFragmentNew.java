package com.bestdealfinance.bdfpartner.FragmentNew;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.application.ToolbarHelper;
import com.bestdealfinance.bdfpartner.base.BaseFragment;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

/**
 * Created by rbq on 10/20/16.
 */

public class EarningsFragmentNew extends BaseFragment {

    public static final String TAG = "EarningsFragmentNew";
    private LineChart mChart;

    public EarningsFragmentNew() {
    }

    public static EarningsFragmentNew newInstance() {
        Bundle args = new Bundle();
        EarningsFragmentNew fragment = new EarningsFragmentNew();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_earnings_new, container, false);

        // here initialize the widgets
        initViews(rootView);

        mChart.setDrawGridBackground(false);
        mChart.getXAxis().disableGridDashedLine();
        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getAxisRight().setDrawGridLines(false);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.setGridBackgroundColor(Color.TRANSPARENT);

        mChart.getXAxis().setDrawAxisLine(false);
        mChart.getAxisRight().setDrawAxisLine(false);


        mChart.getAxisLeft().setDrawLabels(false);
        mChart.getAxisRight().setDrawLabels(false);
        mChart.getXAxis().setDrawLabels(true);
        mChart.getLegend().setEnabled(false);
        //mChart.getXAxis().setDrawGridLines(true);
        mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        // add data
        setData();

        // get the legend (only possible after setting data)
        //Legend l = mChart.getLegend();

        // modify the legend ...
        //l.setPosition(LegendPosition.LEFT_OF_CHART);
        //l.setForm(Legend.LegendForm.LINE);
        //l.setEnabled(false);

        // the labels that should be drawn on the XAxis
        final String[] quarters = new String[]{"", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return quarters[(int) value];
            }

            // we don't draw numbers, so no decimal digits needed
            @Override
            public int getDecimalDigits() {
                return 0;
            }
        };

        XAxis xAxis = mChart.getXAxis();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);

        mChart.animateX(500, Easing.EasingOption.EaseOutSine);
        mChart.invalidate();
        return rootView;
    }

    private ArrayList<String> setXAxisValues() {
        ArrayList<String> xVals = new ArrayList<String>();
        xVals.add("Jan");
        xVals.add("Feb");
        xVals.add("Mar");
        xVals.add("Apr");
        xVals.add("May");
        xVals.add("Jun");

        return xVals;
    }

    private ArrayList<Entry> setYAxisValues() {
        ArrayList<Entry> yVals = new ArrayList<Entry>();

        yVals.add(new Entry(1, 2000));
        yVals.add(new Entry(2, 4000));
        yVals.add(new Entry(3, 5000));
        yVals.add(new Entry(4, 3000));
        yVals.add(new Entry(5, 4000));
        yVals.add(new Entry(6, 6000));

        return yVals;
    }

    private void setData() {
        ArrayList<String> xVals = setXAxisValues();

        ArrayList<Entry> yVals = setYAxisValues();

        LineDataSet lineDataSet;

        // create a dataset and give it a type
        lineDataSet = new LineDataSet(yVals, "DataSet 1");
        lineDataSet.setFillAlpha(110);
        // set1.setFillColor(Color.RED);

        // set the line to be drawn like this "- - - - - -"
        // set1.enableDashedLine(10f, 5f, 0f);
        // set1.enableDashedHighlightLine(10f, 5f, 0f);
        lineDataSet.setColor(ContextCompat.getColor(getActivity(), R.color.Blue500));
        lineDataSet.setCircleColor(ContextCompat.getColor(getActivity(), R.color.Blue500));
        lineDataSet.setLineWidth(2f);
        lineDataSet.setCircleRadius(5f);
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setCircleHoleRadius(3f);
        lineDataSet.setValueTextSize(9f);
        lineDataSet.setDrawFilled(true);

        //set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(lineDataSet); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(dataSets);
        // set data
        mChart.setData(data);

    }

    @Override
    public void initViews(View rootView) {
        mChart = (LineChart) rootView.findViewById(R.id.linechart);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.new_toolbar);
        ToolbarHelper.initializeToolbar(getActivity(), toolbar, "EARNINGS", false, false, true);
    }

    @Override
    public void setClickListener() {

    }
}
