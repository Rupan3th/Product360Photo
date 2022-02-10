package com.example.product360photo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ProductViewActivity extends AppCompatActivity {

    private ArrayList<String> arrayListPictureAssets = new ArrayList<String>();
    private boolean playImage = true;
    private boolean isReverse = false;
    private int indexImage = 0;
    private float x1;
    private float x2;
    private final int minDistance = 60;

    private ImageView mainPhotoView;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private ScheduledFuture<?> future;

    private String imageFolder="";
    private String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/360_photo/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_view);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent intent = getIntent();
        imageFolder = intent.getStringExtra("ImageFolder");

        new Thread(new Runnable() {
            @Override
            public void run() {
                Glide.get(ProductViewActivity.this).clearDiskCache();
            }
        }).start();
//        Glide.get(this).clearMemory();

        ImageView btnBack = (ImageView) findViewById(R.id.back_btn);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoHome();
            }
        });

        mainPhotoView = findViewById(R.id.product_detail_view);

        createListAssetsImage();
        coroutinesStartFunction();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.x1 = event.getX();
                this.playImage = false;
                scheduler.schedule(stopCommand, 0, TimeUnit.MILLISECONDS);
                break;
            case MotionEvent.ACTION_UP:
                this.x2 = event.getX();
                float deltaX = this.x2 - this.x1;
                float absDeltaX = Math.abs(deltaX);
                if (absDeltaX > minDistance){
                    int count = Math.round(absDeltaX) / 30;
                    if (this.x2 > this.x1) {
                        rotateRight(count);
                    } else {
                        rotateLeft(count);
                    }
                }
            default:
                break;
        }
        return true;
    }

    private void rotateRight(int count)  {
        for (int i = 0; i < count; i++) {
            increaseIndex();
            rotate();
        }
    }

    private void rotateLeft(int count) {
        for (int i = 0; i < count; i++) {
            decreaseIndex();
            rotate();
        }
    }

    private void rotate() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //thread lock
                        Glide.with(ProductViewActivity.this).
                                load(arrayListPictureAssets.get(indexImage))
                                .placeholder(mainPhotoView.getDrawable())
                                .into(mainPhotoView);
                        //thread unlock
                    }
                });
            }
        };

        scheduler.schedule(r, 50, TimeUnit.MILLISECONDS);
    }

    private Runnable stopCommand = new Runnable() {
        @Override
        public void run() {
            future.cancel(true);
        }
    };

    private void checkNumberIndex() {
        if (indexImage < 0) {
            indexImage = arrayListPictureAssets.size()-1;
        } else if (indexImage > arrayListPictureAssets.size()-1) {
            indexImage = 0;
        }
    }

    private void increaseIndex() {
        if (isReverse) {
            indexImage--;
        } else {
            indexImage++;
        }

        checkNumberIndex();
    }

    private void decreaseIndex() {
        if (isReverse) {
            indexImage ++;
        } else {
            indexImage --;
        }
        checkNumberIndex();
    }

    private void coroutinesStartFunction() {
        playImageLikeGif();

    }

    private void playImageLikeGif() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(ProductViewActivity.this).
                                    load(arrayListPictureAssets.get(indexImage))
                                    .placeholder(mainPhotoView.getDrawable())
                                    .into(mainPhotoView);
                        }
                    });
                    increaseIndex();

                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        future = scheduler.scheduleAtFixedRate(r, 0, 200, TimeUnit.MILLISECONDS);
    }

    private void createListAssetsImage() {
        arrayListPictureAssets.clear();

        String saved_path = path + imageFolder;
        File dir = new File(saved_path);
        if(dir.exists()){
            for (int i = 0; i < dir.listFiles().length; i++) {
                arrayListPictureAssets.add(saved_path + "/" + dir.listFiles()[i].getName());
            }
        }

        //        arrayListPictureAssets.add("file:///android_asset/AVF_2696.jpg");
//        arrayListPictureAssets.add("file:///android_asset/AVF_2697.jpg");
//        arrayListPictureAssets.add("file:///android_asset/AVF_2698.jpg");
//        arrayListPictureAssets.add("file:///android_asset/AVF_2699.jpg");
//        arrayListPictureAssets.add("file:///android_asset/AVF_2700.jpg");
//        arrayListPictureAssets.add("file:///android_asset/AVF_2701.jpg");
//        arrayListPictureAssets.add("file:///android_asset/AVF_2702.jpg");
//        arrayListPictureAssets.add("file:///android_asset/AVF_2703.jpg");
//        arrayListPictureAssets.add("file:///android_asset/AVF_2704.jpg");
//        arrayListPictureAssets.add("file:///android_asset/AVF_2705.jpg");
//        arrayListPictureAssets.add("file:///android_asset/AVF_2706.jpg");
//        arrayListPictureAssets.add("file:///android_asset/AVF_2707.jpg");
//        arrayListPictureAssets.add("file:///android_asset/AVF_2708.jpg");
//        arrayListPictureAssets.add("file:///android_asset/AVF_2709.jpg");
//        arrayListPictureAssets.add("file:///android_asset/AVF_2710.jpg");
//        arrayListPictureAssets.add("file:///android_asset/AVF_2711.jpg");
//        arrayListPictureAssets.add("file:///android_asset/AVF_2712.jpg");
//        arrayListPictureAssets.add("file:///android_asset/AVF_2713.jpg");
//        arrayListPictureAssets.add("file:///android_asset/AVF_2714.jpg");
//        arrayListPictureAssets.add("file:///android_asset/AVF_2715.jpg");
//        arrayListPictureAssets.add("file:///android_asset/AVF_2716.jpg");
//        arrayListPictureAssets.add("file:///android_asset/AVF_2717.jpg");
//        arrayListPictureAssets.add("file:///android_asset/AVF_2718.jpg");
//        arrayListPictureAssets.add("file:///android_asset/AVF_2719.jpg");
//        arrayListPictureAssets.add("file:///android_asset/AVF_2720.jpg");
//        arrayListPictureAssets.add("file:///android_asset/AVF_2721.jpg");
//        arrayListPictureAssets.add("file:///android_asset/AVF_2722.jpg");
//        arrayListPictureAssets.add("file:///android_asset/AVF_2723.jpg");
//        arrayListPictureAssets.add("file:///android_asset/AVF_2724.jpg");
//        arrayListPictureAssets.add("file:///android_asset/AVF_2725.jpg");
//        arrayListPictureAssets.add("file:///android_asset/AVF_2726.jpg");
//        arrayListPictureAssets.add("file:///android_asset/AVF_2727.jpg");
//        arrayListPictureAssets.add("file:///android_asset/AVF_2728.jpg");
//        arrayListPictureAssets.add("file:///android_asset/AVF_2729.jpg");
//        arrayListPictureAssets.add("file:///android_asset/AVF_2730.jpg");
//        arrayListPictureAssets.add("file:///android_asset/AVF_2731.jpg");
    }

    private void gotoHome(){
        scheduler.schedule(stopCommand, 0, TimeUnit.MILLISECONDS);
        scheduler.shutdownNow();
        arrayListPictureAssets.clear();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}