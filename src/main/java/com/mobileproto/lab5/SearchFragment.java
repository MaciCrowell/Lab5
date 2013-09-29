package com.mobileproto.lab5;

import android.app.Fragment;
import android.content.Intent;
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
public class SearchFragment extends CustomFragment {
    private List<FeedItem> searchResults;
    private FeedListAdapter searchListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.search_fragment, null);

        searchResults = new ArrayList<FeedItem>();
        this.searchListAdapter = new FeedListAdapter(this.getActivity(), searchResults);
        ListView resultsList = (ListView) v.findViewById(R.id.searchResults);
        resultsList.setAdapter(searchListAdapter);


        Button search = (Button)v.findViewById(R.id.searchButton);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView searchString = (TextView)getView().findViewById(R.id.searchField);
                HttpRequest updateHttpRequest = new HttpRequest(SearchFragment.this,"tweet");
                Log.i("search", searchString.getText().toString());
                updateHttpRequest.execute("http://twitterproto.herokuapp.com/tweets?q=" + searchString.getText().toString());
            }
        });

        return v;
    }

    @Override
    public void updateFromHttp(String result, String type){
        Log.i("search", result);
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
        this.searchResults.clear();
        for (int i=0; i < jArray.length(); i++)
        {

            try {

                JSONObject tweetObject = jArray.getJSONObject(i);
                // Pulling items from the array
                String userName = tweetObject.getString("username");
                String text = tweetObject.getString("tweet");
                Log.i("search", text);
                FeedItem tweet = new FeedItem(userName,text);
                searchResults.add(tweet);

            } catch (JSONException e) {
                Log.i("jsonParse", "error in iterating");
            }
        }
        searchListAdapter.notifyDataSetChanged();

    }

}
