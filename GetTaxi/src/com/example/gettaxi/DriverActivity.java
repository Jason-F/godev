package com.example.gettaxi;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class DriverActivity extends Activity
{
  
  private static final String TAG = "TAXICAB";
  private static final int UPDATE_ADDRESS = 1;
  private Handler geoHandler;
  private LocationManager locationManager;
  private double longitude = -1;
  private double latitude = -1;
  private String address = "";
  ArrayAdapter<TaxiRequest> adapter;
  private List<TaxiRequest> taxiRequests;
  private ListView taxiRequestsListView;
  private TaxiRequest taxiRequest;
  
  private LocationListener locationListener = new LocationListener()
  {
    public void onLocationChanged(Location location) {
      Log.v(TAG, "START ONLOCATIONCHANGED");
      if (location != null)
      {
        updateUILocation(location);
      }
      Log.v(TAG, "END ONLOCATIONCHANGED");
    }
    public void onProviderDisabled(String provider) {}
    public void onProviderEnabled(String provider) {}
    public void onStatusChanged(String provider, int status, Bundle extras){}
  };
  
  public void goBackButton_Click(View v)
  {
    finish();
  }
  
  public void getNearbyTaxiRequestsButton_Click(View v)
  {
    String longitudeText = Double.toString(longitude);
    String latitudeText = Double.toString(latitude);
    
    TelephonyManager tMgr =(TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
    String phone = tMgr.getLine1Number();
    
    longitudeText = URLEncoder.encode(longitudeText);
    latitudeText = URLEncoder.encode(latitudeText);
    
    String params = "?latitude=" + latitudeText + "&longitude=" + longitudeText;
    String url_string = "http://mozamagic.net/taxi/getNearbyTaxiRequests/" + params;
    
    taxiRequests.clear();
    
    try
    {
      XMLParser parser = new XMLParser();
      String xml = parser.getXmlFromUrl(url_string); // getting XML
      Log.v("TAXICAB", "PARSE: " + url_string);
      Document doc = parser.getDomElement(xml); // getting DOM element
       
      NodeList nl = doc.getElementsByTagName("taxiRequest");
       
      for (int i = 0; i < nl.getLength(); i++)
      {
        Log.v(TAG, "LOOPING");
        TaxiRequest r = new TaxiRequest();
        Element e = (Element) nl.item(i);
        r.taxiRequest_id = e.getElementsByTagName("taxiRequest_id").item(0).getTextContent();
        r.latitude = e.getElementsByTagName("latitude").item(0).getTextContent();
        r.longitude = e.getElementsByTagName("longitude").item(0).getTextContent();
        r.phone = e.getElementsByTagName("phone").item(0).getTextContent();
        r.address = e.getElementsByTagName("address").item(0).getTextContent();
        r.driverPhone = e.getElementsByTagName("driverPhone").item(0).getTextContent();
        taxiRequests.add(r);
      }
      Log.v(TAG, "THANK YOU");
    }
    catch (Exception e)
    {
      Log.v(TAG, "NOOoo");
    }
    
    adapter.notifyDataSetChanged();
  }
  

  
  private TextView coordinatesTextView = null;
  private TextView addressTextView = null;
  private TextView getNearbyTaxiRequestsButton = null;
  
  protected void onStart()
  {
    super.onStart();
    TelephonyManager tMgr =(TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
    String phone = tMgr.getLine1Number();
    String url = "http://mozamagic.net/taxi/getTaxiRequestByDriverPhone?driverPhone=" + phone;
    TaxiRequestsXMLParser p = new TaxiRequestsXMLParser();
    List<TaxiRequest> requests = p.parse(url);
    if (requests.size() > 0)
    {
      taxiRequest = requests.get(0);
      ((Button) findViewById(R.id.toPickupButton)).setVisibility(0);
    }
    else
    {
      ((Button) findViewById(R.id.toPickupButton)).setVisibility(8);
    }
    taxiRequests.clear();
    adapter.notifyDataSetChanged();  
  }
  
  
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    geoHandler = new Handler(){
      public void handleMessage(Message msg){
        switch (msg.what) {
        case UPDATE_ADDRESS:
          address = (String) msg.obj;
          addressTextView.setText(address);
          break;
          }
        }
      };
      
    super.onCreate(savedInstanceState);
    setContentView(R.layout.driveractivity);
    coordinatesTextView = (TextView) findViewById(R.id.coordinatesTextView);
    coordinatesTextView.setText("GPS Coordinates");
    addressTextView = (TextView) findViewById(R.id.addressTextView);
    addressTextView.setText("Address Text View");
    getNearbyTaxiRequestsButton = (Button) findViewById(R.id.getNearbyTaxiRequestsButton);   
    taxiRequestsListView = (ListView) findViewById(R.id.taxiRequestsListView);
    
    locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, (long).1, 0, locationListener);
    
    Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    if (loc != null)
    {
      updateUILocation(loc);
    }
    
    taxiRequests = new ArrayList<TaxiRequest>();
    adapter = new PassengerAdapter(this, R.layout.passengerlistviewitemrow, taxiRequests);
    taxiRequestsListView.setAdapter(adapter);
    adapter.notifyDataSetChanged();
    
    taxiRequestsListView.setOnItemClickListener(new OnItemClickListener() {
      public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng)
      {
        TaxiRequest r = taxiRequests.get(myItemInt);
        goToPassengerProfile(r);
      }                 
      });
    
  }
  
  public void toPickupButton_Click(View v)
  {
    Log.v(TAG, "STARTING THIS UP");
    Intent intent = new Intent(this, PassengerProfileActivity.class);
    intent.putExtra("latitude", taxiRequest.latitude);
    intent.putExtra("longitude", taxiRequest.longitude);
    intent.putExtra("address", taxiRequest.address);
    intent.putExtra("phone", taxiRequest.phone);
    intent.putExtra("driverPhone", taxiRequest.driverPhone);
    intent.putExtra("taxiRequest_id", taxiRequest.taxiRequest_id); 
    startActivity(intent);
    Log.v(TAG, "YO");  
  }
  
  public void goToPassengerProfile(TaxiRequest r)
  {
    Log.v(TAG, "STARTING THIS UP");
    Intent intent = new Intent(this, PassengerProfileActivity.class);
    intent.putExtra("latitude", r.latitude);
    intent.putExtra("longitude", r.longitude);
    intent.putExtra("address", r.address);
    intent.putExtra("phone", r.phone);
    intent.putExtra("driverPhone", r.driverPhone);
    intent.putExtra("taxiRequest_id", r.taxiRequest_id); 
    startActivity(intent);
    Log.v(TAG, "YO");
  }
  
  public void updateUILocation(Location location)
  {
    Log.v(TAG, "START UPDATEUILOCATION");
    latitude = location.getLatitude();
    longitude = location.getLongitude();
    coordinatesTextView.setText("[" + Double.toString(latitude) + ", " + Double.toString(longitude) + "]");
    (new ReverseGeocodingTask(this)).execute(new Location[] {location});
    Log.v(TAG, "END UPDATEUILOCATION");
  }
  
  public class ReverseGeocodingTask extends AsyncTask<Location, Void, Void> {
    Context mContext;
  
    public ReverseGeocodingTask(Context context)
    {
        super();
        mContext = context;
    }
  
    @Override
    protected Void doInBackground(Location... params) {
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
  
        Location loc = params[0];
        List<Address> addresses = null;
        try {
            // Call the synchronous getFromLocation() method by passing in the lat/long values.
            addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
            // Update UI field with the exception.
            Message.obtain(geoHandler, UPDATE_ADDRESS, "Unable to determine address.").sendToTarget();
        }
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            // Format the first line of address (if available), city, and country name.
            String addressText = String.format("%s, %s, %s",
                    address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                    address.getLocality(),
                    address.getCountryName());
            // Update the UI via a message handler.
            Message.obtain(geoHandler, UPDATE_ADDRESS, addressText).sendToTarget();
        }
        return null;
    }
  }    
  
}
