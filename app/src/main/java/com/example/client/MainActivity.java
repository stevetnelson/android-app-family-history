package com.example.client;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends FragmentActivity {
    private LoginFragment loginFragment;
    private MapFragment mapFragment;
    private DataCache dataCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataCache = DataCache.getInstance();
        setContentView(R.layout.activity_main);
        inflateProperFragment();
    }

    public void setLoggedIn(boolean loggedIn) {
        dataCache.setLoggedIn(loggedIn);
        if (loggedIn == true) {
            deflateLogin();
            inflateProperFragment();
        }
        else if (loggedIn == false) {
            deflateMap();
            inflateProperFragment();
        }
    }

    public void inflateProperFragment() {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        if (!dataCache.isLoggedIn()) {
            loginFragment = (LoginFragment) fragmentManager.findFragmentById(R.id.loginFrame);
            if (loginFragment == null) {
                loginFragment = new LoginFragment();
                fragmentManager.beginTransaction().add(R.id.loginFrame, loginFragment).commit();
            }
        }
        else {
            mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.mapFrame);
            if (mapFragment == null) {
                mapFragment = new MapFragment();
                fragmentManager.beginTransaction().add(R.id.mapFrame, mapFragment).commit();
            }
        }
    }

    public void deflateLogin() {
        if (loginFragment != null) {
            FragmentManager fragmentManager = this.getSupportFragmentManager();
            fragmentManager.beginTransaction().remove(loginFragment).commit();
            loginFragment = null;
        }
    }

    public void deflateMap() {
        if (mapFragment != null) {
            FragmentManager fragmentManager = this.getSupportFragmentManager();
            fragmentManager.beginTransaction().remove(mapFragment).commit();
            mapFragment = null;
        }
    }

    public void inflateActivity(String activityToCreate) {
        Intent intent;
        switch (activityToCreate) {
            case "settings":
                intent = new Intent(this, SettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
            case "search":
                intent = new Intent(this, SearchActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
            case "person":
                intent = new Intent(this, PersonActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
            default:
                return;
        }
        startActivity(intent);
    }
}