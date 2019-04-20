package com.edison.MainFragment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.edison.Customfonts.BoldTextview;
import com.edison.Customfonts.NormalTextview;
import com.edison.HashTagFeedsActivity;
import com.edison.Object.HashtagKey;
import com.edison.Object.MyFriendsList;
import com.edison.OtherUserProfileActivity;
import com.edison.R;
import com.edison.Utils.FontManager;
import com.edison.Utils.NetworkDetector;
import com.edison.Utils.SessionManager;
import com.edison.Utils.Webservcie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.WINDOW_SERVICE;
import static android.view.View.VISIBLE;


/**
 * A simple {@link Fragment} subclass.
 */
public class HashTagSearchFragment extends Fragment {

    SessionManager sm;
    AutoCompleteTextView searchuser;
    AutocompleteCustomArrayAdapter customerAdapter;
    BoldTextview nodata;
    FontManager fm;
    ArrayList<HashtagKey> suggestListArrayList = new ArrayList<>();

    //    FilterListAdapter filterListAdapter;
    ListView list_item;
    View view;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_hash_tag_search, container, false);

        INIT();

        return view;
    }

    private void INIT() {


        sm = new SessionManager(getActivity());
        fm = new FontManager(getActivity());
        searchuser = (AutoCompleteTextView) view.findViewById(R.id.searchuser);
        nodata = (BoldTextview) view.findViewById(R.id.nodata);
        list_item = (ListView) view.findViewById(R.id.list_item);
        searchuser.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s)
            {
                if (NetworkDetector.isNetworkStatusAvialable(getActivity()))
                {if(s.length()>0)
                {
                    getUser_List(s.toString(),"1");
                }

                }
                else
                {
                    Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });



    }


    private void  getUser_List(String s, String s1) {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.accumulate("user_id", sm.getUserObject().getUser_id());
            jsonObject.accumulate("search_value", s);
        } catch (JSONException e) {
            e.printStackTrace();
        }



        AndroidNetworking.post(Webservcie.search_hashtags)
                .addJSONObjectBody(jsonObject)
                .setTag("login")
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(final String response) {



                        try {
                            JSONObject jsonObject1 = new JSONObject(response);
                            if(jsonObject1.optString("success").equalsIgnoreCase("1"))
                            {

                                try {
                                    customerAdapter = new AutocompleteCustomArrayAdapter(getContext(),
                                            R.layout.hashtaglistdesign,R.id.tag_name,suggestListArrayList);
                                    list_item.setAdapter(customerAdapter);
//                                        list_item.showDropDown();

                                    customerAdapter.notifyDataSetChanged();

                                    nodata.setVisibility(View.GONE);
                                    list_item.setVisibility(View.VISIBLE);
                                }
                                catch (Exception e)
                                {

                                }

                                final JSONArray user = jsonObject1.optJSONArray("hash_tags");
                                suggestListArrayList.clear();
                                if(user.length()>0)
                                {

                                    HashtagKey suggestList = null;
                                    for (int i=0; i<user.length();i++)
                                    {
                                        suggestList = new HashtagKey();
                                        suggestList.setData(user.optJSONObject(i).optString("data"));


                                        suggestListArrayList.add(suggestList);
                                    }

                                    try {

                                        customerAdapter.notifyDataSetChanged();

                                        nodata.setVisibility(View.GONE);
                                        list_item.setVisibility(View.VISIBLE);
                                    }
                                    catch (Exception e)
                                    {

                                    }

                                }
                                else
                                {
                                    customerAdapter.notifyDataSetChanged();
                                    nodata.setVisibility(VISIBLE);
                                    list_item.setVisibility(View.GONE);
                                }


                            }
                            else
                            {
                                if(customerAdapter!=null)
                                {
                                    customerAdapter.notifyDataSetChanged();
                                }


                                nodata.setVisibility(VISIBLE);
                                list_item.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });

    }




    public class AutocompleteCustomArrayAdapter extends ArrayAdapter<HashtagKey> {

        final String TAG = "AutocompleteCustomArrayAdapter.java";
        ArrayList<HashtagKey> tempItems, suggestions;
        Context mContext;
        int layoutResourceId,textViewResourceId;
        ArrayList<HashtagKey> items;



        public AutocompleteCustomArrayAdapter(Context mContext, int layoutResourceId,int textViewResourceId,
                                              ArrayList<HashtagKey> data) {
            super(mContext, layoutResourceId,textViewResourceId, data);

            this.layoutResourceId = layoutResourceId;
            this.mContext = mContext;
            this.items = data;
            this.textViewResourceId = textViewResourceId;
            tempItems = new ArrayList<HashtagKey>(items); // this makes the difference.
            suggestions = new ArrayList<HashtagKey>();
        }




        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.hashtaglistdesign, parent, false);
            }
            final HashtagKey people = items.get(position);
            if (people != null) {

                NormalTextview tag_timing = (NormalTextview) view.findViewById(R.id.tag_timing);
                BoldTextview tag_name = (BoldTextview) view.findViewById(R.id.tag_name);





                tag_name.setText(people.getData());
                tag_timing.setVisibility(View.GONE);






            }



            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getActivity(),HashTagFeedsActivity.class);
                    intent.putExtra("HashtagKey",people.getData());
                    startActivity(intent);
                }
            });

            return view;
        }
        @Override
        public Filter getFilter() {
            return nameFilter;
        }

        /**
         * Custom Filter implementation for custom suggestions we provide.
         */
        Filter nameFilter = new Filter() {
            @Override
            public CharSequence convertResultToString(Object resultValue) {
                String str = ((HashtagKey) resultValue).getData();
                return str;
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if (constraint != null) {
                    suggestions.clear();
                    for (HashtagKey people : tempItems) {
                        if (people.getData().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            suggestions.add(people);
                        }
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = suggestions;
                    filterResults.count = suggestions.size();
                    return filterResults;
                } else {
                    return new FilterResults();
                }
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                List<HashtagKey> filterList = (ArrayList<HashtagKey>) results.values;
                if (results != null && results.count > 0) {
                    clear();
                    for (HashtagKey people : filterList) {
                        add(people);
                        notifyDataSetChanged();
                    }
                }
            }
        };
    }



}
