package com.mobileproto.lab5;



import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class FeedActivity extends Activity{
    public static String userName;
    public static String profile = "";
    public ActionBar actionBar;
    public FeedFragment feedFragment = new FeedFragment();
    public ConnectionFragment connectionFragment = new ConnectionFragment();
    public SearchFragment searchFragment = new SearchFragment();

    public void openUserProfile(String username) {

        // Create new fragment and transaction
        UserFragment newFragment = new UserFragment();
        profile = username;
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.fragmentContainer, newFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        Fragment newFragment;
        FragmentTransaction transaction;

        switch (item.getItemId()) {
            case R.id.new_tweet:
                // Create new fragment and transaction
                newFragment = new PostFragment();
                transaction = getFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack
                transaction.replace(R.id.fragmentContainer, newFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
                return true;
            case R.id.follow:
                // Create new fragment and transaction
                newFragment = new FollowFragment();
                transaction = getFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack
                transaction.replace(R.id.fragmentContainer, newFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
                return true;
            case R.id.refresh:
                Log.i("refresh", "trying to refresh");
                feedFragment.refreshFeed();
                connectionFragment.refreshFeed();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        this.userName = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("userName", "");
        if (userName.equals("")){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("UserName");

            final EditText input = new EditText(this);
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String userName= input.getText().toString();
                    FeedActivity.this.userName = userName;
                    getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                            .edit()
                            .putString("userName", userName)
                            .commit();
                }
            });
            builder.show();
        }
        Log.i("userName", this.userName);

        // Define view fragments
        actionBar = getActionBar();
        feedFragment = new FeedFragment();
        connectionFragment = new ConnectionFragment();
        searchFragment = new SearchFragment();

        /*
         *  The following code is used to set up the tabs used for navigation.
         *  You shouldn't need to touch the following code.
         */
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.Tab feedTab = actionBar.newTab().setText(R.string.tab1);
        feedTab.setTabListener(new NavTabListener(feedFragment));

        ActionBar.Tab connectionTab = actionBar.newTab().setText(R.string.tab2);
        connectionTab.setTabListener(new NavTabListener(connectionFragment));

        ActionBar.Tab searchTab = actionBar.newTab().setText(R.string.tab3);
        searchTab.setTabListener(new NavTabListener(searchFragment));

        actionBar.addTab(feedTab);
        actionBar.addTab(connectionTab);
        actionBar.addTab(searchTab);

        actionBar.setStackedBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.android_dark_blue)));

        startService(new Intent(this, NotificationService.class));

    }
}
