package com.ibm.bluebridge.util;

import com.ibm.bluebridge.valueobject.Event;

import java.util.Calendar;

/**
 * Created by manirm on 10/31/2015.
 */
public class Validator {

    private static boolean compareStartEndTime(Calendar startTime, Calendar endTime) {
        return startTime.after(endTime);
    }

    private static boolean isPastYear(int selectedYear) {
        Calendar currentCal = Calendar.getInstance();
        return selectedYear < currentCal.get(Calendar.YEAR);
    }

    public static String validate(Event event) {
        boolean isInValid = false;
        String msg = null;

        int[] eventDate = Utils.splitDate(event.getEventDate());
        int year = eventDate[0];
        int month = eventDate[1];
        int day = eventDate[2];

        int[] startTime = Utils.splitTime(event.getStartTime());
        int hour = startTime[0];
        int minute = startTime[1];
        Calendar startTimeCal = Calendar.getInstance();
        startTimeCal.set(year,month,day,hour,minute);

        int[] endTime = Utils.splitTime(event.getEndTime());
        hour = endTime[0];
        minute = endTime[1];
        Calendar endTimeCal = Calendar.getInstance();
        endTimeCal.set(year, month, day, hour, minute);

        isInValid = Validator.isPastYear(year);
        if (isInValid) {
            return ErrorCodes.DATE_ERROR.getMessage();
        }

        isInValid = Validator.compareStartEndTime(startTimeCal,endTimeCal);
        if(isInValid) {
            return ErrorCodes.TIME_ERROR.getMessage();
        }

        return ErrorCodes.SUCCESS.getMessage();
    }

    private enum ErrorCodes {
        DATE_ERROR("Selected Event Year is over"),
        TIME_ERROR("Start Time is greater than End Time"),
        SUCCESS("");

        private String msg;

        ErrorCodes(String msg) {
            this.msg = msg;
        }

        public String getMessage() {
            return msg;
        }
    }
}
