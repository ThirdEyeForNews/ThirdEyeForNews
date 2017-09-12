package com.demo.thirdeye.utility;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.demo.thirdeye.beans.News;
import com.demo.thirdeye.beans.UserProfile;

import java.util.List;

/**
 * Created by Manu on 8/1/2017.
 */

public class Settings {
    public static Typeface AGENCY_FB;
    public static UserProfile USER_PROFILE;
    public static boolean INTERNET_STATUS = false;
    public static List<News> ONLINE_NEWS;
    public static List<News> OFFLINE_NEWS;
    public static int CURRENT_OPEN_CAMERA = 0;
    public static boolean FLASH_ON = false;
    public static boolean IS_IMAGE = true;



    static final Integer CAMERA = 0x5;

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean askForPermissionForCamera(Activity context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(context, new String[]{permission}, CAMERA);

            } else {

                ActivityCompat.requestPermissions(context, new String[]{permission}, CAMERA);
            }
            return false;
        } else {
            //Toast.makeText(context, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    public static void askForPermissionForSensor(Activity context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(context, new String[]{permission}, CAMERA);

            } else {

                ActivityCompat.requestPermissions(context, new String[]{permission}, CAMERA);
            }
        } else {
            Toast.makeText(context, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }

}
