package com.bestdealfinance.bdfpartner.UI;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.Logs;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.application.Util;
import com.bestdealfinance.bdfpartner.model.DataFieldModel;

/**
 * Created by vikas on 23/3/16.
 */

public class CustomTextView extends LinearLayout implements DataFieldModel {
    LinearLayout parentLayout;
    Context context;
    LayoutInflater layoutInflater;
    String title;
    String tag;
    View view;
    TextView titles;
    TextInputLayout child;
    EditText editText;
    String payload, display_payload;
    String fieldType;
    boolean mandate;
    String originaldata;
    String size;
    String special;


    public CustomTextView(Context context, String id, String title, String type, boolean mandatory, String  size, String special) {
        super(context);
        this.context = context;
        this.tag = id;
        this.title = title;
        this.fieldType = type;
        this.mandate = mandatory;
        this.special=special;
        this.size=size;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.custom_text_view, this, true);
        child = (TextInputLayout) findViewById(R.id.textinputlayout);
        editText = (EditText) findViewById(R.id.editText);

        if (size!=null){
            setEditTextMaxLength(Integer.valueOf(size),editText);
        }
        if (mandatory)
            child.setHint(title + "*");
        else
            child.setHint(title);

        if (type.equals("number")) {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        if (type.equals("numberwc")) {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            TextWatcher watcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    editText.removeTextChangedListener(this);
                    Logs.LogD("Watcher", "Text CHnged");
                    String amount = s.toString();

                    if (amount == null || amount.equals("null") || amount.equals("")) {

                    }
                    else {
                        try {
                            amount=amount.replaceAll(",","");
                            editText.setText(Util.formatRs(amount));
                            editText.setSelection(editText.getText().length());
                        } catch (NumberFormatException n) {
                            Logs.LogD("Exception", n.getLocalizedMessage());
                        }
                    }
                    editText.addTextChangedListener(this);
                }
            };
            editText.addTextChangedListener(watcher);
        }
        editText.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                clearError();
                return false;
            }
        });

//        if (QuotesGroupActivity.fieldValues!=null){
//            editText.setText(QuotesGroupActivity.fieldValues.get(tag));
//        }

    }
    public void setEditTextMaxLength(int length, EditText edt_text) {
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(length);
        edt_text.setFilters(FilterArray);
    }

    @Override
    public String getdata() {
        return null;
    }

    @Override
    public boolean validate() {
        if (mandate && Util.VALIDATE) {
            payload = editText.getText().toString().trim();
            //TODO Mobile Validation
            if (special!=null){
                Logs.LogD("Special", "for "+payload+ " special = "+special );
                if (special.equals("mobile")){
                    return payload.length()==10;
                }
                else if (special.equals("email")){
                    return Util.isCheckEmail(payload);
                }
                else if(special.equals("pan")){
                    return Util.isPANVALID(payload);
                }
                else return true;
            }
            else {
                return payload.length() <= Integer.valueOf(size) && payload.length()>0;
            }

        }
        else
            return true;
    }


    @Override
    public void onClickEvent() {

    }

    @Override
    public String getID() {
        return String.valueOf(this.tag);
    }

    @Override
    public void setData(String data) {
        Logs.LogD("CustomText", "setting data: " + data);
        editText.setText(data);
    }

    @Override
    public String getDisplayData() {
        String amout=editText.getText().toString();
        if (fieldType.equals("numberwc")){
            amout= amout.replaceAll(",","");
            return amout;
        }
        else if (fieldType.equals("durationy")){
            return amout;

        }
        else return editText.getText().toString();
    }

    @Override
    public void addToView() {
        parentLayout.addView(view);
    }

    @Override
    public void setError() {
        child.setErrorEnabled(true);
        child.setError("Enter " + title);
        child.requestFocus();
    }

    @Override
    public void clearError() {
        child.setErrorEnabled(false);
        child.setError(null);
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
        titles.setText(title);
    }

    @Override
    public void setHint() {

    }
}
