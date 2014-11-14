package com.example.gettaxi;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class DriverAdapter extends ArrayAdapter<TaxiDriver>
{
  Context context; 
  List<TaxiDriver> taxiDrivers;
  
  public DriverAdapter(Context context, int layoutResourceId, List<TaxiDriver> taxiDrivers2) {
      super(context, layoutResourceId, taxiDrivers2);
      this.context = context;
      this.taxiDrivers = taxiDrivers2;
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    
    View rowView = inflater.inflate(R.layout.driverlistviewitemrow, parent, false);
    
    TextView nameTextView = (TextView) rowView.findViewById(R.id.nameTextView);
    nameTextView.setText("Name: " + taxiDrivers.get(position).name);
    
    TextView ratingTextView = (TextView) rowView.findViewById(R.id.ratingTextView);
    String rating = "Ratings:  " + "Good: " + taxiDrivers.get(position).numPassed + "          Bad: " + taxiDrivers.get(position).numFailed;
    ratingTextView.setText(rating);
    
    if (taxiDrivers.get(position).address == "")
    {
      taxiDrivers.get(position).address = "Unknown";
    }
    
    TextView textView = (TextView) rowView.findViewById(R.id.addressTextView);
    textView.setText("Address: " + taxiDrivers.get(position).address);
    
    TextView phoneTextView = (TextView) rowView.findViewById(R.id.phoneTextView);
    phoneTextView.setText("Phone: " + taxiDrivers.get(position).phone);
    
    TextView gpsTextView = (TextView) rowView.findViewById(R.id.gpsTextView);
    String gps = "GPS: [ " + taxiDrivers.get(position).latitude + ", " + taxiDrivers.get(position).longitude + " ]";
    gpsTextView.setText(gps);  
    
    return rowView;
  }
  
  
}
