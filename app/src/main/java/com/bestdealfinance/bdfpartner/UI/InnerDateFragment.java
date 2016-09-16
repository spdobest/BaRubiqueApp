package com.bestdealfinance.bdfpartner.UI;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.bestdealfinance.bdfpartner.Logs;
import com.bestdealfinance.bdfpartner.R;

/**
 * Created by vikas on 25/3/16.
 */
public class InnerDateFragment extends DialogFragment {
    String date = "";
    DatePicker picker;
    EditText editText;
    boolean offset;

    public InnerDateFragment() {
    }
    @SuppressLint("ValidFragment")
    public InnerDateFragment(EditText editText, boolean offset) {
        this.editText=editText;
        this.offset=offset;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.date_fragment, container, false);
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        picker = (DatePicker) rootView.findViewById(R.id.datePicker);
        if (offset){
            picker.updateDate(1992, 1, 1);
        }
        else {
            picker.updateDate(2016, 2, 1);
        }

        Button button = (Button) rootView.findViewById(R.id.button7);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO get the Date
                int month = picker.getMonth()+1;
                int year = picker.getYear();
                int day = picker.getDayOfMonth();
                Logs.LogD("Selected Date: ", month + " " + year + " " + day);
                date = day+"-"+month+"-"+year;
//                date = getDate();
                editText.setText(date);
                dismiss();
            }
        });
        return rootView;
    }

    public String getDate() {
        date = picker.getDayOfMonth()+"-" + (picker.getMonth()+1)+"-" + picker.getYear()  ;
        Logs.LogD("Date",date);
        return date;
    }

}
