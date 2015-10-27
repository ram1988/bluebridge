package com.ibm.bluebridge.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.channels.AsynchronousCloseException;
import java.util.ArrayList;
import java.util.List;


import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import com.ibm.bluebridge.valueobject.Event;

/**
 * Created by manirm on 10/10/2015.
 */
public class EventsAdapter {

    private List<Event> eventsList;
    private static final String BASE_RESTURI = "http://school-volunteer-bluebridge.eu-gb.mybluemix.net/api";
    private HttpURLConnection restConnection;
    private Context ctxt;
    private JSONObject respJsonObj;
    private Boolean isPostSubmitted = false;

    public EventsAdapter() {
        this.respJsonObj = new JSONObject();
    }

    public EventsAdapter(Context ctxt) {
        this.respJsonObj = new JSONObject();
        this.ctxt = ctxt;
    }

    /*******Admin Methods**********/
    public List<Event> getAdminEventsList(String admin_id) {
        String allEventsAPI = BASE_RESTURI + "/admin_list_events?admin_id="+admin_id;
        eventsList = new ArrayList<Event>();

        try {
            getResponse(allEventsAPI);
            Object response = respJsonObj.get("response");

            if(response != null ) {
                JSONArray list = (JSONArray) response;
                int contentLength = list.length();

                if (contentLength > 0) {
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject item = list.getJSONObject(i);

                        Event event = new Event();
                        event.setEventId(item.getString("id"));
                        event.setEventName(item.getString("name"));
                        event.setEventDescription(item.getString("duty"));
                        event.setEventDate(item.getString("date"));
                        event.setStartTime(item.getString("start_time"));
                        event.setEndTime(item.getString("end_time"));
                        event.setVenue(item.getString("venue"));
                        event.setTeacherInCharge(item.getString("teacher_in_charge"));
                        event.setBriefingTime(item.getString("briefing_time"));
                        event.setBriefingPlace(item.getString("briefing_place"));
                        event.setMaxVolunteers(item.getInt("max_volunteers"));
                        event.setVacancy(item.getInt("event_vacancy"));
                        event.setCategory(item.getString("category"));

                        eventsList.add(event);

                        //System.out.println("jsonobj--->" + item.get("name"));
                    }
                } else {

                }
            }

        }
        catch(JSONException excep) {
            excep.printStackTrace();
        }
        catch(Exception excep){
            excep.printStackTrace();
        }
        finally {
            // restConnection.disconnect();
        }



        return eventsList;
    }



    public void addEvent(Event event) {

        String addEventsAPI = BASE_RESTURI + "/admin_add_event?admin_id=A000000E";

        JSONObject eventObj = new JSONObject();

        try {
            eventObj.put("name", event.getEventName());
            eventObj.put("duty",event.getEventDescription());
            eventObj.put("date",event.getEventDate());
            eventObj.put("start_time",event.getStartTime());
            eventObj.put("end_time",event.getEndTime());
            eventObj.put("venue",event.getVenue());
            eventObj.put("teacher_in_charge",event.getTeacherInCharge());
            eventObj.put("briefing_time",event.getBriefingTime());
            eventObj.put("briefing_place",event.getBriefingPlace());
            eventObj.put("max_volunteers",event.getMaxVolunteers());
            eventObj.put("event_vacancy",event.getVacancy());
            eventObj.put("category", event.getCategory());

            postResponse(addEventsAPI,eventObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void updateEvent(Event event) {

        String updateEventsAPI = BASE_RESTURI + "/admin_add_event?admin_id=A000000E&event_id="+event.getEventId();

        JSONObject eventObj = new JSONObject();

        try {
            eventObj.put("name", event.getEventName());
            eventObj.put("duty",event.getEventDescription());
            eventObj.put("date",event.getEventDate());
            eventObj.put("start_time",event.getStartTime());
            eventObj.put("end_time",event.getEndTime());
            eventObj.put("venue",event.getVenue());
            eventObj.put("teacher_in_charge",event.getTeacherInCharge());
            eventObj.put("briefing_time",event.getBriefingTime());
            eventObj.put("briefing_place",event.getBriefingPlace());
            eventObj.put("max_volunteers",event.getMaxVolunteers());
            eventObj.put("event_vacancy",event.getVacancy());
            eventObj.put("category", event.getCategory());

            postResponse(updateEventsAPI,eventObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    /*******Parent methods*********/
    public List<Event> getAllEventsList() {
        String allEventsAPI = BASE_RESTURI + "/parent_list_events";
        eventsList = new ArrayList<Event>();

        try {
            getResponse(allEventsAPI);
            Object response = respJsonObj.get("response");

            if(response != null ) {
                JSONArray list = (JSONArray) response;
                int contentLength = list.length();

                if (contentLength > 0) {
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject item = list.getJSONObject(i);

                        Event event = new Event();
                        event.setEventId(item.getString("id"));
                        event.setEventName(item.getString("name"));
                        event.setEventDescription(item.getString("duty"));
                        event.setEventDate(item.getString("date"));
                        event.setStartTime(item.getString("start_time"));
                        event.setEndTime(item.getString("end_time"));
                        event.setVenue(item.getString("venue"));
                        event.setTeacherInCharge(item.getString("teacher_in_charge"));
                        event.setBriefingTime(item.getString("briefing_time"));
                        event.setBriefingPlace(item.getString("briefing_place"));
                        event.setMaxVolunteers(item.getInt("max_volunteers"));
                        event.setVacancy(item.getInt("event_vacancy"));
                        event.setCategory(item.getString("category"));

                        eventsList.add(event);

                        //System.out.println("jsonobj--->" + item.get("name"));
                    }
                } else {

                }
            }

        }
        catch(JSONException excep) {
            excep.printStackTrace();
        }
        catch(Exception excep){
            excep.printStackTrace();
        }
        finally {
            // restConnection.disconnect();
        }



        return eventsList;
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

    private void getResponse(String url) {

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

    private void postResponse(String url,JSONObject input) {
            PostResponseTask postRespObj = new PostResponseTask(url,input);
            postRespObj.start();
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
                        restConnection.setReadTimeout(10000);
                        restConnection.setConnectTimeout(15000);
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

        public PostResponseTask(String url, JSONObject input) {
            this.input = input;
            this.requestUrl = url;
        }

        public void run() {
            try {
                boolean isConnFine = getConnection(requestUrl);
                if(isConnFine) {
                    restConnection.setReadTimeout(10000);
                    restConnection.setConnectTimeout(15000);
                    restConnection.setRequestMethod("POST");
                    restConnection.setUseCaches(false);
                    restConnection.setRequestProperty("Content-Type", "application/json");
                    restConnection.setDoInput(true);
                    restConnection.setDoOutput(true);

                    System.out.println("url-->" + requestUrl);

                    DataOutputStream printout = new DataOutputStream(restConnection.getOutputStream ());
                    printout.write(URLEncoder.encode(input.toString(), "UTF-8").getBytes());
                    printout.flush();
                    printout.close();

                    System.out.println("insertion successful");
                    isPostSubmitted = true;
                } else {
                    //throw ApplicationException
                    isPostSubmitted = false;
                    System.out.println("issue in connection");
                }
            } catch (Exception e ) {
                e.printStackTrace();
                //System.out.println(e.getMessage());
                //Log Exception
            }
            finally {
                restConnection.disconnect();
            }
        }
    }


}
