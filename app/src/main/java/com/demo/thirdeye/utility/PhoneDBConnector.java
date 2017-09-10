package com.demo.thirdeye.utility;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.demo.thirdeye.beans.Address;
import com.demo.thirdeye.beans.News;
import com.demo.thirdeye.beans.UserProfile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Manu on 8/2/2017.
 */

public class PhoneDBConnector {
    //Table Details
    private static final String TAG = "PhoneDBConnector";

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


    private Context context;
    SQLiteDatabase myDatabase;

    public PhoneDBConnector(Context context) {
        this.context = context;
        myDatabase = context.openOrCreateDatabase("ThirdEye",context.MODE_PRIVATE,null);

        myDatabase.execSQL(CREATE_TABLE_USER_PROFILE_QUERY);
        Log.d(TAG, "PhoneDBConnector: "+CREATE_TABLE_USER_PROFILE_QUERY);



    }


    public String insertUserProfile(UserProfile userProfile) {
        Cursor cursorTable = myDatabase.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+USER_PROFILE_TABLE+"'", null);
        if(cursorTable==null) {
            Log.d(TAG, "insertUserProfile: No Table");
            return "No Table";
        }
        Cursor resultSet = myDatabase.rawQuery("Select * from "+USER_PROFILE_TABLE, null);
        if(resultSet!=null) {
            if (resultSet.getCount() > 0) {
                Log.d(TAG, "insertUserProfile: Table has entry already!");
                return "Table has entry already!";
            }
        }
        boolean loginStatus = userProfile.isLogin();
        String INSERT_QUERY = "INSERT INTO "+USER_PROFILE_TABLE+" ("+NAME_COLM+","+MOBILE_NO_COLM+","+EMAIL_ID_COLM+(loginStatus?","+PASSWORD_COLM:"")+","+SUSPENDED_COLM+") VALUES('" + userProfile.getUserName() + "','" + userProfile.getMobileNumber() + "','" + userProfile.getEmailId() + "'"+(loginStatus?",'"+userProfile.getPassword()+"'":"")+",0);";
        Log.d(TAG, "INSERT_QUERY: "+INSERT_QUERY);

