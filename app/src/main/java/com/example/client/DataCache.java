package com.example.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import Result.EventFamilyResult;
import Result.EventSingleResult;
import Result.PersonFamilyResult;
import Result.PersonSingleResult;

public class DataCache {

    private static DataCache instance;
    private PersonSingleResult user;
    private PersonSingleResult spouse = null;
    private ArrayList<PersonSingleResult> allPeople;
    private ArrayList<EventSingleResult> allEvents;
    private final Set<PersonSingleResult> fatherSideMales = new HashSet<>();
    private final Set<PersonSingleResult> fatherSideFemales = new HashSet<>();
    private final Set<PersonSingleResult> motherSideMales = new HashSet<>();
    private final Set<PersonSingleResult> motherSideFemales = new HashSet<>();
    private final Set<String> eventType = new HashSet<>();
    private final Map<String, Integer> eventColors = new HashMap<>();
    private final Map<String, PersonSingleResult> personMap = new HashMap<>();
    private final Map<String, Set<EventSingleResult>> eventMap = new HashMap<>();
    private boolean showMotherSide = true;
    private boolean showFatherSide = true;
    private boolean showMales = true;
    private boolean showFemales = true;
    private boolean showSpouseLines = true;
    private boolean showFamilyTreeLines = true;
    private boolean showLifeStoryLines = true;
    private boolean isLoggedIn = false;
    private EventSingleResult mainSelectedEvent = null;
    private PersonSingleResult personForActivity = null;
    private boolean isEventActivity = false;
    private EventSingleResult eventSelectedEvent = null;

    private DataCache() {}

    public static DataCache getInstance() {
        if (instance == null) {
            instance = new DataCache();
        }
        return instance;
    }

    public void resetInstance() {
        user = null;
        spouse = null;
        allPeople = null;
        allEvents = null;
        fatherSideMales.clear();
        fatherSideFemales.clear();
        motherSideMales.clear();
        motherSideFemales.clear();
        eventType.clear();
        eventColors.clear();
        personMap.clear();
        eventMap.clear();
        showMotherSide = true;
        showFatherSide = true;
        showMales = true;
        showFemales = true;
        showSpouseLines = true;
        showFamilyTreeLines = true;
        showLifeStoryLines = true;
        isLoggedIn = false;
        mainSelectedEvent = null;
        personForActivity = null;
        isEventActivity = false;
        eventSelectedEvent = null;
    }

    public void setUser(PersonSingleResult personSingleResult) {
        user = personSingleResult;
        personMap.put(personSingleResult.getPersonID(), personSingleResult);
    }

    public void parsePeople(PersonFamilyResult personFamilyResult) {
        allPeople = personFamilyResult.getData();
        if (user.getSpouseID() != null) {
            personMap.put(user.getSpouseID(), findPerson(user.getSpouseID()));
            spouse = findPerson(user.getSpouseID());
        }
        if (user.getFatherID() != null) {
            parseFamilyLine(findPerson(user.getFatherID()), fatherSideMales, fatherSideFemales);
        }
        if (user.getMotherID() != null) {
            parseFamilyLine(findPerson(user.getMotherID()), motherSideMales, motherSideFemales);
        }
    }

    private void parseFamilyLine(PersonSingleResult person, Set<PersonSingleResult> maleSet, Set<PersonSingleResult> femaleSet) {
        if (person.getGender().equals("m")) {
            maleSet.add(person);
        }
        else {
            femaleSet.add(person);
        }
        if (person.getFatherID() != null) {
            parseFamilyLine(findPerson(person.getFatherID()), maleSet, femaleSet);
        }
        if (person.getMotherID() != null) {
            parseFamilyLine(findPerson(person.getMotherID()), maleSet, femaleSet);
        }
        personMap.put(person.getPersonID(), person);
    }

    private PersonSingleResult findPerson(String IDToFind) {
        PersonSingleResult personResult = null;
        for (int i = 0; i < allPeople.size(); ++i) {
            if (IDToFind.equals(allPeople.get(i).getPersonID())) {
                personResult = allPeople.get(i);
                break;
            }
        }
        return personResult;
    }

    public void parseEvents(EventFamilyResult eventFamilyResult) {
        allEvents = eventFamilyResult.getData();
        for (int i = 0; i < allEvents.size(); ++i) {
            if (eventMap.get(allEvents.get(i).getPersonID()) != null) {
                eventMap.get(allEvents.get(i).getPersonID()).add(allEvents.get(i));
            }
            else {
                Set<EventSingleResult> eventSet = new HashSet<>();
                eventSet.add(allEvents.get(i));
                eventMap.put(allEvents.get(i).getPersonID(), eventSet);
            }
            eventType.add(allEvents.get(i).getEventType());
        }
        setEventColors();
    }

