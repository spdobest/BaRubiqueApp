package com.bestdealfinance.bdfpartner.application;

import org.json.JSONObject;

import java.io.Serializable;

public class ProfileHelper implements Serializable {

    private String name;
    private String email;
    private String mobile;


    private String occupation;
    private String profession;
    private String annualIncome;
    private String totalExperience;
    private String companyName;


    private String pan;
    private String aadhar_number;
    private String passport_number;

    private String address1;
    private String address2;
    private String address3;
    private String city;
    private String state;
    private String pincode;

    private String bank_holder_name;
    private String bank_name;
    private String branch_name;
    private String account_number;
    private String bank_pan;
    private String ifsc;
    private JSONObject response;

    public ProfileHelper() {
        name = null;
        profession = null;
        occupation = null;
        email = null;
        mobile = null;

        pan = null;
        aadhar_number = null;
        passport_number = null;

        address1 = null;
        address2 = null;
        city = null;
        state = null;
        pincode = null;

        bank_holder_name = null;
        bank_name = null;
        branch_name = null;
        account_number = null;
        bank_pan = null;
        ifsc = null;
    }

    public String getBank_holder_name() {
        return bank_holder_name;
    }

    public void setBank_holder_name(String bank_holder_name) {
        this.bank_holder_name = bank_holder_name;
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getAadhar_number() {
        return aadhar_number;
    }

    public void setAadhar_number(String aadhar_number) {
        this.aadhar_number = aadhar_number;
    }

    public String getPassport_number() {
        return passport_number;
    }

    public void setPassport_number(String passport_number) {
        this.passport_number = passport_number;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getBranch_name() {
        return branch_name;
    }

    public void setBranch_name(String branch_name) {
        this.branch_name = branch_name;
    }

    public String getIfsc() {
        return ifsc;
    }

    public void setIfsc(String ifsc) {
        this.ifsc = ifsc;
    }

    public String getBank_pan() {
        return bank_pan;
    }

    public void setBank_pan(String bank_pan) {
        this.bank_pan = bank_pan;
    }

    public String getAccount_number() {
        return account_number;
    }

    public void setAccount_number(String account_number) {
        this.account_number = account_number;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getAddress3() {
        return address3;
    }

    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    public JSONObject getResponse() {
        return response;
    }

    public void setResponse(JSONObject response) {
        this.response = response;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getAnnualIncome() {
        return annualIncome;
    }

    public void setAnnualIncome(String annualIncome) {
        this.annualIncome = annualIncome;
    }

    public String getTotalExperience() {
        return totalExperience;
    }

    public void setTotalExperience(String totalExperience) {
        this.totalExperience = totalExperience;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
