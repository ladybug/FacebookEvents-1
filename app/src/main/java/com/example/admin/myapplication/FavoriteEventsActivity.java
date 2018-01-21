package com.example.admin.myapplication;

import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class FavoriteEventsActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    EventAdapter adapter;
    List<Event> eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_events);

        //eventList = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.favorite_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get events from FacebookManager here
        FacebookManager fbManager = new FacebookManager();
        fbManager.getPlaceIds();
        //eventList = new ArrayList<Event>(fbManager.getEventList());

        //eventList = fbManager.getEventList();
        eventList = FacebookEvents.eventList;

        //List<Event> eventList = fbManager.getEventList();

        for (int i = 0; i < eventList.size(); ++i) {
            Event ev = eventList.get(i);
           // Toast.makeText(getApplicationContext(), ev.getTitle(), Toast.LENGTH_SHORT).show();
        }

        /*
        eventList.add(
                new Event (
                    1,
                    "Title goes here",
                    "Description here",
                    34.5
        ));

        eventList.add(
                new Event (
                        2,
                        "Title 2 goes here",
                        "Description 2 here",
                        98
                ));
        */

        //! TODO update this activity to always get new events

        adapter = new EventAdapter(this, eventList);
        recyclerView.setAdapter(adapter);
    }
}