    private void setEventColors() {
        Integer counter = 0;
        for (String thisEvent : eventType) {
            thisEvent = thisEvent.toLowerCase();
            eventColors.put(thisEvent, counter % 360);
            counter = counter + 60;
        }
    }

    public Set<EventSingleResult> getEventsOfPerson(String personID) {
        return eventMap.get(personID);
    }

    public EventSingleResult findPersonsEvent(String personID, String eventType) {
        Set<EventSingleResult> events = eventMap.get(personID);
        for (EventSingleResult event : events) {
            if (event.getEventType().equals(eventType)) {
                return event;
            }
        }
        return null;
    }

    public ArrayList<EventSingleResult> getPersonEventsOrdered(String personID) {
        if ((eventMap != null) && (eventMap.get(personID) != null)) {
            Set<EventSingleResult> personEvents = new HashSet<>(eventMap.get(personID));
            ArrayList<EventSingleResult> returnEvents = new ArrayList<>();
            EventSingleResult birthEvent = findPersonsEvent(personID, "birth");
            if (birthEvent != null) {
                returnEvents.add(birthEvent);
                personEvents.remove(birthEvent);
            }
            while (personEvents.size() > 0) {
                birthEvent = null;
                for (EventSingleResult event : personEvents) {
                    if (!(event.getEventType().equals("death") && (personEvents.size() != 1))) {
                        if (birthEvent == null) {
                            birthEvent = event;
                        }
                        if (event.getYear() < birthEvent.getYear() || ((event.getYear() == birthEvent.getYear())
                                && (event.getEventType().toLowerCase().charAt(0) < birthEvent.getEventType().toLowerCase().charAt(0)))) {
                            birthEvent = event;
                        }
                    }
                }
                returnEvents.add(birthEvent);
                personEvents.remove(birthEvent);
            }
            return returnEvents;
        }
        else {
            return new ArrayList<EventSingleResult>();
        }
    }

    public ArrayList<EventSingleResult> getAllAvailableEvents() {
        ArrayList<EventSingleResult> retEvents = new ArrayList<>();
        if (showMotherSide) {
            if (showMales) {
                for (PersonSingleResult person : motherSideMales) {
                    retEvents.addAll(getEventsOfPerson(person.getPersonID()));
                }
            }
            if (showFemales) {
                for (PersonSingleResult person : motherSideFemales) {
                    retEvents.addAll(getEventsOfPerson(person.getPersonID()));
                }
            }
        }
        if (showFatherSide) {
            if (showMales) {
                for (PersonSingleResult person : fatherSideMales) {
                    retEvents.addAll(getEventsOfPerson(person.getPersonID()));
                }
            }
            if (showFemales) {
                for (PersonSingleResult person : fatherSideFemales) {
                    retEvents.addAll(getEventsOfPerson(person.getPersonID()));
                }
            }
        }
        if ((user.getGender().equals("m") && showMales) || (user.getGender().equals("f") && showFemales)) {
            retEvents.addAll(getEventsOfPerson(user.getPersonID()));
        }
        return retEvents;
    }

    public ArrayList<EventSingleResult> getPersonAvailableEventsOrdered(String personID) {
        ArrayList<EventSingleResult> retEvents = new ArrayList<>();
        ArrayList<EventSingleResult> allAvailEvents = getAllAvailableEvents();
        for (EventSingleResult eventToCheck : getPersonEventsOrdered(personID)) {
            for (EventSingleResult event : allAvailEvents) {
                if (event.getEventID().equals(eventToCheck.getEventID())) {
                    retEvents.add(eventToCheck);
                    break;
                }
            }
        }
        return retEvents;
    }

    public HashMap<PersonSingleResult, String> getFamilyMembers(String personID) {
        PersonSingleResult person = getPersonByID(personID);
        HashMap<PersonSingleResult, String> result = new HashMap<>();
        for (PersonSingleResult famPerson : allPeople) {
            if (findFamily(famPerson, person.getPersonID()) != null) {
                result.put(famPerson, findFamily(famPerson, person.getPersonID()));
            }
        }
        if (person.getFatherID() != null) {
            result.put(getPersonByID(person.getFatherID()), "father");
        }
        if (person.getMotherID() != null) {
            result.put(getPersonByID(person.getMotherID()), "mother");
        }
        if (person.getSpouseID() != null) {
            result.put(getPersonByID(person.getSpouseID()), "spouse");
        }
        return result;
    }

    private String findFamily(PersonSingleResult person, String idToFind) {
        if (person.getFatherID() != null && person.getFatherID().equals(idToFind)) {
            return "child";
        }
        else if (person.getMotherID() != null && person.getMotherID().equals(idToFind)) {
            return "child";
        }
        else {
            return null;
        }
    }

