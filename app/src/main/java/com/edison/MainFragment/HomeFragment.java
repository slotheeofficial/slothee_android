package com.edison.MainFragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.edison.Customfonts.BoldTextview;
import com.edison.Customfonts.NormalTextview;
import com.edison.EventBus.EventMessage;
import com.edison.Full_ImageViewActivity;
import com.edison.MainActivity;
import com.edison.Object.FeedObject;
import com.edison.Object.File_path;
import com.edison.R;
import com.edison.UpdateProfile;
import com.edison.Utils.CalloutLink;
import com.edison.Utils.FontManager;
import com.edison.Utils.Hashtag;
import com.edison.Utils.NetworkDetector;
import com.edison.Utils.SessionManager;
import com.edison.Utils.Webservcie;
import com.prabhat1707.verticalpager.VerticalViewPager;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringEscapeUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class HomeFragment extends Fragment {

    View view;
    SessionManager sm;
    //  SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar load_more_progrss;
    int count = 0;
    private boolean loading;

    com.edison.Utils.VerticalViewPager vertical_viewpager;
    Realm realm;
    FontManager fm;
    ViewPagerAdapter adapter;
//        VerticlePagerAdapter verticlePagerAdapter;

    int adapterposition = 0;
    int newindex = 0;
    SlidingImage_Adapter slidingImage_adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        INIT();

        return view;

    }


    @Override
    public void onResume() {
        RealmResults<FeedObject> homeObjectRealmResults = realm.where(FeedObject.class).findAll();
        if (homeObjectRealmResults.size() > 0) {
            adapter.notifyDataSetChanged();
//            verticlePagerAdapter.notifyDataSetChanged();
        } else {

        }
        super.onResume();
    }

    private void INIT() {

        realm = Realm.getDefaultInstance();
        sm = new SessionManager(getActivity());
        fm = new FontManager(getActivity());
        vertical_viewpager = (com.edison.Utils.VerticalViewPager) view.findViewById(R.id.vertical_viewpager);
        vertical_viewpager.setOffscreenPageLimit(0);

        load_more_progrss = (ProgressBar) view.findViewById(R.id.load_more_progrss);
        vertical_viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {

                if (i + 1 == adapter.getCount() && !loading) {
                    newindex = i;
                    ///get home feed
                    if (NetworkDetector.isNetworkStatusAvialable(getActivity())) {
                        count = count + 10;
                        getFeed();
                        load_more_progrss.setVisibility(View.VISIBLE);
                        loading = true;
                    } else {
                        Toast.makeText(getActivity(), "Please check your network connection", Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        RealmResults<FeedObject> homeObjectRealmResults = realm.where(FeedObject.class).findAll();
        setupViewPager(vertical_viewpager, homeObjectRealmResults);

        if (NetworkDetector.isNetworkStatusAvialable(getActivity())) {
            getFeed();
            count = 0;
            loading = true;
        } else {
            Toast.makeText(getActivity(), "Check your Network connection", Toast.LENGTH_SHORT).show();
        }

        if (sm.getUserObject() != null) {
            if (sm.getUserObject().getName() != null && sm.getUserObject().getName().length() > 0) {

            } else {

                Intent intent = new Intent(getActivity(), UpdateProfile.class);
                startActivity(intent);

            }
        }


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageEventFromService(EventMessage event) {

        System.out.println("?SD?FSD?Fs " + vertical_viewpager.getCurrentItem());


//
//        RealmResults<FeedObject> homeObjectRealmResults = realm.where(FeedObject.class).findAll();
//        if(homeObjectRealmResults.size()>0)
//        {
//            vertical_viewpager.setCurrentItem(vertical_viewpager.getCurrentItem()+1,false);
//            adapter.notifyDataSetChanged();
////            verticlePagerAdapter.notifyDataSetChanged();
//        }
//        else
//        {
//
//        }


    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    private void getFeed() {

        JSONObject jsonObject = new JSONObject();

        if (sm.getUserObject() != null) {

            try {
                jsonObject.accumulate("user_id", sm.getUserObject().getUser_id());
                jsonObject.accumulate("start", count);
                jsonObject.accumulate("end", "10");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {

            try {
                jsonObject.accumulate("start", count);
                jsonObject.accumulate("end", "10");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        AndroidNetworking.post(Webservcie.get_post)
                .addJSONObjectBody(jsonObject)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {

                load_more_progrss.setVisibility(View.GONE);
//                swipeRefreshLayout.setRefreshing(false);


                if (response.optString("success").equalsIgnoreCase("0")) {
                    if (loading == true) {
//                        newindex = newindex-1;
                        Toast.makeText(getActivity(), "End of Billboard", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    final JSONArray data = response.optJSONArray("posts");

                    if (realm.isClosed()) {
                        realm = Realm.getDefaultInstance();
                    }

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            if (count == 0) {
                                realm.clear(FeedObject.class);
                            }
                            realm.createOrUpdateAllFromJson(FeedObject.class, data);
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

                    RealmResults<FeedObject> homeObjectRealmResults = realm.where(FeedObject.class).findAll();
//                    verticlePagerAdapter.notifyDataSetChanged();
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


    private void setupViewPager(ViewPager viewPager, RealmResults<FeedObject> productOptionLists) {
        try {

            adapter = new ViewPagerAdapter(getChildFragmentManager());

            for (int i = 0; i < productOptionLists.size(); i++) {
                adapter.addFrag(new ViewPagerFragment(), productOptionLists.get(i).getPost_pk());
            }
            viewPager.setAdapter(adapter);

        } catch (Exception e) {

        }


    }


    public ViewPagerFragment frag(int id) {
        Bundle bundle = new Bundle();
        bundle.putString("postpk", String.valueOf(id));
        bundle.putString("diff", "home");
        ViewPagerFragment fragment = new ViewPagerFragment();
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
//            return mFragmentList.get(position);
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


    public class VerticlePagerAdapter extends PagerAdapter {
        Context mContext;
        LayoutInflater mLayoutInflater;
        RealmResults<FeedObject> newsObjectArrayList;

        public VerticlePagerAdapter(Context context, RealmResults<FeedObject> newsObjectArrayList) {
            this.mContext = context;
            this.newsObjectArrayList = newsObjectArrayList;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return newsObjectArrayList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((RelativeLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View itemView = mLayoutInflater.inflate(R.layout.homefeed_list_design, container, false);

            LinearLayout ll_textview = (LinearLayout) itemView.findViewById(R.id.ll_textview);
            LinearLayout ll_imageview = (LinearLayout) itemView.findViewById(R.id.ll_imageview);
            final ProgressBar user_img_progress = (ProgressBar) itemView.findViewById(R.id.user_img_progress);
            BoldTextview user_name = (BoldTextview) itemView.findViewById(R.id.user_name);
            BoldTextview eye_icon = (BoldTextview) itemView.findViewById(R.id.eye_icon);
            BoldTextview viewcount = (BoldTextview) itemView.findViewById(R.id.viewcount);
            BoldTextview tap_2_chat = (BoldTextview) itemView.findViewById(R.id.tap_2_chat);
            BoldTextview feed_text = (BoldTextview) itemView.findViewById(R.id.feed_text);
            NormalTextview extra = (NormalTextview) itemView.findViewById(R.id.extra);
            NormalTextview posted_date = (NormalTextview) itemView.findViewById(R.id.posted_date);
            NormalTextview posted_designation = (NormalTextview) itemView.findViewById(R.id.posted_designation);
            CircleImageView user_img = (CircleImageView) itemView.findViewById(R.id.user_img);
            final ViewPager viewPager = (ViewPager) itemView.findViewById(R.id.viewPager);
            final TabLayout tabLayout = (TabLayout) itemView.findViewById(R.id.tab_layout);


            fm.setVericalIcon(extra);
            fm.setEyeIcon(eye_icon);

            if (sm.getUserObject() != null) {
                extra.setVisibility(View.VISIBLE);
            } else {
                extra.setVisibility(View.GONE);
            }

            final FeedObject feedObject = newsObjectArrayList.get(position);

            SpannableString commentsContent1 =
                    new SpannableString(StringEscapeUtils.unescapeJava(feedObject.getMessage()));
            ArrayList<int[]> hashtagSpans1 = getSpans(StringEscapeUtils.unescapeJava(feedObject.getMessage()), '#');
            ArrayList<int[]> calloutSpans1 = getSpans(StringEscapeUtils.unescapeJava(feedObject.getMessage()), '@');
            setSpanComment(commentsContent1, hashtagSpans1);
            setSpanUname(commentsContent1, calloutSpans1);
            feed_text.setMovementMethod(LinkMovementMethod.getInstance());
            feed_text.setText(commentsContent1);


            if (feedObject.getType().equalsIgnoreCase("1")) {
                ll_textview.setVisibility(View.VISIBLE);
                ll_imageview.setVisibility(View.GONE);

                if (feedObject.getMessage().length() < 20) {
                    feed_text.setTextSize(40);
                } else if (feedObject.getMessage().length() < 50) {
                    feed_text.setTextSize(30);
                } else if (feedObject.getMessage().length() < 80) {
                    feed_text.setTextSize(20);
                } else {

                }


                feed_text.setGravity(Gravity.CENTER);

                eye_icon.setTextColor(getResources().getColor(R.color.edttextcolor));
                viewcount.setTextColor(getResources().getColor(R.color.edttextcolor));
                tap_2_chat.setTextColor(getResources().getColor(R.color.edttextcolor));

            } else {
                ll_textview.setVisibility(View.VISIBLE);
                ll_imageview.setVisibility(View.VISIBLE);


                if (realm.isClosed()) {
                    realm = Realm.getDefaultInstance();
                }

                slidingImage_adapter = new SlidingImage_Adapter(getActivity(), feedObject.getFile_path());
                viewPager.setAdapter(slidingImage_adapter);
                tabLayout.setupWithViewPager(viewPager, true);

                feed_text.setGravity(Gravity.BOTTOM);

                eye_icon.setTextColor(getResources().getColor(R.color.white));
                viewcount.setTextColor(getResources().getColor(R.color.white));
                tap_2_chat.setTextColor(getResources().getColor(R.color.white));
            }

            user_name.setText(feedObject.getPost_user());
            posted_date.setText(feedObject.getDate());
            posted_designation.setText(feedObject.getPost_designation());
            user_img_progress.setVisibility(View.VISIBLE);
            viewcount.setText(feedObject.getViews() + " views");


            if (sm.getUserObject() != null) {
                if (feedObject.getPost_user_fk().equalsIgnoreCase(sm.getUserObject().getUser_id())) {
                    tap_2_chat.setVisibility(View.GONE);
                } else {
                    tap_2_chat.setVisibility(View.VISIBLE);
                }
            }

            tap_2_chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (sm.getUserObject() != null) {
                        if (feedObject.getFriend_status() == 0) {
                            Toast.makeText(getActivity(), "Working on IT.........(Unknown frd)Navigate 2 OtherUserProfile", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(getActivity(), "Already frd...Navigate 2 121 chat", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        MainActivity activity = (MainActivity) getActivity();
                        activity.ShowAlert();
                    }

                }
            });

            if (feedObject != null) {
                Picasso.with(getActivity())
                        .load(feedObject.getPost_userimage())
                        .placeholder(getResources().getDrawable(R.drawable.profile_img))
                        .into(user_img, new Callback() {
                            @Override
                            public void onSuccess() {
                                user_img_progress.setVisibility(View.GONE);

                            }

                            @Override
                            public void onError() {
                                user_img_progress.setVisibility(View.GONE);
                            }
                        });

            }

            extra.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    System.out.println("###@@@ " + sm.getUserObject().getUser_id() + " " + feedObject.getPost_user_fk());

                    if (sm.getUserObject().getUser_id().equalsIgnoreCase(feedObject.getPost_user_fk())) {
                        PopupMenu popupMenu = new PopupMenu(getActivity(), view);
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getItemId()) {
                                    case R.id.delete:
//                                        if(NetworkDetector.isNetworkStatusAvialable(getContext()))
//                                        {
//                                            new android.support.v7.app.AlertDialog.Builder(getActivity())
//                                                    .setIcon(android.R.drawable.ic_dialog_alert)
//                                                    .setTitle(R.string.app_name)
//                                                    .setMessage("Are you sure want to delete post ?")
//                                                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
//                                                        @Override
//                                                        public void onClick(DialogInterface dialog, int which) {
//
//                                                            DeleteFeed(String.valueOf(feedObject.getPost_pk()));
//                                                            RealmResults<FeedObject> homeObjectRealmResults =  realm.where(FeedObject.class).findAll();
//                                                            System.out.println(">>@#$24before "+homeObjectRealmResults.size());
//                                                            realm.executeTransaction(new Realm.Transaction() {
//                                                                @Override
//                                                                public void execute(Realm realm) {
//
//                                                                    newsObjectArrayList.remove(position);
//                                                                    verticlePagerAdapter.notifyDataSetChanged();
//                                                                    realm.copyToRealmOrUpdate(newsObjectArrayList);
//                                                                }
//                                                            });
//                                                            RealmResults<FeedObject> homeObjectRealmResults1 =  realm.where(FeedObject.class).findAll();
//                                                            System.out.println(">>@#$24after "+homeObjectRealmResults1.size());
//
//                                                        }
//
//                                                    })
//                                                    .setNegativeButton("Cancel", null)
//                                                    .show();
//
//
//                                        }
                                        Toast.makeText(mContext, "Delete", Toast.LENGTH_SHORT).show();
                                        break;
                                    case R.id.share:

                                        Toast.makeText(mContext, "share", Toast.LENGTH_SHORT).show();

                                        break;
                                }
                                return true;
                            }
                        });
                        popupMenu.inflate(R.menu.home_extra);
                        popupMenu.show();
                    } else {
                        PopupMenu popupMenu = new PopupMenu(getActivity(), view);
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getItemId()) {
                                    case R.id.share:
//                                        if(NetworkDetector.isNetworkStatusAvialable(getContext()))
//                                        {
//                                            DeleteFeed(String.valueOf(feedObject.getPost_pk()));
//                                        }
                                        Toast.makeText(mContext, "Others share", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                                return true;
                            }
                        });
                        popupMenu.inflate(R.menu.home_extra_for_others);
                        popupMenu.show();
                    }


                }
            });


            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((RelativeLayout) object);
        }

    }

    private void DeleteFeed(String id) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("user_id", sm.getUserObject().getUser_id());
            jsonObject.accumulate("post_id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(Webservcie.delete_post)
                .addJSONObjectBody(jsonObject)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {


                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });

    }

    public ArrayList<int[]> getSpans(String body, char prefix) {
        ArrayList<int[]> spans = new ArrayList<int[]>();

        Pattern pattern = Pattern.compile(prefix + "\\w+");
        Matcher matcher = pattern.matcher(body);

        // Check all occurrences
        while (matcher.find()) {
            int[] currentSpan = new int[2];
            currentSpan[0] = matcher.start();
            currentSpan[1] = matcher.end();
            spans.add(currentSpan);
        }

        return spans;
    }

    private void setSpanComment(SpannableString commentsContent, ArrayList<int[]> hashtagSpans) {
        for (int i = 0; i < hashtagSpans.size(); i++) {
            int[] span = hashtagSpans.get(i);
            int hashTagStart = span[0];
            int hashTagEnd = span[1];

            commentsContent.setSpan(new Hashtag(getActivity()),
                    hashTagStart,
                    hashTagEnd, 0);

        }


    }

    private void setSpanUname(SpannableString commentsUname, ArrayList<int[]> calloutSpans) {
        for (int i = 0; i < calloutSpans.size(); i++) {
            int[] span = calloutSpans.get(i);
            int calloutStart = span[0];
            int calloutEnd = span[1];
            commentsUname.setSpan(new CalloutLink(getActivity()),
                    calloutStart,
                    calloutEnd, 0);

        }
    }

    public class SlidingImage_Adapter extends PagerAdapter {


        private RealmList<File_path> IMAGES;
        private LayoutInflater inflater;
        private Context context;


        public SlidingImage_Adapter(Context context, RealmList<File_path> IMAGES) {
            this.context = context;
            this.IMAGES = IMAGES;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return IMAGES.size();
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            View imageLayout = inflater.inflate(R.layout.slidingimages_layout, view, false);

            assert imageLayout != null;
            final ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
            final ProgressBar feed_img_progress = (ProgressBar) imageLayout.findViewById(R.id.feed_img_progress);


            final File_path file_path = IMAGES.get(position);
            feed_img_progress.setVisibility(View.VISIBLE);
            Picasso.with(getActivity())
                    .load(file_path.getData())
                    .placeholder(getResources().getDrawable(R.drawable.profile_img))
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            feed_img_progress.setVisibility(View.GONE);

                        }

                        @Override
                        public void onError() {
                            feed_img_progress.setVisibility(View.GONE);
                        }
                    });

//            imageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    Intent intent = new Intent(getActivity(), Full_ImageViewActivity.class);
//                    intent.putExtra("url",file_path.getData());
//                    startActivity(intent);
//
//                }
//            });

            view.addView(imageLayout, 0);

            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }


    }


}
