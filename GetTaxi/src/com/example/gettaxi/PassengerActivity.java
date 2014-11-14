package com.example.gettaxi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class PassengerActivity extends Activity
{
  private static final String TAG = "TAXICAB";
  private static final int UPDATE_ADDRESS = 1;
  private Handler geoHandler;
  private LocationManager locationManager;
  private double longitude = -1;
  private double latitude = -1;
  private String address = "";
  private List<TaxiDriver> taxiDrivers;
  private ArrayAdapter<TaxiDriver> adapter;
  private ListView taxiDriversListView;
  
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
  
  public void getTaxiDriversButton_Click(View v)
  {
  
    String longitudeText = Double.toString(longitude);
    String latitudeText = Double.toString(latitude);
    
    longitudeText = URLEncoder.encode(longitudeText);
    latitudeText = URLEncoder.encode(latitudeText);
    
    String params = "?latitude=" + latitudeText + "&longitude=" + longitudeText;
    String url_string = "http://mozamagic.net/taxi/getNearbyTaxiDrivers/" + params;
    
    taxiDrivers.clear();
    
    Log.v(TAG, "GETTING PASSENGERS");
    
    try
    {
      XMLParser parser = new XMLParser();
      String xml = parser.getXmlFromUrl(url_string); // getting XML
      Document doc = parser.getDomElement(xml); // getting DOM element
       
      NodeList nl = doc.getElementsByTagName("taxiDriver");
       
      for (int i = 0; i < nl.getLength(); i++)
      {
        Log.v(TAG, "LOOPING");
        TaxiDriver d = new TaxiDriver();
        Element e = (Element) nl.item(i);
        d.latitude = e.getElementsByTagName("latitude").item(0).getTextContent();
        d.numFailed = e.getElementsByTagName("numFailed").item(0).getTextContent();
        d.numPassed = e.getElementsByTagName("numPassed").item(0).getTextContent();
        d.address = e.getElementsByTagName("address").item(0).getTextContent();   
        d.longitude = e.getElementsByTagName("longitude").item(0).getTextContent();
        d.phone = e.getElementsByTagName("phone").item(0).getTextContent();
        d.name = e.getElementsByTagName("name").item(0).getTextContent();
        taxiDrivers.add(d);
      }
      Log.v(TAG, "THANK YOU");
    }
    catch (Exception e)
    {
    }
    
    adapter.notifyDataSetChanged();
  }
  
  public void getTaxiButton_Click(View v)
  {
    
    if (getTaxiButton.getText() == "Go To Taxi Information")
    {
      Intent intent = new Intent(this, WaitingActivity.class);
      startActivity(intent);
      return;
    }
    
    String longitudeText = Double.toString(longitude);
    String latitudeText = Double.toString(latitude);
    String addressText;
    
    TelephonyManager tMgr =(TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
    String phone = tMgr.getLine1Number();
    
    longitudeText = URLEncoder.encode(longitudeText);
    latitudeText = URLEncoder.encode(latitudeText);
    addressText = URLEncoder.encode(address);
   
    String params = "?latitude=" + latitudeText + "&longitude=" + longitudeText + "&address=" + addressText + "&phone=" + phone;
    String url = "http://mozamagic.net/taxi/requestTaxi/" + params;
    String html = "";
 
    try
    {
      HttpClient client = new DefaultHttpClient();
      HttpGet get = new HttpGet(url);
      HttpResponse response = client.execute(get);
      InputStream in = response.getEntity().getContent();
      BufferedReader reader = new BufferedReader(new InputStreamReader(in));
      StringBuilder str = new StringBuilder();
      String line = null;
      while((line = reader.readLine()) != null)
      {
          str.append(line);
      }
      in.close();
      html = str.toString();
    }
    catch (Exception e)
    {
      finish();
    }

    Log.v("TAXICAB", "ABOUT TO INTENT");
    Intent intent = new Intent(this, WaitingActivity.class);
    Log.v("TAXICAB", "THE URL = " + url);
    startActivity(intent);
    Log.v("TAXICAB", "ABOUT TO INTENT");
  }
  
  private TextView coordinatesTextView = null;
  private TextView addressTextView = null;
  private TextView getTaxiButton = null;
  
  protected void onStart()
  {
    super.onStart();
    TelephonyManager tMgr =(TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
    String phone = tMgr.getLine1Number();
    String url = "http://mozamagic.net/taxi/getTaxiRequestByPhone/?phone=" + phone;
    TaxiRequestsXMLParser p = new TaxiRequestsXMLParser();
    List<TaxiRequest> requests = p.parse(url);
    if (requests.size() > 0)
    {
      getTaxiButton.setText("Go To Taxi Information");
    }
    else
    {
      getTaxiButton.setText("Automatically Get Taxi");
    }
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
    setContentView(R.layout.activity_main);
    coordinatesTextView = (TextView) findViewById(R.id.coordinatesTextView);
    coordinatesTextView.setText("GPS Coordinates");
    addressTextView = (TextView) findViewById(R.id.addressTextView);
    addressTextView.setText("Address Text View");
    getTaxiButton = (Button) findViewById(R.id.getTaxiButton);
    taxiDriversListView = (ListView) findViewById(R.id.taxiDriversListView);
    
    locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, (long).1, 0, locationListener);
    
    taxiDrivers = new ArrayList<TaxiDriver>();
    adapter = new DriverAdapter(this, R.layout.driverlistviewitemrow, taxiDrivers);
    taxiDriversListView.setAdapter(adapter);
    adapter.notifyDataSetChanged();
    
    Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    if (loc != null)
    {
      updateUILocation(loc);
    }
    
    taxiDriversListView.setOnItemClickListener(new OnItemClickListener() {
      public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng)
      {
        TaxiDriver d = taxiDrivers.get(myItemInt);
        goToDriverProfile(d);
      }                 
      });
    
    Log.v("TAXICAB", "NOT CRASHED YET");
   
  }
  
  public void goToDriverProfile(TaxiDriver d)
  {
    Log.v(TAG, "STARTING THIS UP");
    Intent intent = new Intent(this, DriverProfileActivity.class);
    intent.putExtra("name", d.name);
    intent.putExtra("phone", d.phone);
    intent.putExtra("latitude", d.latitude);
    intent.putExtra("longitude", d.longitude);
    intent.putExtra("numPassed", d.numPassed);
    intent.putExtra("numFailed", d.numFailed);
    intent.putExtra("address", d.address);
    Log.v(TAG, "GOOd");
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

