package com.mobileproto.lab5;

/**
 * Created by mmay on 9/29/13.
 */

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by evan on 9/26/13.
 */
public class FollowFragment extends CustomFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.follow_fragment, null);


        Button follow = (Button)v.findViewById(R.id.followButton);

        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView userString = (TextView) getView().findViewById(R.id.userField);
                PostRequest updateHttpRequest = new PostRequest(FollowFragment.this, "tweet");
                Log.i("user", userString.getText().toString());
                String url = "http://twitterproto.herokuapp.com/" + FeedActivity.userName + "/follow";
                ArrayList urlParams = new ArrayList<String> ();
                String param = "username";
                urlParams.add(url);
                urlParams.add(param);
                urlParams.add(userString.getText().toString());
                updateHttpRequest.execute(urlParams);
            }
        });
        return v;
    }

    @Override
    public void updateFromHttp(String result, String type){
        Log.i("Post", "Completed");

        // Create new fragment and transaction
        Fragment newFragment = new FeedFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.fragmentContainer, newFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

}
