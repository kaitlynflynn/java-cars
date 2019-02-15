package com.lambdaschool.cars;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CarLog implements Serializable
{
    // fields
    private final String text;
    private final String formattedDate;

    // constructor
    public CarLog(String text)
    {
        this.text = text;
        Date date = new Date();
        String strDateFormat = "yyyy-MM-dd hh:mm:ss a";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        formattedDate = dateFormat.format(date);
    }
}
