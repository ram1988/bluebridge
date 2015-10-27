package com.ibm.bluebridge.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by manirm on 10/26/2015.
 */
public class Utils {


    public static String convertDateFormat(String dateInString) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        String resultDate = null;

        try {
            Date date = formatter.parse(dateInString);
            resultDate =  formatter.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return resultDate;
    }

    public static int[] splitDate(String date) {
        String[] splittedDate = date.split("/");
        int[] splitDate = new int[splittedDate.length];

        splitDate[0] = Integer.parseInt(splittedDate[0]);
        splitDate[1] = Integer.parseInt(splittedDate[1]);
        splitDate[2] = Integer.parseInt(splittedDate[2]);

        //0-year,1-month,2-date
        return splitDate;
    }

    public static int[] splitTime(String time) {
        String[] splittedTime = time.split(":");
        int[] splitTime = new int[splittedTime.length];

        splitTime[0] = Integer.parseInt(splittedTime[0]);
        splitTime[1] = Integer.parseInt(splittedTime[1].substring(0,2));

        //0-year,1-month,2-date
        return splitTime;
    }
}