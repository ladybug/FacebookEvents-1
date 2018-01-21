package com.example.admin.myapplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 1/21/2018.
 */

public class FacebookEvents {
    static List<Event> eventList = new ArrayList<Event>();

    public static void setEventList(List<Event> eventList2) {
        eventList = eventList2;
    }
}
