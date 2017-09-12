package com.demo.thirdeye;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.TextView;

import com.demo.thirdeye.utility.MediaAdapter;

import java.util.ArrayList;
import java.util.List;

public class MediaSelectedActivity extends AppCompatActivity {

    private GridView imageGridView;
    private GridView videoGridView;
    private TextView imageHeading;
    private TextView videoHeading;

    String TAG = "MediaSelectedActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_selected);
        Log.d(TAG, "onCreate: ");
        imageGridView = (GridView) findViewById(R.id.imageGridView);
        videoGridView = (GridView) findViewById(R.id.videoGridView);

        imageGridView.setAdapter(new MediaAdapter(this,CameraActivity.selectedImageBmpList));

        List<Uri> videoList = CameraActivity.selectedVideoUriList;
        List<Bitmap> videoBmpList = new ArrayList<>();
        for (int i=0;i<videoList.size();i++){
            Log.d(TAG, "onCreate: "+videoList.get(i).getPath());
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(videoList.get(i).getPath(), MediaStore.Video.Thumbnails.MICRO_KIND);
            videoBmpList.add(bitmap);
        }
        videoGridView.setAdapter(new MediaAdapter(this,videoBmpList));

    }
}
