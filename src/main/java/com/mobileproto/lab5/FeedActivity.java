package com.mobileproto.lab5;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.app.Activity;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class FeedActivity extends Activity{

    public static String userName;

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
        FeedFragment feedFragment = new FeedFragment();
        ConnectionFragment connectionFragment = new ConnectionFragment();
        SearchFragment searchFragment = new SearchFragment();

        /*
         *  The following code is used to set up the tabs used for navigation.
         *  You shouldn't need to touch the following code.
         */
        final ActionBar actionBar = getActionBar();
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

    }



}
