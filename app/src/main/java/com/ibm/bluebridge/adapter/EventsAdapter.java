package com.ibm.bluebridge.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

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
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import com.ibm.bluebridge.valueobject.Children;
import com.ibm.bluebridge.valueobject.Event;
import com.ibm.bluebridge.valueobject.Parent;

/**
 * Created by manirm on 10/10/2015.
 */
public class EventsAdapter {

    private List<Event> eventsList;
    private static final String BASE_RESTURI = "http://school-volunteer-bluebridge.mybluemix.net/api";
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
    public String checkLogin(String nric,String password, String device_id) {
        String loginAPI = BASE_RESTURI + "/login";
        String role = null;

        JSONObject loginInfo = new JSONObject();

        try {
            loginInfo.put("nric", nric);
            loginInfo.put("password",password);
            loginInfo.put("device_id", device_id);

            postResponse(loginAPI, loginInfo, "post");
            Object response = respJsonObj.get("response");

            if(response != null ) {
                JSONObject item = (JSONObject) response;

                if (item!=null) {
                    role = item.getString("role");
                    System.out.println("Role--->"+role);
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return role;
    }


    public void markAttendance(String event_id,String parent_id) {
        String markAttendanceAPI = BASE_RESTURI + "/admin_mark_attendance?event_id="+event_id+"&parent_id="+parent_id;

        try {
            asyncPostResponse(markAttendanceAPI, null, "post");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }


    public List<Event> getAdminEventsList(String admin_id) {
        String allEventsAPI = BASE_RESTURI + "/admin_list_events?admin_id="+admin_id;
        eventsList = new ArrayList<Event>();

        try {
            System.out.println("Admin URI--->" + allEventsAPI);
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
                       // event.setCategory(item.getString("category"));

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

    public List<Event> getAdminCompletedEventsList(String admin_id) {
        String allEventsAPI = BASE_RESTURI + "/admin_list_events?past_years=2&admin_id="+admin_id;
        eventsList = new ArrayList<Event>();

        try {
            System.out.println("Admin URI--->" + allEventsAPI);
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
                        // event.setCategory(item.getString("category"));

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

    public List<Parent> getRegisteredParentsList(String event_id) {
        String registeredParentsAPI = BASE_RESTURI + "/admin_event_list_registration?event_id="+event_id;
        List<Parent> parentsList = new ArrayList<Parent>();

        try {
            System.out.println("Admin URI--->" + registeredParentsAPI);
            getResponse(registeredParentsAPI);
            Object response = respJsonObj.get("response");

            if(response != null ) {
                JSONArray list = (JSONArray) response;
                int contentLength = list.length();

                if (contentLength > 0) {
                    for (int i = 0; i < contentLength; i++) {
                        JSONObject item = list.getJSONObject(i);
                        Parent parent = new Parent();
                        parent.setId(item.getString("id"));
                        parent.setFirstname(item.getString("firstname"));
                        parent.setLastname(item.getString("lastname"));
                        parent.setGender(item.getString("gender"));
                        parent.setContact(item.getString("contact"));
                        parent.setEmail(item.getString("email"));
                        parent.setJob(item.getString("job"));
                        parent.setAddress(item.getString("address"));
                        parent.setHasAttended(item.getBoolean("attended_flag"));

                        JSONArray childrenJsonArr = (JSONArray) item.getJSONArray("children");
                        List<Children> childrenArr = new ArrayList<>(childrenJsonArr.length());
                        for(int j=0;j<childrenJsonArr.length();j++) {
                            JSONObject child = childrenJsonArr.getJSONObject(j);
                            Children children = new Children();

                            System.out.println("children--->" +child);
                            children.setId(child.getString("id"));
                            children.setName(child.getString("name"));
                            children.setGender(child.getString("gender"));
                            children.setBirthDate(child.getString("birth_date"));
                            children.setRegistrationYear(child.getString("registration_year"));



                            childrenArr.add(children);
                        }
                        parent.setChildren(childrenArr);

                        parentsList.add(parent);

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
        return parentsList;
    }



    public void addEvent(Event event,String admin_id) {

        String addEventsAPI = BASE_RESTURI + "/admin_add_event?admin_id="+admin_id;

        JSONObject eventObj = new JSONObject();

        try {
            eventObj.put("name", event.getEventName());
            eventObj.put("duty", event.getEventDescription());
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

            postResponse(addEventsAPI,eventObj,"post");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void updateEvent(Event event,String admin_id) {

        String updateEventsAPI = BASE_RESTURI + "/admin_update_event?admin_id="+admin_id+"&event_id="+event.getEventId();

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

            postResponse(updateEventsAPI,eventObj,"post");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteEvent(Event event,String admin_id) {

        String deleteEventsAPI = BASE_RESTURI + "/admin_cancel_event?admin_id="+admin_id+"&event_id="+event.getEventId();

        try {
            postResponse(deleteEventsAPI,null,"delete");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*******Parent methods*********/
    public List<Event> getAllEventsList(String parent_id) {
        String allEventsAPI = BASE_RESTURI + "/parent_list_events?parent_id=" + parent_id;
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
                        event.setDuration(item.getString("duration_in_hour"));
                        event.setRegistered(item.getBoolean("registered"));
                        event.setAttended(item.getBoolean("attended"));

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

    public List<Event> getAllJoinedEventsList(String parent_id) {
        String joinedEventsAPI = BASE_RESTURI + "/parent_joined_events?parent_id=" + parent_id;
        eventsList = new ArrayList<Event>();

        try {
            getResponse(joinedEventsAPI);
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
                        event.setVacancy(-100);
                        event.setCategory(item.getString("category"));
                        event.setDuration(item.getString("duration_in_hour"));
                        event.setRegistered(item.getBoolean("registered"));
                        event.setAttended(item.getBoolean("attended"));

                        eventsList.add(event);

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

    public List<Event> getAllAttendedEventsList(String parent_id) {
        String attendedEventsAPI = BASE_RESTURI + "/parent_attended_events?parent_id=" + parent_id;
        eventsList = new ArrayList<Event>();

        try {
            getResponse(attendedEventsAPI);
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
                        event.setVacancy(-100);
                        event.setCategory(item.getString("category"));
                        event.setDuration(item.getString("duration_in_hour"));
                        event.setRegistered(item.getBoolean("registered"));
                        event.setAttended(item.getBoolean("attended"));

                        eventsList.add(event);
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

    public void joinEvent(Event event,String parent_id) {

        String joinEventsAPI = BASE_RESTURI + "/parent_register_event?parent_id="+parent_id+"&event_id="+event.getEventId();

        try {
            postResponse(joinEventsAPI,null,"post");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unjoinEvent(Event event,String parent_id) {

        String unjoinEventsAPI = BASE_RESTURI + "/parent_unjoin_event?parent_id="+parent_id+"&event_id="+event.getEventId();

        try {
            postResponse(unjoinEventsAPI,null,"post");
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

    private void postResponse(String url,JSONObject input, String method) {

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

    private void asyncPostResponse(String url,JSONObject input, String method) {

            PostResponseTask postRespObj = new PostResponseTask(url,input,method);
            postRespObj.start();
            try {
                System.out.println("Main Thread Waiting for response...");
            } catch (Exception e) {
                e.printStackTrace();
            }
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
                    isPostSubmitted = true;

            } catch (Exception e ) {
                isPostSubmitted = false;
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
