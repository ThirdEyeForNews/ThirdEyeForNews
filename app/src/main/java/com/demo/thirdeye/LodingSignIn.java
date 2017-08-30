package com.demo.thirdeye;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.os.AsyncTask;


import com.demo.thirdeye.beans.News;
import com.demo.thirdeye.beans.UserProfile;
import com.demo.thirdeye.utility.MongoDBConnector;
import com.demo.thirdeye.utility.PhoneDBConnector;
import com.demo.thirdeye.utility.Settings;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class LodingSignIn extends AppCompatActivity {

    FetchUserDetails fetchUserDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Settings.AGENCY_FB = Typeface.createFromAsset(getAssets(),"font/agency_fb.ttf");
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_loding_sign_in);

        TextView displayCaptionDetail = (TextView)findViewById(R.id.lodingCaption);
        displayCaptionDetail.setTypeface(Settings.AGENCY_FB);

        fetchUserDetails = new FetchUserDetails(isNetworkAvailable());
        fetchUserDetails.execute((Void) null);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.thirdeye_icon)
                        .setContentTitle("Starting MSG")
                        .setContentText("My App got started");

        NotificationManager mNotificationManager =

                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

//When you issue multiple notifications about the same type of event, it’s best practice for your app to try to update an existing notification with this new information, rather than immediately creating a new notification. If you want to update this notification at a later date, you need to assign it an ID. You can then use this ID whenever you issue a subsequent notification. If the previous notification is still visible, the system will update this existing notification, rather than create a new one. In this example, the notification’s ID is 001//

        mNotificationManager.notify(001, mBuilder.build());

    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public class FetchUserDetails extends AsyncTask<Void, Void, Boolean>{

        private boolean networkStatus = false;

        public FetchUserDetails(boolean networkStatus){
            this.networkStatus = networkStatus;
            Settings.INTERNET_STATUS = networkStatus;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            if (networkStatus){
                PhoneDBConnector phoneDBConnector = new PhoneDBConnector(LodingSignIn.this);
                UserProfile userProfilePhone = phoneDBConnector.getUserProfile();
                MongoDBConnector mongoDBConnector = new MongoDBConnector(LodingSignIn.this);
                Settings.OFFLINE_NEWS = new ArrayList<>();
                Settings.ONLINE_NEWS = new ArrayList<>();

                if (null == userProfilePhone)
                    return false;
                UserProfile userProfileMongo = null;
                //mongoDBConnector.getUserProfile(userProfilePhone);
                if (userProfilePhone.equals(userProfileMongo)){
                    if (userProfileMongo.getSuspended()>0){
                        new AlertDialog.Builder(LodingSignIn.this)
                                .setTitle("Suspended!!")
                                .setMessage("You are suspended for "+Settings.USER_PROFILE.getSuspended() +" days...")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        return false;
                    }else {

                        Settings.USER_PROFILE = userProfilePhone;
                        AssetManager assetManager = getAssets();
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
                            in = assetManager.open("image/profile_pic.jpg");
                            userProfile.setProfilePic(BitmapFactory.decodeStream(in));
                        }catch (Exception e){

                        }


                        return true;
                    }
                }else{
                    new AlertDialog.Builder(LodingSignIn.this)
                            .setTitle("Miss match!!")
                            .setMessage("There is some miss match with our DB, You might updated offline")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    return false;
                }

            }else{
                PhoneDBConnector phoneDBConnector = new PhoneDBConnector(LodingSignIn.this);
                Settings.USER_PROFILE = phoneDBConnector.getUserProfile();

                //TODO: get news from Phone DB
                    ArrayList<News> results = new ArrayList();
                    AssetManager assetManager = getAssets();
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
                    Settings.OFFLINE_NEWS = results;
                    Settings.ONLINE_NEWS = null;

            }
            if (null == Settings.USER_PROFILE || !Settings.USER_PROFILE.isLogin())
                return false;

            try {
                // Simulate network access.
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return false;
            }





            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            fetchUserDetails = null;
            if(success) {
                Intent intent = new Intent(LodingSignIn.this, HomePage.class);
                LodingSignIn.this.startActivity(intent);
                finish();
            }else{
                Intent intent = new Intent(LodingSignIn.this, Login.class);
                LodingSignIn.this.startActivity(intent);
                finish();
            }
        }

        @Override
        protected void onCancelled() {
            fetchUserDetails = null;
        }
    }
}
