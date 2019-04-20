package com.edison;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.edison.Customfonts.BoldTextview;
import com.edison.MainFragment.HashtagPagerFragment;
import com.edison.MainFragment.HomeFragment;
import com.edison.MainFragment.ViewPagerFragment;
import com.edison.Object.HastagFeedObject;
import com.edison.Utils.FontManager;
import com.edison.Utils.NetworkDetector;
import com.edison.Utils.SessionManager;
import com.edison.Utils.Webservcie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class HashTagFeedsActivity extends AppCompatActivity {

    ImageView backIcon;
    String HashtagKey;
    SessionManager sm;
    FontManager fm;
    BoldTextview hastagname;


    ProgressBar load_more_progrss;
    int count = 0;
    private boolean loading;
    com.edison.Utils.VerticalViewPager vertical_viewpager;
    Realm realm;
    ViewPagerAdapter adapter;
//        VerticlePagerAdapter verticlePagerAdapter;

    int adapterposition = 0;
    int newindex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hash_tag_feeds);

        HashtagKey = getIntent().getStringExtra("HashtagKey");
        INIT();

    }

    private void INIT() {

        sm = new SessionManager(this);
        backIcon = (ImageView) findViewById(R.id.backIcon);
        hastagname = (BoldTextview) findViewById(R.id.hastagname);
        hastagname.setText(HashtagKey);

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        realm = Realm.getDefaultInstance();
        vertical_viewpager = (com.edison.Utils.VerticalViewPager) findViewById(R.id.vertical_viewpager);
        load_more_progrss = (ProgressBar) findViewById(R.id.load_more_progrss);
        vertical_viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }
            @Override
            public void onPageSelected(int i)
            {

                if( i+1 == adapter.getCount() && !loading)
                {
                    newindex = i;
                    ///get home feed
                    if(NetworkDetector.isNetworkStatusAvialable(HashTagFeedsActivity.this))
                    {
                        count = count+10;
                        getFeed();
                        load_more_progrss.setVisibility(View.VISIBLE);
                        loading = true;
                    }
                    else
                    {
                        Toast.makeText(HashTagFeedsActivity.this,
                                "Please check your network connection", Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });




        if(NetworkDetector.isNetworkStatusAvialable(HashTagFeedsActivity.this))
        {
            getFeed();
            count = 0;
            loading = true;
        }
        else
        {
            Toast.makeText(HashTagFeedsActivity.this,
                    "Please check your network connection", Toast.LENGTH_SHORT).show();
        }

    }



    private void getFeed() {

        JSONObject jsonObject = new JSONObject();

        if(sm.getUserObject()!=null)
        {
            try {
                jsonObject.accumulate("user_id",sm.getUserObject().getUser_id());
                jsonObject.accumulate("search_value",HashtagKey);
                jsonObject.accumulate("start",count);
                jsonObject.accumulate("end","10");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        else
        {
            try {
                jsonObject.accumulate("search_value",HashtagKey);
                jsonObject.accumulate("start",count);
                jsonObject.accumulate("end","10");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }



        AndroidNetworking.post(Webservcie.search_result_hashtags)
                .addJSONObjectBody(jsonObject)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {

                load_more_progrss.setVisibility(View.GONE);
//                swipeRefreshLayout.setRefreshing(false);


                if(response.optString("success").equalsIgnoreCase("0"))
                {
                    if(loading==true)
                    {
//                        newindex = newindex-1;
                        Toast.makeText(HashTagFeedsActivity.this, "No more data found", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    final JSONArray data = response.optJSONArray("posts");

                    if (realm.isClosed()){
                        realm = Realm.getDefaultInstance();
                    }

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            if(count==0)
                            {
                                realm.clear(HastagFeedObject.class);
                            }
                            realm.createOrUpdateAllFromJson(HastagFeedObject.class, data);
                        }
                    });

//                    realm.executeTransaction(new Realm.Transaction() {
//                        @Override
//                        public void execute(Realm realm) {
//                            try {
//                                if(count==0)
//                                {
//                                    realm.clear(FeedObject.class);
//                                }
//                                realm.createOrUpdateAllFromJson(FeedObject.class, data);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            } finally {
//                                realm.close();
//                            }
//                        }
//                    });



                    RealmResults<HastagFeedObject> homeObjectRealmResults =  realm.where(HastagFeedObject.class).findAll();
//                    verticlePagerAdapter.notifyDataSetChanged();
                    System.out.println(">>@#$24 "+homeObjectRealmResults.size());
                    setupViewPager(vertical_viewpager, homeObjectRealmResults);
                    vertical_viewpager.setCurrentItem(newindex, false);
                    adapter.notifyDataSetChanged();

                    loading = false;

                }




            }

            @Override
            public void onError(ANError anError) {

                load_more_progrss.setVisibility(View.GONE);
//                swipeRefreshLayout.setRefreshing(false);

            }
        });

    }


    private void setupViewPager(ViewPager viewPager, RealmResults<HastagFeedObject> productOptionLists) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        for (int i = 0; i < productOptionLists.size(); i++) {
            adapter.addFrag(new HashtagPagerFragment(),productOptionLists.get(i).getPost_pk());
        }
        viewPager.setAdapter(adapter);

    }


    public HashtagPagerFragment frag(int id) {
        Bundle bundle = new Bundle();
        bundle.putString("postpk", String.valueOf(id));
        bundle.putString("diff", "hashtag");
        HashtagPagerFragment fragment = new HashtagPagerFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<Integer> id = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {

            adapterposition = position;
            return frag(id.get(position));

        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, int post_pk) {
            mFragmentList.add(fragment);
            id.add(post_pk);
        }


    }


}
