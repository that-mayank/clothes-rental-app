package com.nineleaps.leaps.dto.otp;

public class SmsPojo {

    private String PhoneNo;
    private String storePhoneNo;

    public String getPhoneNo() {
        return PhoneNo;
    }

    public String getStorePhoneNo() {
        return storePhoneNo;
    }

    public String setStorePhoneNo(String storePhoneNo) {
        return storePhoneNo = storePhoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        PhoneNo = phoneNo;
    }
}

