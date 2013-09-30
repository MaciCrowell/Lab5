package com.mobileproto.lab5;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by evan on 9/26/13.
 */
public class PostFragment extends CustomFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.post_fragment, null);


        Button post = (Button)v.findViewById(R.id.postButton);

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView postString = (TextView) getView().findViewById(R.id.postField);
                PostRequest updateHttpRequest = new PostRequest(PostFragment.this, "tweet");
                Log.i("post", postString.getText().toString());
                String url = "http://twitterproto.herokuapp.com/" + FeedActivity.userName + "/tweets";
                ArrayList urlParams = new ArrayList<String> ();
                urlParams.add(url);
                urlParams.add(postString.getText().toString());
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