        try {
            myDatabase.execSQL(INSERT_QUERY);
            Log.d(TAG, "insertUserProfile: Inserted");

            return "Inserted";
        }catch (Exception e){
            Log.d(TAG, "Exception: "+e.getMessage());
            return "Exception";
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
                    Log.d(TAG, "resultSet: "+resultSet.getString(0));

                    UserProfile userProfile = new UserProfile(resultSet.getString(0), resultSet.getString(2), resultSet.getString(3), resultSet.getString(17),null==resultSet.getString(17)?false:true,resultSet.getInt(18));
                    userProfile.setGender(null!=resultSet.getString(1)?resultSet.getString(1):null);
                    Address address = new Address((null!=resultSet.getString(4)?resultSet.getString(4):null),(null!=resultSet.getString(5)?resultSet.getString(5):null),
                            (null!=resultSet.getString(6)?resultSet.getString(6):null),(null!=resultSet.getString(7)?resultSet.getString(7):null),(null!=resultSet.getString(8)?resultSet.getString(8):null),(null!=resultSet.getString(9)?resultSet.getString(9):null));
                    userProfile.setAddress(address);
                    userProfile.setWalletAmount(null!=resultSet.getString(10)?Integer.parseInt(resultSet.getString(10)):0);
                    userProfile.setDiscription(null!=resultSet.getString(11)?resultSet.getString(11):null);
                    Bitmap[] idProof = new Bitmap[4];

                    idProof[0] = (null!=resultSet.getBlob(12)? BitmapFactory.decodeByteArray(resultSet.getBlob(12),0,resultSet.getBlob(12).length):null);
                    idProof[1] = (null!=resultSet.getBlob(13)? BitmapFactory.decodeByteArray(resultSet.getBlob(13),0,resultSet.getBlob(13).length):null);
                    idProof[2] = (null!=resultSet.getBlob(14)? BitmapFactory.decodeByteArray(resultSet.getBlob(14),0,resultSet.getBlob(14).length):null);
                    idProof[3] = (null!=resultSet.getBlob(15)? BitmapFactory.decodeByteArray(resultSet.getBlob(15),0,resultSet.getBlob(15).length):null);
                    userProfile.setIdProof(idProof);
                    userProfile.setProfilePic((null!=resultSet.getBlob(16)? BitmapFactory.decodeByteArray(resultSet.getBlob(16),0,resultSet.getBlob(16).length):null));

                    return userProfile;
                }
                cursor.close();
            }
            Log.d(TAG, "Result: No Table");

            return null;

        }catch (Exception e){
            Log.d(TAG, "Exception: "+e.getMessage());

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

    public List<News> getOfflineNews() {
        ArrayList<News> results = new ArrayList();
        AssetManager assetManager = context.getAssets();
        for (int index = 0; index < 10; index++) {
            ArrayList<Bitmap> newsPics = new ArrayList();
            UserProfile userProfile=null;
            try {
                InputStream in = assetManager.open("image/news1_1.jpg");
                newsPics.add(BitmapFactory.decodeStream(in));
                in = assetManager.open("image/news1_2.jpg");
                newsPics.add(BitmapFactory.decodeStream(in));
                in = assetManager.open("image/news1_3.jpg");
                newsPics.add(BitmapFactory.decodeStream(in));
                in = assetManager.open("image/news1_4.jpg");
                newsPics.add(BitmapFactory.decodeStream(in));
                userProfile = new UserProfile("Manu Baby","8714443740","manuasdsadasd","asdasd",true,0);
                in = assetManager.open("image/profile_pic.jpg");
                userProfile.setProfilePic(BitmapFactory.decodeStream(in));
            }catch (Exception e){

            }
            Calendar c = Calendar.getInstance();
            String date = c.get(Calendar.DATE)+"-"+c.get(Calendar.MONTH)+"-"+c.get(Calendar.YEAR);
            String time = c.get(Calendar.HOUR)+":"+c.get(Calendar.MINUTE)+":"+c.get(Calendar.SECOND);

            News obj = new News("Dileep continues to be the 'in-man' as court extends judicial remand",newsPics,"The districts court in Angamaly extended the remand period of Actor Dileep till August 22nd, 2017 in the Malayalam actress assault case. Dileep presented before the judge via video conference after which the judge extended the judicial custody of Dileep, reports suggest. Dileep is jailed at a sub jail in Aluva and his bail plea was rejected by both, districts court and the Kerala High Court, in the actress assault case. Dileep was arrested on July 10th, 2017 by the Aluva Club police after the officials found irrefutable evidence against him in connection with the case.The districts court in Angamaly extended the remand period of Actor Dileep till August 22nd, 2017 in the Malayalam actress assault case. Dileep presented before the judge via video conference after which the judge extended the judicial custody of Dileep, reports suggest. Dileep is jailed at a sub jail in Aluva and his bail plea was rejected by both, districts court and the Kerala High Court, in the actress assault case. Dileep was arrested on July 10th, 2017 by the Aluva Club police after the officials found irrefutable evidence against him in connection with the case.The districts court in Angamaly extended the remand period of Actor Dileep till August 22nd, 2017 in the Malayalam actress assault case. Dileep presented before the judge via video conference after which the judge extended the judicial custody of Dileep, reports suggest. Dileep is jailed at a sub jail in Aluva and his bail plea was rejected by both, districts court and the Kerala High Court, in the actress assault case. Dileep was arrested on July 10th, 2017 by the Aluva Club police after the officials found irrefutable evidence against him in connection with the case.",2,10,5,userProfile,date,time);
            results.add(obj);
        }
        return results;
    }
}
