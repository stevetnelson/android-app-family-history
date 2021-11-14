package com.example.client;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import androidx.fragment.app.FragmentActivity;

public class SettingsActivity extends FragmentActivity {

    ToggleButton lifeStoryLinesButton;
    ToggleButton familyTreeLinesButton;
    ToggleButton spouseLinesButton;
    ToggleButton fatherSideButton;
    ToggleButton motherSideButton;
    ToggleButton maleEventButton;
    ToggleButton femaleEventButton;
    Button logoutButton;
    DataCache dataCache = DataCache.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        lifeStoryLinesButton = findViewById(R.id.lifeStoryLinesButton);
        familyTreeLinesButton = findViewById(R.id.familyTreeLinesButton);
        spouseLinesButton = findViewById(R.id.spouseLinesButton);
        fatherSideButton = findViewById(R.id.fatherSideButton);
        motherSideButton = findViewById(R.id.mothersSideButton);
        maleEventButton = findViewById(R.id.maleEventButton);
        femaleEventButton = findViewById(R.id.femaleEventButton);
        logoutButton = findViewById(R.id.logoutButton);

        if (dataCache.isShowLifeStoryLines()) {
            lifeStoryLinesButton.setChecked(true);
        }
        if (dataCache.isShowFamilyTreeLines()) {
            familyTreeLinesButton.setChecked(true);
        }
        if (dataCache.isShowSpouseLines()) {
            spouseLinesButton.setChecked(true);
        }
        if (dataCache.isShowFatherSide()) {
            fatherSideButton.setChecked(true);
        }
        if (dataCache.isShowMotherSide()) {
            motherSideButton.setChecked(true);
        }
        if (dataCache.isShowMales()) {
            maleEventButton.setChecked(true);
        }
        if (dataCache.isShowFemales()) {
            femaleEventButton.setChecked(true);
        }

        lifeStoryLinesButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataCache.setShowLifeStoryLines(isChecked);
            }
        });
        familyTreeLinesButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataCache.setShowFamilyTreeLines(isChecked);
            }
        });
        spouseLinesButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataCache.setShowSpouseLines(isChecked);
            }
        });
        fatherSideButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataCache.setShowFatherSide(isChecked);
            }
        });
        motherSideButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataCache.setShowMotherSide(isChecked);
            }
        });
        maleEventButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataCache.setShowMales(isChecked);
            }
        });
        femaleEventButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataCache.setShowFemales(isChecked);
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataCache.setLoggedIn(false);
                finish();
            }
        });

    }

}