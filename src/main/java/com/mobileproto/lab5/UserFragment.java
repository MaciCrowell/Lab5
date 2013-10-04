package com.mobileproto.lab5;

/**
 * Created by mmay on 9/29/13.
 */

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by evan on 9/26/13.
 */
public class UserFragment extends CustomFragment {
    private List<FeedItem> searchResults;
    private FeedListAdapter searchListAdapter;
    public Boolean following = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.user_fragment, null);

        try {
            ActionBar actionbar = (ActionBar) getActivity().getActionBar();
            actionbar.selectTab(null);
        } catch (Exception e) {}

        TextView name = (TextView) v.findViewById(R.id.userName);
        name.setText("@" + FeedActivity.profile);

        ImageView followBtn = (ImageView) v.findViewById(R.id.followButton);
        followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView clickedButton = (ImageView) view;
                PostRequest updateHttpRequest = new PostRequest(UserFragment.this, "follow");
                Log.i("user", FeedActivity.profile);
                String url = "http://twitterproto.herokuapp.com/" + FeedActivity.userName + "/follow";
                ArrayList urlParams = new ArrayList<String> ();
                String param = "username";
                urlParams.add(url);
                urlParams.add(param);
                urlParams.add(FeedActivity.profile);
                clickedButton.setImageResource(R.drawable.ic_rating_important);
                updateHttpRequest.execute(urlParams);
            }
        });

        searchResults = new ArrayList<FeedItem>();
        this.searchListAdapter = new FeedListAdapter(this.getActivity(), searchResults);
        ListView resultsList = (ListView) v.findViewById(R.id.searchResults);
        resultsList.setAdapter(searchListAdapter);

        HttpRequest updateHttpRequest = new HttpRequest(UserFragment.this, "tweets");
        updateHttpRequest.execute("http://twitterproto.herokuapp.com/" + FeedActivity.profile + "/tweets");

        HttpRequest followHttpRequest = new HttpRequest(UserFragment.this, "following");
        followHttpRequest.execute("http://twitterproto.herokuapp.com/" + FeedActivity.profile + "/followers");

        return v;
    }

    @Override
    public void updateFromHttp(String result, String type){
        Log.i("String type", type);

        if (type.equals("tweets")) {
            Log.i("search", result);
            JSONArray jArray = new JSONArray();
            // ArrayList tweets = new ArrayList();
            JSONObject jsonObj = null;
            try{
                jsonObj = new JSONObject(result);
            }catch (JSONException e){
                Log.i("jsonParse", "error converting string to json object");
            }
            try {
                jArray = jsonObj.getJSONArray("tweets");
            } catch(JSONException e) {
                e.printStackTrace();
                Log.i("jsonParse", "error converting to json array");
            }
            this.searchResults.clear();
            for (int i=0; i < jArray.length(); i++)
            {

                try {

                    JSONObject tweetObject = jArray.getJSONObject(i);
                    // Pulling items from the array
                    String userName = tweetObject.getString("username");
                    String text = tweetObject.getString("tweet");
                    Log.i("search", text);
                    FeedItem tweet = new FeedItem(userName,text);
                    searchResults.add(tweet);

                } catch (JSONException e) {
                    Log.i("jsonParse", "error in iterating");
                }
            }
            searchListAdapter.notifyDataSetChanged();
        } else if (type.equals("following")) {

            //get current user's username
            String current_user = getActivity().getSharedPreferences("PREFERENCE", 0).getString("userName", "");

            //check if you follow this user
            Log.i("following", result);
            JSONArray jArray = new JSONArray();
            // ArrayList tweets = new ArrayList();
            JSONObject jsonObj = null;
            try{
                jsonObj = new JSONObject(result);
            }catch (JSONException e){
                Log.i("jsonParse", "error converting string to json object");
            }
            try {
                jArray = jsonObj.getJSONArray("followers");
            } catch(JSONException e) {
                e.printStackTrace();
                Log.i("jsonParse", "error converting to json array");
            }

            for (int i=0; i < jArray.length(); i++)
            {

                try {

                    String userName = jArray.getString(i);
                    Log.i("User Name", userName);
                    Log.i("Current User", current_user);
                    if (userName.equals(current_user)) {
                        following = true;
                    }

                } catch (JSONException e) {
                    Log.i("jsonParse", "error in iterating");
                }
            }

            // if the current user is not following the displayed profile
            // add button to follow user
            if (following) {
                ImageView btn = (ImageView) getView().findViewById(R.id.followButton);
                btn.setImageResource(R.drawable.ic_rating_important);
            }

        }

    }
}