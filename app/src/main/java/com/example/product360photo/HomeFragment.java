package com.example.product360photo;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

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

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mainPhotoView = view.findViewById(R.id.main_activity_photo_image);
        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return onTouchEvent(v, event);
            }
        });

//        Glide.with(HomeFragment.this).
//                load(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/360_photo/"+"product_03.jpg")
//                .placeholder(mainPhotoView.getDrawable())
//                .into(mainPhotoView);

        createListAssetsImage();
        coroutinesStartFunction();
        return view;
    }

    private Runnable stopCommand = new Runnable() {
        @Override
        public void run() {
            future.cancel(true);
        }
    };

    private boolean onTouchEvent(View view, MotionEvent event){
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
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (HomeFragment.this.isVisible()) {
                            //thread lock
                            Glide.with(HomeFragment.this).
                                    load(arrayListPictureAssets.get(indexImage))
                                    .placeholder(mainPhotoView.getDrawable())
                                    .into(mainPhotoView);
                            //thread unlock
                        }
                    }
                });
            }
        };

        scheduler.schedule(r, 0, TimeUnit.MILLISECONDS);
    }

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
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (HomeFragment.this.isVisible()) {
                            Glide.with(HomeFragment.this).
                                    load(arrayListPictureAssets.get(indexImage))
                                    .placeholder(mainPhotoView.getDrawable())
                                    .into(mainPhotoView);
                        }
                    }
                });
                increaseIndex();
            }
        };
        future = scheduler.scheduleAtFixedRate(r, 0, 100, TimeUnit.MILLISECONDS);
    }

    private void createListAssetsImage() {
        arrayListPictureAssets.clear();

        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/360_photo";
        File dir = new File(path);
        File[] files = dir.listFiles();

        for (int i=0; i< files.length; i++) {
            arrayListPictureAssets.add(path + "/" + files[i].getName());
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
}