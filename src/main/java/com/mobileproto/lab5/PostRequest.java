package com.mobileproto.lab5;

/**
 * Created by mmay on 9/29/13.
 */


import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.http.message.BasicNameValuePair;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mingram on 9/26/13.
 */
public class PostRequest extends AsyncTask<ArrayList, Void, String> {
    private CustomFragment myFragment;
    private String type;

    public PostRequest(CustomFragment myFragment, String type) {
        this.myFragment = myFragment;
        this.type = type;
    }


    @Override
    protected String doInBackground(ArrayList... uri){
        HttpClient httpclient = new DefaultHttpClient();
        ArrayList<String> current = uri[0];
        String url = current.get(0);
        String param = current.get(1);
        String tweet = current.get(2);
        HttpResponse response;
        String responseString = null;
        try {
            HttpPost httppost = new HttpPost(url);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair(param, tweet));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            response = httpclient.execute(httppost);
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                responseString = out.toString();
            } else{
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            //TODO Handle problems..
        } catch (IOException e) {
            //TODO Handle problems..
        }
        return responseString;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        myFragment.updateFromHttp(result,this.type);
    }
}