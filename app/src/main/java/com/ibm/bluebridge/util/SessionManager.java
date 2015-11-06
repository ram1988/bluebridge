package com.ibm.bluebridge.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.ibm.bluebridge.EventAdminHomeTabActivity;
import com.ibm.bluebridge.EventParentViewActivity;
import com.ibm.bluebridge.R;
import com.ibm.bluebridge.adapter.EventsAdapter;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by huangq on 4/11/2015.
 * Singleton class for login session
 */
public class SessionManager {
    private static SessionManager sessionInstance;
    private RESTApi REST_API;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private Boolean isLoggedIn = false;

    // User session value KEY in Shared Preference
    private String userIdKey = "nric";
    private String userRoleKey = "role";
    private String userGenderKey = "role";
    private String userFirstNameKey = "gender";
    private String userLastNameKey = "gender";
    private String userJobKey = "gender";
    private String userAddrKey = "gender";
    private String userChildFirstNameKey = "gender";
    private String userChildLastNameKey = "gender";



    // User session fields
    private String firstname = "";
    private String lastname = "";
    private String userId = "";
    private String userRole = "";
    private String userGender = "";
    private String userJob = "";
    private String userAddr = "";

    private String childFirstName = "";
    private String childLastName = "";


    private SessionManager(Context ctxt){
        REST_API = new RESTApi();
        pref = ctxt.getSharedPreferences(CONSTANTS.PREFERENCE_NAME, 0); // 0 - for private mode
        editor = pref.edit();
    }

    public static SessionManager getSessionInstance(Context ctxt){
        if(sessionInstance == null)
            sessionInstance = new SessionManager(ctxt);
        return sessionInstance;
    }

    public Boolean doLogin(String nric, String password, String device_id) {
        String loginAPI = REST_API.getBaseRestURL() + "/login";

        JSONObject loginInfo = new JSONObject();

        try {
            loginInfo.put("nric", nric);
            loginInfo.put("password", password);
            loginInfo.put("device_id", device_id);

            REST_API.postResponse(loginAPI, loginInfo, "post");
            JSONObject obj = REST_API.getRespObj();
            Object response = null;
            if(obj != null && obj.has("response"))
                response = obj.get("response");

            if(response != null ) {
                JSONObject item = (JSONObject) response;
                setUserId(nric);
                setUserRole(item.getString(userRoleKey));
                setFirstname(item.getString(userFirstNameKey));
                setLastname(item.getString(userLastNameKey));
                setUserGender(item.getString(userGenderKey));
                setUserJob(item.getString(userJobKey));
                setUserAddr(item.getString(userAddrKey));

                Log.d("SessionManager", "Role--->" + userRole);
                isLoggedIn = true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return isLoggedIn;
    }

    public boolean logout(){
        editor.clear();
        editor.commit(); // commit changes

        isLoggedIn = false;
        
        return true;
    }

    /*
     *  Getters and Setters
     */
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
        editor.putString(userIdKey, userId);
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
        editor.putString(userRoleKey, userRole);
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
        editor.putString(userGenderKey, userGender);
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
        editor.putString(userFirstNameKey, userGender);
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
        editor.putString(userLastNameKey, userGender);
    }

    public String getUserJob() {
        return userJob;
    }

    public void setUserJob(String userJob) {
        this.userJob = userJob;
        editor.putString(userJobKey, userGender);
    }

    public String getUserAddr() {
        return userAddr;
    }

    public void setUserAddr(String userAddr) {
        this.userAddr = userAddr;
        editor.putString(userAddrKey, userGender);
    }

    public String getChildFirstName() {
        return childFirstName;
    }

    public void setChildFirstName(String childFirstName) {
        this.childFirstName = childFirstName;
        editor.putString(userChildFirstNameKey, userGender);
    }

    public String getChildLastName() {
        return childLastName;
    }

    public void setChildLastName(String childLastName) {
        this.childLastName = childLastName;
        editor.putString(userChildLastNameKey, userGender);
    }
}
