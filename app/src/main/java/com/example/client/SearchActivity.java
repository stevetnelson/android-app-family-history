package com.example.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import Result.EventSingleResult;
import Result.PersonSingleResult;

public class SearchActivity extends AppCompatActivity {
    public static final int PERSON_ITEM_VIEW_TYPE = 0;
    public static final int EVENT_ITEM_VIEW_TYPE = 1;
    DataCache dataCache = DataCache.getInstance();
    SearchAdapter adapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.actionSearch);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() != 0) {
                    updateShownData(newText.toUpperCase());
                }
                else {
                    resetShownData();
                }
                return false;
            }
        });
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        adapter = new SearchAdapter(new ArrayList<PersonSingleResult>(), new ArrayList<EventSingleResult>());
        recyclerView.setAdapter(adapter);
    }


    private void resetShownData() {
        adapter.events.clear();
        adapter.people.clear();
        adapter.notifyDataSetChanged();
    }


    private void updateShownData(String text) {
        adapter.events.clear();
        adapter.events.addAll(dataCache.searchEventsToShow(text));
        adapter.people.clear();
        adapter.people.addAll(dataCache.searchPeopleToShow(text));
        adapter.notifyDataSetChanged();
    }


    private class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {
        private final List<PersonSingleResult> people;
        private final List<EventSingleResult> events;


        SearchAdapter(List<PersonSingleResult> people, List<EventSingleResult> events) {
            this.people = people;
            this.events = events;
        }

        @Override
        public int getItemViewType(int position) {
            return position < people.size() ? PERSON_ITEM_VIEW_TYPE : EVENT_ITEM_VIEW_TYPE;
        }

        @NonNull
        @Override
        public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;

            if (viewType == PERSON_ITEM_VIEW_TYPE) {
                view = getLayoutInflater().inflate(R.layout.search_person_item, parent, false);
            }
            else {
                view = getLayoutInflater().inflate(R.layout.search_event_item, parent, false);
            }
            return new SearchViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
            if (position < people.size()) {
                holder.bind(people.get(position));
            }
            else {
                holder.bind(events.get(position - people.size()));
            }
        }

        @Override
        public int getItemCount() {return people.size() + events.size();}

    }

    private class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final int viewType;

        private PersonSingleResult person;
        private final TextView personGender;
        private final TextView name;

        private EventSingleResult event;
        private final TextView eventType;
        private final TextView eventCity;
        private final TextView eventCountry;
        private final TextView eventYear;


        SearchViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;

            itemView.setOnClickListener(this);

            if (viewType == PERSON_ITEM_VIEW_TYPE) {
                personGender = itemView.findViewById(R.id.search_person_gender);
                name = itemView.findViewById(R.id.search_person_name);
                eventType = null;
                eventCity = null;
                eventCountry = null;
                eventYear = null;
            }
            else {
                name = itemView.findViewById(R.id.search_event_name);
                eventType = itemView.findViewById(R.id.search_event_type);
                eventCity = itemView.findViewById(R.id.search_event_city);
                eventCountry = itemView.findViewById(R.id.search_event_country);
                eventYear = itemView.findViewById(R.id.search_event_year);
                personGender = null;
            }
        }

        private void bind(PersonSingleResult person) {
            this.person = person;
            personGender.setText("(" + person.getGender().toUpperCase() + ")");
            name.setText(person.getFirstName() + " " + person.getLastName());

        }
        private void bind(EventSingleResult event) {
            PersonSingleResult tempPerson = DataCache.getInstance().getPersonByID(event.getPersonID());
            this.event = event;
            eventType.setText(event.getEventType().toUpperCase());
            eventCity.setText(event.getCity() + ", ");
            eventCountry.setText(event.getCountry() + " ");
            eventYear.setText("(" + Integer.toString(event.getYear()) + ")");
            name.setText(tempPerson.getFirstName() + " " + tempPerson.getLastName());
        }

        @Override
        public void onClick(View view) {
            if (viewType == PERSON_ITEM_VIEW_TYPE) {
                dataCache.setPersonForActivity(person);
                Intent intent = new Intent(SearchActivity.this, PersonActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            else {
                dataCache.setEventSelectedEvent(event);
                dataCache.setEventActivity(true);
                Intent intent = new Intent(SearchActivity.this, EventActivity.class);
                startActivity(intent);
            }
        }

    }
}