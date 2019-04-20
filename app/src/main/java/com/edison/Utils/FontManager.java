package com.edison.Utils;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

import com.edison.R;


/**
 * Created by Bazictips on 23-Feb-16.
 */
public class FontManager {

    Context c;

    public FontManager(Context context){
        this.c = context;
    }


    public void setAppMedium(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "AppSans_medium.otf");
        tv.setTypeface(font);
    }

    public void setBebasRegular(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "bebasbold.otf");
        tv.setTypeface(font);
    }

    public void setAppRegular(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "AppSans_regular.otf");
        tv.setTypeface(font);
    }

    public void setOpenSansRegular(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "opensans_regular.ttf");
        tv.setTypeface(font);
    }

    public void setHelvRegular(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "helv.otf");
        tv.setTypeface(font);
    }

    public void setLockIcon(TextView tv) {

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_lock));
    }

    public void setLinkedIn(TextView tv) {

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_linkedin_square));
    }


    public void setUnlockIcon(TextView tv) {

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_unlock_alt));
    }


    public void setOpenSansLight(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/opensans_light.ttf");
        tv.setTypeface(font);
    }

    public void setLocationIcon(TextView tv) {

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_map_marker));
    }

    public void setBackIcon(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_chevron_left));
    }

    public void setGroupIcon(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_users));
    }

    public void setEmailIcon(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_envelope));
    }

    public void setAccuracyIcon(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_crosshairs));
    }

    public void setPassIcon(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_unlock_alt));
    }

    public void setLikeIcon(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_thumbs_up));
    }

    public void setAddFriend_Icon(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_user_plus));
    }

    public void setAddressBook(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_address_book));
    }
    public void setImage_Icon(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_image));
    }
    public void setNotificaitionIcon(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_bell));
    }

    public void setTickIcon_1(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_check_circle));
    }

    public void setHomeIcon(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_home));
    }


    public void setEditIcon(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_pencil));
    }

    public void setDocumentIcon(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_file_text_o));
    }
    public void setExit(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_sign_out));
    }
//
    public void setDeleteIcon(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_trash));
    }
    public void setSettingsIcon(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_share_alt));
    }
    public void setSettingsCOg(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_cog));
    }
    public void setCloseIcon(TextView tv) {

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_times));
    }


    public void setHeartIcon(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_heart_o));
    }
    public void setChatIcon(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_comments));
    }
    public void setPLayIcon(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_play_circle_o));
    }

    public void setShareIcon(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_share));
    }

    public void setCalendarIcon(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_calendar));
    }

    public void setCalendar_check_Icon(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_calendar_check));
    }

    public void setSearchIcon(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_search));
    }

    public void setUsersIcon(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_users));
    }
    public void setCancel(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_times_circle));
    }
    public void setFacebook(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_facebook));
    }
    public void setGoogle(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_google));
    }
    public void setFilter(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_filter));
    }
    public void setAdd(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_plus_circle));
    }

    public void setMinus_Icon(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_minus_circle));
    }
    public void setAdd_Icon(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_plus_circle));
    }

    public void setRightArrow(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_angle_right));
    }

    public void setCommentIcon(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_comment_o));
    }

       public void setEyeIcon(TextView tv) {

           Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
           tv.setTypeface(font);
           tv.setText(c.getString(R.string.fa_eye));
       }

    public void setNotificationIcon(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_play_circle_o));
    }

    public void setHomeicon(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_home));
    }

    public void setUserIcon(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_user));
     }
    public void setUserIcon1(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_user_plus));
    }

    public void setAttachIcon(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_paperclip));
    }
       public void setAudioicon(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_headphones));
    }
    public void setVideoicon(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_video_camera));
    }

    public void setCallicon(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_phone_square));
    }
    public void setVericalIcon(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_ellipsis_v));
    }

    public void setHorizontalIcon(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "fonts/FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(c.getString(R.string.fa_ellipsis_h));
    }



}
