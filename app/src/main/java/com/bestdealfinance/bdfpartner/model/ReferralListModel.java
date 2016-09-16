package com.bestdealfinance.bdfpartner.model;

/**
 * Created by vikas on 17/3/16.
 */
public class ReferralListModel {

    private String lead_id;
    private String name;
    private String email;
    private String phone;
    private String lead_state;
    private String product_type;
    private String loan_key;
    private String loan_value;
    private String loan_eligible;
    private String date_created;
    private String refID;
    private String product_name;

    public String getRefID() {
        return refID;
    }

    public void setRefID(String refID) {
        this.refID = refID;
    }

    public String getPayout() {
        return payout;

    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public void setPayout(String payout) {
        this.payout = payout;
    }

    private String payout;



    public String getLead_id() {
        return lead_id;
    }

    public void setLead_id(String lead_id) {
        this.lead_id = lead_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLead_state() {
        return lead_state;
    }

    public void setLead_state(String lead_state) {
        this.lead_state = lead_state;
    }

    public String getProduct_type() {
        return product_type;
    }

    public void setProduct_type(String product_type) {
        this.product_type = product_type;
    }

    public String getLoan_key() {
        return loan_key;
    }

    public void setLoan_key(String loan_key) {
        this.loan_key = loan_key;
    }

    public String getLoan_value() {
        return loan_value;
    }

    public void setLoan_value(String loan_value) {
        this.loan_value = loan_value;
    }

    public String getLoan_eligible() {
        return loan_eligible;
    }

    public void setLoan_eligible(String loan_eligible) {
        this.loan_eligible = loan_eligible;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }
}