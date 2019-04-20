package com.edison;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.edison.Customfonts.BoldTextview;
import com.edison.Object.SelectedImage;
import com.google.gson.Gson;
import com.iceteck.silicompressorr.SiliCompressor;
import com.squareup.picasso.Picasso;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmResults;


public class CustomPhotoGalleryActivity extends AppCompatActivity {

    private GridView grdImages;
    private BoldTextview btnSelect,selected_count;
    ImageView closea;
    private ImageAdapter imageAdapter;
    private String[] arrPath;
    private boolean[] thumbnailsselection;
    private int ids[];
    private int count;

    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_photo_gallery);

        realm = Realm.getDefaultInstance();
//        realm.executeTransaction(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//
//               realm.where(SelectedImage.class).findAll().clear();
//
//            }
//        });

        closea= (ImageView) findViewById(R.id.closea);
        grdImages= (GridView) findViewById(R.id.grdImages);
        btnSelect= (BoldTextview) findViewById(R.id.done);
        selected_count= (BoldTextview) findViewById(R.id.selected_count);
        RealmResults<SelectedImage> selectedImages = realm.where(SelectedImage.class).findAll();
        selected_count.setText(String.valueOf(selectedImages.size()+"/6"));
        final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
        final String orderBy = MediaStore.Images.Media._ID;
        @SuppressWarnings("deprecation")
        Cursor imagecursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null,orderBy);
        int image_column_index = imagecursor.getColumnIndex(MediaStore.Images.Media._ID);
        this.count = imagecursor.getCount();
        this.arrPath = new String[this.count];
        ids = new int[count];
        this.thumbnailsselection = new boolean[this.count];
        for (int i = 0; i < this.count; i++) {
            imagecursor.moveToPosition(i);
            ids[i] = imagecursor.getInt(image_column_index);
            int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
            arrPath[i] = imagecursor.getString(dataColumnIndex);
        }

        imageAdapter = new ImageAdapter(arrPath);
        grdImages.setAdapter(imageAdapter);
        imagecursor.close();

        btnSelect.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {


                RealmResults<SelectedImage> selectedImages = realm.where(SelectedImage.class).findAll();
                if(selectedImages.size()>0)
                {
                    Intent i = new Intent();
//                    i.putExtra("data", new Gson().toJson(selectedImages));
                    setResult(Activity.RESULT_OK, i);
                    finish();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Please select at least one image", Toast.LENGTH_LONG).show();
                }

                final int len = thumbnailsselection.length;
                int cnt = 0;
                String selectImages = "";
                for (int i = 0; i < len; i++) {
                    if (thumbnailsselection[i]) {
                        cnt++;
                        selectImages = selectImages + arrPath[i] + "|";

                    }
                }
                if (cnt == 0) {
                    Toast.makeText(getApplicationContext(), "Please select at least one image", Toast.LENGTH_LONG).show();
                } else {


                }
            }
        });

        closea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();


            }
        });
    }
    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();

    }

    /**
     * Class method
     */

    /**
     * This method used to set bitmap.
     * @param iv represented ImageView
     * @param id represented id
     */

    private void setBitmap(final ImageView iv, final int id) {

        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                return MediaStore.Images.Thumbnails.getThumbnail(getApplicationContext().getContentResolver(), id, MediaStore.Images.Thumbnails.MINI_KIND, null);
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                super.onPostExecute(result);
                iv.setImageBitmap(result);
            }
        }.execute();
    }

    /**
     * List adapter
     * @author tasol
     */

    public class ImageAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        String[] arrPath;

        public ImageAdapter(String[] arrPath) {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.arrPath = arrPath;
        }

        public int getCount() {
            return count;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.custom_gallery_item, null);
                holder.imgThumb = (ImageView) convertView.findViewById(R.id.imgThumb);
                holder.chkImage = (CheckBox) convertView.findViewById(R.id.chkImage);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.chkImage.setId(position);
            holder.imgThumb.setId(position);
            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            final DisplayMetrics displayMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(displayMetrics);
            int width1 = displayMetrics.widthPixels;

            double width = Double.parseDouble(String.valueOf(width1));
            double height = width/3;
            int new_height = (int) Math.round(height);
            android.view.ViewGroup.LayoutParams layoutParams = holder.imgThumb.getLayoutParams();
            layoutParams.width = new_height;
            layoutParams.height = new_height;
            holder.imgThumb.setLayoutParams(layoutParams);
