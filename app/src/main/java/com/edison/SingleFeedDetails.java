package com.edison;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.edison.Customfonts.BoldTextview;
import com.edison.Customfonts.BoldTextview_Content;
import com.edison.Customfonts.NormalTextview;
import com.edison.Customfonts.NormalTextview_Content;
import com.edison.MainFragment.ViewPagerFragment;
import com.edison.Object.File_path;
import com.edison.Object.MyPostObject;
import com.edison.Object.TempIBobjt;
import com.edison.Utils.CalloutLink;
import com.edison.Utils.FontManager;
import com.edison.Utils.Hashtag;
import com.edison.Utils.Hashtag_yt;
import com.edison.Utils.HorizontalViewPager;
import com.edison.Utils.NetworkDetector;
import com.edison.Utils.SessionManager;
import com.edison.Utils.Webservcie;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmList;

public class SingleFeedDetails extends AppCompatActivity implements View.OnClickListener {

    SessionManager sm;
    FontManager fm;
    ProgressBar user_img_progress;
    BoldTextview user_name,  tap_2_chat, viewcount, eye_icon;
    BoldTextview_Content feed_text;
    NormalTextview_Content feed_text_image;
    NormalTextview posted_date;
    NormalTextview extra;
    NormalTextview posted_designation;
    CircleImageView user_img;
    HorizontalViewPager viewPager;
    LinearLayout SliderDots;
    Realm realm;
    String postpk;
    LinearLayout ll_textview, ll_imageview, btmll,ll_textview_image;
    TabLayout tabLayout;
    float downX;
    float mStartDragX = 0;
    String post_pk,dialog_id,friend_status;

    SlidingImage_Adapter slidingImage_adapter;

