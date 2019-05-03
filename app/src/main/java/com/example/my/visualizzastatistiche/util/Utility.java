package com.example.my.visualizzastatistiche.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utility {

    public static String getMonthFromNumber(Date date){
        DateFormat fmt = new SimpleDateFormat("dd MMMM", Locale.ITALY);
        String data = fmt.format(date);
        return data;
    }
}
