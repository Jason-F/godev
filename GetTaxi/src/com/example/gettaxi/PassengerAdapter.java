package com.example.gettaxi;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PassengerAdapter extends ArrayAdapter<TaxiRequest>
{
  Context context; 
  List<TaxiRequest> taxiRequests;
  
  public PassengerAdapter(Context context, int layoutResourceId, List<TaxiRequest> taxiRequests2) {
      super(context, layoutResourceId, taxiRequests2);
      this.context = context;
      this.taxiRequests = taxiRequests2;
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    
    View rowView = inflater.inflate(R.layout.passengerlistviewitemrow, parent, false);
    TextView textView = (TextView) rowView.findViewById(R.id.addressTextView);
    textView.setText(taxiRequests.get(position).address);
    TextView phoneTextView = (TextView) rowView.findViewById(R.id.phoneTextView);
    phoneTextView.setText("Phone: " + taxiRequests.get(position).phone);
    TextView gpsTextView = (TextView) rowView.findViewById(R.id.gpsTextView);
    String str = "GPS: [ " + taxiRequests.get(position).latitude + ", " + taxiRequests.get(position).longitude + " ]";
    Log.v("TAXICAB", taxiRequests.get(position).longitude);
    gpsTextView.setText(str);  
    return rowView;
  }
  
}
