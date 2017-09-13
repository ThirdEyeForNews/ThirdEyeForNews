package com.demo.thirdeye;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.thirdeye.utility.MediaAdapter;

import java.util.ArrayList;
import java.util.List;

public class MediaSelectedActivity extends AppCompatActivity {

    private GridView imageGridView;
    private Button proceed;
    private List<Bitmap> reachedImageBmpList;
    private List<Boolean> reachedImageFlagList = new ArrayList<>();

    private List<Uri> reachedVideoList;
    private List<Boolean> reachedVideoFlagList = new ArrayList<>();

    public static List<Bitmap> sendImageBmpList = new ArrayList<>();
    public static List<Uri> sendVideoList = new ArrayList<>();


    String TAG = "MediaSelectedActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_selected);

        reachedImageBmpList = CameraActivity.selectedImageBmpList;
        reachedVideoList = CameraActivity.selectedVideoUriList;

        imageGridView = (GridView) findViewById(R.id.imageGridView);
        proceed = (Button) findViewById(R.id.mediaSelectionProceed);

        List<Bitmap> imageListToDisplay = new ArrayList<>();

        final Paint paint = new Paint();
        paint.setColor(0x80808080);

        final Bitmap tic = BitmapFactory.decodeResource(getResources(),R.drawable.green_tic);
        for (int i=0;i<reachedImageBmpList.size();i++){
            reachedImageFlagList.add(true);
            Bitmap bmp = reachedImageBmpList.get(i);

            Bitmap bmOverlay = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());
            Canvas canvas = new Canvas(bmOverlay);
            canvas.drawBitmap(bmp, new Matrix(), paint);
            Rect src = new Rect(0,0,tic.getWidth()-1, tic.getHeight()-1);
            Rect dest = new Rect(0,0,bmp.getWidth()/4-1, bmp.getHeight()/4-1);
            canvas.drawBitmap(tic, src,dest, new Paint());
            imageListToDisplay.add(bmOverlay);

        }


        List<Bitmap> videoListToDisplay = new ArrayList<>();
        final Bitmap videoIcon = BitmapFactory.decodeResource(getResources(),R.drawable.video_start_icon);

        for (int i=0;i<reachedVideoList.size();i++){
            reachedVideoFlagList.add(true);
            Bitmap videoBmp = getVideoThumbnail(this,reachedVideoList.get(i));
            if (null != videoBmp) {
                int videoBmpWidth = videoBmp.getWidth();
                int videoBmpHight = videoBmp.getHeight();

                Rect srcVideo = new Rect(0, 0, videoIcon.getWidth() - 1, videoIcon.getHeight() - 1);
                Rect destVedio = new Rect(videoBmpWidth - videoBmpWidth / 4 - 1, videoBmpHight - videoBmpHight / 4 - 1, videoBmpWidth - 1, videoBmpHight - 1);
                final Bitmap videoBmpWithIcon = Bitmap.createBitmap(videoBmpWidth, videoBmpHight, videoBmp.getConfig());
                Canvas canvas = new Canvas(videoBmpWithIcon);
                canvas.drawBitmap(videoBmp, new Matrix(), null);
                canvas.drawBitmap(videoIcon, srcVideo, destVedio, new Paint());

                final Bitmap bmOverlay = Bitmap.createBitmap(videoBmpWidth, videoBmpHight, videoBmpWithIcon.getConfig());
                Canvas canvasWithOverlay = new Canvas(bmOverlay);
                canvasWithOverlay.drawBitmap(videoBmpWithIcon, new Matrix(), paint);
                Rect srcTic = new Rect(0, 0, tic.getWidth() - 1, tic.getHeight() - 1);
                Rect destTic = new Rect(0, 0, videoBmpWidth / 4 - 1, videoBmpHight / 4 - 1);
                canvasWithOverlay.drawBitmap(tic, srcTic, destTic, new Paint());

                videoListToDisplay.add(bmOverlay);
            }
        }
        final List<Bitmap> selectedMedia = new ArrayList<>();
        if (imageListToDisplay.size()!=0)
            selectedMedia.addAll(imageListToDisplay);
        if (videoListToDisplay.size()!=0)
            selectedMedia.addAll(videoListToDisplay);
        if (selectedMedia.size()!=0)
            imageGridView.setAdapter(new MediaAdapter(this,selectedMedia));

        imageGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: "+position);
                boolean isImageSelected = false;
                int selectedMediaIndex = 0;
                if (position<reachedImageBmpList.size()){
                    selectedMediaIndex = position;
                    isImageSelected = true;
                }else{
                    isImageSelected = false;
                    selectedMediaIndex = position-reachedImageBmpList.size();
                }

                if (isImageSelected){
                    if (reachedImageFlagList.get(selectedMediaIndex)){
                        reachedImageFlagList.set(selectedMediaIndex,false);
                        selectedMedia.set(position,reachedImageBmpList.get(selectedMediaIndex));
                        imageGridView.setAdapter(new MediaAdapter(MediaSelectedActivity.this,selectedMedia));
                    }else{
                        reachedImageFlagList.set(selectedMediaIndex,true);
                        Bitmap bmp = selectedMedia.get(position);
                        Bitmap bmOverlay = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());
                        Canvas canvas = new Canvas(bmOverlay);
                        canvas.drawBitmap(bmp, new Matrix(), paint);
                        Rect src = new Rect(0,0,tic.getWidth()-1, tic.getHeight()-1);
                        Rect dest = new Rect(0,0,bmp.getWidth()/4-1, bmp.getHeight()/4-1);
                        canvas.drawBitmap(tic, src,dest, new Paint());
                        selectedMedia.set(position,bmOverlay);
                        imageGridView.setAdapter(new MediaAdapter(MediaSelectedActivity.this,selectedMedia));
                    }
                }else{
                    if (reachedVideoFlagList.get(selectedMediaIndex)){
                        reachedVideoFlagList.set(selectedMediaIndex,false);

                        Bitmap videoBmp = getVideoThumbnail(MediaSelectedActivity.this,reachedVideoList.get(selectedMediaIndex));
                        if (null != videoBmp) {
                            int videoBmpWidth = videoBmp.getWidth();
                            int videoBmpHight = videoBmp.getHeight();

                            Rect srcVideo = new Rect(0, 0, videoIcon.getWidth() - 1, videoIcon.getHeight() - 1);
                            Rect destVedio = new Rect(videoBmpWidth - videoBmpWidth / 4 - 1, videoBmpHight - videoBmpHight / 4 - 1, videoBmpWidth - 1, videoBmpHight - 1);
                            final Bitmap videoBmpWithIcon = Bitmap.createBitmap(videoBmpWidth, videoBmpHight, videoBmp.getConfig());
                            Canvas canvas = new Canvas(videoBmpWithIcon);
                            canvas.drawBitmap(videoBmp, new Matrix(), null);
                            canvas.drawBitmap(videoIcon, srcVideo, destVedio, new Paint());
                            selectedMedia.set(position, videoBmpWithIcon);
                            imageGridView.setAdapter(new MediaAdapter(MediaSelectedActivity.this, selectedMedia));
                        }
                    }else{
                        reachedVideoFlagList.set(selectedMediaIndex,true);

                        Bitmap bmp = selectedMedia.get(position);
                        Bitmap bmOverlay = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());
                        Canvas canvas = new Canvas(bmOverlay);
                        canvas.drawBitmap(bmp, new Matrix(), paint);
                        Rect src = new Rect(0,0,tic.getWidth()-1, tic.getHeight()-1);
                        Rect dest = new Rect(0,0,bmp.getWidth()/4-1, bmp.getHeight()/4-1);
                        canvas.drawBitmap(tic, src,dest, new Paint());
                        selectedMedia.set(position,bmOverlay);
                        imageGridView.setAdapter(new MediaAdapter(MediaSelectedActivity.this,selectedMedia));
                    }
                }

                return ;
            }
        });

    proceed.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (reachedImageFlagList.contains(true) || reachedVideoFlagList.contains(true)){
                if (reachedImageFlagList.contains(true)){
                    for (int i=0;i<reachedImageBmpList.size();i++){
                        if (reachedImageFlagList.get(i)){
                            sendImageBmpList.add(reachedImageBmpList.get(i));
                        }
                    }
                }
                if (reachedVideoFlagList.contains(true)){
                    for (int i=0;i<reachedVideoFlagList.size();i++){
                        if (reachedVideoFlagList.get(i)){
                            sendVideoList.add(reachedVideoList.get(i));
                        }
                    }
                }

                Intent intent = new Intent(MediaSelectedActivity.this,NewsPostActivity.class);
                MediaSelectedActivity.this.startActivity(intent);

            }else{
                Toast.makeText(MediaSelectedActivity.this,"Please select one File",Toast.LENGTH_SHORT);
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
