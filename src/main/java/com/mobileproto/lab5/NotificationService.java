package com.mobileproto.lab5;

/**
 * Created by mingram on 10/1/13.
 */
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationService extends Service {
    private List<FeedNotification> notifications = new ArrayList<FeedNotification>();
    private Integer followers_length = -1;
    private Integer mentions_length = -1;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void updatePreferences() {
        ServiceHttpRequest updateHttpRequest = new ServiceHttpRequest(this,"followers_silent");
        updateHttpRequest.execute("http://twitterproto.herokuapp.com/" + FeedActivity.userName +"/followers");
        ServiceHttpRequest mentionHttpRequest = new ServiceHttpRequest(this,"mentions_silent");
        mentionHttpRequest.execute("http://twitterproto.herokuapp.com/tweets?q=@" + FeedActivity.userName);
    }

    @Override
    public void onCreate() {
        Timer timer = new Timer ();
        TimerTask checkUpdates = new TimerTask () {
            @Override
            public void run () {


                String followers = getApplicationContext().getSharedPreferences("PREFERENCE", 0).getString("followers", "");
                String mentions = getApplicationContext().getSharedPreferences("PREFERENCE", 0).getString("mentions", "");
                Integer follow_length = followers.length();
                Integer mention_length = mentions.length();

                if (followers_length.equals(-1)) {
                    followers_length = follow_length;
                }
                else if (!(follow_length.equals(followers_length))) {
                    Log.i("Followers Length", followers_length.toString());
                    Log.i("Follow Length", follow_length.toString());
                    followers_length = follow_length;
                    alert("You have a new follower!", "Check the app for details...");
                }

                if (mentions_length.equals(-1)) {
                    mentions_length = mention_length;
                }
                else if (!(mention_length.equals(mentions_length))) {
                    mentions_length = mention_length;
                    alert("A friend mentioned you!", "Check the app for details...");
                }
                Log.i("Followers", follow_length.toString());
                Log.i("Mentions", mention_length.toString());

                updatePreferences();
            }
        };

// schedule the task to run starting now and then every hour...
        timer.schedule (checkUpdates, 0l, 1000*5);

    }

    public void alert(String title, String text) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(text);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, FeedActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(FeedActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder.setAutoCancel(true);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(010, mBuilder.build());
    }

    public void saveMentions(String result){
        this.getSharedPreferences("PREFERENCE", 0)
                    .edit()
                    .putString("mentions",result)
                    .commit();
    }

    public void saveFollowers(String result){
        Log.i("saveFollowers",this.toString());
        this.getSharedPreferences("PREFERENCE", 0)
                    .edit()
                    .putString("followers",result)
                    .commit();
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
            if (type.equals("followers") || type.equals("followers_silent")) {
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
            } else if (type.equals("mentions") || type.equals("mentions_silent")) {
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
        }
    }

}
