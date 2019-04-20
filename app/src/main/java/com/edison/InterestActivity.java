package com.edison;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.edison.Customfonts.BoldTextview;
import com.edison.Object.Category;
import com.edison.Object.SelectedInterest;
import com.edison.Object.UserObject;
import com.edison.Utils.NetworkDetector;
import com.edison.Utils.SessionManager;
import com.edison.Utils.Webservcie;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightGridView;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import co.lujun.androidtagview.ColorFactory;
import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;
import io.realm.Realm;
import io.realm.RealmResults;

public class InterestActivity extends AppCompatActivity implements View.OnClickListener {

    SessionManager sm;
    ProgressBar progressbar_interest;
    ImageView tap_2_updateInterest;
    BoldTextview skip;
//    TagContainerLayout lifestyle_tagview;
    TagContainerLayout tech_tagview;
    ArrayList<Category> tech_list;
    ArrayList<Category> selectedList;
//    ArrayList<Category> lifestyle_list;
    Realm realm;
    ExpandableHeightGridView interstlist;
    CategoryListAdapter categoryListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest);


        INIT();

    }

    @Override
    public void onBackPressed()
    {
        Toast.makeText(this, "You cannot go back", Toast.LENGTH_SHORT).show();
    }

    private void INIT() {

        realm = Realm.getDefaultInstance();

        selectedList = new ArrayList<>();
        sm = new SessionManager(this);
        progressbar_interest = (ProgressBar) findViewById(R.id.progressbar_interest);
        interstlist = (ExpandableHeightGridView) findViewById(R.id.interstlist);
        tap_2_updateInterest = (ImageView) findViewById(R.id.tap_2_updateInterest);
        skip = (BoldTextview) findViewById(R.id.skip);
        tech_tagview = (TagContainerLayout) findViewById(R.id.tech_tagview);
//        lifestyle_tagview = (TagContainerLayout) findViewById(R.id.lifestyle_tagview);

        progressbar_interest.setVisibility(View.GONE);
        tech_tagview.setVisibility(View.GONE);

        skip.setOnClickListener(this);
        tap_2_updateInterest.setOnClickListener(this);


        if(NetworkDetector.isNetworkStatusAvialable(InterestActivity.this))
        {
           getInterest();
        }
        else
        {
            Toast.makeText(InterestActivity.this, "Check your Network connection", Toast.LENGTH_SHORT).show();
        }



//        tech_tagview.setOnTagClickListener(new TagView.OnTagClickListener() {
//            @Override
//            public void onTagClick(final int position, String text) {
//
//
//
//                realm.executeTransaction(new Realm.Transaction() {
//                    @Override
//                    public void execute(Realm realm) {
//
//                        SelectedInterest selectedInterest = realm.where(SelectedInterest.class).equalTo("subcategory_id",
//                                Integer.parseInt(tech_list.get(position).getSubcategory_id())).findFirst();
//
//                        if(selectedInterest!=null)
//                        {
//                            tech_tagview.getTagView(position).setTagBackgroundColor(getResources().getColor(R.color.white));
//                            tech_tagview.getTagView(position).setTagBorderColor(getResources().getColor(R.color.appcolor));
//                            tech_tagview.getTagView(position).setTagTextColor(getResources().getColor(R.color.appcolor));
//                            selectedInterest.removeFromRealm();
//                        }
//                        else
//                        {
//                            selectedInterest = new SelectedInterest();
//                            tech_tagview.getTagView(position).setTagBackgroundColor(getResources().getColor(R.color.appcolor));
//                            tech_tagview.getTagView(position).setTagBorderColor(getResources().getColor(R.color.white));
//                            tech_tagview.getTagView(position).setTagTextColor(getResources().getColor(R.color.white));
//                            selectedInterest.setSubcategory_id(Integer.parseInt(tech_list.get(position).getSubcategory_id()));
//                            selectedInterest.setSubcategory_name(tech_list.get(position).getSubcategory_name());
//                            selectedInterest.setCategory_id("1");
//                            realm.copyToRealmOrUpdate(selectedInterest);
//                        }
//
//                        RealmResults<SelectedInterest> selectedInterestArrayList = realm.where(SelectedInterest.class)
//                                .findAll();
//                        System.out.println(">>>EWRWEr "+selectedInterestArrayList.size());
//
//                    }
//                });
//
//
//            }
//
//            @Override
//            public void onTagLongClick(int position, String text) {
//
//            }
//
//
//
//            @Override
//            public void onTagCrossClick(int position) {
//
//            }
//        });


//        tech_tagview.setBackgroundColor(getResources().getColor(R.color.white));
//        tech_tagview.setTagBorderRadius(6);

//        lifestyle_tagview.setBackgroundColor(getResources().getColor(R.color.white));
//        lifestyle_tagview.setTagBorderRadius(6);

//        tech_tagview.setBackgroundColor(getResources().getColor(R.color.white));
//        tech_tagview.setBorderColor(getResources().getColor(R.color.white));
//        tech_tagview.setTheme(ColorFactory.NONE);
//        tech_tagview.setTagBackgroundColor(getResources().getColor(R.color.white));
//        tech_tagview.setTagBorderColor(getResources().getColor(R.color.appcolor));
//        tech_tagview.setTagTextColor(getResources().getColor(R.color.appcolor));
//        tech_tagview.setGravity(Gravity.CENTER);






    }

    private void getInterest() {

        AndroidNetworking.get(Webservcie.get_category)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                realm.clear(SelectedInterest.class);
                            }
                        });

                        if(response.optString("success").equalsIgnoreCase("1"))
                        {

                            JSONArray categoryyy = response.optJSONArray("category");
                            JSONArray sub_category = categoryyy.optJSONObject(0).optJSONArray("sub_category");
                            if(sub_category.length()>0)
                            {
                                tech_list = new ArrayList<>();
                                Category category;
//                                String[] techStringList = new String[sub_category.length()];
                                for (int i=0;i<sub_category.length();i++)
                                {
                                    category = new Category();
                                    category.setSubcategory_id(sub_category.optJSONObject(i).optString("subcategory_id"));
                                    category.setSubcategory_name(sub_category.optJSONObject(i).optString("subcategory_name"));
                                    category.setSelected("0");
//                                    techStringList[i]=sub_category.optJSONObject(i).optString("subcategory_name");
                                    tech_list.add(category);

                                }

                                categoryListAdapter = new CategoryListAdapter(InterestActivity.this,
                                        tech_list);
                                interstlist.setAdapter(categoryListAdapter);
                                interstlist.setExpanded(true);
                                interstlist.setFocusable(false);

//                                tech_tagview.setTags(techStringList);
                            }

                            

                        }

                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });

    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.tap_2_updateInterest:
                if(NetworkDetector.isNetworkStatusAvialable(InterestActivity.this))
                {

                    if(selectedList.size()>0)
                    {
                        UpdateInterest();
//                        sendArray();
                    }
                    else
                    {
                        Toast.makeText(this, "Please select atleast one interest", Toast.LENGTH_SHORT).show();
                    }

                }
                else
                {
                    Toast.makeText(this, "Check your network connection", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.skip:
                Intent intent = new Intent(InterestActivity.this,MainActivity.class);
                startActivity(intent);
                break;
        }

    }

    public class CategoryListAdapter extends BaseAdapter
    {
        ArrayList<Category> suggestListObjectArrayList;
        Context context;
        LayoutInflater inflater;

        public CategoryListAdapter(Context context,ArrayList<Category> suggestListObjectArrayList)
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
            new_height = (int) Math.round(new_height*2.9);
            android.view.ViewGroup.LayoutParams layoutParams = viewHolder.ll.getLayoutParams();
            layoutParams.width = new_height;
            layoutParams.height = (int) new_height;
//            viewHolder.ll.setLayoutParams(layoutParams);

            final Category suggestListObject = suggestListObjectArrayList.get(position);
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


                    if(suggestListObject.getSelected().equalsIgnoreCase("1"))
                    {
                        suggestListObject.setSelected("0");
                        notifyDataSetChanged();
                        selectedList.remove(suggestListObject);
                    }
                    else
                    {
                        suggestListObject.setSelected("1");
                        notifyDataSetChanged();
                        selectedList.add(suggestListObject);
                    }

                    System.out.println("%^#%^#% "+selectedList.size());

                }
            });


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(suggestListObject.getSelected().equalsIgnoreCase("1"))
                    {
                        suggestListObject.setSelected("0");
                        notifyDataSetChanged();
                        selectedList.remove(suggestListObject);
                    }
                    else
                    {
                        suggestListObject.setSelected("1");
                        notifyDataSetChanged();
                        selectedList.add(suggestListObject);
                    }

                    System.out.println("%^#%^#% "+selectedList.size());

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

    private void UpdateInterest() {

        final ACProgressFlower acProgressFlower;
        acProgressFlower = new ACProgressFlower.Builder(InterestActivity.this)
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

                            Toast.makeText(InterestActivity.this, response.optString("message"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(InterestActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();


                        }
                        else
                        {
                            Toast.makeText(InterestActivity.this, response.optString("message"), Toast.LENGTH_SHORT).show();
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


        JSONObject jsonObject = null;
        for (Category selectedInterest : selectedList)
        {
            jsonObject = new JSONObject();
            try {
                jsonObject.accumulate("category_id",selectedInterest.getSubcategory_id());
                jsonObject.accumulate("subcategory_id",selectedInterest.getSubcategory_id());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


//        System.out.println(">!@#$123 "+jsonArray);


        return jsonArray;
    }
}
