package com.example.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import Result.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ModelTest {

    @BeforeEach
    public void resetDataCache() {
        DataCache.getInstance().resetInstance();
    }

    @Test
    public void calcFamilyRelationshipsSuccess() {
        ArrayList<PersonSingleResult> people = new ArrayList<>();
        people.add(new PersonSingleResult(null, true, "user", "user", "userFirst", "userLast",
                "m", "father", "mother", "spouse"));
        people.add(new PersonSingleResult(null, true, "user", "spouse", "spouseFirst", "spouseLast",
                "f", null, null, "user"));
        people.add(new PersonSingleResult(null, true, "user", "father", "fatherFirst", "fatherLast",
                "m", null, null, "mother"));
        people.add(new PersonSingleResult(null, true, "user", "mother", "motherFirst", "motherLast",
                "f", null, null, "father"));
        DataCache dataCache = DataCache.getInstance();
        dataCache.setUser(people.get(0));
        dataCache.parsePeople(new PersonFamilyResult("", true, people));
        HashMap<PersonSingleResult, String> familyMembers = dataCache.getFamilyMembers(dataCache.getUser().getPersonID());
        assertEquals(familyMembers.get(people.get(1)), "spouse");
        assertEquals(familyMembers.get(people.get(2)), "father");
        assertEquals(familyMembers.get(people.get(3)), "mother");
    }

    @Test
    public void calcFamilyRelationshipsAbnormal() {
        ArrayList<PersonSingleResult> people = new ArrayList<>();
        people.add(new PersonSingleResult(null, true, "user", "user", "userFirst", "userLast",
                "m", null, null, null));
        DataCache dataCache = DataCache.getInstance();
        dataCache.setUser(people.get(0));
        dataCache.parsePeople(new PersonFamilyResult("", true, people));
        HashMap<PersonSingleResult, String> familyMembers = dataCache.getFamilyMembers(dataCache.getUser().getPersonID());
        assertEquals(familyMembers.size(), 0);
    }

    @Test
    public void filterEventsSuccess() {
        ArrayList<PersonSingleResult> people = new ArrayList<>();
        people.add(new PersonSingleResult(null, true, "user", "user", "userFirst", "userLast",
                "m", "father", "mother", null));
        people.add(new PersonSingleResult(null, true, "user", "father", "fatherFirst", "fatherLast",
                "m", null, null, "mother"));
        people.add(new PersonSingleResult(null, true, "user", "mother", "motherFirst", "motherLast",
                "f", null, null, "father"));
        DataCache dataCache = DataCache.getInstance();
        dataCache.setUser(people.get(0));
        dataCache.parsePeople(new PersonFamilyResult("", true, people));
        ArrayList<EventSingleResult> events = new ArrayList<>();
        events.add(new EventSingleResult(null, true, "user", "userBirth", "user", 10, 10,
                "Sweden", "Stockholm", "birth", 2000));
        events.add(new EventSingleResult(null, true, "user", "fatherBirth", "father", 10, 10,
                "Sweden", "Stockholm", "birth", 1980));
        events.add(new EventSingleResult(null, true, "user", "motherBirth", "mother", 10, 10,
                "Sweden", "Stockholm", "birth", 1980));
        dataCache.parseEvents(new EventFamilyResult("", true, events));
        dataCache.setShowMales(false);
        ArrayList<EventSingleResult> filteredEvents = dataCache.getAllAvailableEvents();
        assertEquals(filteredEvents.size(), 1);
        assertEquals(filteredEvents.get(0).getPersonID(), "mother");
    }

    @Test
    public void filterEventsAbnormal() {
        ArrayList<PersonSingleResult> people = new ArrayList<>();
        people.add(new PersonSingleResult(null, true, "user", "user", "userFirst", "userLast",
                "m", "father", "mother", null));
        people.add(new PersonSingleResult(null, true, "user", "father", "fatherFirst", "fatherLast",
                "m", null, null, "mother"));
        people.add(new PersonSingleResult(null, true, "user", "mother", "motherFirst", "motherLast",
                "f", null, null, "father"));
        DataCache dataCache = DataCache.getInstance();
        dataCache.setUser(people.get(0));
        dataCache.parsePeople(new PersonFamilyResult("", true, people));
        ArrayList<EventSingleResult> events = new ArrayList<>();
        events.add(new EventSingleResult(null, true, "user", "userBirth", "user", 10, 10,
                "Sweden", "Stockholm", "birth", 2000));
        events.add(new EventSingleResult(null, true, "user", "fatherBirth", "father", 10, 10,
                "Sweden", "Stockholm", "birth", 1980));
        events.add(new EventSingleResult(null, true, "user", "motherBirth", "mother", 10, 10,
                "Sweden", "Stockholm", "birth", 1980));
        dataCache.parseEvents(new EventFamilyResult("", true, events));
        dataCache.setShowMales(false);
        dataCache.setShowFemales(false);
        ArrayList<EventSingleResult> filteredEvents = dataCache.getAllAvailableEvents();
        assertEquals(filteredEvents.size(), 0);
    }

    @Test
    public void sortIndividEventsSuccess() {
        ArrayList<PersonSingleResult> people = new ArrayList<>();
        people.add(new PersonSingleResult(null, true, "user", "user", "userFirst", "userLast",
                "m", null, null, null));
        DataCache dataCache = DataCache.getInstance();
        dataCache.setUser(people.get(0));
        dataCache.parsePeople(new PersonFamilyResult("", true, people));
        ArrayList<EventSingleResult> events = new ArrayList<>();
        events.add(new EventSingleResult(null, true, "user", "userBirth", "user", 10, 10,
                "Sweden", "Stockholm", "birth", 1980));
        events.add(new EventSingleResult(null, true, "user", "userParty", "user", 10, 10,
                "Sweden", "Stockholm", "party", 1980));
        events.add(new EventSingleResult(null, true, "user", "usermMrriage", "user", 10, 10,
                "Sweden", "Stockholm", "marriage", 1990));
        events.add(new EventSingleResult(null, true, "user", "userJob", "user", 10, 10,
                "Sweden", "Stockholm", "job", 2000));
        events.add(new EventSingleResult(null, true, "user", "userDeath", "user", 10, 10,
                "Sweden", "Stockholm", "death", 2000));
        dataCache.parseEvents(new EventFamilyResult("", true, events));
        ArrayList<EventSingleResult> orderedEvents = dataCache.getPersonEventsOrdered(dataCache.getUser().getPersonID());
        assertEquals(orderedEvents.size(), 5);
        assertEquals(orderedEvents.get(0).getEventType(), "birth");
        assertEquals(orderedEvents.get(1).getEventType(), "party");
        assertEquals(orderedEvents.get(2).getEventType(), "marriage");
        assertEquals(orderedEvents.get(3).getEventType(), "job");
        assertEquals(orderedEvents.get(4).getEventType(), "death");
    }

    @Test
    public void sortIndividEventsAbnormal() {
        ArrayList<PersonSingleResult> people = new ArrayList<>();
        people.add(new PersonSingleResult(null, true, "user", "user", "userFirst", "userLast",
                "m", null, null, null));
        DataCache dataCache = DataCache.getInstance();
        dataCache.setUser(people.get(0));
        dataCache.parsePeople(new PersonFamilyResult("", true, people));
        ArrayList<EventSingleResult> orderedEvents = dataCache.getPersonEventsOrdered(dataCache.getUser().getPersonID());
        assertEquals(orderedEvents.size(), 0);
    }

    @Test
    public void searchSuccess() {
        ArrayList<PersonSingleResult> people = new ArrayList<>();
        people.add(new PersonSingleResult(null, true, "user", "user", "userFirst", "userLast",
                "m", "father", "mother", null));
        people.add(new PersonSingleResult(null, true, "user", "father", "fatherFirst", "fatherLast",
                "m", null, null, "mother"));
        people.add(new PersonSingleResult(null, true, "user", "mother", "motherFirst", "motherLast",
                "f", null, null, "father"));
        DataCache dataCache = DataCache.getInstance();
        dataCache.setUser(people.get(0));
        dataCache.parsePeople(new PersonFamilyResult("", true, people));
        ArrayList<EventSingleResult> events = new ArrayList<>();
        events.add(new EventSingleResult(null, true, "user", "userBirth", "user", 10, 10,
                "Sweden", "Stockholm", "birth", 2000));
        events.add(new EventSingleResult(null, true, "user", "fatherBirth", "father", 10, 10,
                "Sweden", "Stockholm", "birth", 1980));
        events.add(new EventSingleResult(null, true, "user", "motherBirth", "mother", 10, 10,
                "Sweden", "Stockholm", "birth", 1980));
        dataCache.parseEvents(new EventFamilyResult("", true, events));
        ArrayList<PersonSingleResult> searchResultPeople = dataCache.searchPeopleToShow("h");
        ArrayList<EventSingleResult> searchResultEvents = dataCache.searchEventsToShow("2000");
        assertEquals(searchResultPeople.size(), 2);
        assertEquals(searchResultPeople.get(0).getPersonID(), "father");
        assertEquals(searchResultPeople.get(1).getPersonID(), "mother");
        assertEquals(searchResultEvents.size(), 1);
        assertEquals(searchResultEvents.get(0).getPersonID(), "user");
    }

    @Test
    public void searchAbnormal() {
        DataCache dataCache = DataCache.getInstance();
        ArrayList<PersonSingleResult> searchResultPeople = dataCache.searchPeopleToShow("a");
        ArrayList<EventSingleResult> searchResultEvents = dataCache.searchEventsToShow("a");
        assertEquals(searchResultPeople.size(), 0);
        assertEquals(searchResultEvents.size(), 0);
    }
}