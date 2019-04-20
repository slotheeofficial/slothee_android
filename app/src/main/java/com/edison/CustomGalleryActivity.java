package com.edison;

import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.edison.Customfonts.BoldTextview;
import com.edison.Object.CustomGallery;
import com.edison.Utils.Action;
import com.edison.Utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class CustomGalleryActivity extends BaseActivity {

    RecyclerView recyclerView;
    BoldTextview count,done;

    ImageView closea;

    //    GalleryAdapter adapter;
    ImageListRecyclerAdapter imageListRecyclerAdapter;
    private HashMap<String, CustomGallery> imagesUri;
    String action;
    Handler handler;
    private ImageLoader imageLoader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_gallery);

        action = getIntent().getAction();
        if (action == null) {
            finish();
        }





        initImageLoader();
        init();
    }


    public void initImageLoader() {

        imageLoader = Utils.initImageLoader(getActivity());

    }

    private void init() {

        count = (BoldTextview) findViewById(R.id.count);
        done = (BoldTextview) findViewById(R.id.done);
        closea = (ImageView) findViewById(R.id.closea);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        handler = new Handler();
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
        imageListRecyclerAdapter = new ImageListRecyclerAdapter(getApplicationContext());

        recyclerView.setAdapter(imageListRecyclerAdapter);
        imageListRecyclerAdapter.setMultiplePick(true);


        imageListRecyclerAdapter.setEventListner(new ImageListRecyclerAdapter.EventListener() {
            @Override
            public void onItemClickListener(int position, ImageListRecyclerAdapter.VerticalItemHolder holder) {
                if (imageListRecyclerAdapter.isMultiSelected()) {
                    imageListRecyclerAdapter.changeSelection(holder, position);
                } else {
                    CustomGallery customGallery = imageListRecyclerAdapter.getItem(position);
                    Intent intent = new Intent();
                    intent.putExtra("single_path", customGallery.sdcardPath);
                    setResult(RESULT_OK, intent);
                    finish();
                }

            }
        });


//		btnGalleryOk = (Button) findViewById(R.id.btnGalleryOk);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<CustomGallery> selected = imageListRecyclerAdapter.getSelected();

                String[] allPath = new String[selected.size()];
                for (int i = 0; i < allPath.length; i++) {
                    allPath[i] = selected.get(i).sdcardPath;
                }

                Intent data = new Intent().putExtra("all_path", allPath);
                setResult(RESULT_OK, data);
                finish();
            }
        });

        new Thread() {

            @Override
            public void run() {
                Looper.prepare();
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        imageListRecyclerAdapter.addAll(getGalleryPhotos());
                        //checkImageStatus();
                    }
                });
                Looper.loop();
            }


        }.start();

    }

    private ArrayList<CustomGallery> getGalleryPhotos() {
        ArrayList<CustomGallery> galleryList = new ArrayList<CustomGallery>();

        try {
            final String[] columns = {MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media._ID};
            final String orderBy = MediaStore.Images.Media._ID;

            Cursor imagecursor = managedQuery(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
                    null, null, orderBy);

            if (imagecursor != null && imagecursor.getCount() > 0) {

                while (imagecursor.moveToNext()) {
                    CustomGallery item = new CustomGallery();

                    int dataColumnIndex = imagecursor
                            .getColumnIndex(MediaStore.Images.Media.DATA);

                    item.sdcardPath = imagecursor.getString(dataColumnIndex);

                    galleryList.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // show newest photo at beginning of the list
        Collections.reverse(galleryList);
        return galleryList;
    }

}
