package com.ibm.bluebridge.valueobject;

import java.util.List;

/**
 * Created by manirm on 10/30/2015.
 */
public class Parent extends User {

    private List<Children> children;
    private String job;
    private String address;
    private boolean hasAttended;

    public boolean isAttended() {
        return hasAttended;
    }

    public void setHasAttended(boolean hasAttended) {
        this.hasAttended = hasAttended;
    }

    public List<Children> getChildren() {
        return children;
    }

    public void setChildren(List<Children> children) {
        this.children = children;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

