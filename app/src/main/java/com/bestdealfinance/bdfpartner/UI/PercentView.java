package com.bestdealfinance.bdfpartner.UI;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

import com.bestdealfinance.bdfpartner.Logs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by devesh on 15/4/15.
 */
public class PercentView extends View {
    final float width = 15;
    Paint paint;
    Paint paint_outer;
//    Paint text;
    float percentage = 90;
    ArrayList<Integer> count = new ArrayList<>();
    ArrayList<String> colors = new ArrayList<>();
    private Bundle bundle;
    private RectF rect;
    private float[] percent;
    private RectF rect_out;
    private List<Paint> paintList;
//    private List<Paint> paintList_inner;

    public PercentView(Context context) {
        super(context);
        //initViews();
    }

    public PercentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //initViews();
    }

    public PercentView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //initViews();
    }

    public void setData(ArrayList<Integer> mcount, ArrayList <String>mcolors, int total) {
        Logs.LogD("Values",mcount.toString());
        Logs.LogD("Values","Total: "+total);
        init(mcount, mcolors, total);

    }
    private void init(ArrayList<Integer> mcount, ArrayList<String> mcolors, int total) {
        count=mcount;
        colors=mcolors;
        percent = new float[count.size()];
//TODO Full the Percent Values.

        for (int i=0;i<count.size();i++){
            percent[i] = (float) ((double) count.get(i)/ (double) total) * 360;
        }

        paintList = new ArrayList<>();
        for (int i=0;i<count.size();i++){
            Paint temp = new Paint();
            temp.setColor(Color.parseColor(colors.get(i)));
            temp.setAntiAlias(true);
            temp.setStyle(Paint.Style.STROKE);
            paintList.add(temp);
        }

//        text = new Paint();
//        text.setColor(getContext().getResources().getColor(R.color.WhiteSmoke));
//        text.setAntiAlias(true);
//        text.setStyle(Paint.Style.FILL);

//        paint_outer = new Paint();
//        paint_outer.setColor(Color.parseColor("#27c1e6"));
//        paint_outer.setAntiAlias(true);
//        paint_outer.setStyle(Paint.Style.STROKE);
//        paint_outer.setStrokeWidth(width);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        rect = new RectF();
        rect_out = new RectF();
        int inRadius = getHeight() / 2 - getHeight() / 6;
        int outRadius = inRadius + (getHeight() / 20);
        rect.set(getHeight() / 2 - outRadius, getHeight() / 2 - outRadius, getHeight() / 2 + outRadius, getHeight() / 2 + outRadius);
        rect_out.set((getHeight() / 2 - inRadius), getHeight() / 2 - inRadius, getHeight() / 2 + inRadius, getHeight() / 2 + inRadius);

        Logs.LogD("stroke", Integer.toString(getHeight() / 20));


//        canvas.drawArc(rect, 0, 360, false, paint);
//        canvas.drawArc(rect_out, 0, 360, false, paint_outer);
        float current = 0;
        for (int i = 0; i < count.size(); i++) {
//            Paint temp_inner = paintList_inner.get(i);
            Logs.LogD("Making","Percent: "+percent[i]);
            Paint temp = paintList.get(i);
            temp.setStrokeWidth(getHeight() / 9);
            canvas.drawArc(rect, current, percent[i], false, temp);
//            canvas.drawArc(rect_out, current, percent[i], false, temp_inner);
            current = current + percent[i];
        }

    }
}