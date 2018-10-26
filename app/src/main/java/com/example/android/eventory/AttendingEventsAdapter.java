package com.example.android.eventory;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.eventory.R;
import com.example.android.eventory.Signingformation.EventInformation;

import java.util.ArrayList;
import java.util.List;



public class AttendingEventsAdapter extends RecyclerView.Adapter<AttendingEventsAdapter.AttendingEventsViewHolder> {

    private static final String TAG = "Atte";
    private ArrayList<EventInformation> eventsList;

    public AttendingEventsAdapter(ArrayList<EventInformation> events){
        eventsList=events;
        Log.d(TAG, "AttendingEventsAdapter: " + events.size());
    }


    @NonNull
    @Override
    public AttendingEventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.list_item_event, parent, false);
        return new AttendingEventsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendingEventsAdapter.AttendingEventsViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: ");
        holder.bind(position);

    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: " + eventsList.size());
        return eventsList.size();
    }

    class AttendingEventsViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "AttendingViewHolder";
        TextView mEventName, mEventPlace, mEventDate, mEventDistance, mEventType;
        String eventID;

        AttendingEventsViewHolder(View itemView) {
            super(itemView);
            mEventName = itemView.findViewById(R.id.tv_event_name);
            mEventPlace = itemView.findViewById(R.id.tv_event_place);
            mEventDate = itemView.findViewById(R.id.tv_event_date);
            mEventDistance = itemView.findViewById(R.id.tv_event_distance);
            mEventType = itemView.findViewById(R.id.tv_event_type);
            Log.d(TAG, "AttendingEventsViewHolder: " + mEventName);
        }

        void bind(int position) {
            Log.d(TAG, "bind: ");
            EventInformation event;
            if ((event = eventsList.get(position)) == null)
                return;

            mEventName.setText(event.getEvent_name());
            mEventPlace.setText(event.getPlace_name());
            mEventType.setText(event.getType());
            mEventDistance.setVisibility(View.INVISIBLE);
            mEventDate.setText(event.getDate());
            eventID = event.getEventID();
            Log.d(TAG, "bind: eventID :: " + eventID);
        }
    }
}