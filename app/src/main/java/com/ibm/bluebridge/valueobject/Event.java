package com.ibm.bluebridge.valueobject;

/**
 * Created by manirm on 10/23/2015.
 */
public class Event extends BlueBridgeVO{
    private String eventId;
    private String eventName;
    private String eventDescription;
    private String eventDate;
    private String startTime;
    private String endTime;
    private String venue;
    private String teacherInCharge;
    private String briefingTime;
    private String briefingPlace;
    private int maxVolunteers;
    private int vacancy;
    private String category;
    private String duration;
    private boolean registered;
    private boolean attended;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getTeacherInCharge() {
        return teacherInCharge;
    }

    public void setTeacherInCharge(String teacherInCharge) {
        this.teacherInCharge = teacherInCharge;
    }

    public String getBriefingPlace() {
        return briefingPlace;
    }

    public void setBriefingPlace(String briefingPlace) {
        this.briefingPlace = briefingPlace;
    }

    public int getVacancy() {
        return vacancy;
    }

    public void setVacancy(int vacancy) {
        this.vacancy = vacancy;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getMaxVolunteers() {
        return maxVolunteers;
    }

    public void setMaxVolunteers(int maxVolunteers) {
        this.maxVolunteers = maxVolunteers;
    }

    public String getBriefingTime() {
        return briefingTime;
    }

    public void setBriefingTime(String briefingTime) {
        this.briefingTime = briefingTime;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public boolean getRegistered() { return registered; }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }

    public boolean getAttended() {
        return attended;
    }

    public void setAttended(boolean attended) {
        this.attended = attended;
    }

}
