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

        recyclerView = (RecyclerView) findViewById(R.id.favorite_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        eventList = FacebookEvents.eventList;

        adapter = new EventAdapter(this, eventList);
        recyclerView.setAdapter(adapter);
    }
}
