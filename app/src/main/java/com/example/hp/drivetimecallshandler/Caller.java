package com.example.hp.drivetimecallshandler;

import java.io.Serializable;

/**
 * Created by varunrao on 29/03/17.
 */

public class Caller implements Serializable{
    String name;
    String number;
    String time;
    Caller(String n,String n2,String t)
    {
        name=n;
        number=n2;
        time=t;
    }
}
