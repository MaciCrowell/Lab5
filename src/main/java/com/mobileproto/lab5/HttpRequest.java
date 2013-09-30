package com.mobileproto.lab5;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by mingram on 9/26/13.
 */
public class HttpRequest extends AsyncTask<String, Void, String>{
    private CustomFragment myFragment;
    private String type;

    public HttpRequest(CustomFragment myFragment, String type) {
        this.myFragment = myFragment;
        this.type = type;
    }


    @Override
    protected String doInBackground(String... uri){
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String  responseString = null;
        try {
            response = httpclient.execute(new HttpGet(uri[0]));
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
        if (result != null) {
        super.onPostExecute(result);
        myFragment.updateFromHttp(result,this.type);
        }
    }
}