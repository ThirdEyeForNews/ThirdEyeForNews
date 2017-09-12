package com.demo.thirdeye.utility;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.audiofx.BassBoost;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.demo.thirdeye.CameraActivity;
import com.demo.thirdeye.HomePage;
import com.demo.thirdeye.LoadingSignIn;
import com.demo.thirdeye.Login;
import com.demo.thirdeye.LoginMainActivity;
import com.demo.thirdeye.MobileVerificationActivity;
import com.demo.thirdeye.R;
import com.demo.thirdeye.SignUpPage;
import com.demo.thirdeye.beans.Address;
import com.demo.thirdeye.beans.News;
import com.demo.thirdeye.beans.UserProfile;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.StitchClient;
import com.mongodb.stitch.android.auth.Auth;
import com.mongodb.stitch.android.auth.AvailableAuthProviders;
import com.mongodb.stitch.android.auth.anonymous.AnonymousAuthProvider;
import com.mongodb.stitch.android.services.mongodb.MongoClient;

import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by ammu on 8/11/2017.
 */

public class MongoDBConnector  {

    private static UserProfile USER_PROFILE = null;
    private static List<News> NEWS_LIST = null;

    private static final String TAG = "MongoDBConnector";



    private static final String DB_NAME = "ThirdEye";
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
    private static final String MOBILE_VERIFICATION_STATUS_COLM = "MOBILE_VERIFICATION_STATUS";
    private static final String EMAIL_VERIFICATION_STATUS_COLM = "EMAIL_VERIFICATION_STATUS";
    private static final String ID_PROOF_VERIFICATION_STATUS_COLM = "ID_PROOF_VERIFICATION_STATUS";



    private static final String NEWS_TABLE = "NEWS";


    private static final String HEADING_COLM = "HEADING";
    private static final String DETAILS_COLM = "DETAILS";
    private static final String PICS_COLM = "PICS";
    private static final String VIEW_COUNT_COLM = "VIEW_COUNT";
    private static final String LIKE_COUNT_COLM = "LIKE_COUNT";
    private static final String DISLIKE_COUNT_COLM = "DISLIKE_COUNT";
    private static final String AUTHOR_MOBILE_NO_COLM = "AUTHOR_MOBILE_NO";
    private static final String CREATED_TIME_COLM = "CREATED_TIME";
    private static final String LAST_UPDATE_TIME_COLM = "LAST_UPDATED_TIME";


    static Context context;
    private static StitchClient stitchClient;
    private static MongoClient mongoClient;
    private static String APP_ID = "thirdeye-zszpa";
    private static String MONGODB_SERVICE_NAME = "mongodb-atlas";
    public final static UserProfile[] userProfile = new UserProfile[1];



    public MongoDBConnector(Context context){
        MongoDBConnector.context = context;
        Settings.INTERNET_STATUS =  Settings.isNetworkAvailable(context);
        if (Settings.INTERNET_STATUS) {
            stitchClient = new StitchClient(context, APP_ID);
            mongoClient = new MongoClient(stitchClient, MONGODB_SERVICE_NAME);
            if (!stitchClient.isAuthenticated()) {
                doAnonymousAuthentication();
            }
        }
    }


    private static void doAnonymousAuthentication() {

        stitchClient.getAuthProviders().continueWith(new Continuation<AvailableAuthProviders, Object>() {
            @Override
            public Object then(@NonNull Task<AvailableAuthProviders> task) throws Exception {
                try {
                    if (task.isSuccessful()){

                        if (task.getResult().hasAnonymous()){

                            stitchClient.logInWithProvider(new AnonymousAuthProvider()).continueWith(new Continuation<Auth, Object>() {
                                @Override
                                public Object then(@NonNull Task<Auth> task) throws Exception {
                                    try {
                                        return null;
                                    }
                                    catch (Exception ex){
                                        throw ex;
                                    }
                                }
                            });
                        }
                    }
                    else {
                    }
                    return null;

                }
                catch (Exception ex){
                    throw ex;
                }
            }
        });

    }

