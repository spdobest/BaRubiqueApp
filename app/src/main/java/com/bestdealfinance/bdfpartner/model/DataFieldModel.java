package com.bestdealfinance.bdfpartner.model;

/**
 * Created by vikas on 23/3/16.
 */
public interface DataFieldModel {

    String ab="wew";

    String getdata();

    String getDisplayData();

    String getID();

    void onClickEvent();

    void addToView();

    void setError();

    void setData(String data);

    void clearError();

    void hideFromView();

    String fieldType();

    void showinView();

    boolean validate();

    void setTitle();

    void setHint();


}
