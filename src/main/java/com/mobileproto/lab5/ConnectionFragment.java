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
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by evan on 9/25/13.
 */
public class ConnectionFragment extends CustomFragment {
    private List<FeedNotification> notifications;
    ConnectionListAdapter connectionListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HttpRequest updateHttpRequest = new HttpRequest(this,"followers");
        updateHttpRequest.execute("http://twitterproto.herokuapp.com/" + FeedActivity.userName +"/followers");
        HttpRequest mentionHttpRequest = new HttpRequest(this,"mentions");
        mentionHttpRequest.execute("http://twitterproto.herokuapp.com/tweets?q=@" + FeedActivity.userName);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.connections_fragment, null);
        notifications = new ArrayList<FeedNotification>();
        connectionListAdapter = new ConnectionListAdapter(this.getActivity(), notifications);
        ListView connectionList = (ListView) v.findViewById(R.id.connectionListView);
        connectionList.setAdapter(connectionListAdapter);
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
        if (type.equals("followers")) {
            try {
                jArray = jsonObj.getJSONArray("followers");
            } catch(JSONException e) {
                e.printStackTrace();
                Log.i("jsonParse", "error converting to json array");
            }

            for (int i=0; i < jArray.length(); i++)
            {

                try {

                    String follower = jArray.getString(i);
                    // Pulling items from the array

                    FollowNotification follow = new FollowNotification("@" + follower, "@" + FeedActivity.userName);
                    notifications.add(follow);

                } catch (JSONException e) {
                    Log.i("jsonParse", "error in iterating");
                }
            }
        } else if (type.equals("mentions")) {
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
                    MentionNotification mention = new MentionNotification(tweeter, FeedActivity.userName, text);
                    notifications.add(mention);

                } catch (JSONException e) {
                    Log.i("jsonParse", "error in iterating");
                }
            }

        }
        connectionListAdapter.notifyDataSetChanged();

    }
}