    public ArrayList<PersonSingleResult> searchPeopleToShow(String text) {
        ArrayList<PersonSingleResult> people = new ArrayList<>();
        String tempString;
        if (allPeople != null) {
            for (int i = 0; i < allPeople.size(); ++i) {
                tempString = (allPeople.get(i).getFirstName() + " " + allPeople.get(i).getLastName()).toUpperCase();
                if (tempString.contains(text.toUpperCase())) {
                    people.add(allPeople.get(i));
                }
            }
        }
        return people;
    }

    public ArrayList<EventSingleResult> searchEventsToShow(String text) {
        text = text.toUpperCase();
        ArrayList<EventSingleResult> events = new ArrayList<>();
        if (allEvents != null) {
            ArrayList<EventSingleResult> tempEvents = getAllAvailableEvents();
            String tempString;
            for (int i = 0; i < tempEvents.size(); ++i) {
                tempString = (tempEvents.get(i).getCity().toUpperCase() + ", " + tempEvents.get(i).getCountry().toUpperCase() +
                        " (" + tempEvents.get(i).getYear() + ")").toUpperCase();
                if (tempString.contains(text) || tempEvents.get(i).getEventType().toUpperCase().contains(text)) {
                    events.add(tempEvents.get(i));
                }
            }
        }
        return events;
    }

    public EventSingleResult findBirthEvent(String personID) {
        getEventsOfPerson(personID);
        EventSingleResult birthEvent = findPersonsEvent(personID, "birth");
        if (birthEvent != null) {
            return birthEvent;
        }
        for (EventSingleResult event : getEventsOfPerson(personID)) {
            if (birthEvent == null) {
                birthEvent = event;
            }
            if (event.getYear() < birthEvent.getYear()) {
                birthEvent = event;
            }
        }
        return birthEvent;
    }

    public boolean genderIsShowable(String personID) {
        if ((getPersonByID(personID).getGender().equals("m") && showMales) ||
                (getPersonByID(personID).getGender().equals("f") && showFemales)) {
            return true;
        }
        return false;
    }

    public PersonSingleResult getPersonByID(String IDOfPerson) {
        return personMap.get(IDOfPerson);
    }

    public Integer getEventColor(String eventType) {
        return eventColors.get(eventType);
    }

    public PersonSingleResult getUser() {
        return user;
    }

    public boolean isShowMotherSide() {
        return showMotherSide;
    }

    public void setShowMotherSide(boolean showMotherSide) {
        this.showMotherSide = showMotherSide;
    }

    public boolean isShowFatherSide() {
        return showFatherSide;
    }

    public void setShowFatherSide(boolean showFatherSide) {
        this.showFatherSide = showFatherSide;
    }

    public boolean isShowMales() { return showMales; }

    public void setShowMales(boolean showMales) {
        this.showMales = showMales;
    }

    public boolean isShowFemales() { return showFemales; }

    public void setShowFemales(boolean showFemales) { this.showFemales = showFemales; }

    public boolean isShowSpouseLines() { return showSpouseLines; }

    public void setShowSpouseLines(boolean showSpouseLines) { this.showSpouseLines = showSpouseLines; }

    public boolean isShowFamilyTreeLines() { return showFamilyTreeLines; }

    public void setShowFamilyTreeLines(boolean showFamilyTreeLines) { this.showFamilyTreeLines = showFamilyTreeLines; }

    public boolean isShowLifeStoryLines() { return showLifeStoryLines; }

    public void setShowLifeStoryLines(boolean showLifeStoryLines) { this.showLifeStoryLines = showLifeStoryLines; }

    public Set<PersonSingleResult> getFatherSideMales() { return fatherSideMales; }

    public Set<PersonSingleResult> getFatherSideFemales() { return fatherSideFemales; }

    public Set<PersonSingleResult> getMotherSideMales() { return motherSideMales; }

    public Set<PersonSingleResult> getMotherSideFemales() { return motherSideFemales; }

    public PersonSingleResult getSpouse() { return spouse; }

    public boolean isLoggedIn() { return isLoggedIn; }

    public void setLoggedIn(boolean loggedIn) { isLoggedIn = loggedIn; }

    public EventSingleResult getMainSelectedEvent() { return mainSelectedEvent; }

    public void setMainSelectedEvent(EventSingleResult mainSelectedEvent) { this.mainSelectedEvent = mainSelectedEvent; }

    public PersonSingleResult getPersonForActivity() { return personForActivity; }

    public void setPersonForActivity(PersonSingleResult personForActivity) { this.personForActivity = personForActivity; }

    public boolean isEventActivity() { return isEventActivity; }

    public void setEventActivity(boolean eventActivity) { isEventActivity = eventActivity; }

    public EventSingleResult getEventSelectedEvent() { return eventSelectedEvent; }

    public void setEventSelectedEvent(EventSingleResult eventSelectedEvent) { this.eventSelectedEvent = eventSelectedEvent; }
}
