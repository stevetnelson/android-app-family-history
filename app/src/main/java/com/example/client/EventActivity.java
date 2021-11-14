package com.example.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;

public class EventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        FragmentManager fragmentManager = this.getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.mapFrameEvent);
        if (mapFragment == null) {
            mapFragment = new MapFragment();
            fragmentManager.beginTransaction().add(R.id.mapFrameEvent, mapFragment).commit();
        }
    }

    public void inflateActivity() {
        Intent intent = new Intent(this, PersonActivity.class);
        startActivity(intent);
    }
}