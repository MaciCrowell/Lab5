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
public class FeedFragment extends Fragment {
    private List<FeedItem> sampleData;
    private FeedListAdapter feedListAdapter;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.feed_fragment, null);

         /*
         * Creating some sample test data to see what the layout looks like.
         * You should eventually delete this.
         */
        HttpRequest updateHttpRequest = new HttpRequest(this);
        updateHttpRequest.execute("http://twitterproto.herokuapp.com/tweets", "hi");
        FeedItem item1 = new FeedItem("@TimRyan", "Dear reader, you are reading.");
        FeedItem item2 = new FeedItem("@EvanSimpson", "Hey @TimRyan");
        FeedItem item3 = new FeedItem("@JulianaNazare", "Everything happens so much.");
        FeedItem item4 = new FeedItem("@reyner", "dGhlIGNvb2wgbmV3IHRoaW5nIHRvIGRvIGlzIGJhc2U2NCBlY29kZSB5b3VyIHR3ZWV0cw==");
        this.sampleData = new ArrayList<FeedItem>();
        sampleData.add(item1);
        sampleData.add(item2);
        sampleData.add(item3);
        sampleData.add(item4);

        // Set up the ArrayAdapter for the feedList
        this.feedListAdapter = new FeedListAdapter(this.getActivity(), sampleData);
        ListView feedList = (ListView) v.findViewById(R.id.feedList);
        feedList.setAdapter(feedListAdapter);


        return v;

    }

    public void update(String result){
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
