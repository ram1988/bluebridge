package com.ibm.bluebridge.util;

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
                System.out.println("Main Thread Waiting for response...");
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
                System.out.println("Main Thread Waiting for response...");
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
            System.out.println("Main Thread Waiting for response...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean getConnection(String urlStr) {
        boolean isConnFine = true;
        try {
            URL url = new URL(urlStr);
            restConnection = (HttpURLConnection) url.openConnection();
            System.out.println("connection established");
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
                    synchronized(respJsonObj) {
                        restConnection.setReadTimeout(5000);
                        restConnection.setConnectTimeout(6000);
                        BufferedReader in = new BufferedReader(new InputStreamReader(restConnection.getInputStream()));
                        StringBuffer jsonStr = new StringBuffer();
                        String line = null;
                        while ((line = in.readLine()) != null) {
                            System.out.println("line-->" + line);
                            jsonStr.append(line);
                        }
                        System.out.println("jsonStrfrom-->" + jsonStr.toString());
                        respJsonObj.put("response", new JSONArray(jsonStr.toString()));
                        System.out.println("jsonObj-->" + respJsonObj);
                        respJsonObj.notifyAll();
                    }
                } else {
                    //throw ApplicationException
                    respJsonObj.put("response", null);
                    System.out.println("issue in connection");
                }
            } catch (Exception e ) {
                try {
                    synchronized(respJsonObj) {
                        respJsonObj.put("response", null);
                        respJsonObj.notifyAll();
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                System.out.println("issue in connection");
                e.printStackTrace();
                //System.out.println(e.getMessage());
                //Log Exception
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

                            if (result.length() > 0) {
                                respJsonObj.put("response", new JSONObject(result.toString()));
                            }
                        }
                    } else if (httpMethod.equals("delete")) {
                        HttpDelete httpRequest = new HttpDelete(requestUrl);
                        HttpResponse httpResponse = httpClient.execute(httpRequest);
                    }
                    respJsonObj.notifyAll();
                }

                System.out.println("action successful for " + requestUrl);
                //isPostSubmitted = true;

            } catch (Exception e ) {
                //isPostSubmitted = false;
                System.out.println("issue in connection");
                e.printStackTrace();
                //System.out.println(e.getMessage());
                //Log Exception
            }
            finally {

            }
        }
    }
}
