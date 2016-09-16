package com.bestdealfinance.bdfpartner.model;

/**
 * Created by vikas on 16/5/16.
 */
public class DropDownModel {

    private String id;
    private String item_value;
    private String description;

    public DropDownModel(String id, String item_value, String description) {
        this.id = id;
        this.item_value = item_value;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItem_value() {
        return item_value;
    }

    public void setItem_value(String item_value) {
        this.item_value = item_value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
