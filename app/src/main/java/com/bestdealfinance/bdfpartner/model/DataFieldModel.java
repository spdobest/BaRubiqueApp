package com.bestdealfinance.bdfpartner.model;

/**
 * Created by vikas on 23/3/16.
 */
public interface DataFieldModel {

    public String getdata();

    public String getDisplayData();


    public String getID();

    public void onClickEvent();

    public void addToView();

    public void setError();
    public void setData(String data);

    public void clearError();

    public void hideFromView();

    public String fieldType();

    public void showinView();

    public boolean validate();
    String ab="wew";

    public void setTitle();
    public void setHint();


}
