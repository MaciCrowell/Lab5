package com.mobileproto.lab5;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by evan on 9/25/13.
 */
public class FeedFragment extends CustomFragment {
    private List<FeedItem> sampleData = new ArrayList<FeedItem>();
    private FeedListAdapter feedListAdapter;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HttpRequest updateHttpRequest = new HttpRequest(this,"tweet");
        updateHttpRequest.execute("http://twitterproto.herokuapp.com/tweets");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.feed_fragment, null);

         /*
         * Creating some sample test data to see what the layout looks like.
         * You should eventually delete this.
         */

        // Set up the ArrayAdapter for the feedList
        this.feedListAdapter = new FeedListAdapter(this.getActivity(), sampleData);
        ListView feedList = (ListView) v.findViewById(R.id.feedList);
        feedList.setAdapter(feedListAdapter);

        return v;

    }

    @Override
    public void updateFromHttp(String result, String type){
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

        for (int i=0; i < jArray.length(); i++)
        {

            try {

                JSONObject tweetObject = jArray.getJSONObject(i);
                // Pulling items from the array
                String userName = tweetObject.getString("username");
                String text = tweetObject.getString("tweet");
                FeedItem tweet = new FeedItem(userName,text);
                sampleData.add(tweet);

            } catch (JSONException e) {
                Log.i("jsonParse", "error in iterating");
            }
        }
        feedListAdapter.notifyDataSetChanged();

    }
}
