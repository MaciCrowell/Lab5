package com.mobileproto.lab5;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
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
public class ConnectionFragment extends CustomFragment {
    private List<FeedNotification> notifications = new ArrayList<FeedNotification>();
    ConnectionListAdapter connectionListAdapter;
    private long lastUpdate = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.connections_fragment, null);
        //notifications = new ArrayList<FeedNotification>();
        connectionListAdapter = new ConnectionListAdapter(this.getActivity(), notifications);
        ListView connectionList = (ListView) v.findViewById(R.id.connectionListView);
        connectionList.setAdapter(connectionListAdapter);

        connectionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final TextView userName = (TextView) view.findViewById(R.id.connectionItemUser);
                String name = userName.getText().toString().substring(1);

                ((FeedActivity) getActivity()).openUserProfile(name);
            }
        });

        if (lastUpdate == 0) {
            String followersJSON = this.getActivity().getSharedPreferences("PREFERENCE", 0).getString("followers", "");
            if (!(followersJSON.equals(""))){
                Log.i("loadOld", followersJSON);
                updateFromHttp(followersJSON, "followers");
            }
            String mentionsJSON = this.getActivity().getSharedPreferences("PREFERENCE", 0).getString("mentions", "");
            if (!(mentionsJSON.equals(""))){
                Log.i("loadOld", mentionsJSON);
                updateFromHttp(mentionsJSON, "mentions");
            }
        }

        if ((System.currentTimeMillis() - lastUpdate) > 60000) {
            lastUpdate = System.currentTimeMillis();
            HttpRequest updateHttpRequest = new HttpRequest(this,"followers");
            updateHttpRequest.execute("http://twitterproto.herokuapp.com/" + FeedActivity.userName +"/followers");
            HttpRequest mentionHttpRequest = new HttpRequest(this,"mentions");
            mentionHttpRequest.execute("http://twitterproto.herokuapp.com/tweets?q=@" + FeedActivity.userName);
        }


        return v;
    }

    public void refreshFeed() {
        lastUpdate = System.currentTimeMillis();
        HttpRequest updateHttpRequest = new HttpRequest(this,"followers");
        updateHttpRequest.execute("http://twitterproto.herokuapp.com/" + FeedActivity.userName +"/followers");
        HttpRequest mentionHttpRequest = new HttpRequest(this,"mentions");
        mentionHttpRequest.execute("http://twitterproto.herokuapp.com/tweets?q=@" + FeedActivity.userName);
    }

    public void saveMentions(String result){
        if (isAdded()) {
            this.getActivity().getSharedPreferences("PREFERENCE", 0)
                    .edit()
                    .putString("mentions",result)
                    .commit();
        }
    }

    public void saveFollowers(String result){
        Log.i("saveFollowers",this.toString());
        if (isAdded()) {
            this.getActivity().getSharedPreferences("PREFERENCE", 0)
                    .edit()
                    .putString("followers",result)
                    .commit();
        }
    }

    public boolean checkIfFollowerInList(String follower){
        for (int i=0; i < notifications.size(); i++) {
            if (notifications.get(i).getClass().toString().equals("class com.mobileproto.lab5.FollowNotification")){
                if (notifications.get(i).userFrom.equals("@" + follower)){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkIfMentionInList(String tweeter, String text){
        for (int i=0; i < notifications.size(); i++) {
            Log.i("jsonParse", notifications.get(i).getClass().toString());
            if (notifications.get(i).getClass().toString().equals("class com.mobileproto.lab5.MentionNotification")){
                Log.i("jsonParse", "mention in list" + notifications.get(i).userFrom);
                Log.i("jsonParse", "mention " + tweeter);
                if (notifications.get(i).userFrom.equals("@" + tweeter) && notifications.get(i).text.equals(text)){
                    Log.i("jsonParse", "true");
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void updateFromHttp(String result, String type){
        if (result != null && !result.equals("")) {
            JSONArray jArray = new JSONArray();
            // ArrayList tweets = new ArrayList();
            JSONObject jsonObj = null;
            try{
                jsonObj = new JSONObject(result);
            }catch (JSONException e){
                Log.i("jsonParse", "error converting string to json object");
            }
            if (type.equals("followers")) {
                saveFollowers(result);

                try {
                    jArray = jsonObj.getJSONArray("followers");
                } catch(JSONException e) {
                    e.printStackTrace();
                    Log.i("jsonParse", "error converting to json array");
                }


                for (int i=jArray.length()-1; 0 <= i; i--)
                {
                    try {

                        String follower = jArray.getString(i);
                        // Pulling items from the array
                        if (!checkIfFollowerInList(follower)) {
                            Log.i("jsonParse", "hmm..." + follower);
                            FollowNotification follow = new FollowNotification("@" + follower, "@" + FeedActivity.userName);
                            notifications.add(follow);
                        }

                    } catch (JSONException e) {
                        Log.i("jsonParse", "error in iterating");
                    }
                }
            } else if (type.equals("mentions")) {
                saveMentions(result);
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

                        String tweeter = tweetObject.getString("username");
                        String text = tweetObject.getString("tweet");
                        if (!checkIfMentionInList(tweeter, text)) {
                            MentionNotification mention = new MentionNotification("@" + tweeter, "@" + FeedActivity.userName, text);
                            notifications.add(mention);
                        }

                    } catch (JSONException e) {
                        Log.i("jsonParse", "error in iterating");
                    }
                }
            }
            connectionListAdapter.notifyDataSetChanged();
        }
    }
}
