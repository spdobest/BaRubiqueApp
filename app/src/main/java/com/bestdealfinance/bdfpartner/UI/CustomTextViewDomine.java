package com.bestdealfinance.bdfpartner.UI;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.R;

/**
 * Created by disha on 10/3/16.
 */
public class CustomTextViewDomine extends TextView {

    private final static int DOMINE = 0;
    private final static int PROXIMA = 1;

    private final static SparseArray<Typeface> mTypefaces = new SparseArray<Typeface>(3);

    public CustomTextViewDomine(Context context) {
        super(context);
    }

    public CustomTextViewDomine(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttributes(context, attrs);
    }

    public CustomTextViewDomine(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        parseAttributes(context, attrs);
    }

    private void parseAttributes(Context context, AttributeSet attrs) {


        setTypeface(Typeface.createFromAsset(context.getAssets(),  getResources().getString(R.string.domine)));
    }

//    private Typeface obtaintTypeface(Context context, int typefaceValue) throws IllegalArgumentException {
//        Typeface typeface = mTypefaces.get(typefaceValue);
//        if (typeface == null) {
//            typeface = createTypeface(context, typefaceValue);
//            mTypefaces.put(typefaceValue, typeface);
//        }
//        return typeface;
//    }
//
//    private Typeface createTypeface(Context context, int typefaceValue) throws IllegalArgumentException {
//        Typeface typeface;
//        switch (typefaceValue) {
//            case DOMINE:
//                typeface = Typeface.createFromAsset(context.getAssets(), "domine.ttf");
//                break;
//
//            case PROXIMA:
//                typeface = Typeface.createFromAsset(context.getAssets(), "proxima.ttf");
//                break;
//            default:
//                throw new IllegalArgumentException("Unknown `typeface` attribute value " + typefaceValue);
//        }
//        return typeface;
//    }

}