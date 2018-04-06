package com.bestdealfinance.bdfpartner.model;

/**
 * Created by siba.prasad on 22-12-2016.
 */

public class ModelPreview {
    public String field_name;

    public String field_value;

    public String field_meta_id;

    public String base_id;

    public int rowType;

    public ModelPreview() {
    }

    public ModelPreview(String field_name, String field_value, String field_meta_id, String base_id, int rowType) {
        this.field_name = field_name;
        this.field_value = field_value;
        this.field_meta_id = field_meta_id;
        this.base_id = base_id;
        this.rowType = rowType;
    }
}
