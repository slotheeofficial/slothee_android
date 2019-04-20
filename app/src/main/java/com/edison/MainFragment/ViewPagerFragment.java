package com.edison.MainFragment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.InputType;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.edison.Customfonts.BoldTextview;
import com.edison.Customfonts.BoldTextview_Content;
import com.edison.Customfonts.NormalTextview;
import com.edison.Customfonts.NormalTextview_Content;
import com.edison.EventBus.EventMessage;
import com.edison.Full_ImageViewActivity;
import com.edison.MainActivity;
import com.edison.Object.FeedObject;
import com.edison.Object.File_path;
import com.edison.Object.MyPostObject;
import com.edison.Object.TempIBobjt;
import com.edison.OneToOneChat;
import com.edison.OtherUserProfileActivity;
import com.edison.R;
import com.edison.Utils.CalloutLink;
import com.edison.Utils.FontManager;
import com.edison.Utils.Hashtag;
import com.edison.Utils.Hashtag_yt;
import com.edison.Utils.HorizontalViewPager;
import com.edison.Utils.NetworkDetector;
import com.edison.Utils.OnSwipeTouchListener;
import com.edison.Utils.SessionManager;
import com.edison.Utils.SimpleGestureFilter;
import com.edison.Utils.Webservcie;
import com.google.gson.Gson;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import org.apache.commons.lang3.StringEscapeUtils;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewPagerFragment extends Fragment implements View.OnClickListener {

    public Handler mHandler;
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
    LinearLayout ll_textview, ll_imageview, ll_textview_image,btmll;
    TabLayout tabLayout;
    float downX;
    float mStartDragX = 0;
    SlidingImage_Adapter slidingImage_adapter;

//    ExpandableTextView expTv1;
    // create boolean for fetching data
    private boolean isViewShown = false;

    public ViewPagerFragment() {
        // Required empty public constructor
    }

    View itemView;

    FeedObject feedObject;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        itemView = inflater.inflate(R.layout.fragment_view_pager, container, false);

        if (!isViewShown) {
            INIT();
            isViewShown = true;
        }

        return itemView;

    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);


        if (getView() != null) {
            isViewShown = true;
            // fetchdata() contains logic to show data when page is selected mostly asynctask to fill the data
            INIT();


        } else {
            isViewShown = false;
        }
    }

    private void INIT() {

        Bundle bundle = this.getArguments();
        postpk = bundle.getString("postpk");

        sm = new SessionManager(getContext());
        fm = new FontManager(getContext());
        realm = Realm.getDefaultInstance();
        mHandler = new Handler();
        ll_textview = (LinearLayout) itemView.findViewById(R.id.ll_textview);
        ll_imageview = (LinearLayout) itemView.findViewById(R.id.ll_imageview);
        ll_textview_image = (LinearLayout) itemView.findViewById(R.id.ll_textview_image);
        btmll = (LinearLayout) itemView.findViewById(R.id.btmll);
        user_img_progress = (ProgressBar) itemView.findViewById(R.id.user_img_progress);
        feed_text_image = (NormalTextview_Content) itemView.findViewById(R.id.feed_text_image);
        user_name = (BoldTextview) itemView.findViewById(R.id.user_name);
        eye_icon = (BoldTextview) itemView.findViewById(R.id.eye_icon);
        viewcount = (BoldTextview) itemView.findViewById(R.id.viewcount);
        tap_2_chat = (BoldTextview) itemView.findViewById(R.id.tap_2_chat);
        feed_text = (BoldTextview_Content) itemView.findViewById(R.id.feed_text);
        extra = (NormalTextview) itemView.findViewById(R.id.extra);
        posted_date = (NormalTextview) itemView.findViewById(R.id.posted_date);
        posted_designation = (NormalTextview) itemView.findViewById(R.id.posted_designation);
        user_img = (CircleImageView) itemView.findViewById(R.id.user_img);
        viewPager = (HorizontalViewPager) itemView.findViewById(R.id.viewPager);
        tabLayout = (TabLayout) itemView.findViewById(R.id.tab_layout);

        if (sm.getUserObject() != null) {
            extra.setVisibility(View.VISIBLE);
        } else {
            extra.setVisibility(View.GONE);
        }

        fm.setEyeIcon(eye_icon);

        feedObject = realm.where(FeedObject.class).equalTo("post_pk", Integer.parseInt(postpk)).findFirst();

        ArrayList<int[]> hashtagSpans1 = getSpans(StringEscapeUtils.unescapeJava(feedObject.getMessage()), '#');
        ArrayList<int[]> calloutSpans1 = getSpans(StringEscapeUtils.unescapeJava(feedObject.getMessage()), '@');
        SpannableString commentsContent1 =
                new SpannableString(StringEscapeUtils.unescapeJava(feedObject.getMessage()));

//        setSpanUname(commentsContent1, calloutSpans1);

        if (feedObject.getType().equalsIgnoreCase("1")) {
            ll_textview.setVisibility(View.VISIBLE);
            ll_textview_image.setVisibility(View.GONE);
            ll_imageview.setVisibility(View.GONE);

            if (feedObject.getMessage().length() < 20) {
               feed_text.setTextSize(50);
            } else if (feedObject.getMessage().length() < 80) {
                feed_text.setTextSize(40);
            }
//            else if (feedObject.getMessage().length() < 120) {
//
//                feed_text.setTextSize(30);
//            }
            else {
                feed_text.setTextSize(30);
            }

            setSpanComment(commentsContent1, hashtagSpans1);
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


            setSpanComment_Yt(commentsContent1, hashtagSpans1);
            feed_text_image.setMovementMethod(LinkMovementMethod.getInstance());
            feed_text_image.setText(commentsContent1);

            slidingImage_adapter = new SlidingImage_Adapter(getActivity(), feedObject.getFile_path());
            viewPager.setAdapter(slidingImage_adapter);
            if(feedObject.getFile_path().size()>1)
            {
                tabLayout.setupWithViewPager(viewPager, true);
            }
            else
            {

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

        user_name.setText(feedObject.getPost_user());
        posted_date.setText(feedObject.getDate());
        posted_designation.setText(feedObject.getPost_designation());
        user_name.setText(feedObject.getPost_user());
        user_img_progress.setVisibility(View.VISIBLE);
        viewcount.setText(feedObject.getViews() + " views");

        if (sm.getUserObject() != null)
        {
            if (feedObject.getPost_user_fk().equalsIgnoreCase(sm.getUserObject().getUser_id()))
            {
                tap_2_chat.setVisibility(View.GONE);
            }
            else
            {
                tap_2_chat.setVisibility(View.VISIBLE);
            }

//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {

            if(isViewShown)
            {

            }
            else {
                if(NetworkDetector.isNetworkStatusAvialable(getActivity()))
                {
                    UpdateCount();
                    isViewShown = false;
                }
            }


        }

        tap_2_chat.setOnClickListener(this);
        extra.setOnClickListener(this);
        user_name.setOnClickListener(this);
        user_img.setOnClickListener(this);

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

        fm.setVericalIcon(extra);

    }

    public static void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {

        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                String text;
                int lineEndIndex;
                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (maxLine == 0) {
                    lineEndIndex = tv.getLayout().getLineEnd(0);
                    text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                } else {
                    lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                    text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                }
                tv.setText(text);
                tv.setMovementMethod(LinkMovementMethod.getInstance());
                tv.setText(
                        addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText,
                                viewMore), TextView.BufferType.SPANNABLE);
            }
        });

    }

    private static SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv,
                                                                            final int maxLine, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(spanableText)) {
            ssb.setSpan(new ClickableSpan() {

                @Override
                public void onClick(View widget) {
                    tv.setLayoutParams(tv.getLayoutParams());
                    tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                    tv.invalidate();
                    if (viewMore) {
                        makeTextViewResizable(tv, -1, "View Less", false);
                    } else {
                        makeTextViewResizable(tv, 2, "View More", true);
                    }

                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;

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
            jsonObject.accumulate("post_id",feedObject.getPost_pk());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(Webservcie.update_viewcount)
                .addJSONObjectBody(jsonObject)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {


                System.out.println("&#^*&# "+response);

                final JSONArray data = response.optJSONArray("posts");

                if (realm.isClosed()){
                    realm = Realm.getDefaultInstance();
                }

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        realm.createOrUpdateAllFromJson(FeedObject.class, data);

                    }
                });

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

            commentsContent.setSpan(new Hashtag(getActivity()),
                    hashTagStart,
                    hashTagEnd, 0);

        }


    }
    private void setSpanComment_Yt(SpannableString commentsContent, ArrayList<int[]> hashtagSpans) {
        for(int i = 0; i < hashtagSpans.size(); i++) {
            int[] span = hashtagSpans.get(i);
            int hashTagStart = span[0];
            int hashTagEnd = span[1];

            commentsContent.setSpan(new Hashtag_yt(getActivity()),
                    hashTagStart,
                    hashTagEnd, 0);

        }


    }

    private void setSpanUname(SpannableString commentsUname, ArrayList<int[]> calloutSpans) {
        for(int i = 0; i < calloutSpans.size(); i++) {
            int[] span = calloutSpans.get(i);
            int calloutStart = span[0];
            int calloutEnd = span[1];
            commentsUname.setSpan(new CalloutLink(getActivity()),
                    calloutStart,
                    calloutEnd, 0);

        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.tap_2_chat:
                if(sm.getUserObject()!=null)
                {
                    if(feedObject.getFriend_status()==0)
                    {
                        Intent intent2 = new Intent(getActivity(),OtherUserProfileActivity.class);
                        intent2.putExtra("id",String.valueOf(feedObject.getPost_user_fk()));
                        intent2.putExtra("name",feedObject.getPost_user());
                        startActivity(intent2);
                    }
                    else
                    {
                        NavigateToChat();
                    }
                }
                else
                {
                    MainActivity activity = (MainActivity) getActivity();
                    activity.ShowAlert();
                }
                break;


            case R.id.user_img:


                if(sm.getUserObject()!=null)
                {
                    if(sm.getUserObject().getUser_id().equalsIgnoreCase(feedObject.getPost_user_fk()))
                    {
                        MyProfileFragment fragment = new MyProfileFragment();
                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                                android.R.anim.fade_out);
                        fragmentTransaction.replace(R.id.frame, fragment, "home");
                        fragmentTransaction.commit();
                    }
                    else
                    {
                        Intent intent = new Intent(getActivity(), OtherUserProfileActivity.class);
                        intent.putExtra("id",String.valueOf(feedObject.getPost_user_fk()));
                        intent.putExtra("name",feedObject.getPost_user());
                        startActivity(intent);
                    }
                }
                else
                {
                    MainActivity activity = (MainActivity) getActivity();
                    activity.ShowAlert();
                }

                break;


            case R.id.user_name:

                if(sm.getUserObject()!=null)
                {
                    if(sm.getUserObject().getUser_id().equalsIgnoreCase(feedObject.getPost_user_fk()))
                    {
                        MyProfileFragment fragment = new MyProfileFragment();
                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                                android.R.anim.fade_out);
                        fragmentTransaction.replace(R.id.frame, fragment, "home");
                        fragmentTransaction.commit();
                    }
                    else
                    {
                        Intent intent = new Intent(getActivity(), OtherUserProfileActivity.class);
                        intent.putExtra("id",String.valueOf(feedObject.getPost_user_fk()));
                        intent.putExtra("name",feedObject.getPost_user());
                        startActivity(intent);
                    }
                }
                else
                {
                    MainActivity activity = (MainActivity) getActivity();
                    activity.ShowAlert();
                }

                break;


            case R.id.extra:

                if(sm.getUserObject().getUser_id().equalsIgnoreCase(feedObject.getPost_user_fk()))
                {
                    PopupMenu popupMenu = new PopupMenu(getActivity(),view);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId())
                            {
                                case R.id.delete:
                                        if(NetworkDetector.isNetworkStatusAvialable(getContext()))
                                        {
                                            new android.support.v7.app.AlertDialog.Builder(getActivity())
                                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                                    .setTitle(R.string.app_name)
                                                    .setMessage("Are you sure want to delete post ?")
                                                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                            DeleteFeed(String.valueOf(feedObject.getPost_pk()));
                                                            RealmResults<FeedObject> homeObjectRealmResults =  realm.where(FeedObject.class).findAll();
                                                            final FeedObject feedObject11 = realm.where(FeedObject.class).equalTo("post_pk", feedObject.getPost_pk()).findFirst();
//                                                            final FeedObject feedObject1 = homeObjectRealmResults.where()
//                                                                    .contains("post_pk",String.valueOf(feedObject.getPost_pk())).findFirst();
                                                            System.out.println(">>@#$24before "+homeObjectRealmResults.size());



                                                            realm.executeTransaction(new Realm.Transaction() {
                                                                @Override
                                                                public void execute(Realm realm)
                                                                {
                                                                    if(feedObject11!=null)
                                                                    {
                                                                        feedObject11.removeFromRealm();
//                                                                        EventBus eventBus = EventBus.getDefault();
//                                                                        eventBus.post(new EventMessage("delete"));

                                                                        Intent intent = new Intent(getContext(),MainActivity.class);
                                                                        startActivity(intent);

                                                                    }
                                                                }
                                                            });
                                                            RealmResults<FeedObject> homeObjectRealmResults1 =  realm.where(FeedObject.class).findAll();
                                                            System.out.println(">>@#$24after "+homeObjectRealmResults1.size());

                                                        }

                                                    })
                                                    .setNegativeButton("Cancel", null)
                                                    .show();


                                        }
