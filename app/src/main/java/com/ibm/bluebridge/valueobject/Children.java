package com.ibm.bluebridge.valueobject;

/**
 * Created by manirm on 11/3/2015.
 */
public class Children extends BlueBridgeVO{
    private String id;
    private String name;
    private String regYear;
    private String gender;
    private String birthDate;

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRegistrationYear() {
        return regYear;
    }

    public void setRegistrationYear(String regYear) {
        this.regYear = regYear;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}
