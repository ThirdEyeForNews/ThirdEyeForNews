package com.demo.thirdeye.beans;

/**
 * Created by Manu on 8/2/2017.
 */

public class Address {

    private String houseNoOrName;
    private String streetName;
    private String postOfficeName;
    private String postOfficeCode;
    private String district;
    private String state;

    public Address(String houseNoOrName, String streetName, String postOfficeName, String postOfficeCode, String district, String state) {
        this.houseNoOrName = houseNoOrName;
        this.streetName = streetName;
        this.postOfficeName = postOfficeName;
        this.postOfficeCode = postOfficeCode;
        this.district = district;
        this.state = state;
    }

    public String getHouseNoOrName() {
        return houseNoOrName;
    }

    public void setHouseNoOrName(String houseNoOrName) {
        this.houseNoOrName = houseNoOrName;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getPostOfficeName() {
        return postOfficeName;
    }

    public void setPostOfficeName(String postOfficeName) {
        this.postOfficeName = postOfficeName;
    }

    public String getPostOfficeCode() {
        return postOfficeCode;
    }

    public void setPostOfficeCode(String postOfficeCode) {
        this.postOfficeCode = postOfficeCode;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address)) return false;
        Address address = (Address) o;
        return houseNoOrName.equals(address.houseNoOrName) &&
                streetName.equals(address.streetName) &&
                postOfficeName.equals(address.postOfficeName) &&
                postOfficeCode.equals(address.postOfficeCode) &&
                district.equals(address.district) &&
                state.equals(address.state);
    }

}
