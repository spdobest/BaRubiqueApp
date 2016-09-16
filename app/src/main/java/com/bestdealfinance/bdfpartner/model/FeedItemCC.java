package com.bestdealfinance.bdfpartner.model;

/**
 * Created with Love by devesh on 19/10/15.
 */
public class FeedItemCC {
    public String getCc_id() {
        return cc_id;
    }

    public String getFinbank() {
        return finbank;
    }

    public void setFinbank(String finbank) {
        this.finbank = finbank;
    }

    public void setCc_id(String cc_id) {
        this.cc_id = cc_id;
    }

    private String title;
    private String thumbnail;
    private String category;
    private String finbank;
    private String finbank_type;

    public String getFinbank_type() {
        return finbank_type;
    }

    public void setFinbank_type(String finbank_type) {
        this.finbank_type = finbank_type;
    }

    private String fees;
    private String roi;
    private String cc_id;
    private String features;
    private String product_id;
    private String filledQual;

    public String getFullQual() {
        return fullQual;
    }

    public void setFullQual(String fullQual) {
        this.fullQual = fullQual;
    }

    private String fullQual;

    public String getFilledQual() {
        return filledQual;
    }

    public void setFilledQual(String filledQual) {
        this.filledQual = filledQual;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFees() {
        return fees;
    }

    public void setFees(String fees) {
        this.fees = fees;
    }

    public String getRoi() {
        return roi;
    }

    public void setRoi(String roi) {
        this.roi = roi;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}