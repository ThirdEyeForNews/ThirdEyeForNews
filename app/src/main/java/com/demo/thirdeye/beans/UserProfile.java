package com.demo.thirdeye.beans;

import android.graphics.Bitmap;

/**
 * Created by Manu on 8/2/2017.
 */

public class UserProfile {

    private String name;
    private String gender;

    public UserProfile() {

    }
    private String mobileNumber;
    private String emailId;
    private Bitmap profilePic;
    private Bitmap[] idProof = new Bitmap[4];
    private String discription;
    private int walletAmount;
    private Address address;
    private String password;
    private boolean login;
    private int suspended;

    public UserProfile(String name, String mobileNumber, String emailId, String password,boolean login,int suspended) {
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.emailId = emailId;
        this.password = password;
        this.login = login;
        this.suspended = suspended;
    }



    public Bitmap getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(Bitmap profilePic) {
        this.profilePic = profilePic;
    }

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    public int getSuspended() {
        return suspended;
    }

    public void setSuspended(int suspended) {
        this.suspended = suspended;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return name;
    }

    public void setUserName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public Bitmap[] getIdProof() {
        return idProof;
    }

    public void setIdProof(Bitmap[] idProof) {
        this.idProof = idProof;
    }

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }

    public int getWalletAmount() {
        return walletAmount;
    }

    public void setWalletAmount(int walletAmount) {
        this.walletAmount = walletAmount;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProfile that = (UserProfile) o;
        return walletAmount == that.walletAmount &&
                login == that.login &&
                suspended == that.suspended &&
                name.equals(that.name) &&
                gender.equals(that.gender) &&
                mobileNumber.equals(that.mobileNumber) &&
                emailId.equals(that.emailId) &&
                profilePic.sameAs(that.profilePic) &&
                idProof[0].sameAs(that.idProof[0]) &&
                idProof[1].sameAs(that.idProof[1]) &&
                idProof[2].sameAs(that.idProof[2]) &&
                idProof[3].sameAs(that.idProof[3]) &&
                discription.equals(that.discription) &&
                address.equals(that.address);
    }

}
