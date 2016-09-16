package com.bestdealfinance.bdfpartner.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.Logs;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.application.Util;
import com.bestdealfinance.bdfpartner.model.DataFieldModel;

import java.util.List;

/**
 * Created by vikas on 25/3/16.
 */
public class CustomDropdownView extends LinearLayout implements DataFieldModel {
    LinearLayout parentLayout;
    Context context;
    LayoutInflater layoutInflater;
    String  title;
    String tag;
    View view;
    TextView titles, error;
    TextInputLayout child;
    String payload;
    String display_payload;
    EditText editText;
    List<String>fieldOptions;
    List<String> fieldValues;
    int selected_position;
    boolean mandate;

    public CustomDropdownView(final Context context, final String id, final String title, final List<String> fieldOption, final List<String> fieldValues, boolean mandatory) {
        super(context);
        this.context = context;
        this.fieldOptions=fieldOption;
        this.fieldValues=fieldValues;
        this.tag=id;
        this.mandate=mandatory;
        this.title=title;
        this.layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.custom_text_view, this, true);
        child= (TextInputLayout) findViewById(R.id.textinputlayout);
        editText = (EditText) findViewById(R.id.editText);
        editText.setInputType(InputType.TYPE_NULL);
        TextWatcher abcd= new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clearError();
                Bundle bundle=new Bundle();
                Logs.LogD("selected", s.toString());
                selected_position=fieldOption.indexOf(s.toString());
                if (selected_position!=-1) {
                    String field = fieldValues.get(selected_position);
                    bundle.putString("field", field);
                    bundle.putString("id",id);
                    bundle.putString("value", s.toString());

                    Intent intent = new Intent(Util.SOME_ACTION);
                    intent.putExtras(bundle);
                    if (s.toString().equals("")){
                        //Do Nothing
                    }
                    else {
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    }
                    Logs.LogD("Sent Broadcast", bundle.toString());
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        editText.addTextChangedListener(abcd);
        editText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.SingleChoice(context, title, editText,  fieldOptions.toArray(new String[fieldOption.size()]));
            }
        });
        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Util.SingleChoice(context,title, editText, fieldOptions.toArray(new String[fieldOption.size()]));
                }
            }
        });

        if (mandatory)
            child.setHint(title+"*");
        else
            child.setHint(title);
    }

    @Override
    public boolean validate() {
        if (mandate && Util.VALIDATE) {
            payload = editText.getText().toString().trim();
            return payload.length() > 1;
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
    public void clearError() {
        child.setErrorEnabled(false);
        child.setError(null);
    }


    @Override
    public String getdata() {
            if (fieldValues.size()!=0 && selected_position!=-1){
                payload = fieldValues.get(selected_position);
                return payload;
            }
        else return null;

    }
    @Override
    public String getDisplayData() {
        if (fieldValues.size()!=0 && selected_position!=-1){
            display_payload = fieldOptions.get(selected_position);
            return display_payload;
        }
        else return  null;
    }

    @Override
    public void onClickEvent() {

    }

    @Override
    public void addToView() {
        parentLayout.addView(view);
    }

    @Override
    public String getID() {
        return String.valueOf(this.tag);
    }
    @Override
    public void hideFromView() {

    }
    @Override
    public void setData(String data) {
        editText.setText(data);
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