    public void insertUserProfile(final UserProfile userProfile) {
        if (!stitchClient.isAuthenticated()) {
            doAnonymousAuthentication();
            return ;
        } else {

            final Document query = new Document(MOBILE_NO_COLM, userProfile.getMobileNumber());
            mongoClient.getDatabase(DB_NAME).getCollection(USER_PROFILE_TABLE).find(query).continueWith(new Continuation<List<Document>, Integer>() {
                @Override
                public Integer then(@NonNull Task<List<Document>> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().size() == 0) {
                            return 0;
                        } else
                            return 1;

                    }
                    return 2;
                }
            }).addOnCompleteListener(new OnCompleteListener<Integer>() {
                @Override
                public void onComplete(@NonNull Task<Integer> task) {
                    switch (task.getResult()) {
                        case 0:
                            Document userDocument = new Document();
                            if (null != userProfile.getUserName())
                                userDocument.append(NAME_COLM, userProfile.getUserName());
                            if (null != userProfile.getEmailId())
                                userDocument.append(EMAIL_ID_COLM, userProfile.getEmailId());
                            if (null != userProfile.getMobileNumber())
                                userDocument.append(MOBILE_NO_COLM, userProfile.getMobileNumber());
                            if (null != userProfile.getPassword())
                                userDocument.append(PASSWORD_COLM, userProfile.getPassword());
                            userDocument.append(SUSPENDED_COLM, 0);
                            userDocument.append(MOBILE_VERIFICATION_STATUS_COLM, 0);
                            userDocument.append(EMAIL_VERIFICATION_STATUS_COLM, 0);
                            userDocument.append(ID_PROOF_VERIFICATION_STATUS_COLM, 0);

                            mongoClient.getDatabase(DB_NAME).getCollection(USER_PROFILE_TABLE).insertOne(userDocument).continueWith(new Continuation<Void, Object>() {
                                @Override
                                public Object then(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        Toast.makeText(context, "Your Profile Inserted seccessfully...", Toast.LENGTH_LONG).show();
                                        PhoneDBConnector phoneDBConnector = new PhoneDBConnector(context);
                                        Toast.makeText(context, phoneDBConnector.insertUserProfile(userProfile), Toast.LENGTH_LONG).show();
                                        Settings.USER_PROFILE = userProfile;
                                        Intent homPage = new Intent(context, HomePage.class);
                                        context.startActivity(homPage);
                                        ((SignUpPage) context).showProgress(false);
                                        ((Activity) context).finish();
                                        return null;
                                    }
                                    Toast.makeText(context, "Network error, Please try again after some time", Toast.LENGTH_LONG).show();
                                    ((SignUpPage) context).showProgress(false);
                                    return null;
                                }



                            });
                            break;
                        case 1:
                            Toast.makeText(context, "Profile already exist, please login", Toast.LENGTH_LONG).show();
                            ((SignUpPage) context).showProgress(false);

                            break;
                        case 2:
                            Toast.makeText(context, "Network error, Please try again after some time", Toast.LENGTH_LONG).show();
                            ((SignUpPage) context).showProgress(false);

                            break;
                        default:
                            ((SignUpPage) context).showProgress(false);

                    }

                }
            });
        }
    }

    public void getNews(final RecyclerView mRecyclerView) {
        NEWS_LIST = null;
        if (!stitchClient.isAuthenticated()) {
            doAnonymousAuthentication();
            return ;
        } else {
            final Document query = new Document( );
            final Document sort = new Document( LAST_UPDATE_TIME_COLM,1);
            mongoClient.getDatabase(DB_NAME).getCollection(NEWS_TABLE).find(query).continueWith(new Continuation<List<Document>, List<News>>() {
                @Override
                public List<News> then(@NonNull Task<List<Document>> task) {
                    List<News> newsList = null;
                    if (task.isSuccessful()) {
                        if (task.getResult().size() == 0) {
                            return null;
                        }
                        newsList = new ArrayList<>();
                        for (Document resultSet:
                                task.getResult()) {
                            final News news = new News();
                            news.setHeading(resultSet.getString(HEADING_COLM));
                            news.setDetails(resultSet.getString(DETAILS_COLM));
                            news.setViewCount(resultSet.getInteger(VIEW_COUNT_COLM));
                            news.setLikeCount(resultSet.getInteger(LIKE_COUNT_COLM));
                            news.setDislikeCount(resultSet.getInteger(DISLIKE_COUNT_COLM));
                            news.setUserProfile(new UserProfile("",resultSet.getString(MOBILE_NO_COLM),"","",false,0));
                            news.setNewsPic(Arrays.asList(null ==resultSet.get(PICS_COLM,Bitmap.class)? BitmapFactory.decodeResource(context.getResources(), R.drawable.dummy):resultSet.get(PICS_COLM,Bitmap.class)));
                            news.setHeading(resultSet.getString(HEADING_COLM));
                            Calendar createdDate = resultSet.get(CREATED_TIME_COLM, Calendar.class);
                            news.setDate(createdDate.get(Calendar.DATE)+"-"+createdDate.get(Calendar.MONTH)+"-"+createdDate.get(Calendar.YEAR));
                            news.setTime(createdDate.get(Calendar.HOUR)+":"+createdDate.get(Calendar.MINUTE)+":"+createdDate.get(Calendar.SECOND));
                            newsList.add(news);
                        }

                    }
                    return newsList;
                }
            }).addOnCompleteListener(new OnCompleteListener<List<News>>() {
                @Override
                public void onComplete(@NonNull Task<List<News>> task) {
                    for (final News news:
                         task.getResult()) {
                        final Document query = new Document( MOBILE_NO_COLM,news.getUserProfile().getMobileNumber());
                        mongoClient.getDatabase(DB_NAME).getCollection(USER_PROFILE_TABLE).find(query).continueWith(new Continuation<List<Document>, UserProfile>() {
                            @Override
                            public UserProfile then(@NonNull Task<List<Document>> task) {
                                final UserProfile userProfile = new UserProfile();
                                userProfile.setUserName("Error");
                                if (task.isSuccessful()) {
                                    if (task.getResult().size() == 0) {
                                        Toast.makeText(context, "No Result Fount!", Toast.LENGTH_LONG).show();
                                        return userProfile;
                                    } else {
                                        final Document resultSet = task.getResult().get(0);
                                        try {
                                            userProfile.setUserName(resultSet.getString(NAME_COLM));
                                            userProfile.setMobileNumber(resultSet.getString(MOBILE_NO_COLM));
                                            userProfile.setEmailId(resultSet.getString(EMAIL_ID_COLM));
                                            userProfile.setPassword(resultSet.getString(PASSWORD_COLM));
                                            userProfile.setLogin(false);
                                            userProfile.setSuspended(resultSet.getInteger(SUSPENDED_COLM));
                                            userProfile.setGender(null != resultSet.getString(GENDER_COLM) ? resultSet.getString(GENDER_COLM) : null);
                                            Address address = new Address((null != resultSet.getString(HOUSE_NO_COLM) ? resultSet.getString(HOUSE_NO_COLM) : null), (null != resultSet.getString(STREET_NAME_COLM) ? resultSet.getString(STREET_NAME_COLM) : null),
                                                    (null != resultSet.getString(POST_OFFICE_NAME_COLM) ? resultSet.getString(POST_OFFICE_NAME_COLM) : null), (null != resultSet.getString(POST_OFFICE_CODE_COLM) ? resultSet.getString(POST_OFFICE_CODE_COLM) : null), (null != resultSet.getString(DISTRICT_COLM) ? resultSet.getString(DISTRICT_COLM) : null), (null != resultSet.getString(STATE_COLM) ? resultSet.getString(STATE_COLM) : null));
                                            userProfile.setAddress(address);
                                            userProfile.setWalletAmount(null != resultSet.getString(WALLET_AMOUNT_COLM) ? Integer.parseInt(resultSet.getString(WALLET_AMOUNT_COLM)) : 0);
                                            userProfile.setDiscription(null != resultSet.getString(DESCRIPTION_COLM) ? resultSet.getString(DESCRIPTION_COLM) : null);
                                            Bitmap[] idProof = new Bitmap[4];

                                            idProof[0] = (null != resultSet.get(ID_PIC_1_COLM, Bitmap.class) ? resultSet.get(ID_PIC_1_COLM, Bitmap.class) : null);
                                            idProof[1] = (null != resultSet.get(ID_PIC_2_COLM, Bitmap.class) ? resultSet.get(ID_PIC_2_COLM, Bitmap.class) : null);
                                            idProof[2] = (null != resultSet.get(ID_PIC_3_COLM, Bitmap.class) ? resultSet.get(ID_PIC_3_COLM, Bitmap.class) : null);
                                            idProof[3] = (null != resultSet.get(ID_PIC_4_COLM, Bitmap.class) ? resultSet.get(ID_PIC_4_COLM, Bitmap.class) : null);
                                            userProfile.setIdProof(idProof);
                                            userProfile.setProfilePic((null != resultSet.get(PROFILE_PIC_COLM, Bitmap.class) ? resultSet.get(PROFILE_PIC_COLM, Bitmap.class) : null));
                                            return userProfile;
                                        } catch (Exception e) {
                                            Toast.makeText(context, "Got Exception!", Toast.LENGTH_LONG).show();
                                            return userProfile;
                                        }
                                    }
                                }
                                Toast.makeText(context, "Network Error!", Toast.LENGTH_LONG).show();
                                return userProfile;
                            }
                        }).addOnCompleteListener(new OnCompleteListener<UserProfile>() {
                            @Override
                            public void onComplete(@NonNull Task<UserProfile> task) {

                                UserProfile userProfile = task.getResult();
                                news.setUserProfile(userProfile);
                                Settings.ONLINE_NEWS.add(news);
                                RecyclerView.Adapter mAdapter = new RecyclerViewAdapter(Settings.ONLINE_NEWS);
                                mRecyclerView.setAdapter(mAdapter);
                            }
                        });
                    }

                }
            });
        }
    }

    public void insertNews(News news) {
        if (!stitchClient.isAuthenticated()) {
        } else {

            Document userDocument = new Document();
            if (null != news.getHeading())
                userDocument.append(HEADING_COLM, news.getHeading());
            if (null != news.getDetails())
                userDocument.append(DETAILS_COLM, news.getDetails());
            userDocument.append(VIEW_COUNT_COLM, news.getViewCount());
            userDocument.append(LIKE_COUNT_COLM, news.getLikeCount());
            userDocument.append(DISLIKE_COUNT_COLM, news.getDislikeCount());
            userDocument.append(AUTHOR_MOBILE_NO_COLM, news.getUserProfile().getMobileNumber());
            if (null != news.getNewsPic())
                userDocument.append(PICS_COLM, news.getNewsPic());

            mongoClient.getDatabase(DB_NAME).getCollection(NEWS_TABLE).insertOne(userDocument).continueWith(new Continuation<Void, Object>() {
                @Override
                public Object then(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {
                        Toast.makeText(context, "News Story added successfully... ", Toast.LENGTH_LONG).show();
                        return task.getResult();
                    }
                    return null;
                }
            });


        }
    }

    public void Login(String phoneNoOrEmail,final String mPassword,final boolean keepMeSignIn) {
        if (!stitchClient.isAuthenticated())
            doAnonymousAuthentication();

            final Document query = new Document( MOBILE_NO_COLM,phoneNoOrEmail);
            mongoClient.getDatabase(DB_NAME).getCollection(USER_PROFILE_TABLE).find(query).continueWith(new Continuation<List<Document>, UserProfile>() {
                @Override
                public UserProfile then(@NonNull Task<List<Document>> task) {
                    final UserProfile userProfile = new UserProfile();
                    if (task.isSuccessful()) {
                        if (task.getResult().size() == 0) {
                            Toast.makeText(context, "No Result Fount!", Toast.LENGTH_LONG).show();
                            return userProfile;
                        } else {
                            final Document resultSet = task.getResult().get(0);
                            Log.d(TAG,"Fetch user : "+resultSet.getString(NAME_COLM));
                            try {
                                userProfile.setUserName(resultSet.getString(NAME_COLM));
                                userProfile.setMobileNumber(resultSet.getString(MOBILE_NO_COLM));
                                userProfile.setEmailId(resultSet.getString(EMAIL_ID_COLM));
                                userProfile.setPassword(resultSet.getString(PASSWORD_COLM));
                                userProfile.setLogin(false);
                                userProfile.setSuspended(resultSet.getInteger(SUSPENDED_COLM));
                                userProfile.setGender(null != resultSet.getString(GENDER_COLM) ? resultSet.getString(GENDER_COLM) : null);
                                Address address = new Address((null != resultSet.getString(HOUSE_NO_COLM) ? resultSet.getString(HOUSE_NO_COLM) : null), (null != resultSet.getString(STREET_NAME_COLM) ? resultSet.getString(STREET_NAME_COLM) : null),
                                        (null != resultSet.getString(POST_OFFICE_NAME_COLM) ? resultSet.getString(POST_OFFICE_NAME_COLM) : null), (null != resultSet.getString(POST_OFFICE_CODE_COLM) ? resultSet.getString(POST_OFFICE_CODE_COLM) : null), (null != resultSet.getString(DISTRICT_COLM) ? resultSet.getString(DISTRICT_COLM) : null), (null != resultSet.getString(STATE_COLM) ? resultSet.getString(STATE_COLM) : null));
                                userProfile.setAddress(address);
                                userProfile.setWalletAmount(null != resultSet.getString(WALLET_AMOUNT_COLM) ? Integer.parseInt(resultSet.getString(WALLET_AMOUNT_COLM)) : 0);
                                userProfile.setDiscription(null != resultSet.getString(DESCRIPTION_COLM) ? resultSet.getString(DESCRIPTION_COLM) : null);
                                Bitmap[] idProof = new Bitmap[4];

                                idProof[0] = (null != resultSet.get(ID_PIC_1_COLM, Bitmap.class) ? resultSet.get(ID_PIC_1_COLM, Bitmap.class) : null);
                                idProof[1] = (null != resultSet.get(ID_PIC_2_COLM, Bitmap.class) ? resultSet.get(ID_PIC_2_COLM, Bitmap.class) : null);
                                idProof[2] = (null != resultSet.get(ID_PIC_3_COLM, Bitmap.class) ? resultSet.get(ID_PIC_3_COLM, Bitmap.class) : null);
                                idProof[3] = (null != resultSet.get(ID_PIC_4_COLM, Bitmap.class) ? resultSet.get(ID_PIC_4_COLM, Bitmap.class) : null);
                                userProfile.setIdProof(idProof);
                                userProfile.setProfilePic((null != resultSet.get(PROFILE_PIC_COLM, Bitmap.class) ? resultSet.get(PROFILE_PIC_COLM, Bitmap.class) : null));
                                return userProfile;
                            } catch (Exception e) {
                                Toast.makeText(context, "Got Exception!", Toast.LENGTH_LONG).show();
                                return userProfile;
                            }
                        }
                    }
                    Toast.makeText(context, "Network Error!", Toast.LENGTH_LONG).show();
                    return userProfile;
                }
            }).addOnCompleteListener(new OnCompleteListener<UserProfile>() {
                @Override
                public void onComplete(@NonNull Task<UserProfile> task) {

                    UserProfile userProfile = task.getResult();
                    if (null != userProfile){
                            if (mPassword.equals(userProfile.getPassword())) {
                                userProfile.setLogin(keepMeSignIn);
                                Settings.USER_PROFILE = userProfile;
                                PhoneDBConnector phoneDBConnector = new PhoneDBConnector(context);
                                phoneDBConnector.insertUserProfile(userProfile);

                                Intent homPage = new Intent(context, HomePage.class);
                                context.startActivity(homPage);
                                ((Activity) context).finish();
                            }else
                                Toast.makeText(context, "Wrong password!", Toast.LENGTH_LONG).show();

                    }
                    ((LoginMainActivity) context).showProgress(false);

                }
            });

    }

    public void getNewsFirst() {
        Log.d(TAG,"getNewsFirst");

        if (!stitchClient.isAuthenticated()) {
            Log.d(TAG,"No Auth");
            return ;
        } else {
            final Document query = new Document( );
            final Document sort = new Document( LAST_UPDATE_TIME_COLM,1);
            mongoClient.getDatabase(DB_NAME).getCollection(NEWS_TABLE).find(query).continueWith(new Continuation<List<Document>, List<News>>() {
                @Override
                public List<News> then(@NonNull Task<List<Document>> task) {
                    List<News> newsList = null;
                    if (task.isSuccessful()) {
                        if (task.getResult().size() == 0) {
                            Log.d(TAG,"No News in Remort DB");
                            return null;
                        }
                        Log.d(TAG,"result Size from remort : "+task.getResult().size());
                        newsList = new ArrayList<>();
                        try {
                            for (Document resultSet :
                                    task.getResult()) {
                                final News news = new News();
                                news.setHeading(resultSet.getString(HEADING_COLM));
                                news.setDetails(resultSet.getString(DETAILS_COLM));
                                news.setViewCount(resultSet.getDouble(VIEW_COUNT_COLM));
                                news.setLikeCount(resultSet.getDouble(LIKE_COUNT_COLM));
                                news.setDislikeCount(resultSet.getDouble(DISLIKE_COUNT_COLM));
                                news.setUserProfile(new UserProfile("", resultSet.getString(MOBILE_NO_COLM), "", "", false, 0));
                                news.setNewsPic(Arrays.asList(null == resultSet.get(PICS_COLM, Bitmap.class) ? BitmapFactory.decodeResource(context.getResources(), R.drawable.dummy) : resultSet.get(PICS_COLM, Bitmap.class)));
                                news.setHeading(resultSet.getString(HEADING_COLM));
                                Calendar createdDate = Calendar.getInstance();//(null == resultSet.get(CREATED_TIME_COLM, Calendar.class))?Calendar.getInstance():resultSet.get(CREATED_TIME_COLM, Calendar.class);
                                news.setDate(createdDate.get(Calendar.DATE) + "-" + createdDate.get(Calendar.MONTH) + "-" + createdDate.get(Calendar.YEAR));
                                news.setTime(createdDate.get(Calendar.HOUR) + ":" + createdDate.get(Calendar.MINUTE) + ":" + createdDate.get(Calendar.SECOND));
                                Log.d(TAG,"News Heading : "+news.getHeading());
                                newsList.add(news);
                            }
                        }catch (Exception e){
                            Log.d(TAG,"Exception : "+e.getMessage());

                        }

                    }
                    return newsList;
                }
            }).addOnCompleteListener(new OnCompleteListener<List<News>>() {
                @Override
                public void onComplete(@NonNull Task<List<News>> task) {

                    Settings.ONLINE_NEWS = task.getResult();
                    Log.d(TAG,"Result : "+Settings.ONLINE_NEWS.get(0).getHeading());

                    Intent intent = new Intent(context, HomePage.class);
                    context.startActivity(intent);
                    ((Activity) context).finish();

                }
            });

        }
    }
}
