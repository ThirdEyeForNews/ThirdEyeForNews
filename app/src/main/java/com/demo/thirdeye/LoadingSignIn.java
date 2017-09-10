package com.demo.thirdeye;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class LoadingSignIn extends AppCompatActivity {

    private static final String TAG = "LodingSignIn";

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

        fetchUserDetails = new FetchUserDetails(Settings.isNetworkAvailable(LoadingSignIn.this));
        fetchUserDetails.execute((Void) null);

        /*NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.thirdeye_icon)
                        .setContentTitle("Starting MSG")
                        .setContentText("My App got started");

        NotificationManager mNotificationManager =

                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(001, mBuilder.build());*/

    }


    public class FetchUserDetails extends AsyncTask<Void, Void, Integer>{

        private boolean networkStatus = false;

        public FetchUserDetails(boolean networkStatus){
            this.networkStatus = networkStatus;
            Settings.INTERNET_STATUS = networkStatus;
        }

        @Override
        protected Integer doInBackground(Void... params) {


            PhoneDBConnector phoneDBConnector = new PhoneDBConnector(LoadingSignIn.this);
            UserProfile userProfilePhone = phoneDBConnector.getUserProfile();
            Settings.OFFLINE_NEWS = phoneDBConnector.getOfflineNews();

            MongoDBConnector mongoDBConnector = new MongoDBConnector(LoadingSignIn.this);

            if (null == userProfilePhone)
                return 0;
            Settings.USER_PROFILE = userProfilePhone;

            if (!userProfilePhone.isLogin()){
                return 0;
            }

            if (Settings.INTERNET_STATUS){
                mongoDBConnector.getNewsFirst();
                return 2;
            }

            return 1;
        }

        @Override
        protected void onPostExecute(final Integer status) {
            fetchUserDetails = null;
            Intent intent = null;
            switch (status){
                case 0 :
                    intent = new Intent(LoadingSignIn.this, Login.class);
                    LoadingSignIn.this.startActivity(intent);
                    finish();
                    break;
                case 1 :
                    intent = new Intent(LoadingSignIn.this, HomePage.class);
                    LoadingSignIn.this.startActivity(intent);
                    finish();
                    break;
                case 2 :
                    Log.d(TAG, "getting remort news");
                    break;
                default:
            }

        }

        @Override
        protected void onCancelled() {
            fetchUserDetails = null;
        }
    }
}
