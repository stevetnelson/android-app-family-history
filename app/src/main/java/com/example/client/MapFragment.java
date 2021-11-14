package com.example.client;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import java.util.ArrayList;
import java.util.Set;
import Result.EventSingleResult;
import Result.PersonSingleResult;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap map;
    private TextView eventPersonName;
    private TextView eventGender;
    private TextView eventType;
    private TextView eventLocation;
    private boolean hasBeenCreated = false;
    private final DataCache dataCache = DataCache.getInstance();
    private EventSingleResult selectedEvent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (!dataCache.isEventActivity()) {
            setHasOptionsMenu(true);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mainSearchButton:
                ((MainActivity) getActivity()).inflateActivity("search");
                return true;
            case R.id.mainSettingsButton:
                ((MainActivity) getActivity()).inflateActivity("settings");
                resetMap();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(layoutInflater, container, savedInstanceState);
        View view = layoutInflater.inflate(R.layout.fragment_map, container, false);
        eventPersonName = view.findViewById(R.id.eventPersonName);
        eventGender = view.findViewById(R.id.eventGender);
        eventType= view.findViewById(R.id.eventType);
        eventLocation = view.findViewById(R.id.eventLocation);
        LinearLayout eventInfoHolder = view.findViewById(R.id.eventInfoHolder);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        eventInfoHolder.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (dataCache.isEventActivity()) {
                    ((EventActivity) getActivity()).inflateActivity();
                }
                else if (selectedEvent != null) {
                    ((MainActivity) getActivity()).inflateActivity("person");
                }
            }
        });

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if(selectedEvent != null) {
            map.clear();
            displayMarkers();
            displayEventInfo(selectedEvent);
            setMarkerLines();
            LatLng latLng = new LatLng(selectedEvent.getLatitude(), selectedEvent.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }
        else if (dataCache.isEventActivity()) {
            selectedEvent = dataCache.getEventSelectedEvent();
            map.clear();
            displayMarkers();
            displayEventInfo(selectedEvent);
            setMarkerLines();
            LatLng latLng = new LatLng(selectedEvent.getLatitude(), selectedEvent.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }
        else {
            displayMarkers();
        }

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                selectedEvent = (EventSingleResult) marker.getTag();
                if (!dataCache.isEventActivity()) {
                    dataCache.setMainSelectedEvent(selectedEvent);
                }
                dataCache.setPersonForActivity(dataCache.getPersonByID(selectedEvent.getPersonID()));
                map.clear();
                displayMarkers();
                displayEventInfo(selectedEvent);
                setMarkerLines();
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!dataCache.isEventActivity()) {
            if (!hasBeenCreated) {
                hasBeenCreated = true;
                if (dataCache.getMainSelectedEvent() != null) {
                    selectedEvent = dataCache.getMainSelectedEvent();
                    dataCache.setPersonForActivity(dataCache.getPersonByID(selectedEvent.getPersonID()));
                }
            }
            else {
                resetMap();
            }
            if (!dataCache.isLoggedIn()) {
                ((MainActivity) getActivity()).setLoggedIn(false);
            }
        }
    }

    @Override
    public void onDestroy() {
        if (dataCache.isEventActivity()) {
            dataCache.setEventActivity(false);
        }
        super.onDestroy();
    }

    private void setMarkerLines() {
        PersonSingleResult person = dataCache.getPersonByID(selectedEvent.getPersonID());
        if (person.getSpouseID() != null && dataCache.isShowSpouseLines()) {
            if (dataCache.genderIsShowable(person.getSpouseID())) {
                EventSingleResult spouseEvent = dataCache.findBirthEvent(person.getSpouseID());
                if (spouseEvent != null) {
                    map.addPolyline(new PolylineOptions()
                            .add(new LatLng(selectedEvent.getLatitude(), selectedEvent.getLongitude()), new LatLng(spouseEvent.getLatitude(), spouseEvent.getLongitude()))
                            .width(16)
                            .color(Color.RED));
                }
            }
        }
        if (dataCache.genderIsShowable(person.getPersonID()) && dataCache.isShowLifeStoryLines()) {
            ArrayList<EventSingleResult> personEventsOrdered = dataCache.getPersonEventsOrdered(person.getPersonID());
            int width = 16;
            EventSingleResult prevEvent;
            if (personEventsOrdered.size() > 0) {
                prevEvent = personEventsOrdered.get(0);
                for (int i = 1; i < personEventsOrdered.size(); ++i) {
                    EventSingleResult currEvent = personEventsOrdered.get(i);
                    map.addPolyline(new PolylineOptions()
                            .add(new LatLng(prevEvent.getLatitude(), prevEvent.getLongitude()), new LatLng(currEvent.getLatitude(), currEvent.getLongitude()))
                            .width(width)
                            .color(Color.GREEN));
                    prevEvent = currEvent;
                    width -= 4;
                    if (width < 4) {
                        width = 2;
                    }
                }
            }
        }
        if (dataCache.isShowFamilyTreeLines()) {
            if (person.getMotherID() != null && dataCache.isShowMotherSide()) {
                EventSingleResult motherEvent = dataCache.findBirthEvent(person.getMotherID());
                if (motherEvent != null) {
                    if (dataCache.genderIsShowable(person.getMotherID())) {
                        Polyline line = map.addPolyline(new PolylineOptions()
                                .add(new LatLng(selectedEvent.getLatitude(), selectedEvent.getLongitude()), new LatLng(motherEvent.getLatitude(), motherEvent.getLongitude()))
                                .width(16)
                                .color(Color.BLUE));
                        drawFamilyLines(person.getMotherID(), 12, motherEvent, Color.BLUE);
                    }
                }
            }
            if (person.getFatherID() != null && dataCache.isShowFatherSide()) {
                EventSingleResult fatherEvent = dataCache.findBirthEvent(person.getFatherID());
                if (fatherEvent != null) {
                    if (dataCache.genderIsShowable(person.getFatherID())) {
                        map.addPolyline(new PolylineOptions()
                                .add(new LatLng(selectedEvent.getLatitude(), selectedEvent.getLongitude()), new LatLng(fatherEvent.getLatitude(), fatherEvent.getLongitude()))
                                .width(16)
                                .color(Color.YELLOW));
                        drawFamilyLines(person.getFatherID(), 12, fatherEvent, Color.YELLOW);
                    }
                }
            }
        }
    }

    private void drawFamilyLines(String personID, int width, EventSingleResult birthEvent, int color) {
        PersonSingleResult person = dataCache.getPersonByID(personID);
        if (width < 4) {
            width = 2;
        }
        if (person.getMotherID() != null) {
            EventSingleResult motherEvent = dataCache.findBirthEvent(person.getMotherID());
            if (motherEvent != null) {
                if (dataCache.genderIsShowable(person.getMotherID())) {
                    map.addPolyline(new PolylineOptions()
                            .add(new LatLng(birthEvent.getLatitude(), birthEvent.getLongitude()), new LatLng(motherEvent.getLatitude(), motherEvent.getLongitude()))
                            .width(width)
                            .color(color));
                    drawFamilyLines(person.getMotherID(), width - 4, motherEvent, color);
                }
            }
        }
        if (person.getFatherID() != null) {
            EventSingleResult fatherEvent = dataCache.findBirthEvent(person.getFatherID());
            if (fatherEvent != null) {
                if (dataCache.genderIsShowable(person.getFatherID())) {
                    map.addPolyline(new PolylineOptions()
                            .add(new LatLng(birthEvent.getLatitude(), birthEvent.getLongitude()), new LatLng(fatherEvent.getLatitude(), fatherEvent.getLongitude()))
                            .width(width)
                            .color(color));
                    drawFamilyLines(person.getFatherID(), width - 4, fatherEvent, color);
                }
            }
        }
    }

    private void displayEventInfo(EventSingleResult event) {
        String name = dataCache.getPersonByID(event.getPersonID()).getFirstName() + " " + dataCache.getPersonByID(event.getPersonID()).getLastName();
        eventPersonName.setText(name);
        String gender = dataCache.getPersonByID(event.getPersonID()).getGender();
        if (gender.equals("m")) {
            eventGender.setText("(Male)");
            eventGender.setTextColor(Color.parseColor("#6495ED"));
        }
        else {
            eventGender.setText("(Female)");
            eventGender.setTextColor(Color.parseColor("#FFC0CB"));
        }
        eventType.setText(event.getEventType().toUpperCase());
        eventLocation.setText(event.getCity() + ", " + event.getCountry() + " (" + event.getYear() + ")");

        LatLng latLng = new LatLng(event.getLatitude(), event.getLongitude());
        map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    private void displayMarkers() {
        if (dataCache.isShowFatherSide() && dataCache.isShowMales()) {
            displayGroupMarker(dataCache.getFatherSideMales());
        }
        if (dataCache.isShowFatherSide() && dataCache.isShowFemales()) {
            displayGroupMarker(dataCache.getFatherSideFemales());
        }
        if (dataCache.isShowMotherSide() && dataCache.isShowMales()) {
            displayGroupMarker(dataCache.getMotherSideMales());
        }
        if (dataCache.isShowMotherSide() && dataCache.isShowFemales()) {
            displayGroupMarker(dataCache.getMotherSideFemales());
        }
        if ((dataCache.getUser().getGender().equals("m") && dataCache.isShowMales()) || (dataCache.getUser().getGender().equals("f") && dataCache.isShowFemales())) {
            for(EventSingleResult event : dataCache.getEventsOfPerson(dataCache.getUser().getPersonID())) {
                LatLng latLng = new LatLng(event.getLatitude(), event.getLongitude());
                Marker marker = map.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(dataCache.getEventColor(event.getEventType().toLowerCase()))));
                marker.setTag(event);
            }
        }
        if (dataCache.getSpouse() != null) {
            if ((dataCache.getSpouse().getGender().equals("m") && dataCache.isShowMales()) || (dataCache.getSpouse().getGender().equals("f") && dataCache.isShowFemales())) {
                for(EventSingleResult event : dataCache.getEventsOfPerson(dataCache.getSpouse().getPersonID())) {
                    LatLng latLng = new LatLng(event.getLatitude(), event.getLongitude());
                    Marker marker = map.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(dataCache.getEventColor(event.getEventType().toLowerCase()))));
                    marker.setTag(event);
                }
            }
        }
    }

    private void displayGroupMarker(Set<PersonSingleResult> groupToShow) {
        for (PersonSingleResult person : groupToShow) {
            for(EventSingleResult event : dataCache.getEventsOfPerson(person.getPersonID())) {
                LatLng latLng = new LatLng(event.getLatitude(), event.getLongitude());
                Marker marker = map.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(dataCache.getEventColor(event.getEventType().toLowerCase()))));
                marker.setTag(event);
            }
        }
    }

    public void resetMap() {
        map.clear();
        displayMarkers();
        if (selectedEvent != null) {
            displayEventInfo(selectedEvent);
            for (EventSingleResult myEvent : dataCache.getAllAvailableEvents()) {
                if (myEvent.getEventID().equals(selectedEvent.getEventID())) {
                    setMarkerLines();
                    break;
                }
            }
        }
    }

}
