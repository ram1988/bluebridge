package com.ibm.bluebridge.util;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by huangq on 6/11/2015.
 */
public class RESTApi {
    private JSONObject respJsonObj;
    private static final String BASE_RESTURI = CONSTANTS.BM_ROUTE + "api";
    private HttpURLConnection restConnection;
    //private Boolean isPostSubmitted = false;

    public RESTApi(){
        this.respJsonObj = new JSONObject();
    }

    public String getBaseRestURL(){
        return BASE_RESTURI;
    }

    public JSONObject getRespObj(){
        return respJsonObj;
    }

    public void getResponse(String url) {
        synchronized (respJsonObj) {
            GetResponseTask getRespObj = new GetResponseTask(url);
            getRespObj.start();
            try {
                Log.d("RESTApi", "Main Thread Waiting for response...");
                respJsonObj.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void postResponse(String url,JSONObject input, String method) {

        synchronized (respJsonObj) {
            PostResponseTask postRespObj = new PostResponseTask(url,input,method);
            postRespObj.start();
            try {
                Log.d("RESTApi", "Main Thread Waiting for response...");
                respJsonObj.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void asyncPostResponse(String url,JSONObject input, String method) {

        PostResponseTask postRespObj = new PostResponseTask(url,input,method);
        postRespObj.start();
        try {
            Log.d("RESTApi", "Main Thread Waiting for response...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean getConnection(String urlStr) {
        boolean isConnFine = true;
        try {
            URL url = new URL(urlStr);
            restConnection = (HttpURLConnection) url.openConnection();
            Log.d("RESTApi", "connection established");
        }
        catch (MalformedURLException e) {
            isConnFine = false;
            //Log Exception
            e.printStackTrace();
        }
        catch(Exception excep) {
            isConnFine = false;
            //Log Exception
            excep.printStackTrace();
        }

        return isConnFine;
    }

    private class GetResponseTask extends Thread {
        private String requestUrl;

        public GetResponseTask(String url) {
            requestUrl = url;
        }
        public void run() {
            try {
                boolean isConnFine = getConnection(requestUrl);
                if(isConnFine) {
                    System.out.println("URL--> " + requestUrl);
                    synchronized(respJsonObj) {
                        restConnection.setReadTimeout(15000);
                        restConnection.setConnectTimeout(16000);
                        BufferedReader in = new BufferedReader(new InputStreamReader(restConnection.getInputStream()));
                        StringBuffer jsonStr = new StringBuffer();
                        String line = null;
                        while ((line = in.readLine()) != null) {
                            Log.d("RESTApi", "line-->" + line);
                            jsonStr.append(line);
                        }
                        Log.d("RESTApi", "jsonStrfrom-->" + jsonStr.toString());
                        try{
                            respJsonObj.put("response", new JSONArray(jsonStr.toString()));
                        }
                        catch(JSONException excep) {
                            respJsonObj.put("response", new JSONObject(jsonStr.toString()));
                        }
                        Log.d("RESTApi", "jsonObj-->" + respJsonObj);
                        respJsonObj.notifyAll();
                    }
                } else {
                    //throw ApplicationException
                    Log.d("RESTApi", "issue in connection");
                }
            } catch (Exception e ) {
                synchronized(respJsonObj) {
                    respJsonObj.notifyAll();
                }
                Log.e("RESTApi", "issue in connection");
                e.printStackTrace();
            }
            finally {
                restConnection.disconnect();
            }
        }
    }

    private class PostResponseTask extends Thread {

        private String requestUrl;
        private JSONObject input;
        private String httpMethod;

        public PostResponseTask(String url, JSONObject input, String method) {
            this.input = input;
            this.requestUrl = url;
            this.httpMethod = method;
        }

        public void run() {

            try {
                synchronized(respJsonObj) {
                    HttpClient httpClient = new DefaultHttpClient();

                    if (httpMethod.equals("post")) {
                        HttpPost httpRequest = new HttpPost(requestUrl);
                        if (input != null) {
                            StringEntity se = new StringEntity(input.toString());
                            httpRequest.setEntity(se);
                        }
                        httpRequest.setHeader("Accept", "application/json");
                        httpRequest.setHeader("Content-Type", "application/json");


                        HttpResponse httpResponse = httpClient.execute(httpRequest);

                        int statusCode = httpResponse.getStatusLine().getStatusCode();

                        if (statusCode == 200) {
                            BufferedReader rd = new BufferedReader(
                                    new InputStreamReader(httpResponse.getEntity().getContent()));

                            StringBuffer result = new StringBuffer();
                            String line = "";
                            while ((line = rd.readLine()) != null) {
                                result.append(line);
                            }

                            if (result.length() > 0 ) {
                                try {
                                    respJsonObj.put("response", new JSONObject(result.toString()));
                                }
                                catch(JSONException excep) {
                                    Log.d("RESTApi", "json exception. ignored and passed");
                                }
                                //respJsonObj.put("response", new JSONObject(result.toString()));
                            }
                        }
                    } else if (httpMethod.equals("delete")) {
                        HttpDelete httpRequest = new HttpDelete(requestUrl);
                        HttpResponse httpResponse = httpClient.execute(httpRequest);
                    }
                    respJsonObj.notifyAll();
                }

                Log.d("RESTApi", "action successful for " + requestUrl);
                //isPostSubmitted = true;

            } catch (Exception e ) {
                //isPostSubmitted = false;
                Log.d("RESTApi", "issue in connection");
                e.printStackTrace();
                //Log.d("RESTApi", e.getMessage());
                //Log Exception
            }
            finally {

            }
        }
    }
}