    MyPostObject myPostObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_feed_details);

        sm = new SessionManager(SingleFeedDetails.this);
        fm = new FontManager(SingleFeedDetails.this);
        realm = Realm.getDefaultInstance();
        INIT();

    }

    private void INIT() {

//        myPostObject = new Gson().fromJson(getIntent().getStringExtra("singlefeeddata"),MyPostObject.class);

        dialog_id = getIntent().getStringExtra("dialog_id");
        friend_status = getIntent().getStringExtra("friend_status");
        post_pk = getIntent().getStringExtra("post_pk");
        myPostObject = realm.where(MyPostObject.class).contains("post_pk",post_pk).findFirst();

        ll_textview = (LinearLayout) findViewById(R.id.ll_textview);
        ll_imageview = (LinearLayout) findViewById(R.id.ll_imageview);
        ll_textview_image = (LinearLayout) findViewById(R.id.ll_textview_image);
        btmll = (LinearLayout) findViewById(R.id.btmll);
        user_img_progress = (ProgressBar) findViewById(R.id.user_img_progress);
        feed_text_image = (NormalTextview_Content) findViewById(R.id.feed_text_image);
        user_name = (BoldTextview) findViewById(R.id.user_name);
        eye_icon = (BoldTextview) findViewById(R.id.eye_icon);
        viewcount = (BoldTextview) findViewById(R.id.viewcount);
        tap_2_chat = (BoldTextview) findViewById(R.id.tap_2_chat);
        feed_text = (BoldTextview_Content) findViewById(R.id.feed_text);
        extra = (NormalTextview) findViewById(R.id.extra);
        posted_date = (NormalTextview) findViewById(R.id.posted_date);
        posted_designation = (NormalTextview) findViewById(R.id.posted_designation);
        user_img = (CircleImageView) findViewById(R.id.user_img);
        viewPager = (HorizontalViewPager) findViewById(R.id.viewPager);
//        SliderDots = (LinearLayout) findViewById(R.id.SliderDots);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);

//        mTextHashTagHelper = HashTagHelper.Creator.create(getResources().getColor(R.color.blue), new HashTagHelper.OnHashTagClickListener() {
//            @Override
//            public void onHashTagClicked(String hashTag)
//            {
//
//                Toast.makeText(getActivity(), ">>> "+hashTag, Toast.LENGTH_SHORT).show();
//
//            }
//        });
//        mTextHashTagHelper.handle(feed_text);

        if (sm.getUserObject() != null) {
            extra.setVisibility(View.VISIBLE);
        } else {
            extra.setVisibility(View.GONE);
        }

        fm.setEyeIcon(eye_icon);

        if (myPostObject.getType().equalsIgnoreCase("1")) {
            ll_textview.setVisibility(View.VISIBLE);
            ll_textview_image.setVisibility(View.GONE);
            ll_imageview.setVisibility(View.GONE);
//            if (myPostObject.getMessage().length() < 20) {
//                feed_text.setTextSize(50);
//            } else if (myPostObject.getMessage().length() < 50) {
//                feed_text.setTextSize(35);
//            } else if (myPostObject.getMessage().length() < 80) {
//                feed_text.setTextSize(25);
//            } else {
//
//            }
            if (myPostObject.getMessage().length() < 20)
            {
                feed_text.setTextSize(50);
            }
            else if (myPostObject.getMessage().length() < 80)
            {
                feed_text.setTextSize(40);
            }
//            else if (feedObject.getMessage().length() < 120) {
//
//                feed_text.setTextSize(30);
//            }
            else
            {
                feed_text.setTextSize(30);
            }


            SpannableString commentsContent1 =
                    new SpannableString(StringEscapeUtils.unescapeJava(myPostObject.getMessage()));
            ArrayList<int[]> hashtagSpans1 = getSpans(StringEscapeUtils.unescapeJava(myPostObject.getMessage()), '#');
            ArrayList<int[]> calloutSpans1 = getSpans(StringEscapeUtils.unescapeJava(myPostObject.getMessage()), '@');
            setSpanComment(commentsContent1, hashtagSpans1);
//            setSpanUname(commentsContent1, calloutSpans1);

            feed_text.setMovementMethod(LinkMovementMethod.getInstance());
            feed_text.setText(commentsContent1);

            eye_icon.setTextColor(getResources().getColor(R.color.edttextcolor));
            viewcount.setTextColor(getResources().getColor(R.color.edttextcolor));
            tap_2_chat.setTextColor(getResources().getColor(R.color.edttextcolor));
            btmll.setBackground(getResources().getDrawable(R.color.white));
        } else {
            ll_textview.setVisibility(View.GONE);
            ll_imageview.setVisibility(View.VISIBLE);
            ll_textview_image.setVisibility(View.VISIBLE);

            SpannableString commentsContent1 =
                    new SpannableString(StringEscapeUtils.unescapeJava(myPostObject.getMessage()));
            ArrayList<int[]> hashtagSpans1 = getSpans(StringEscapeUtils.unescapeJava(myPostObject.getMessage()), '#');
            ArrayList<int[]> calloutSpans1 = getSpans(StringEscapeUtils.unescapeJava(myPostObject.getMessage()), '@');
            setSpanComment_Yt(commentsContent1, hashtagSpans1);
//            setSpanUname(commentsContent1, calloutSpans1);

            slidingImage_adapter = new SlidingImage_Adapter(SingleFeedDetails.this, myPostObject.getFile_path());
            viewPager.setAdapter(slidingImage_adapter);
            tabLayout.setupWithViewPager(viewPager, true);

            feed_text_image.setMovementMethod(LinkMovementMethod.getInstance());
            feed_text_image.setText(commentsContent1);

            if(myPostObject.getFile_path().size()>1)
            {
                tabLayout.setVisibility(View.VISIBLE);
                tabLayout.setupWithViewPager(viewPager, true);
            }
            else
            {
                tabLayout.setVisibility(View.GONE);
            }

            if(commentsContent1.length()>0)
            {
                feed_text_image.setVisibility(View.VISIBLE);
            }
            else
            {
                feed_text_image.setVisibility(View.GONE);
            }

            eye_icon.setTextColor(getResources().getColor(R.color.white));
            viewcount.setTextColor(getResources().getColor(R.color.white));
            tap_2_chat.setTextColor(getResources().getColor(R.color.white));
            btmll.setBackground(getResources().getDrawable(R.color._bgtransparent));
        }

        user_name.setText(myPostObject.getPost_pk());
        posted_date.setText(myPostObject.getDate());
        posted_designation.setText(myPostObject.getPost_designation());
        user_name.setText(myPostObject.getPost_user());
        user_img_progress.setVisibility(View.VISIBLE);
        viewcount.setText(myPostObject.getViews() + " views");


        if (sm.getUserObject() != null) {
            if (myPostObject.getPost_user_fk().equalsIgnoreCase(sm.getUserObject().getUser_id()))
            {
                tap_2_chat.setVisibility(View.GONE);
            }
            else
            {

                if(friend_status.equalsIgnoreCase("1"))
                {
                    tap_2_chat.setVisibility(View.VISIBLE);
                }
                else
                {
                    tap_2_chat.setVisibility(View.GONE);
                }

            }

            if(NetworkDetector.isNetworkStatusAvialable(SingleFeedDetails.this))
            {
                UpdateCount();
            }
        }

        tap_2_chat.setOnClickListener(this);
        extra.setOnClickListener(this);


        if (myPostObject != null) {
            Picasso.with(SingleFeedDetails.this)
                    .load(myPostObject.getPost_userimage())
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
        fm.setVericalIcon(extra);



    }

    private void MuteUser(String id) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("follower_id", sm.getUserObject().getUser_id());
            jsonObject.accumulate("following_id",id);
            jsonObject.accumulate("status", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(Webservcie.mute_unmute)
                .addJSONObjectBody(jsonObject)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if(response.optString("success").equalsIgnoreCase("1"))
                        {
                            finish();
                        }

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
    public void UpdateCount()
    {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.accumulate("user_id",sm.getUserObject().getUser_id());
            jsonObject.accumulate("post_id",myPostObject.getPost_pk());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(Webservcie.update_viewcount)
                .addJSONObjectBody(jsonObject)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {



            }

            @Override
            public void onError(ANError anError) {

            }
        });

    }
    private void setSpanComment(SpannableString commentsContent, ArrayList<int[]> hashtagSpans) {
        for(int i = 0; i < hashtagSpans.size(); i++) {
            int[] span = hashtagSpans.get(i);
            int hashTagStart = span[0];
            int hashTagEnd = span[1];

            commentsContent.setSpan(new Hashtag(SingleFeedDetails.this),
                    hashTagStart,
                    hashTagEnd, 0);

        }


    }
    private void setSpanComment_Yt(SpannableString commentsContent, ArrayList<int[]> hashtagSpans) {

        for(int i = 0; i < hashtagSpans.size(); i++) {
            int[] span = hashtagSpans.get(i);
            int hashTagStart = span[0];
            int hashTagEnd = span[1];

            commentsContent.setSpan(new Hashtag_yt(SingleFeedDetails.this),
                    hashTagStart,
                    hashTagEnd, 0);

        }


    }
    private void setSpanUname(SpannableString commentsUname, ArrayList<int[]> calloutSpans) {
        for(int i = 0; i < calloutSpans.size(); i++) {
            int[] span = calloutSpans.get(i);
            int calloutStart = span[0];
            int calloutEnd = span[1];
            commentsUname.setSpan(new CalloutLink(SingleFeedDetails.this),
                    calloutStart,
                    calloutEnd, 0);

        }
    }
    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.tap_2_chat:
                NavigateToChat();
                break;


            case R.id.extra:

                if(sm.getUserObject().getUser_id().equalsIgnoreCase(myPostObject.getPost_user_fk()))
                {
                    PopupMenu popupMenu = new PopupMenu(SingleFeedDetails.this,view);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId())
                            {
                                case R.id.delete:
                                    if(NetworkDetector.isNetworkStatusAvialable(SingleFeedDetails.this))
                                    {
                                        new android.support.v7.app.AlertDialog.Builder(SingleFeedDetails.this)
                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .setTitle(R.string.app_name)
                                                .setMessage("Are you sure want to delete post ?")
                                                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                        DeleteFeed(String.valueOf(myPostObject.getPost_pk()));


                                                    }

                                                })
                                                .setNegativeButton("Cancel", null)
                                                .show();


                                    }
                                    break;
                                case R.id.share:

                                    shareTextUrl();

                                    break;
                            }
                            return true;
                        }
                    });
                    popupMenu.inflate(R.menu.home_extra);
                    popupMenu.show();
                }
                else
                {
                    PopupMenu popupMenu = new PopupMenu(SingleFeedDetails.this,view);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId())
                            {
                                case R.id.share:
                                    shareTextUrl();
                                    break;

                                case R.id.mute:
                                    if(NetworkDetector.isNetworkStatusAvialable(SingleFeedDetails.this))
                                    {
                                        new android.support.v7.app.AlertDialog.Builder(SingleFeedDetails.this)
                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .setTitle(R.string.app_name)
                                                .setMessage("Are you sure want to mute "+ myPostObject.getPost_user()+"?")
                                                .setPositiveButton("Mute", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                        if (NetworkDetector.isNetworkStatusAvialable(SingleFeedDetails.this)) {

                                                            MuteUser(myPostObject.getPost_user_fk());

                                                        } else {
                                                            Toast.makeText(SingleFeedDetails.this, "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
                                                        }

                                                    }

                                                })
                                                .setNegativeButton("Cancel", null)
                                                .show();
                                    }
                                    else

                                    {
                                        Toast.makeText(SingleFeedDetails.this, "Please check your network connection", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                            }
                            return true;
                        }
                    });
                    popupMenu.inflate(R.menu.home_extra_for_others);
                    popupMenu.show();
                }

                break;
        }

    }

    private void shareTextUrl() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
        share.putExtra(Intent.EXTRA_TEXT, myPostObject.getLink());

        startActivity(Intent.createChooser(share, "Share link!"));
    }


    public class SlidingImage_Adapter extends PagerAdapter {


        private RealmList<File_path> IMAGES;
        private LayoutInflater inflater;
        private Context context;


        public SlidingImage_Adapter(Context context,RealmList<File_path> IMAGES) {
            this.context = context;
            this.IMAGES=IMAGES;
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

            final File_path file_path  = IMAGES.get(position);
            feed_img_progress.setVisibility(View.VISIBLE);
            Picasso.with(SingleFeedDetails.this)
                    .load(file_path.getData())
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

            view.addView(imageLayout, 0);


            imageView.setOnClickListener(new ViewPagerFragment.DoubleClickListener() {
                @Override
                public void onSingleClick(View v) {


//                    Intent intent = new Intent(getActivity(), Full_ImageViewActivity.class);
//                    intent.putExtra("url",file_path.getData());
//                    startActivity(intent);


                }

                @Override
                public void onDoubleClick(View v) {

                    Intent intent = new Intent(SingleFeedDetails.this, Full_ImageViewActivity.class);
                    intent.putExtra("url",file_path.getData());
                    startActivity(intent);

                }
            });

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


    public abstract class DoubleClickListener implements View.OnClickListener {

        private static final long DOUBLE_CLICK_TIME_DELTA = 300;//milliseconds

        long lastClickTime = 0;

        @Override
        public void onClick(View v) {
            long clickTime = System.currentTimeMillis();
            if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA){
                onDoubleClick(v);
            } else {
                onSingleClick(v);
            }
            lastClickTime = clickTime;
        }

        public abstract void onSingleClick(View v);
        public abstract void onDoubleClick(View v);
    }


    private void DeleteFeed(String id) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("user_id",sm.getUserObject().getUser_id());
            jsonObject.accumulate("post_id",id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(Webservcie.delete_post)
                .addJSONObjectBody(jsonObject)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        finish();

                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });

    }

    private void NavigateToChat() {

        if(dialog_id.length()>0)
        {
            TempIBobjt tempIBobjt = new TempIBobjt();
            tempIBobjt.setDialog_id(dialog_id);
            tempIBobjt.setGroup_id(myPostObject.getPost_user_fk());
            tempIBobjt.setGroup_image(myPostObject.getPost_userimage());
            tempIBobjt.setGroup_name(myPostObject.getPost_user());
            tempIBobjt.setQuickblox_id("");
            Intent intent = new Intent(SingleFeedDetails.this, OneToOneChat.class);
            intent.putExtra("qbchatdialog",new Gson().toJson(tempIBobjt));
            startActivity(intent);
        }
        else
        {
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.accumulate("sender_id",sm.getUserObject().getUser_id());
                jsonObject.accumulate("receiver_id",myPostObject.getPost_user_fk());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            AndroidNetworking.post(Webservcie.dialog_create)
                    .addJSONObjectBody(jsonObject)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {


                            if(response.optString("success").equalsIgnoreCase("1"))
                            {

                                TempIBobjt tempIBobjt = new TempIBobjt();
                                tempIBobjt.setDialog_id(response.optString("data"));
                                tempIBobjt.setGroup_id(myPostObject.getPost_user_fk());
                                tempIBobjt.setGroup_image(myPostObject.getPost_userimage());
                                tempIBobjt.setGroup_name(myPostObject.getPost_user());
                                Intent intent = new Intent(SingleFeedDetails.this, OneToOneChat.class);
                                intent.putExtra("qbchatdialog",new Gson().toJson(tempIBobjt));
                                startActivity(intent);
                            }
                            else
                            {
                                Toast.makeText(SingleFeedDetails.this, "Try again later", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {

                        }
                    });
        }

    }
}
