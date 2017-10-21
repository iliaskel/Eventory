package com.example.android.eventory.Home;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.eventory.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ikelasid on 10/12/2017.
 */

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {
    private static final String TAG = "HomeAdapter";

    List<EventInformation> mEventsList;

    public HomeAdapter(List<EventInformation> eventsList){
        Log.d(TAG, "HomeAdapter: entered");
        mEventsList=eventsList;
    }

    @Override
    public HomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context=parent.getContext();
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.list_item_event,parent,false);
        HomeViewHolder viewHolder=new HomeViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(HomeViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: "+mEventsList.size());
        return mEventsList.size();
    }

    public class HomeViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "HomeViewHolder";

        TextView mEventName,mEventPlace,mEventDate,mEventDistance,mEventType;
        public HomeViewHolder(View itemView) {
            super(itemView);
            mEventName=(TextView)itemView.findViewById(R.id.tv_event_name);
            mEventPlace=(TextView)itemView.findViewById(R.id.tv_event_place);
            mEventDate=(TextView)itemView.findViewById(R.id.tv_event_date);
            mEventDistance=(TextView)itemView.findViewById(R.id.tv_event_distance);
            mEventType=(TextView)itemView.findViewById(R.id.tv_event_type);
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
        }
    }
}
