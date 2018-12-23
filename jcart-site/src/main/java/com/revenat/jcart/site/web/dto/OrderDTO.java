package com.revenat.jcart.site.web.dto;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;

public class OrderDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "{message.firstName_is_required}")
    private String firstName;
    @NotEmpty(message = "{message.last_name_is_required}")
    private String lastName;
    @NotEmpty(message = "{message.emailId_is_required}")
    @Email
    private String emailId;
    @NotEmpty(message = "{message.phone_is_required}")
    private String phone;

    @NotEmpty(message = "{message.addressLine_is_required}")
    private String addressLine1;
    private String addressLine2;
    @NotEmpty(message = "{message.city_is_required}")
    private String city;
    @NotEmpty(message = "{message.state_is_required}")
    private String state;
    @NotEmpty(message = "{message.zipCode_is_required}")
    private String zipCode;
    @NotEmpty(message = "{message.country_is_required}")
    private String country;

    @NotEmpty(message = "{message.firstName_is_required}")
    private String billingFirstName;
    @NotEmpty(message = "{message.lastName_is_required}")
    private String billingLastName;
    @NotEmpty(message = "{message.addressLine_is_required}")
    private String billingAddressLine1;
    private String billingAddressLine2;
    @NotEmpty(message = "{message.city_is_required}")
    private String billingCity;
    @NotEmpty(message = "{message.state_is_required}")
    private String billingState;
    @NotEmpty(message = "{message.zipCode_is_required}")
    private String billingZipCode;
    @NotEmpty(message = "{message.country_is_required}")
    private String billingCountry;

    @NotEmpty(message = "{message.ccNumber_is_required}")
    private String ccNumber;
    @NotEmpty(message = "{message.cvv_is_required}")
    private String cvv;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
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

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getBillingFirstName() {
        return billingFirstName;
    }

    public void setBillingFirstName(String billingFirstName) {
        this.billingFirstName = billingFirstName;
    }

    public String getBillingLastName() {
        return billingLastName;
    }

    public void setBillingLastName(String billingLastName) {
        this.billingLastName = billingLastName;
    }

    public String getBillingAddressLine1() {
        return billingAddressLine1;
    }

    public void setBillingAddressLine1(String billingAddressLine1) {
        this.billingAddressLine1 = billingAddressLine1;
    }

    public String getBillingAddressLine2() {
        return billingAddressLine2;
    }

    public void setBillingAddressLine2(String billingAddressLine2) {
        this.billingAddressLine2 = billingAddressLine2;
    }

    public String getBillingCity() {
        return billingCity;
    }

    public void setBillingCity(String billingCity) {
        this.billingCity = billingCity;
    }

    public String getBillingState() {
        return billingState;
    }

    public void setBillingState(String billingState) {
        this.billingState = billingState;
    }

    public String getBillingZipCode() {
        return billingZipCode;
    }

    public void setBillingZipCode(String billingZipCode) {
        this.billingZipCode = billingZipCode;
    }

    public String getBillingCountry() {
        return billingCountry;
    }

    public void setBillingCountry(String billingCountry) {
        this.billingCountry = billingCountry;
    }

    public String getCcNumber() {
        return ccNumber;
    }

    public void setCcNumber(String ccNumber) {
        this.ccNumber = ccNumber;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }
}
