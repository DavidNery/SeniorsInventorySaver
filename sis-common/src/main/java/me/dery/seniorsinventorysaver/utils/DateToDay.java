package me.dery.seniorsinventorysaver.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateToDay {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static String dateToDay(Date date) {
        return dateFormat.format(date);
    }

    public static Date dayToDate(String day) {
        try {
            return dateFormat.parse(day);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new Date();
    }

    public static String today() {
        return dateFormat.format(new Date());
    }

}
