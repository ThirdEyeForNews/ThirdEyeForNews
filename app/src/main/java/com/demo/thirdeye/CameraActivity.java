package com.demo.thirdeye;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.CamcorderProfile;
import android.media.ExifInterface;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.thirdeye.utility.CameraPreview;
import com.demo.thirdeye.utility.Settings;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class CameraActivity extends AppCompatActivity implements SensorEventListener{

    private final int VIDEO_MAX_TIME = 60000; // Set max duration 60 sec.
    private final int VIDEO_MAX_SIZE = 50000000; // Set max file size 50M
    private static int RESULT_LOAD_IMAGE = 1;


    private ImageButton capture;
    private Camera mCamera;
    private CameraPreview cameraPreview;
    private int deviceHeight;
    private int deviceWidth;
    private File sdRoot;
    private String dir;
    private String fileName;
    private SensorManager sensorManager = null;
    private int orientation;
    private ExifInterface exif;
    private ImageView rotatingImage,flashImage;
    private int degrees = -1;
    private static final String TAG = "CameraActivity";
    private LinearLayout imageHorizontalLayout;
    private ArrayList<Bitmap> capturedImages = new ArrayList<>();
    private ArrayList<Boolean> capturedImagesSelection = new ArrayList<>();
    private ArrayList<ImageView> capturedImagesView = new ArrayList<>();
    private Switch videoSwitchView;
    private TextView videoTxt,videoTimer;
    private ImageView goOn,galleryIcon;
    private MediaRecorder recorder;
    private boolean recording = false;
    private int videoCount = 0;

    private ArrayList<Bitmap> videoBmpList = new ArrayList<>();
    private ArrayList<Boolean> selectedVideoBmp = new ArrayList<>();
    private ArrayList<String> videoFilePath = new ArrayList<>();
    private ArrayList<ImageView> capturedvideoView = new ArrayList<>();

    public static List<Uri> selectedVideoUriList = new ArrayList<>();
    public static List<Bitmap> selectedImageBmpList = new ArrayList<>();


    private CountDownTimer countDownTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        capture = (ImageButton)findViewById(R.id.capturImage);
        rotatingImage = (ImageView) findViewById(R.id.rotateCam);
        flashImage = (ImageView) findViewById(R.id.flashImage);
        videoSwitchView = (Switch)findViewById(R.id.videoSwitch);
        videoTxt = (TextView)findViewById(R.id.videoTxt);
        videoTimer = (TextView)findViewById(R.id.videoTimer);
        goOn = (ImageView) findViewById(R.id.goOnToHeading);
        galleryIcon = (ImageView) findViewById(R.id.goOnGallery);
        imageHorizontalLayout = (LinearLayout) findViewById(R.id.imageHorizontalLayout);


        videoTxt.setTypeface(Settings.AGENCY_FB);
        videoTimer.setTypeface(Settings.AGENCY_FB);
        videoSwitchView.setTypeface(Settings.AGENCY_FB);

        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        deviceHeight = display.getHeight();
        deviceWidth = display.getWidth();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        checkGalleryOrGoOn();

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: ");
                capture.setVisibility(View.GONE);
                videoSwitchView.setVisibility(View.GONE);
                videoTxt.setVisibility(View.GONE);

                if (Settings.IS_IMAGE) {
                    if (Settings.FLASH_ON && Settings.CURRENT_OPEN_CAMERA == 0) {
                        Camera.Parameters params = mCamera.getParameters();
                        params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                        mCamera.setParameters(params);
                        mCamera.startPreview();

                    } else if (Settings.CURRENT_OPEN_CAMERA == 0) {
                        Camera.Parameters params = mCamera.getParameters();
                        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        mCamera.setParameters(params);
                        mCamera.startPreview();

                    }
                    mCamera.takePicture(null, null, mPicture);
                }else{
                    capture.setVisibility(View.VISIBLE);

                    Log.d(TAG, "onClick: record");
                    if (recording){
                        try {
                            recorder.stop();
                        }catch (Exception e){
                            Log.d(TAG, "onClick:Estop "+e.getMessage());
                        }
                        countDownTimer.cancel();
                        videoSwitchView.setVisibility(View.VISIBLE);
                        videoTxt.setVisibility(View.VISIBLE);
                        rotatingImage.clearAnimation();
                        rotatingImage.setVisibility(View.VISIBLE);
                        goOn.setVisibility(View.VISIBLE);
                        videoTimer.setVisibility(View.GONE);

                        releaseMediaRecorder(); // release the MediaRecorder object

                        //Exit after saved
                        //finish();

                        videoFilePath.add(getCurrentVideoFilePath());
                        Bitmap videoBmp = ThumbnailUtils.createVideoThumbnail(getCurrentVideoFilePath(), MediaStore.Video.Thumbnails.MICRO_KIND);
                        int videoBmpWidth = videoBmp.getWidth(),videoBmpHight = videoBmp.getHeight();
                        videoCount++;
                        Bitmap videoIcon = BitmapFactory.decodeResource(getResources(),R.drawable.video_start_icon);
                        Rect srcVideo = new Rect(0,0,videoIcon.getWidth()-1, videoIcon.getHeight()-1);
                        Rect destVedio = new Rect(videoBmpWidth-videoBmpWidth/4-1, videoBmpHight-videoBmpHight/4-1,videoBmpWidth-1, videoBmpHight-1);
                        final Bitmap videoBmpWithIcon = Bitmap.createBitmap(videoBmpWidth, videoBmpHight, videoBmp.getConfig());
                        Canvas canvas = new Canvas(videoBmpWithIcon);
                        canvas.drawBitmap(videoBmp, new Matrix(), null);
                        canvas.drawBitmap(videoIcon, srcVideo,destVedio, new Paint());
                        videoBmpList.add(videoBmpWithIcon);
                        selectedVideoBmp.add(true);
                        final Paint paint = new Paint();
                        paint.setColor(0x80808080);

                        Bitmap tic = BitmapFactory.decodeResource(getResources(),R.drawable.green_tic);

                        final Bitmap bmOverlay = Bitmap.createBitmap(videoBmpWidth, videoBmpHight, videoBmpWithIcon.getConfig());
                        Canvas canvasWithOverlay = new Canvas(bmOverlay);
                        canvasWithOverlay.drawBitmap(videoBmpWithIcon, new Matrix(), paint);
                        Rect srcTic = new Rect(0,0,tic.getWidth()-1, tic.getHeight()-1);
                        Rect destTic = new Rect(0,0,videoBmpWidth/4-1, videoBmpHight/4-1);
                        canvasWithOverlay.drawBitmap(tic, srcTic,destTic, new Paint());


                        final ImageView image = new ImageView(CameraActivity.this);

                        image.setImageBitmap(Bitmap.createScaledBitmap(bmOverlay, imageHorizontalLayout.getHeight(),
                                imageHorizontalLayout.getHeight(), false));
                        capturedvideoView.add(image);

                        imageHorizontalLayout.addView(image);
                        image.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                int index;
                                if (selectedVideoBmp.get((index=capturedvideoView.indexOf(image)))){
                                    selectedVideoBmp.set(index,false);
                                    image.setImageBitmap(Bitmap.createScaledBitmap(videoBmpWithIcon, imageHorizontalLayout.getHeight(),
                                            imageHorizontalLayout.getHeight(), false));

                                }else{
                                    selectedVideoBmp.set(index,true);
                                    image.setImageBitmap(Bitmap.createScaledBitmap(bmOverlay, imageHorizontalLayout.getHeight(),
                                            imageHorizontalLayout.getHeight(), false));

                                }
                                Log.d(TAG, "onClick: index " +index);
                                checkGalleryOrGoOn();

                            }
                        });


                        Bitmap bMap =   Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.video_start_icon), capture.getWidth(), capture.getHeight(), true);
                        BitmapDrawable ob = new BitmapDrawable(getResources(), bMap);
                        capture.setBackground(ob);
                        recording = false;
                    }else {
                        rotatingImage.clearAnimation();
                        rotatingImage.setVisibility(View.GONE);
                        Log.d(TAG, "onClick: rotatingImage GONE");
                        goOn.setVisibility(View.GONE);
                        releaseCamera();
                        if (Settings.CURRENT_OPEN_CAMERA == 0) {
                            if (!prepareMediaRecorder(Camera.CameraInfo.CAMERA_FACING_BACK)) {
                                Toast.makeText(CameraActivity.this,
                                        "Fail in prepareMediaRecorder()!\n - Ended -" + Environment.getExternalStorageDirectory().getAbsolutePath(),
                                        Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }else{
                            if (!prepareMediaRecorder(Camera.CameraInfo.CAMERA_FACING_FRONT)) {
                                Toast.makeText(CameraActivity.this,
                                        "Fail in prepareMediaRecorder()!\n - Ended -" + Environment.getExternalStorageDirectory().getAbsolutePath(),
                                        Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }
                        try {
                            countDownTimer = new CountDownTimer(300000, 1000) {

                                @Override
                                public void onTick(long millisUntilFinished) {
                                    long millisUntil = 300000-millisUntilFinished;
                                    videoTimer.setVisibility(View.VISIBLE);
                                    String min = (millisUntil / 60000<10)?"0"+millisUntil / 60000:millisUntil / 60000+"";
                                    String sec = (((millisUntil % 60000) / 1000)<10)?"0"+((millisUntil % 60000) / 1000):((millisUntil % 60000) / 1000)+"";
                                    videoTimer.setText(min+":"+sec);
                                }

                                @Override
                                public void onFinish() {

                                }
                            };
                            countDownTimer.start();
                            recorder.start();
                        }catch (Exception e){
                            Log.d(TAG, "onClick: EStart"+e.getMessage());

                        }
                        recording = true;
                        Bitmap bMap =   Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.stop_vedio_icon), capture.getWidth(), capture.getHeight(), true);
                        BitmapDrawable ob = new BitmapDrawable(getResources(), bMap);
                        capture.setBackground(ob);
                        recording = true;
                    }


                }
                checkGalleryOrGoOn();
            }
        });

        rotatingImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mCamera){
                    mCamera.stopPreview();
                }

                releaseCamera();

                FrameLayout preview = (FrameLayout) findViewById(R.id.cam_preview);
                preview.removeViewAt(0);

                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                Log.d(TAG, "onClick: "+Camera.getNumberOfCameras());
                if (Settings.CURRENT_OPEN_CAMERA == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    createCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
                    Settings.CURRENT_OPEN_CAMERA = 0;

                }
                else {
                    createCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
                    Settings.CURRENT_OPEN_CAMERA = 1;

                }

            }
        });

        videoSwitchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (Settings.IS_IMAGE) {
                    Settings.IS_IMAGE = false;
                    Bitmap bMap =   Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.video_start_icon), capture.getWidth(), capture.getHeight(), false);
                    BitmapDrawable ob = new BitmapDrawable(getResources(), bMap);
                    capture.setBackground(ob);
                }
                else {

                    Bitmap bMap =   Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.capture_icon), capture.getWidth(), capture.getHeight(), false);
                    BitmapDrawable ob = new BitmapDrawable(getResources(), bMap);
                    capture.setBackground(ob);
                    Settings.IS_IMAGE = true;
                }
            }
        });

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            flashImage.setVisibility(View.GONE);
        }else{
            flashImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Settings.FLASH_ON){
                        Settings.FLASH_ON = false;
                        flashImage.setImageResource(R.drawable.flash_off);
                    }else{
                        Settings.FLASH_ON = true;
                        flashImage.setImageResource(R.drawable.flash_on);
                    }
                }
            });
        }


        goOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean imageAvailable = false;
                if (null!=capturedImagesSelection && capturedImagesSelection.size()!=0 && capturedImagesSelection.contains(true)){
                    imageAvailable = true;
                }
                boolean videoAvailable = false;
                if (null!=selectedVideoBmp && selectedVideoBmp.size()!=0 && selectedVideoBmp.contains(true)){
                    videoAvailable = true;
                }

                if (imageAvailable || videoAvailable){
                    if (imageAvailable){
                        for (int i =0;i<capturedImages.size();i++){
                            if (capturedImagesSelection.get(i)){
                                if (!selectedImageBmpList.contains(capturedImages.get(i)))
                                    selectedImageBmpList.add(capturedImages.get(i));
                            }
                        }
                    }
                    if (videoAvailable){
                        for (int i =0;i<videoFilePath.size();i++){
                            if (selectedVideoBmp.get(i)){
                                if (!selectedVideoUriList.contains(Uri.fromFile(new File(videoFilePath.get(i)))))
                                    selectedVideoUriList.add(Uri.fromFile(new File(videoFilePath.get(i))));
                            }
                        }
                    }
                    Log.d(TAG, "onClick: calling MediaSelectedActivity");
                    Intent homPage = new Intent(CameraActivity.this,MediaSelectedActivity.class);
                    CameraActivity.this.startActivity(homPage);
                }else{
                    Toast.makeText(CameraActivity.this,"Select any one file to go",Toast.LENGTH_SHORT).show();
                }
            }
        });

        galleryIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                i.setType("*/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

    }

    public void checkGalleryOrGoOn(){
        boolean imageAvailable = false;
        if (null!=capturedImagesSelection && capturedImagesSelection.size()!=0 && capturedImagesSelection.contains(true)){
            imageAvailable = true;
        }
        boolean videoAvailable = false;
        if (null!=selectedVideoBmp && selectedVideoBmp.size()!=0 && selectedVideoBmp.contains(true)){
            videoAvailable = true;
        }

        if (imageAvailable || videoAvailable){
            goOn.setVisibility(View.VISIBLE);
            galleryIcon.setVisibility(View.GONE);
        }else{
            goOn.setVisibility(View.GONE);
            galleryIcon.setVisibility(View.VISIBLE);
        }
    }

    private void releaseMediaRecorder(){
        if (recorder != null) {
            recorder.reset();   // clear recorder configuration
            recorder.release(); // release the recorder object
            recorder = new MediaRecorder();
            mCamera.lock();           // lock camera for later use
        }
    }
    private boolean prepareMediaRecorder(int camSelection){
        mCamera = getCameraInstance(camSelection);
        Camera.Parameters params = mCamera.getParameters();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        params.setRotation(90);
        mCamera.setParameters(params);

        if (this.getResources().getConfiguration().orientation
                != Configuration.ORIENTATION_LANDSCAPE) {
            mCamera.setDisplayOrientation(90);
        }
        else {
            mCamera.setDisplayOrientation(0);
        }

        recorder = new MediaRecorder();

        mCamera.unlock();
        recorder.setCamera(mCamera);

        recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));


        recorder.setOutputFile(getCurrentVideoFilePath());

        recorder.setMaxDuration(VIDEO_MAX_TIME);
        recorder.setMaxFileSize(VIDEO_MAX_SIZE);

        recorder.setPreviewDisplay(cameraPreview.getHolder().getSurface());
        try {
            recorder.prepare();
        } catch (IllegalStateException e) {
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            releaseMediaRecorder();
            return false;
        }
        return true;

    }

    public String getCurrentVideoFilePath(){
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ThirdEye/tmp/video/";
        File dir = new File(path);
        if(!dir.exists())
            dir.mkdirs();
        String myfile = path + "story_" +videoCount+ ".mp4";
        return myfile;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Test if there is a camera on the device and if the SD card is
        // mounted.
        if (!checkCameraHardware(this)) {
            Toast.makeText(CameraActivity.this,"No Camera",Toast.LENGTH_LONG).show();
        } else if (!checkSDCard()) {
            Toast.makeText(CameraActivity.this,"No SD Card",Toast.LENGTH_LONG).show();
        }

        // Creating the camera
        createCamera(Camera.CameraInfo.CAMERA_FACING_BACK);

        // Register this class as a listener for the accelerometer sensor
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaRecorder();
        releaseCamera();
        FrameLayout preview = (FrameLayout) findViewById(R.id.cam_preview);
        preview.removeViewAt(0);
    }



    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release(); // release the camera for other applications
            mCamera = null;
        }
    }

    private void createCamera(int camSelection) {
        // Create an instance of Camera
        mCamera = getCameraInstance(camSelection);

        Camera.Parameters params = mCamera.getParameters();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        params.setPictureFormat(PixelFormat.JPEG);
        params.setRotation(90);

        mCamera.setParameters(params);

        cameraPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.cam_preview);


        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(deviceWidth, deviceHeight);
        preview.setLayoutParams(layoutParams);


        preview.addView(cameraPreview, 0);
        Log.d(TAG,"Cam added");
    }

    public static Camera getCameraInstance(int camSelection) {
        int cameraCount = 0;
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == camSelection) {
                try {
                    cam = Camera.open(camIdx);
                } catch (RuntimeException e) {
                    Log.e(TAG, "Camera failed to open: " + e.getLocalizedMessage());
                }
            }
        }


        // returns null if camera is unavailable
        return cam;
    }


    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        public void onPictureTaken(byte[] data, Camera camera) {


            try {
                final Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                capturedImages.add(bmp);

                final Paint paint = new Paint();
                paint.setColor(0x80808080);

                Bitmap tic = BitmapFactory.decodeResource(getResources(),R.drawable.green_tic);
                final Bitmap bmOverlay = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());
                Canvas canvas = new Canvas(bmOverlay);
                canvas.drawBitmap(bmp, new Matrix(), paint);
                Rect src = new Rect(0,0,tic.getWidth()-1, tic.getHeight()-1);
                Rect dest = new Rect(0,0,bmp.getWidth()/4-1, bmp.getHeight()/4-1);
                canvas.drawBitmap(tic, src,dest, new Paint());

                final ImageView image = new ImageView(CameraActivity.this);

                image.setImageBitmap(Bitmap.createScaledBitmap(bmOverlay, imageHorizontalLayout.getHeight(),
                        imageHorizontalLayout.getHeight(), false));



                capturedImagesView.add(image);

                imageHorizontalLayout.addView(image);

                capturedImagesSelection.add(true);
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int index;
                        if (capturedImagesSelection.get((index=capturedImagesView.indexOf(view)))){
                            capturedImagesSelection.set(index,false);
                            capturedImagesView.get(index).setImageBitmap(Bitmap.createScaledBitmap(bmp, imageHorizontalLayout.getHeight(),
                                    imageHorizontalLayout.getHeight(), false));

                        }else{
                            capturedImagesSelection.set(index,true);
                            capturedImagesView.get(index).setImageBitmap(Bitmap.createScaledBitmap(bmOverlay, imageHorizontalLayout.getHeight(),
                                    imageHorizontalLayout.getHeight(), false));

                        }
                        Log.d(TAG, "onClick: index " +index);
                        checkGalleryOrGoOn();
                    }
                });

                releaseCamera();

                FrameLayout preview = (FrameLayout) findViewById(R.id.cam_preview);
                preview.removeViewAt(0);

                createCamera(Settings.CURRENT_OPEN_CAMERA);


            } catch (Exception e){
                Log.d(TAG, "Exception: "+e.getMessage());
            }finally {
                videoSwitchView.setVisibility(View.VISIBLE);
                videoTxt.setVisibility(View.VISIBLE);
                capture.setVisibility(View.VISIBLE);
            }

            if (null != capturedImages && capturedImages.size()!=0){
                goOn.setVisibility(View.VISIBLE);
            }

            videoSwitchView.setVisibility(View.VISIBLE);
            videoTxt.setVisibility(View.VISIBLE);
            capture.setVisibility(View.VISIBLE);
            checkGalleryOrGoOn();


        }
    };

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    private boolean checkSDCard() {
        boolean state = false;

        String sd = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(sd)) {
            state = true;
        }

        return state;
    }

    public void onSensorChanged(SensorEvent event) {
        synchronized (this) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                RotateAnimation animation = null;
                if (event.values[0] < 4 && event.values[0] > -4) {
                    if (event.values[1] > 0 && orientation != ExifInterface.ORIENTATION_ROTATE_90) {
                        // UP
                       orientation = ExifInterface.ORIENTATION_ROTATE_90;

                        animation = getRotateAnimation(0);
                        degrees = 270;
                    } else if (event.values[1] < 0 && orientation != ExifInterface.ORIENTATION_ROTATE_270) {
                        // UP SIDE DOWN
                        orientation = ExifInterface.ORIENTATION_ROTATE_270;
                        animation = getRotateAnimation(0);
                        degrees = 90;
                    }
                } else if (event.values[1] < 4 && event.values[1] > -4) {
                    if (event.values[0] > 0 && orientation != ExifInterface.ORIENTATION_NORMAL) {
                        // LEFT
                        orientation = ExifInterface.ORIENTATION_NORMAL;
                        animation = getRotateAnimation(0);
                        degrees = 0;
                    } else if (event.values[0] < 0 && orientation != ExifInterface.ORIENTATION_ROTATE_180) {
                        // RIGHT
                        orientation = ExifInterface.ORIENTATION_ROTATE_180;
                        animation = getRotateAnimation(0);
                        degrees = 180;
                    }
                }
                if (animation != null) {
                    rotatingImage.startAnimation(animation);
                }

            }

        }
    }

    /**
     * Calculating the degrees needed to rotate the image imposed on the button
     * so it is always facing the user in the right direction
     *
     * @param toDegrees
     * @return
     */
    private RotateAnimation getRotateAnimation(float toDegrees) {
        float compensation = 0;

        if (Math.abs(degrees - toDegrees) > 180) {
            compensation = 360;
        }

        if (toDegrees == 0) {
            compensation = -compensation;
        }

        RotateAnimation animation = new RotateAnimation(degrees, toDegrees - compensation, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(250);
        animation.setFillAfter(true);

        return animation;
    }
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            int selectedItemCount = 0;
            List<Uri> selectedItems = new ArrayList<>();
            if (null != data.getClipData()) {
                for (int i=0;i<(selectedItemCount = data.getClipData().getItemCount());i++){
                    selectedItems.add(data.getClipData().getItemAt(i).getUri());
                }
            }else{
                selectedItems.add(data.getData());
            }
            Log.d(TAG, "onActivityResult: "+selectedItems.size());
            try {
                String[] columns = { MediaStore.Images.Media.DATA,
                        MediaStore.Images.Media.MIME_TYPE };
                for (int i = 0; i < selectedItems.size(); i++) {
                    Cursor cursor = getContentResolver().query(selectedItems.get(i), columns, null, null, null);
                    cursor.moveToFirst();


                    int mimeTypeColumnIndex = cursor.getColumnIndex( columns[1] );
                    String mimeType    = cursor.getString(mimeTypeColumnIndex);
                    cursor.close();
                    if(mimeType.startsWith("image")) {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedItems.get(i));
                        if (!selectedImageBmpList.contains(bitmap))
                            selectedImageBmpList.add(bitmap);
                    }
                    else if(mimeType.startsWith("video")) {
                        if (!selectedVideoUriList.contains(selectedItems.get(i)))
                        selectedVideoUriList.add(selectedItems.get(i));
                    }
                }
                Intent homPage = new Intent(CameraActivity.this,MediaSelectedActivity.class);
                CameraActivity.this.startActivity(homPage);
            }catch (Exception e){
                Log.d(TAG, "onActivityResult Exception: "+e.getMessage());
            }

        }


    }

}