//                                    Toast.makeText(getContext(), "Delete", Toast.LENGTH_SHORT).show();
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
                    PopupMenu popupMenu = new PopupMenu(getActivity(),view);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId())
                            {
                                case R.id.share:
//                                        if(NetworkDetector.isNetworkStatusAvialable(getContext()))
//                                        {
//                                            DeleteFeed(String.valueOf(feedObject.getPost_pk()));
//                                        }
                                    shareTextUrl();
                                    break;
                                 case R.id.mute:
                                        if(NetworkDetector.isNetworkStatusAvialable(getContext()))
                                        {
                                            new android.support.v7.app.AlertDialog.Builder(getActivity())
                                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                                    .setTitle(R.string.app_name)
                                                    .setMessage("Are you sure want to mute "+ feedObject.getPost_user()+"?")
                                                    .setPositiveButton("Mute", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                            if (NetworkDetector.isNetworkStatusAvialable(getActivity())) {

                                                                MuteUser(feedObject.getPost_user_fk());

                                                            } else {
                                                                Toast.makeText(getActivity(), "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
                                                            }

                                                        }

                                                    })
                                                    .setNegativeButton("Cancel", null)
                                                    .show();
                                        }
                                        else

                                        {
                                            Toast.makeText(getActivity(), "Please check your network connection", Toast.LENGTH_SHORT).show();
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
        share.putExtra(Intent.EXTRA_TEXT, feedObject.getLink());

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
            Picasso.with(getActivity())
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

//            imageView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View view) {
//
//                    Intent intent = new Intent(getActivity(), Full_ImageViewActivity.class);
//                    intent.putExtra("url",file_path.getData());
//                    startActivity(intent);
//
//                    return true;
//                }
//            });

            imageView.setOnClickListener(new DoubleClickListener() {
                @Override
                public void onSingleClick(View v) {

//                    Intent intent = new Intent(getActivity(), Full_ImageViewActivity.class);
//                    intent.putExtra("url",file_path.getData());
//                    startActivity(intent);

                }

                @Override
                public void onDoubleClick(View v) {

                    Intent intent = new Intent(getActivity(), Full_ImageViewActivity.class);
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

    public abstract static class DoubleClickListener implements View.OnClickListener {

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


                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });

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
                            Intent intent = new Intent(getContext(),MainActivity.class);
                            startActivity(intent);
                        }

                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });

    }

    private void NavigateToChat() {

        if(feedObject.getDialog_id().length()>0)
        {
            TempIBobjt tempIBobjt = new TempIBobjt();
            tempIBobjt.setDialog_id(feedObject.getDialog_id());
            tempIBobjt.setGroup_id(feedObject.getPost_user_fk());
            tempIBobjt.setGroup_image(feedObject.getPost_userimage());
            tempIBobjt.setGroup_name(feedObject.getPost_user());
            tempIBobjt.setQuickblox_id("");
            Intent intent = new Intent(getActivity(), OneToOneChat.class);
            intent.putExtra("qbchatdialog",new Gson().toJson(tempIBobjt));
            startActivity(intent);
        }
        else
        {
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.accumulate("sender_id",sm.getUserObject().getUser_id());
                jsonObject.accumulate("receiver_id",feedObject.getPost_user_fk());
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
                                tempIBobjt.setGroup_id(feedObject.getPost_user_fk());
                                tempIBobjt.setGroup_image(feedObject.getPost_userimage());
                                tempIBobjt.setGroup_name(feedObject.getPost_user());
                                Intent intent = new Intent(getActivity(), OneToOneChat.class);
                                intent.putExtra("qbchatdialog",new Gson().toJson(tempIBobjt));
                                startActivity(intent);
                            }
                            else
                            {
                                Toast.makeText(getActivity(), "Try again later", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {

                        }
                    });
        }

    }

}
