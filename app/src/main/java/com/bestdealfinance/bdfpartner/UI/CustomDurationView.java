package com.bestdealfinance.bdfpartner.UI;

/**
 * Created by vikas on 19/5/16.
 */

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.view.LayoutInflater;
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

public class CustomDurationView extends LinearLayout implements DataFieldModel {
    LinearLayout parentLayout;
    Context context;
    LayoutInflater layoutInflater;
    String  title;
    String tag;
    View view;
    TextView titles;
    TextInputLayout child;
    EditText editText;
    String payload,display_payload;
    String fieldType;
    boolean mandate;
    FragmentManager fragmentManager;


    public CustomDurationView(final Context context, String id, String title, String type, final FragmentManager manager, final boolean mandatory) {
        super(context);
        this.context = context;
        this.tag=id;
        this.title=title;
        this.fieldType=type;
        this.fragmentManager=manager;
        this.mandate=mandatory;
        this.layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.custom_text_view, this, true);
        child= (TextInputLayout) findViewById(R.id.textinputlayout);
        editText = (EditText) findViewById(R.id.editText);

        if (mandatory)
            child.setHint(title+"*");
        else
            child.setHint(title);

        if (type.equals("number")){
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    clearError();
                    InnerDurationFragment dateFragment = new InnerDurationFragment(editText, context);
                    dateFragment.show(manager, "date");
                }
            }
        });
        editText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clearError();
                InnerDurationFragment dateFragment = new InnerDurationFragment(editText, context);
                dateFragment.show(manager, "date");

            }
        });
    }

    @Override
    public String getdata()
    {
        return null;
    }

    @Override
    public boolean validate() {
        if (mandate && Util.VALIDATE) {
            payload = editText.getText().toString().trim();
            return payload.length() != 0;
        }
        else return true;
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
        display_payload=editText.getText().toString().trim();
        return display_payload;
    }
    @Override
    public void addToView() {
        parentLayout.addView(view);
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
