package com.demo.thirdeye.utility;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;

import com.demo.thirdeye.beans.Address;
import com.demo.thirdeye.beans.UserProfile;

/**
 * Created by Manu on 8/2/2017.
 */

public class PhoneDBConnector {
    //Table Details
    private static final String USER_PROFILE_TABLE = "USER_PROFILE";

    private static final String NAME_COLM = "NAME";
    private static final String GENDER_COLM = "GENDER";
    private static final String MOBILE_NO_COLM = "MOBILE_NUMBER";
    private static final String EMAIL_ID_COLM = "EMAIL_ID";
    private static final String HOUSE_NO_COLM = "HOUSE_NO";
    private static final String STREET_NAME_COLM = "STREET_NAME";
    private static final String POST_OFFICE_NAME_COLM = "POST_OFFICE_NAME";
    private static final String POST_OFFICE_CODE_COLM = "POST_OFFICE_CODE";
    private static final String DISTRICT_COLM = "DISTRICT";
    private static final String STATE_COLM = "STATE";
    private static final String WALLET_AMOUNT_COLM = "WALLET_AMOUNT";
    private static final String DESCRIPTION_COLM = "DESCRIPTION";
    private static final String ID_PIC_1_COLM = "ID_PIC_1";
    private static final String ID_PIC_2_COLM = "ID_PIC_2";
    private static final String ID_PIC_3_COLM = "ID_PIC_3";
    private static final String ID_PIC_4_COLM = "ID_PIC_4";
    private static final String PROFILE_PIC_COLM = "PROFILE_PIC";
    private static final String PASSWORD_COLM = "PASSWORD";
    private static final String SUSPENDED_COLM = "SUSPENDED";



    private static final String CREATE_TABLE_USER_PROFILE_QUERY = "CREATE TABLE IF NOT EXISTS "+USER_PROFILE_TABLE+" ("+NAME_COLM+" VARCHAR NOT NULL,"+GENDER_COLM+" VARCHAR,"+MOBILE_NO_COLM+" VARCHAR NOT NULL,"+EMAIL_ID_COLM+" VARCHAR NOT NULL," +
            HOUSE_NO_COLM+" VARCHAR,"+STREET_NAME_COLM+" VARCHAR,"+POST_OFFICE_NAME_COLM+" VARCHAR,"+POST_OFFICE_CODE_COLM+" VARCHAR,"+
            DISTRICT_COLM+" VARCHAR,"+STATE_COLM+" VARCHAR,"+WALLET_AMOUNT_COLM+" VARCHAR,"+DESCRIPTION_COLM+" VARCHAR,"+ID_PIC_1_COLM+" BLOB,"+ID_PIC_2_COLM+" BLOB,"+ID_PIC_3_COLM+" BLOB," +
            ID_PIC_4_COLM+" BLOB,"+PROFILE_PIC_COLM+" BLOB,"+PASSWORD_COLM+" VARCHAR,"+SUSPENDED_COLM+" VARCHAR);";


    private static final String NEWS_TABLE = "NEWS";



    SQLiteDatabase myDatabase;

    public PhoneDBConnector(Context context) {
        myDatabase = context.openOrCreateDatabase("ThirdEye",context.MODE_PRIVATE,null);

        myDatabase.execSQL(CREATE_TABLE_USER_PROFILE_QUERY);


    }


    public String insertUserProfile(UserProfile userProfile) {
        Cursor cursorTable = myDatabase.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+USER_PROFILE_TABLE+"'", null);
        if(cursorTable==null) {
            return "No Table";
        }
        Cursor resultSet = myDatabase.rawQuery("Select * from "+USER_PROFILE_TABLE, null);
        if(resultSet!=null) {
            if (resultSet.getCount() > 0) {
                return "Table has entry already!";
            }
        }
        boolean loginStatus = userProfile.isLogin();
        String INSERT_QUERY = "INSERT INTO "+USER_PROFILE_TABLE+" ("+NAME_COLM+","+MOBILE_NO_COLM+","+EMAIL_ID_COLM+(loginStatus?","+PASSWORD_COLM:"")+","+SUSPENDED_COLM+") VALUES('" + userProfile.getUserName() + "','" + userProfile.getMobileNumber() + "','" + userProfile.getEmailId() + "'"+(loginStatus?",'"+userProfile.getPassword()+"'":"")+",0);";
        try {
            myDatabase.execSQL(INSERT_QUERY);
            return "Inserted";
        }catch (Exception e){
            return INSERT_QUERY;
        }
    }

    public UserProfile getUserProfile(){
        try {
            Cursor cursor = myDatabase.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+USER_PROFILE_TABLE+"'", null);
            if(cursor!=null) {
                if(cursor.getCount()>0) {
                    cursor.close();
                    Cursor resultSet = myDatabase.rawQuery("Select * from "+USER_PROFILE_TABLE, null);
                    resultSet.moveToFirst();
                    UserProfile userProfile = new UserProfile(resultSet.getString(0), resultSet.getString(5), resultSet.getString(6), resultSet.getString(20),null==resultSet.getString(20)?false:true,resultSet.getInt(21));
                    userProfile.setGender(null!=resultSet.getString(4)?resultSet.getString(4):null);
                    Address address = new Address((null!=resultSet.getString(7)?resultSet.getString(7):null),(null!=resultSet.getString(8)?resultSet.getString(8):null),
                            (null!=resultSet.getString(9)?resultSet.getString(9):null),(null!=resultSet.getString(10)?resultSet.getString(10):null),(null!=resultSet.getString(11)?resultSet.getString(11):null),(null!=resultSet.getString(12)?resultSet.getString(12):null));
                    userProfile.setAddress(address);
                    userProfile.setWalletAmount(null!=resultSet.getString(13)?Integer.parseInt(resultSet.getString(13)):0);
                    userProfile.setDiscription(null!=resultSet.getString(14)?resultSet.getString(14):null);
                    Bitmap[] idProof = new Bitmap[4];

                    idProof[0] = (null!=resultSet.getBlob(15)? BitmapFactory.decodeByteArray(resultSet.getBlob(15),0,resultSet.getBlob(15).length):null);
                    idProof[1] = (null!=resultSet.getBlob(16)? BitmapFactory.decodeByteArray(resultSet.getBlob(16),0,resultSet.getBlob(16).length):null);
                    idProof[2] = (null!=resultSet.getBlob(17)? BitmapFactory.decodeByteArray(resultSet.getBlob(16),0,resultSet.getBlob(17).length):null);
                    idProof[3] = (null!=resultSet.getBlob(18)? BitmapFactory.decodeByteArray(resultSet.getBlob(16),0,resultSet.getBlob(18).length):null);
                    userProfile.setIdProof(idProof);
                    userProfile.setProfilePic((null!=resultSet.getBlob(19)? BitmapFactory.decodeByteArray(resultSet.getBlob(19),0,resultSet.getBlob(19).length):null));

                    return userProfile;
                }
                cursor.close();
            }
            return null;

        }catch (Exception e){
            return null;
        }
    }
    public boolean updateUserProfilePassword(UserProfile userProfile) {
        String UPDATE_QUERY = "UPDATE "+USER_PROFILE_TABLE+" SET "+PASSWORD_COLM+" = "+userProfile.getPassword();
        try {
            myDatabase.execSQL(UPDATE_QUERY);
            return true;
        }catch (Exception e){
            return false;
        }
    }

}
