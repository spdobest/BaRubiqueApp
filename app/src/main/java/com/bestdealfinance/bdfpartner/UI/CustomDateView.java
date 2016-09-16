package com.bestdealfinance.bdfpartner.UI;


import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.activity.QuotesGroupActivity;
import com.bestdealfinance.bdfpartner.application.Util;
import com.bestdealfinance.bdfpartner.model.DataFieldModel;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

/**
 * Created by vikas on 25/3/16.
 */
public class CustomDateView extends LinearLayout implements DataFieldModel {
    private static String Date, display_date;
    Context thisContext;

    private TextView titles;
    private TextView error;
    private String hint;
    TextInputLayout child;
    private String type;
    String tag, title;
    EditText editText;
    boolean mandate;


    public CustomDateView(final Context context, final FragmentManager manager, String title, boolean mandatory, final boolean offset) {
        super(context);
        thisContext = context;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_text_view, this, true);
        child= (TextInputLayout) findViewById(R.id.textinputlayout);
        editText = (EditText) findViewById(R.id.editText);
        mandate=mandatory;
        if (mandatory)
        child.setHint(title+"*");
        else
        child.setHint(title);
        editText.setInputType(InputType.TYPE_NULL);
        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    clearError();
                    //InnerDateFragment dateFragment = new InnerDateFragment(editText, offset);
                    //dateFragment.show(manager, "date");
                    Calendar now = Calendar.getInstance();
                    if(offset)
                    {
                        now.set(1992,0,1);
                    }
                    DatePickerDialog dpd = DatePickerDialog.newInstance(
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                    //Toast.makeText(context,""+year+monthOfYear+dayOfMonth,Toast.LENGTH_LONG).show();
                                    editText.setText(""+dayOfMonth+"-"+(monthOfYear+1)+"-"+year);
                                }
                            },
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH)
                    );
                    dpd.setTitle("SELECT DATE");
                    dpd.setAccentColor(getResources().getColor(R.color.colorPrimary));
                    dpd.show(manager,"Select Date");
                }
            }
        });
        editText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clearError();
               // InnerDateFragment dateFragment = new InnerDateFragment(editText, offset);
                //dateFragment.show(manager, "date");

                Calendar now = Calendar.getInstance();
                if(offset)
                {
                    now.set(1992,1,1);
                }
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                //Toast.makeText(context,""+year+monthOfYear+dayOfMonth,Toast.LENGTH_LONG).show();
                                editText.setText(""+dayOfMonth+"-"+(monthOfYear+1)+"-"+year);
                            }
                        },
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setTitle("SELECT DATE");
                dpd.setAccentColor(getResources().getColor(R.color.colorPrimary));
                dpd.show(manager,"Select Date");

            }
        });
        if (QuotesGroupActivity.fieldValues!=null){
            editText.setText(QuotesGroupActivity.fieldValues.get(tag));
        }
    }
    @Override
    public String getID() {
        return String.valueOf(this.tag);
    }
    @Override
    public boolean validate() {
        if (mandate && Util.VALIDATE) {
            Date = editText.getText().toString().trim();
            return Date.length() > 2;
        }
        else return true;
    }

    @Override
    public void setError() {
        child.setErrorEnabled(true);
        child.setError("Enter "+title);
        child.requestFocus();
    }
    @Override
    public void setData(String data) {
        editText.setText(data);
    }
    @Override
    public void clearError() {
        child.setErrorEnabled(false);
        child.setError(null);
    }

    @Override
    public String getDisplayData() {
        display_date=editText.getText().toString().trim();
        return display_date;
    }
    @Override
    public String getdata() {

        return null;
    }

    @Override
    public void onClickEvent() {

    }


    @Override
    public void addToView() {

    }



    @Override
    public void hideFromView() {

    }

    @Override
    public String fieldType() {
        return null;
    }

    @Override
    public void showinView() {

    }

    @Override
    public void setTitle() {

    }

    @Override
    public void setHint() {

    }



}
