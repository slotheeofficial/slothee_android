package com.edison;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.edison.Customfonts.BoldTextview;
import com.edison.Customfonts.Button;
import com.edison.MainFragment.MyProfileFragment;
import com.edison.Object.Category;
import com.edison.Object.File_path;
import com.edison.Object.MyPostObject;
import com.edison.Object.SelectedInterest;
import com.edison.Utils.NetworkDetector;
import com.edison.Utils.SessionManager;
import com.edison.Utils.Webservcie;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightGridView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import io.realm.Realm;
import io.realm.RealmResults;

public class UpdateInterest extends AppCompatActivity {

    GridView Technology_list;
//    ExpandableHeightGridView Technology_list,Lifestyle_list;
    SessionManager sm;
    MyPostListAdapter tech_listadapter;
//    MyLifeStyleAdapter myLifeStyleAdapter;
    Realm realm;
    ImageView close1;
    Button updateinterest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_interest);

        INIT();

    }

    private void INIT() {
        realm = Realm.getDefaultInstance();
        sm = new SessionManager(this);
        close1 = (ImageView) findViewById(R.id.close1);
        updateinterest = (Button) findViewById(R.id.updateinterest);
//        Lifestyle_list = (ExpandableHeightGridView) findViewById(R.id.Lifestyle_list);
        Technology_list = (GridView) findViewById(R.id.Technology_list);

        if(NetworkDetector.isNetworkStatusAvialable(UpdateInterest.this))
        {
            getInterest();
        }
        else
        {
            Toast.makeText(UpdateInterest.this, "Check your Network connection", Toast.LENGTH_SHORT).show();
        }

        close1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        updateinterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RealmResults<SelectedInterest> selectedInterest = realm.where(SelectedInterest.class).contains("selected","1").findAll();
                if(selectedInterest.size()>0)
                {
                    if(NetworkDetector.isNetworkStatusAvialable(UpdateInterest.this))
                    {
                        UpdateInterest();
                    }
                    else
                    {
                        Toast.makeText(UpdateInterest.this, "Check your network connection", Toast.LENGTH_SHORT).show();
                    }

                }
                else
                {
                    Toast.makeText(UpdateInterest.this, "Please select atleast 1 interest", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void UpdateInterest() {

        final ACProgressFlower acProgressFlower;
        acProgressFlower = new ACProgressFlower.Builder(UpdateInterest.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("")
                .fadeColor(Color.DKGRAY).build();
        acProgressFlower.show();
        acProgressFlower.setCancelable(false);

        JSONObject jsonObject = null;

        try
        {
            jsonObject = new JSONObject();
            jsonObject.accumulate("user_id",sm.getUserObject().getUser_id());
            jsonObject.accumulate("interests",sendArray());
        }
        catch (Exception e)
        {

        }


        AndroidNetworking.post(Webservcie.update_interest)
                .addJSONObjectBody(jsonObject)
                .setTag("login")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        acProgressFlower.dismiss();


                        if(response.optString("success").equalsIgnoreCase("1"))
                        {

                            Toast.makeText(UpdateInterest.this, response.optString("message"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(UpdateInterest.this, MainActivity.class);
                            startActivity(intent);
                            finish();


                        }
                        else
                        {
                            Toast.makeText(UpdateInterest.this, response.optString("message"), Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        acProgressFlower.dismiss();

                    }
                });


    }

    private JSONArray sendArray() {

        JSONArray jsonArray = new JSONArray();
        RealmResults<SelectedInterest> selectedInterests = realm.where(SelectedInterest.class).
                contains("selected","1").findAll();
        JSONObject jsonObject = null;
        for (SelectedInterest selectedInterest : selectedInterests)
        {
            jsonObject = new JSONObject();
            try {
                jsonObject.accumulate("category_id",selectedInterest.getCategory_id());
                jsonObject.accumulate("subcategory_id",selectedInterest.getSubcategory_id());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        System.out.println(">!@#$123 "+jsonArray);


        return jsonArray;
    }

    private void getInterest() {

        AndroidNetworking.get(Webservcie.get_category)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if(response.optString("success").equalsIgnoreCase("1"))
                        {
                            JSONArray categoryyy = response.optJSONArray("category");
                            if(categoryyy!=null&&categoryyy.length()>0)
                            {
                                JSONArray sub_category = categoryyy.optJSONObject(0).optJSONArray("sub_category");
                                if(sub_category!=null && sub_category.length()>0)
                                {
                                    for (int i=0;i<sub_category.length();i++)
                                    {


                                        SelectedInterest selectedInterest = realm.where(SelectedInterest.class)
                                                .equalTo("subcategory_id", Integer.parseInt(sub_category.optJSONObject(i).optString
                                                        ("subcategory_id"))).findFirst();
                                        if(selectedInterest!=null)
                                        {

                                        }
                                        else
                                        {
                                            SelectedInterest selectedInterest1 = new SelectedInterest();
                                            realm.beginTransaction();
                                            selectedInterest1.setCategory_id(categoryyy.optJSONObject(0).optString("category_id"));
                                            selectedInterest1.setSubcategory_name(sub_category.optJSONObject(i).optString("subcategory_name"));
                                            selectedInterest1.setSelected("0");
                                            selectedInterest1.setSubcategory_id(Integer.parseInt(sub_category.optJSONObject(i).optString("subcategory_id")));
                                            realm.copyToRealmOrUpdate(selectedInterest1);
                                            realm.commitTransaction();
                                        }


                                    }
                                    RealmResults<SelectedInterest> selectedInterest = realm.where(SelectedInterest.class)
                                            .contains("category_id",categoryyy.optJSONObject(0).optString("category_id")).findAll();
                                    tech_listadapter = new MyPostListAdapter(UpdateInterest.this,selectedInterest);
//                                    Technology_list.setExpanded(true);
                                    Technology_list.setAdapter(tech_listadapter);


                                }
                            }


//                            ///lifestyle
//                            JSONArray sub_category2 = categoryyy.optJSONObject(1).optJSONArray("sub_category");
//                            if(sub_category2!=null&&sub_category2.length()>0)
//                            {
//                                for (int i=0;i<sub_category2.length();i++)
//                                {
//
//                                    SelectedInterest selectedInterest1 = realm.where(SelectedInterest.class)
//                                            .equalTo("subcategory_id",Integer.parseInt(sub_category2.optJSONObject(i).
//                                                    optString("subcategory_id"))).findFirst();
//                                    if(selectedInterest1!=null)
//                                    {
//
//                                    }
//                                    else
//                                    {
//                                        selectedInterest1 = new SelectedInterest();
//                                        realm.beginTransaction();
//                                        selectedInterest1.setCategory_id(categoryyy.optJSONObject(1).optString("category_id"));
//                                        selectedInterest1.setSubcategory_name(sub_category2.optJSONObject(i).optString("subcategory_name"));
//                                        selectedInterest1.setSelected("0");
//                                        selectedInterest1.setSubcategory_id(Integer.parseInt(sub_category2.optJSONObject(i).optString("subcategory_id")));
//                                        realm.copyToRealmOrUpdate(selectedInterest1);
//                                        realm.commitTransaction();
//                                    }
//                                }
//
//                                RealmResults<SelectedInterest> selectedInterest = realm.where(SelectedInterest.class)
//                                        .contains("category_id",categoryyy.optJSONObject(1).optString("category_id")).findAll();
//                                myLifeStyleAdapter = new MyLifeStyleAdapter(UpdateInterest.this,selectedInterest);
//                                Lifestyle_list.setExpanded(true);
//                                Lifestyle_list.setAdapter(myLifeStyleAdapter);
//
//                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });

    }

    public class MyPostListAdapter extends BaseAdapter
    {
        RealmResults<SelectedInterest> suggestListObjectArrayList;
        Context context;
        LayoutInflater inflater;

        public MyPostListAdapter(Context context,RealmResults<SelectedInterest> suggestListObjectArrayList)
        {
            this.context = context;
            this.suggestListObjectArrayList = suggestListObjectArrayList;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount()
        {
            return suggestListObjectArrayList.size();
        }

        @Override
        public Object getItem(int size)
        {
            return suggestListObjectArrayList.get(size);
        }

        @Override
        public long getItemId(int size)
        {
            return 0;
        }

        @Override
        public View getView(final int position, View convertview, ViewGroup viewGroup)
        {

            View view = convertview;
            final ViewHolder viewHolder;
            if(view==null)
            {
                view = inflater.inflate(R.layout.interestlistdesign,viewGroup,false);
                viewHolder = new ViewHolder();
                viewHolder.ll = (LinearLayout) view.findViewById(R.id.ll);
                viewHolder.interest_name = (BoldTextview) view.findViewById(R.id.interest_name);
                viewHolder.checkbox = (CheckBox) view.findViewById(R.id.checkbox);
            }
            else
            {
                viewHolder = (ViewHolder) view.getTag();
            }

            WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
            final DisplayMetrics displayMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(displayMetrics);
            int Screenwidth = displayMetrics.widthPixels;

            int new_height = (int) (Screenwidth/10);
            new_height = (int) Math.round(new_height*3.15);
            android.view.ViewGroup.LayoutParams layoutParams = viewHolder.ll.getLayoutParams();
            layoutParams.width = new_height;
            layoutParams.height = (int) new_height;
//            viewHolder.ll.setLayoutParams(layoutParams);

            final SelectedInterest suggestListObject = suggestListObjectArrayList.get(position);
            viewHolder.interest_name.setText(StringEscapeUtils.unescapeJava(suggestListObject.getSubcategory_name()));

            if(suggestListObject.getSelected().equalsIgnoreCase("1"))
            {
                viewHolder.checkbox.setChecked(true);
            }
            else
            {
                viewHolder.checkbox.setChecked(false);
            }


            view.setTag(viewHolder);
            viewHolder.checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {

                            SelectedInterest selectedInterest1 = realm.where(SelectedInterest.class)
                                    .equalTo("subcategory_id",
                                            Integer.parseInt(String.valueOf(suggestListObject.getSubcategory_id()))).findFirst();

                            if(selectedInterest1.getSelected().equalsIgnoreCase("1"))
                            {
                                selectedInterest1.setSelected("0");
                                notifyDataSetChanged();
                            }
                            else
                            {
                                selectedInterest1.setSelected("1");
                                notifyDataSetChanged();
                            }

                        }
                    });

                    RealmResults<SelectedInterest> selectedInterest = realm.where(SelectedInterest.class).contains("selected","1").findAll();
                    System.out.println("sFsdfsdfsdf **&^%& "+selectedInterest.size());
                }
            });


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {

                            SelectedInterest selectedInterest1 = realm.where(SelectedInterest.class)
                                    .equalTo("subcategory_id",
                                            Integer.parseInt(String.valueOf(suggestListObject.getSubcategory_id()))).findFirst();

                            if(selectedInterest1.getSelected().equalsIgnoreCase("1"))
                            {
                                selectedInterest1.setSelected("0");
                                notifyDataSetChanged();
                            }
                            else
                            {
                                selectedInterest1.setSelected("1");
                                notifyDataSetChanged();
                            }

                        }
                    });


                    RealmResults<SelectedInterest> selectedInterest = realm.where(SelectedInterest.class).contains("selected","1").findAll();
                    System.out.println("sFsdfsdfsdf **&^%& "+selectedInterest.size());


                }
            });

            return view;
        }

        class ViewHolder
        {
            LinearLayout ll;
            CheckBox checkbox;
            BoldTextview interest_name,mentor,mentees,peer;
        }



    }

    public class MyLifeStyleAdapter extends BaseAdapter
    {
        RealmResults<SelectedInterest> suggestListObjectArrayList;
        Context context;
        LayoutInflater inflater;

        public MyLifeStyleAdapter(Context context,RealmResults<SelectedInterest> suggestListObjectArrayList)
        {
            this.context = context;
            this.suggestListObjectArrayList = suggestListObjectArrayList;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount()
        {
            return suggestListObjectArrayList.size();
        }

        @Override
        public Object getItem(int size)
        {
            return suggestListObjectArrayList.get(size);
        }

        @Override
        public long getItemId(int size)
        {
            return 0;
        }

        @Override
        public View getView(final int position, View convertview, ViewGroup viewGroup)
        {

            View view = convertview;
            final ViewHolder viewHolder;
            if(view==null)
            {
                view = inflater.inflate(R.layout.interestlistdesign,viewGroup,false);
                viewHolder = new ViewHolder();
                viewHolder.ll = (LinearLayout) view.findViewById(R.id.ll);
                viewHolder.interest_name = (BoldTextview) view.findViewById(R.id.interest_name);
                viewHolder.checkbox = (CheckBox) view.findViewById(R.id.checkbox);
            }
            else
            {
                viewHolder = (ViewHolder) view.getTag();
            }

            WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
            final DisplayMetrics displayMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(displayMetrics);
            int Screenwidth = displayMetrics.widthPixels;

            int new_height = (int) (Screenwidth/10);
            new_height = (int) Math.round(new_height*3.15);
            android.view.ViewGroup.LayoutParams layoutParams = viewHolder.ll.getLayoutParams();
            layoutParams.width = new_height;
            layoutParams.height = (int) new_height;
            viewHolder.ll.setLayoutParams(layoutParams);

            final SelectedInterest suggestListObject = suggestListObjectArrayList.get(position);
            viewHolder.interest_name.setText(StringEscapeUtils.unescapeJava(suggestListObject.getSubcategory_name()));

            if(suggestListObject.getSelected().equalsIgnoreCase("1"))
            {
                viewHolder.checkbox.setChecked(true);
            }
            else
            {
                viewHolder.checkbox.setChecked(false);
            }


            view.setTag(viewHolder);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {

                            SelectedInterest selectedInterest1 = realm.where(SelectedInterest.class)
                                    .equalTo("subcategory_id",
                                            Integer.parseInt(String.valueOf(suggestListObject.getSubcategory_id()))).findFirst();

                            if(selectedInterest1.getSelected().equalsIgnoreCase("1"))
                            {
                                selectedInterest1.setSelected("0");
                                notifyDataSetChanged();
                            }
                            else
                            {
                                selectedInterest1.setSelected("1");
                                notifyDataSetChanged();
                            }

                        }
                    });


                    RealmResults<SelectedInterest> selectedInterest = realm.where(SelectedInterest.class).contains("selected","1").findAll();
                    System.out.println("sFsdfsdfsdf **&^%& "+selectedInterest.size());


                }
            });
            viewHolder.checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {

                            SelectedInterest selectedInterest1 = realm.where(SelectedInterest.class)
                                    .equalTo("subcategory_id",
                                            Integer.parseInt(String.valueOf(suggestListObject.getSubcategory_id()))).findFirst();

                            if(selectedInterest1.getSelected().equalsIgnoreCase("1"))
                            {
                                selectedInterest1.setSelected("0");
                                notifyDataSetChanged();
                            }
                            else
                            {
                                selectedInterest1.setSelected("1");
                                notifyDataSetChanged();
                            }

                        }
                    });


                }
            });
            return view;
        }

        class ViewHolder
        {
            LinearLayout ll;
            CheckBox checkbox;
            BoldTextview interest_name,mentor,mentees,peer;
        }



    }

}
