package com.mobileproto.lab5;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by evan on 9/25/13.
 */
public class FeedFragment extends Fragment {


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
        new HttpRequest().execute("http://twitterproto.herokuapp.com/tweets");
        FeedItem item1 = new FeedItem("@TimRyan", "Dear reader, you are reading.");
        FeedItem item2 = new FeedItem("@EvanSimpson", "Hey @TimRyan");
        FeedItem item3 = new FeedItem("@JulianaNazare", "Everything happens so much.");
        FeedItem item4 = new FeedItem("@reyner", "dGhlIGNvb2wgbmV3IHRoaW5nIHRvIGRvIGlzIGJhc2U2NCBlY29kZSB5b3VyIHR3ZWV0cw==");
        List<FeedItem> sampleData = new ArrayList<FeedItem>();
        sampleData.add(item1);
        sampleData.add(item2);
        sampleData.add(item3);
        sampleData.add(item4);

        // Set up the ArrayAdapter for the feedList
        FeedListAdapter feedListAdapter = new FeedListAdapter(this.getActivity(), sampleData);
        ListView feedList = (ListView) v.findViewById(R.id.feedList);
        feedList.setAdapter(feedListAdapter);


        return v;

    }
}
