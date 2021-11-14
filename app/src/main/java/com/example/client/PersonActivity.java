package com.example.client;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import Result.EventSingleResult;
import Result.PersonSingleResult;

public class PersonActivity extends AppCompatActivity {
    DataCache dataCache = DataCache.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        PersonSingleResult person = dataCache.getPersonForActivity();
        String genderString = (person.getGender().equals("m")) ? "Male" : "Female";
        TextView firstName = findViewById(R.id.person_activity_first_name);
        firstName.setText(person.getFirstName());
        TextView lastName = findViewById(R.id.person_activity_last_name);
        lastName.setText(person.getLastName());
        TextView gender = findViewById(R.id.person_activity_gender);
        gender.setText(genderString);

        ArrayList<PersonSingleResult> personList = new ArrayList<>();
        ArrayList<String> relationList = new ArrayList<>();
        for(Map.Entry< PersonSingleResult, String> entry : dataCache.getFamilyMembers(person.getPersonID()).entrySet()) {
            personList.add(entry.getKey());
            relationList.add(entry.getValue());
        }

        ExpandableListView expandableListView = findViewById(R.id.expandableListView);
        expandableListView.setAdapter(new ExpandableListAdapter(personList, dataCache.getPersonAvailableEventsOrdered(person.getPersonID()), relationList));
    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter {
        public static final int EVENT_ITEM_VIEW_TYPE = 0;
        public static final int PERSON_ITEM_VIEW_TYPE = 1;

        private final List<EventSingleResult> events;
        private final List<PersonSingleResult> people;
        private final List<String> relations;

        ExpandableListAdapter(List<PersonSingleResult> people, List<EventSingleResult> events, List<String> relations) {
            this.people = people;
            this.events = events;
            this.relations = relations;
        }

        @Override
        public int getGroupCount() { return 2; }

        @Override
        public int getChildrenCount(int groupPosition) {
            switch (groupPosition) {
                case EVENT_ITEM_VIEW_TYPE:
                    return events.size();
                case PERSON_ITEM_VIEW_TYPE:
                    return people.size();
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            switch (groupPosition) {
                case EVENT_ITEM_VIEW_TYPE:
                    return "Life Events";
                case PERSON_ITEM_VIEW_TYPE:
                    return "Family";
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            switch (groupPosition) {
                case EVENT_ITEM_VIEW_TYPE:
                    return events.get(childPosition);
                case PERSON_ITEM_VIEW_TYPE:
                    return people.get(childPosition);
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public long getGroupId(int groupPosition) { return groupPosition; }

        @Override
        public long getChildId(int groupPosition, int childPosition) { return childPosition; }

        @Override
        public boolean hasStableIds() { return false; }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_group, parent, false);
            }

            TextView titleView = convertView.findViewById(R.id.list_title);

            switch (groupPosition) {
                case EVENT_ITEM_VIEW_TYPE:
                    titleView.setText("Life Events");
                    break;
                case PERSON_ITEM_VIEW_TYPE:
                    titleView.setText("Family");
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View itemView = getLayoutInflater().inflate(R.layout.expandable_item, parent, false);

            switch (groupPosition) {
                case EVENT_ITEM_VIEW_TYPE:
                    initializeEventView(itemView, childPosition);
                    break;
                case PERSON_ITEM_VIEW_TYPE:
                    initializePersonView(itemView, childPosition);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
            return itemView;
        }

        private void initializeEventView(View itemView, final int childPosition) {
            TextView topRow = itemView.findViewById(R.id.expandable_item_top);
            topRow.setText(events.get(childPosition).getEventType().toUpperCase() + ": "
                    + events.get(childPosition).getCountry() + ", " + events.get(childPosition).getCountry()
                    + " (" + Integer.toString(events.get(childPosition).getYear()) + ")");

            TextView name = itemView.findViewById(R.id.expandable_item_bottom);
            name.setText(dataCache.getPersonByID(events.get(childPosition).getPersonID()).getFirstName()
                    + " " + dataCache.getPersonByID(events.get(childPosition).getPersonID()).getLastName());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dataCache.setEventSelectedEvent(events.get(childPosition));
                    dataCache.setEventActivity(true);
                    Intent intent = new Intent(PersonActivity.this, EventActivity.class);
                    startActivity(intent);
                }
            });
        }

        private void initializePersonView(View itemView, final int childPosition) {
            TextView name = itemView.findViewById(R.id.expandable_item_top);
            name.setText(people.get(childPosition).getFirstName() + " " + people.get(childPosition).getLastName() +
                    " (" + people.get(childPosition).getGender().toUpperCase() + ")");

            TextView relation = itemView.findViewById(R.id.expandable_item_bottom);
            relation.setText(relations.get(childPosition).toUpperCase());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dataCache.setPersonForActivity(people.get(childPosition));
                    Intent intent = new Intent(PersonActivity.this, PersonActivity.class);
                    startActivity(intent);
                }
            });
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) { return true; }

    }
}