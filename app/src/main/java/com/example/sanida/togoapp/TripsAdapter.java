package com.example.sanida.togoapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

class TripsAdapter extends BaseAdapter {
    Context context;
    LayoutInflater layoutInflater;
    ArrayList<Trip> trips;
    public TripsAdapter(Context con,ArrayList<Trip> trips)
    {
        context=con;
        layoutInflater = LayoutInflater.from(context);
        this.trips=trips;
    }
    @Override
    public int getCount() {
        return trips.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.trip, null, false);
            holder = new ViewHolder();
            holder.tripName = (TextView) convertView.findViewById(R.id.nameTxt);
            holder.tripStart = (TextView) convertView.findViewById(R.id.startTxt);
            holder.tripEnd = (TextView) convertView.findViewById(R.id.endTxt);
            holder.tripDate = (TextView) convertView.findViewById(R.id.dateTxt);
            holder.tripSeats = (TextView) convertView.findViewById(R.id.seatsTxt);
            holder.tripDriving = (TextView) convertView.findViewById(R.id.driving);
            holder.tripUser = (TextView) convertView.findViewById(R.id.userTxt);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Trip trip=trips.get(position);
        holder.tripName.setText(trip.getTripName());
        holder.tripStart.setText(trip.getStartLocation());
        holder.tripEnd.setText(trip.getEndLocation());
        holder.tripDate.setText(trip.getDate() + " " + trip.getTime());
        holder.tripSeats.setText(trip.getSeats());
        holder.tripDriving.setText(trip.getDriving().toString());
        holder.tripUser.setText(trip.getUser());

        return convertView;
    }
    public class ViewHolder {
        TextView tripName,tripStart,tripEnd, tripDate, tripSeats, tripDriving, tripUser;

    }
    @Override
    public Object getItem(int position) {
        return trips.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
}
