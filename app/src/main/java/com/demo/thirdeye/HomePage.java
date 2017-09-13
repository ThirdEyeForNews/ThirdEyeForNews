package com.demo.thirdeye;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.thirdeye.beans.News;
import com.demo.thirdeye.beans.UserProfile;
import com.demo.thirdeye.utility.MongoDBConnector;
import com.demo.thirdeye.utility.RecyclerViewAdapter;
import com.demo.thirdeye.utility.Settings;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomePage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "HomePage";

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"Started");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Settings.askForPermissionForCamera(HomePage.this, Manifest.permission.CAMERA)){
                    Intent intent = new Intent(HomePage.this, CameraActivity.class);
                    HomePage.this.startActivity(intent);
                }
                //Settings.askForPermissionForSensor(HomePage.this, Manifest.permission.BODY_SENSORS);

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView = navigationView.getHeaderView(0);

        TextView navigationHeaderName = (TextView) hView.findViewById(R.id.navigationHeaderName);
        TextView navigationHeaderEmail = (TextView) hView.findViewById(R.id.navigationHeaderEmail);

        navigationHeaderName.setTypeface(Settings.AGENCY_FB);
        navigationHeaderEmail.setTypeface(Settings.AGENCY_FB);



        if(Settings.USER_PROFILE != null){

            navigationHeaderName.setText(Settings.USER_PROFILE.getUserName()==null?"Guest User":Settings.USER_PROFILE.getUserName());
            navigationHeaderEmail.setText(Settings.USER_PROFILE.getEmailId()==null?"Login":Settings.USER_PROFILE.getEmailId());
            Log.d(TAG,"Setting user");

        }

        mRecyclerView = (RecyclerView) findViewById(R.id.recycleViewHomePage);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        MongoDBConnector mongoDBConnector = new MongoDBConnector(this);

            //mongoDBConnector.getNews(mRecyclerView);
            if (null != Settings.ONLINE_NEWS && Settings.ONLINE_NEWS.size()!=0) {
                mAdapter = new RecyclerViewAdapter(Settings.ONLINE_NEWS);
                mRecyclerView.setAdapter(mAdapter);
                Log.d(TAG,"Setting online news : completed");
            } else {

                if (null != Settings.OFFLINE_NEWS && Settings.OFFLINE_NEWS.size() != 0) {
                    Snackbar.make(this.mRecyclerView, "No Internet showing old News", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    mAdapter = new RecyclerViewAdapter(Settings.OFFLINE_NEWS);
                    mRecyclerView.setAdapter(mAdapter);
                } else
                    Snackbar.make(this.mRecyclerView, "No Internet, No cache news", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
            }

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mRecyclerView.setLayoutManager(mLayoutManager);
                if (Settings.INTERNET_STATUS) {
                    if (null == Settings.ONLINE_NEWS){
                        Snackbar.make(recyclerView, "Internet is connected but internal server error in Third Eye", Snackbar.LENGTH_LONG)
                                .setAction("Retry", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                    }
                                }).show();

                    }
                }else{
                    if (null != Settings.OFFLINE_NEWS){
                        Snackbar.make(recyclerView, "No Internet showing old News", Snackbar.LENGTH_LONG)
                                .setAction("Retry", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (Settings.isNetworkAvailable(HomePage.this)){
                                            Settings.INTERNET_STATUS = true;
                                            Settings.ONLINE_NEWS = Settings.OFFLINE_NEWS;
                                        }
                                    }
                                }).show();
                    }else
                        Snackbar.make(recyclerView, "No Internet, No cache news", Snackbar.LENGTH_LONG)
                                .setAction("Retry", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (Settings.isNetworkAvailable(HomePage.this)){
                                            Settings.INTERNET_STATUS = true;
                                            Settings.ONLINE_NEWS = Settings.OFFLINE_NEWS;

                                        }
                                    }
                                }).show();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_my_news) {
            // Handle the camera action
        } else if (id == R.id.nav_top_news) {

        } else if (id == R.id.nav_followers) {

        } else if (id == R.id.nav_settings) {
            /* Inserted by Prasobh for Settings Page*/
            Intent intent = new Intent(HomePage.this, SettingsActivity.class);
            HomePage.this.startActivity(intent);

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_terms) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED)
        {

            Intent intent = new Intent(HomePage.this, CameraActivity.class);
            HomePage.this.startActivity(intent);
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }
}