//            Picasso.with(CustomPhotoGalleryActivity.this).
//                    load("file://"+arrPath[position])
//                   .into(holder.imgThumb);




            Glide.with(CustomPhotoGalleryActivity.this).load("file://" + arrPath[position]).into(holder.imgThumb);


            ///check already selected or not
            SelectedImage selectedImage = realm.where(SelectedImage.class)
                    .contains("originalImagePath",arrPath[position].trim()).findFirst();
            if(selectedImage!=null)
            {

                thumbnailsselection[holder.chkImage.getId()] = true;
                holder.chkImage.setChecked(true);
            }
            else
            {
                holder.chkImage.setChecked(false);
                thumbnailsselection[holder.chkImage.getId()] = false;
            }


            holder.chkImage.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                    CheckBox cb = (CheckBox) v;
                    int id = cb.getId();
                    if (thumbnailsselection[id]) {

                        final SelectedImage selectedImage = realm.where(SelectedImage.class)
                                .contains("originalImagePath",arrPath[position].trim()).findFirst();
                        if(selectedImage!=null)
                        {
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    selectedImage.removeFromRealm();
                                }
                            });
                        }

                        cb.setChecked(false);
                        thumbnailsselection[id] = false;

                        RealmResults<SelectedImage> selectedImages = realm.where(SelectedImage.class).findAll();
                        selected_count.setText(String.valueOf(selectedImages.size()+"/6"));

                    } else {

                        RealmResults<SelectedImage> selectedImages = realm.where(SelectedImage.class).findAll();
                        if(selectedImages.size()<6)
                        {
                            final SelectedImage selectedImage = new SelectedImage();
                            if(selectedImage!=null)
                            {

                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {

                                        selectedImage.setOriginalImagePath(arrPath[position].trim());
                                        selectedImage.setCompressedImagePath(arrPath[position].trim());
                                        realm.copyToRealmOrUpdate(selectedImage);

                                    }
                                });
                            }

                            cb.setChecked(true);
                            thumbnailsselection[id] = true;
                            selected_count.setText(String.valueOf(selectedImages.size()+"/6"));
                        }
                        else
                        {
                            cb.setChecked(false);
                            thumbnailsselection[id] = false;
                            Toast.makeText(CustomPhotoGalleryActivity.this, "You can upload only 6 images", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            });
            holder.imgThumb.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    int id = holder.chkImage.getId();
                    if (thumbnailsselection[id]) {
                        holder.chkImage.setChecked(false);
                        thumbnailsselection[id] = false;

                        final SelectedImage selectedImage = realm.where(SelectedImage.class)
                                .contains("originalImagePath",arrPath[position].trim()).findFirst();
                        if(selectedImage!=null)
                        {
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    selectedImage.removeFromRealm();
                                }
                            });
                        }
                        RealmResults<SelectedImage> selectedImages = realm.where(SelectedImage.class).findAll();
                        selected_count.setText(String.valueOf(selectedImages.size()+"/6"));

                    } else {

                        RealmResults<SelectedImage> selectedImages = realm.where(SelectedImage.class).findAll();
                        if(selectedImages.size()<6)
                        {
                            holder.chkImage.setChecked(true);
                            thumbnailsselection[id] = true;

                            final SelectedImage selectedImage = new SelectedImage();
                            if(selectedImage!=null)
                            {
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {

                                        selectedImage.setOriginalImagePath(arrPath[position].trim());
                                        selectedImage.setCompressedImagePath(arrPath[position].trim());
                                        realm.copyToRealmOrUpdate(selectedImage);

                                    }
                                });
                            }
                            selected_count.setText(String.valueOf(selectedImages.size()+"/6"));
                        }
                        else
                        {
                            Toast.makeText(CustomPhotoGalleryActivity.this, "You can upload only 6 images", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

//            try {
//                setBitmap(holder.imgThumb, ids[position]);
//            }
//            catch (Throwable e) {
//            }

            holder.chkImage.setChecked(thumbnailsselection[position]);
            holder.id = position;

            return convertView;

        }
    }


    /**
     * Inner class
     * @author tasol
     */
    class ViewHolder {
        ImageView imgThumb;
        CheckBox chkImage;
        int id;
    }


//    class ImageCompressionAsyncTask extends AsyncTask<String, String, String> {
//
//        Context mContext;
//
//        public ImageCompressionAsyncTask(Context context){
//            mContext = context;
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//
//            File filesDir = CustomPhotoGalleryActivity.this.getFilesDir();
//            File imageFile = new File(filesDir, "fro_crop5" + ".jpg");
//            String filePathaa = SiliCompressor.with(mContext).compress(params[0],imageFile.getAbsoluteFile());
//            return filePathaa;
//
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//
//            System.out.println(">$@#4 "+s);
//
//            compressedFilePth.add(new File(s));
//            File imageFile = new File(s);
//            filepath = imageFile.getPath();
//
//        }
//    }
}




