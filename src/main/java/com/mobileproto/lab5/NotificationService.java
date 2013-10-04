package com.mobileproto.lab5;

/**
 * Created by mingram on 10/1/13.
 */
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

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class NotificationService extends Service {
    private Integer followers_length = -1;
    private Integer mentions_length = -1;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Timer timer = new Timer ();
        TimerTask checkUpdates = new TimerTask () {
            @Override
            public void run () {
                ConnectionFragment connections = new ConnectionFragment();
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

                //Does not work at the moment
                connections.silentRefresh();
                // if new mention or follower, send notification
            }
        };

// schedule the task to run starting now and then every hour...
        timer.schedule (checkUpdates, 0l, 1000);

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

}
