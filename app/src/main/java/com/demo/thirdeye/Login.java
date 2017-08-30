package com.demo.thirdeye;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.demo.thirdeye.beans.DataObject;
import com.demo.thirdeye.utility.CustomPageAdapter;
import com.demo.thirdeye.utility.DynamicViewPager;
import com.demo.thirdeye.utility.PageAdapterForNews;
import com.demo.thirdeye.utility.Settings;

import java.util.ArrayList;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Login extends AppCompatActivity {

    private DynamicViewPager viewPager;
    private PagerAdapter pagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);


        List<DataObject> getData = dataSource();
        viewPager = (DynamicViewPager)findViewById(R.id.viewpager);
        viewPager.setMaxPages(3);
        viewPager.setBackgroundAsset(R.drawable.login_bk1);
        CustomPageAdapter mCustomPagerAdapter = new CustomPageAdapter(this, getData);
        viewPager.setAdapter(mCustomPagerAdapter);
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager,true);

        final TextView login = (TextView)findViewById(R.id.login);
        login.setTypeface(Settings.AGENCY_FB);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login.setTextColor(Color.BLUE);
                Intent intent = new Intent(Login.this, LoginMainActivity.class);
                Login.this.startActivity(intent);
                Login.this.overridePendingTransition(R.animator.enter_left, R.animator.exit_right);

                finish();
            }
        });

        final TextView signUp = (TextView)findViewById(R.id.signUp);
        signUp.setTypeface(Settings.AGENCY_FB);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp.setTextColor(Color.BLUE);
                Intent intent = new Intent(Login.this, MobileVerificationActivity.class);
                Login.this.startActivity(intent);
                Login.this.overridePendingTransition(R.animator.enter_right, R.animator.exit_left);
                finish();
            }
        });
        ImageView skip = (ImageView) findViewById(R.id.skipFromStarupPage);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent homPage = new Intent(Login.this,HomePage.class);
                Login.this.startActivity(homPage);
                finish();
            }
        });
    }
    private List<DataObject> dataSource(){
        List<DataObject> data = new ArrayList<>();
        data.add(new DataObject(R.drawable.thirdeye_icon, "1",R.string.page_1_caption,R.string.page_1_caption_details));
        data.add(new DataObject(R.drawable.login_page_2_img, "2",R.string.page_2_caption,R.string.page_2_caption_details));
        data.add(new DataObject(R.drawable.login_page_3_img, "3",R.string.page_3_caption,R.string.page_3_caption_details));
        return data;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent homPage = new Intent(Login.this,HomePage.class);
        Login.this.startActivity(homPage);
        finish();
    }
}
