package com.demo.thirdeye;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.thirdeye.beans.News;
import com.demo.thirdeye.utility.MongoDBConnector;
import com.demo.thirdeye.utility.PageAdapterForNews;
import com.demo.thirdeye.utility.Settings;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

public class NewsPostActivity extends AppCompatActivity {

    private List<Bitmap> showImageAndVideo = new ArrayList<>();
    private ViewPager imageView;
    private TabLayout tabLayout;
    private Button post;
    private EditText heading,details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_post);

        imageView = (ViewPager) findViewById(R.id.postNewsPics);
        tabLayout = (TabLayout) findViewById(R.id.postTabLayout);
        post = (Button) findViewById(R.id.postButton);
        heading = (EditText) findViewById(R.id.postHeading);
        details = (EditText) findViewById(R.id.postDetails);

        post.setTypeface(Settings.AGENCY_FB);
        heading.setTypeface(Settings.AGENCY_FB);
        details.setTypeface(Settings.AGENCY_FB);


        if (MediaSelectedActivity.sendImageBmpList.size()!=0){
            showImageAndVideo.addAll(MediaSelectedActivity.sendImageBmpList);
        }
        final Bitmap videoIcon = BitmapFactory.decodeResource(getResources(),R.drawable.video_start_icon);

        if (MediaSelectedActivity.sendVideoList.size()!=0){
            for (int i=0;i<MediaSelectedActivity.sendVideoList.size();i++) {

                Bitmap videoBmp = getVideoThumbnail(this, MediaSelectedActivity.sendVideoList.get(i));
                if (null != videoBmp) {
                    int videoBmpWidth = videoBmp.getWidth();
                    int videoBmpHight = videoBmp.getHeight();

                    Rect srcVideo = new Rect(0, 0, videoIcon.getWidth() - 1, videoIcon.getHeight() - 1);
                    Rect destVedio = new Rect(videoBmpWidth - videoBmpWidth / 4 - 1, videoBmpHight - videoBmpHight / 4 - 1, videoBmpWidth - 1, videoBmpHight - 1);
                    final Bitmap videoBmpWithIcon = Bitmap.createBitmap(videoBmpWidth, videoBmpHight, videoBmp.getConfig());
                    Canvas canvas = new Canvas(videoBmpWithIcon);
                    canvas.drawBitmap(videoBmp, new Matrix(), null);
                    canvas.drawBitmap(videoIcon, srcVideo, destVedio, new Paint());
                    showImageAndVideo.add(videoBmpWithIcon);
                }
            }
        }

        PageAdapterForNews newPageAdapter = new PageAdapterForNews(this, showImageAndVideo);
        imageView.setAdapter(newPageAdapter);
        tabLayout.setupWithViewPager(imageView, true);

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                News news = new News();
                news.setHeading(heading.getText().toString());
                news.setNewsPic(showImageAndVideo);
                news.setDetails(details.getText().toString());
                news.setLikeCount(0);
                news.setDislikeCount(0);
                news.setViewCount(0);
                news.setUserProfile(Settings.USER_PROFILE);
                news.setDateAndTime(Calendar.getInstance());

                MongoDBConnector mongoDBConnector = new MongoDBConnector(NewsPostActivity.this);
                if (Settings.INTERNET_STATUS){
                    mongoDBConnector.insertNews(news);
                }else{
                    Toast.makeText(NewsPostActivity.this,"Please On your internet to POST",Toast.LENGTH_LONG);
                }
            }
        });

    }
    private Bitmap getVideoThumbnail(Context context, Uri uri) throws IllegalArgumentException,
            SecurityException{
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(context,uri);
        return retriever.getFrameAtTime();
    }
}
