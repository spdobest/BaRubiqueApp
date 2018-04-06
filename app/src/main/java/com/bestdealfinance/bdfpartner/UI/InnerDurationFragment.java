package com.bestdealfinance.bdfpartner.UI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.Logs;
import com.bestdealfinance.bdfpartner.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vikas on 25/3/16.
 *
 */
public class InnerDurationFragment extends DialogFragment {
    int date ;
    DatePicker picker;
    EditText editText;
    List<String> categories_month = new ArrayList<String>();
    List<String> categories_years = new ArrayList<String>();
    Context mcontext;
    Spinner month, year;
    int select_month=0;
    int select_year=0;

    public InnerDurationFragment() {
    }
    @SuppressLint("ValidFragment")
    public InnerDurationFragment(EditText editText, Context context) {
        this.editText=editText;
        this.mcontext=context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.duration_fragment, container, false);
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        month = rootView.findViewById(R.id.spinner_month);
        year = rootView.findViewById(R.id.spinner_year);
        for (int i=0;i<12;i++){
            categories_month.add(String.valueOf(i));
        }
        for (int i=0;i<30;i++){
            categories_years.add(String.valueOf(i));
        }

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<String>(mcontext, android.R.layout.simple_spinner_item, categories_month);
        final ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(mcontext, android.R.layout.simple_spinner_item, categories_years);
        month.setAdapter(monthAdapter);
        year.setAdapter(yearAdapter);
        month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                select_month = parent.getSelectedItemPosition();
                month.setSelection(position);
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                select_year=parent.getSelectedItemPosition();
                year.setSelection(position);
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button button = rootView.findViewById(R.id.button7);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO get the Date
                editText.setText(getDate());
                dismiss();
            }
        });
        return rootView;
    }

    public String getDate() {
        date=select_month+12*select_year;
        Logs.LogD("Date","deate= "+date);
        return String.valueOf(date);
    }

}
