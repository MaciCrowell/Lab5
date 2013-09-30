package com.mobileproto.lab5;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

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
    private long lastUpdate = 0;


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

        // Set up the ArrayAdapter for the feedList

        this.feedListAdapter = new FeedListAdapter(this.getActivity(), sampleData);
        ListView feedList = (ListView) v.findViewById(R.id.feedList);
        feedList.setAdapter(feedListAdapter);
        if (lastUpdate == 0) {
            String tweetsJSON = this.getActivity().getSharedPreferences("PREFERENCE", 0).getString("tweets", "");
            if (!(tweetsJSON.equals(""))){
                Log.i("loadOld", tweetsJSON);
                updateFromHttp(tweetsJSON, "tweets");
            }
        }

        if ((System.currentTimeMillis() - lastUpdate) > 60000) {
            lastUpdate = System.currentTimeMillis();
            HttpRequest updateHttpRequest = new HttpRequest(this,"tweet");
            updateHttpRequest.execute("http://twitterproto.herokuapp.com/tweets");
        }


        feedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final TextView userName = (TextView) view.findViewById(R.id.feedItemUser);
                String name = userName.getText().toString();

                ((FeedActivity) getActivity()).openUserProfile(name);
            }
        });

        return v;

    }


    public void saveTweets(String result){
        this.getActivity().getSharedPreferences("PREFERENCE", 0)
                .edit()
                .putString("tweets",result)
                .commit();
    }

    public boolean checkIfTweetInList(String tweeter, String text){
        for (int i=0; i < sampleData.size(); i++) {
            Log.i("jsonParse", sampleData.get(i).getClass().toString());
            if (sampleData.get(i).getClass().toString().equals("class com.mobileproto.lab5.MentionNotification")){
                Log.i("jsonParse", "mention in list" + sampleData.get(i).userName);
                Log.i("jsonParse", "mention " + tweeter);
                if (sampleData.get(i).userName.equals("@" + tweeter) && sampleData.get(i).text.equals(text)){
                    Log.i("jsonParse", "true");
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void updateFromHttp(String result, String type){
        if (result != null && !result.isEmpty()) {
            if (!result.equals("")){
                saveTweets(result);
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
                sampleData.clear();
                for (int i=0; i < jArray.length(); i++)


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
            this.feedListAdapter.notifyDataSetChanged();

        } else {Log.i("jsonParse", "result is null");}

    }
}
