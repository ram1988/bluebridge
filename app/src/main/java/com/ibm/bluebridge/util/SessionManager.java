package com.ibm.bluebridge.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.ibm.bluebridge.EventLoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by huangq on 4/11/2015.
 * Singleton class for login session
 */
public class SessionManager {
    private static SessionManager sessionInstance;

    private RESTApi REST_API;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Boolean isLoggedIn = false;
    private Context ctxt;

    // User session value KEY in Shared Preference
    private String userIdKey = "id";
    private String userRoleKey = "role";
    private String userGenderKey = "gender";
    private String userFirstNameKey = "firstname";
    private String userLastNameKey = "lastname";
    private String userEmailKey = "email";
    private String userContactKey = "contact";
    private String userJobKey = "job";
    private String userAddrKey = "address";
    private String userChildrenKey = "children";
    private String userChildIdKey = "id";
    private String userChildNameKey = "name";
    private String userChildGenderKey = "gender";
    private String userChildRegistrationYearKey = "registration_year";
    private String userChildBirthDateKey = "birth_date";



    // User session fields
    private String firstname = "";
    private String lastname = "";
    private String userId = "";
    private String userRole = "";
    private String userGender = "";
    private String userContact = "";
    private String userEmail = "";
    private String userJob = "";
    private String userAddr = "";


    private String childId = "";
    private String childName = "";
    private String childGender = "";
    private String childRegistrationYear = "";
    private String childBirthDate = "";


    private SessionManager(Context ctxt){
        this.REST_API = new RESTApi();
        this.ctxt = ctxt;
        this.pref = ctxt.getSharedPreferences(CONSTANTS.PREFERENCE_NAME, 0); // 0 - for private mode
        this.editor = pref.edit();
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
                setUserEmail(item.getString(userEmailKey));
                setUserContact(item.getString(userContactKey));

                if(userRole.equals("parent")){
                    setUserJob(item.getString(userJobKey));
                    setUserAddr(item.getString(userAddrKey));

                    JSONArray children = item.getJSONArray(userChildrenKey);
                    JSONObject child = children.getJSONObject(0);  //For now, we assume only 1 child.

                    setChildId(child.getString(userChildIdKey));
                    setChildName(child.getString(userChildNameKey));
                    setChildGender(child.getString(userChildGenderKey));
                    setChildRegistrationYear(child.getString(userChildRegistrationYearKey));
                    setChildBirthDate(child.getString(userChildBirthDateKey));
                }

                Log.d("SessionManager", "Role--->" + userRole);
                isLoggedIn = true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            isLoggedIn = false;
            return isLoggedIn;
        }
        catch(Exception e) {
            e.printStackTrace();
            isLoggedIn = false;
            return isLoggedIn;
        }

        return isLoggedIn;
    }

    public boolean isLoggedIn(){
        return isLoggedIn;
    }

    // Safe Logout
    public boolean logout(){
        editor.clear();
        editor.commit(); // commit changes

        isLoggedIn = false;

        Intent loginPage = new Intent(ctxt, EventLoginActivity.class);
        loginPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctxt.startActivity(loginPage);

        return true;
    }

    public boolean isAdmin(){
        return getUserRole().equals("admin");
    }

    public boolean isParent(){
        return getUserRole().equals("parent");
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
        editor.putString(userFirstNameKey, firstname);
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
        editor.putString(userLastNameKey, lastname);
    }

    public String getUserJob() {
        return userJob;
    }

    public void setUserJob(String userJob) {
        this.userJob = userJob;
        editor.putString(userJobKey, userJob);
    }

    public String getUserAddr() {
        return userAddr;
    }

    public void setUserAddr(String userAddr) {
        this.userAddr = userAddr;
        editor.putString(userAddrKey, userAddr);
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
        editor.putString(userChildNameKey, childName);
    }

    public String getChildGender() {
        return childGender;
    }

    public void setChildGender(String childGender) {
        this.childGender = childGender;
        editor.putString(userChildGenderKey, childGender);
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
        editor.putString(userChildIdKey, childId);
    }

    public String getChildRegistrationYear() {
        return childRegistrationYear;
    }

    public void setChildRegistrationYear(String childRegistrationYear) {
        this.childRegistrationYear = childRegistrationYear;
        editor.putString(userChildRegistrationYearKey, childRegistrationYear);
    }

    public String getChildBirthDate() {
        return childBirthDate;
    }

    public void setChildBirthDate(String childBirthDate) {
        this.childBirthDate = childBirthDate;
        editor.putString(userChildBirthDateKey, childBirthDate);
    }

    public String getUserContact() {
        return userContact;
    }

    public void setUserContact(String userContact) {
        this.userContact = userContact;
        editor.putString(userContactKey, userContact);
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
        editor.putString(userEmailKey, userEmail);
    }
}
