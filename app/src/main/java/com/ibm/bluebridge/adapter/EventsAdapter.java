package com.ibm.bluebridge.adapter;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import com.ibm.bluebridge.util.RESTApi;
import com.ibm.bluebridge.valueobject.Children;
import com.ibm.bluebridge.valueobject.Event;
import com.ibm.bluebridge.valueobject.Parent;

/**
 * Created by manirm on 10/10/2015.
 */
public class EventsAdapter {
    private List<Event> eventsList;
    private Context ctxt;
    private RESTApi REST_API;

    public EventsAdapter() {
        this.REST_API = new RESTApi();
    }

    public EventsAdapter(Context ctxt) {
        this.REST_API = new RESTApi();
        this.ctxt = ctxt;
    }

    public void markAttendance(String event_id,String parent_id) {
        String markAttendanceAPI = REST_API.getBaseRestURL() + "/admin_mark_attendance?event_id="+event_id+"&parent_id="+parent_id;

        try {
            REST_API.asyncPostResponse(markAttendanceAPI, null, "post");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }


    public List<Event> getAdminEventsList(String admin_id) {
        String allEventsAPI = REST_API.getBaseRestURL() + "/admin_list_events?admin_id="+admin_id;
        eventsList = new ArrayList<Event>();

        try {
            System.out.println("Admin URI--->" + allEventsAPI);
            REST_API.getResponse(allEventsAPI);
            Object response = REST_API.getRespObj().get("response");

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

    public List<Event> getAdminCompletedEventsList(String admin_id) {
        String allEventsAPI = REST_API.getBaseRestURL() + "/admin_list_events?past_years=2&admin_id="+admin_id;
        eventsList = new ArrayList<Event>();

        try {
            System.out.println("Admin URI--->" + allEventsAPI);
            REST_API.getResponse(allEventsAPI);
            Object response = REST_API.getRespObj().get("response");

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

    public List<Parent> getRegisteredParentsList(String event_id) {
        String registeredParentsAPI = REST_API.getBaseRestURL() + "/admin_event_list_registration?event_id="+event_id;
        List<Parent> parentsList = new ArrayList<Parent>();

        try {
            System.out.println("Admin URI--->" + registeredParentsAPI);
            REST_API.getResponse(registeredParentsAPI);
            Object response = REST_API.getRespObj().get("response");

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

        String addEventsAPI = REST_API.getBaseRestURL() + "/admin_add_event?admin_id="+admin_id;

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

            REST_API.postResponse(addEventsAPI, eventObj, "post");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void updateEvent(Event event,String admin_id) {

        String updateEventsAPI = REST_API.getBaseRestURL() + "/admin_update_event?admin_id="+admin_id+"&event_id="+event.getEventId();

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

            REST_API.postResponse(updateEventsAPI, eventObj, "post");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteEvent(Event event,String admin_id) {

        String deleteEventsAPI = REST_API.getBaseRestURL() + "/admin_cancel_event?admin_id="+admin_id+"&event_id="+event.getEventId();

        try {
            REST_API.postResponse(deleteEventsAPI, null, "delete");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*******Parent methods*********/
    public List<Event> getAllEventsList(String parent_id) {
        String allEventsAPI = REST_API.getBaseRestURL() + "/parent_get_events?parent_id=" + parent_id;
        eventsList = new ArrayList<Event>();

        try {
            REST_API.getResponse(allEventsAPI);
            Object response = REST_API.getRespObj().get("response");

            if( !response.equals("") ) {
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
        String joinedEventsAPI = REST_API.getBaseRestURL() + "/parent_joined_events?parent_id=" + parent_id;
        eventsList = new ArrayList<Event>();

        try {
            REST_API.getResponse(joinedEventsAPI);
            Object response = REST_API.getRespObj().get("response");

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
        String attendedEventsAPI = REST_API.getBaseRestURL() + "/parent_attended_events?parent_id=" + parent_id;
        eventsList = new ArrayList<Event>();

        try {
            REST_API.getResponse(attendedEventsAPI);
            Object response = REST_API.getRespObj().get("response");

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

        String joinEventsAPI = REST_API.getBaseRestURL() + "/parent_register_event?parent_id="+parent_id+"&event_id="+event.getEventId();

        try {
            REST_API.postResponse(joinEventsAPI,null,"post");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unjoinEvent(Event event,String parent_id) {

        String unjoinEventsAPI = REST_API.getBaseRestURL() + "/parent_unjoin_event?parent_id="+parent_id+"&event_id="+event.getEventId();

        try {
            REST_API.postResponse(unjoinEventsAPI, null, "post");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
