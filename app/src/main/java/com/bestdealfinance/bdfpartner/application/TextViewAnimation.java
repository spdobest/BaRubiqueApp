package com.bestdealfinance.bdfpartner.application;

import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.TextView;

public class TextViewAnimation extends Animation {

    private TextView textView;

    private float newProgress;

    public TextViewAnimation(TextView arcView, int newProgress) {
        this.textView = arcView;
        this.newProgress = newProgress;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation transformation) {
        int progress = (int) (0 + ((newProgress) * interpolatedTime));
        textView.setText(progress);
    }
}