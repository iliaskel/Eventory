package com.example.android.eventory;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.EventLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.eventory.Activities.EventsActivity;
import com.example.android.eventory.Signingformation.EventInformation;

import java.util.List;

import javax.xml.datatype.Duration;

/**
 * Created by ikelasid on 10/12/2017.
 */

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.HomeViewHolder> {
    private static final String TAG = "EventsAdapter";
    final private EventItemClickListener mOnClickListener;

    private List<EventInformation> mEventsList;

    public EventsAdapter(List<EventInformation> eventsList, EventItemClickListener listener){
        Log.d(TAG, "EventsAdapter: entered");
        mEventsList=eventsList;
        mOnClickListener = listener;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context=parent.getContext();
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.list_item_event,parent,false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: "+mEventsList.size());
        return mEventsList.size();
    }

    public class HomeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private static final String TAG = "HomeViewHolder";

        TextView mEventName,mEventPlace,mEventDate,mEventDistance,mEventType;
        String eventID;
        HomeViewHolder(View itemView) {
            super(itemView);
            mEventName= itemView.findViewById(R.id.tv_event_name);
            mEventPlace= itemView.findViewById(R.id.tv_event_place);
            mEventDate= itemView.findViewById(R.id.tv_event_date);
            mEventDistance= itemView.findViewById(R.id.tv_event_distance);
            mEventType= itemView.findViewById(R.id.tv_event_type);

            itemView.setOnClickListener(this);
        }

        public void bind(int position) {
            Log.d(TAG, "bind: ");
            EventInformation event;
            if((event=mEventsList.get(position))==null)
                return;

            mEventName.setText(event.getEvent_name());
            mEventPlace.setText(event.getPlace_name());
            mEventType.setText(event.getType());
            mEventDistance.setVisibility(View.INVISIBLE);
            mEventDate.setText(event.getDate());
            eventID = event.getEventID();
            Log.d(TAG, "bind: eventID :: " + eventID);
        }


        @Override
        public void onClick(View view) {
            mOnClickListener.onEventItemClickListener(view,eventID);
        }
    }

    public interface EventItemClickListener{
        void onEventItemClickListener(View clickedEvent,String eventID);
    }
}
