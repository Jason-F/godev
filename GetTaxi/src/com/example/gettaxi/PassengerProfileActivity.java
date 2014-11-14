package com.example.gettaxi;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PassengerProfileActivity extends Activity
{
  public TaxiRequest taxiRequest;
  
  protected void onCreate(Bundle savedInstanceState)
  {
    Log.v("TAXICAB", "STARTED PASSENGER PROFILE");
    super.onCreate(savedInstanceState);
    setContentView(R.layout.passengerprofileactivity);

    taxiRequest = new TaxiRequest();
    Bundle extras = getIntent().getExtras();
    taxiRequest.phone = extras.getString("phone");
    taxiRequest.latitude = extras.getString("latitude");
    taxiRequest.longitude = extras.getString("longitude");
    taxiRequest.address = extras.getString("address");
    
    ((TextView) findViewById(R.id.phoneTextView)).setText("Phone " + taxiRequest.phone);
    ((TextView) findViewById(R.id.gpsTextView)).setText("Coordinates: [ "+ taxiRequest.latitude +", " + taxiRequest.longitude + " ]");
    ((TextView) findViewById(R.id.addressTextView)).setText("Address: " + taxiRequest.address);
    
    Log.v("TAXICAB", "DONE");
    
    update();
   }
  
  public void update()
  {
    TelephonyManager tMgr =(TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
    String phone = tMgr.getLine1Number();
    
    TaxiRequestsXMLParser p = new TaxiRequestsXMLParser();
    String url = "http://mozamagic.net/taxi/getTaxiRequestByDriverPhone/?driverPhone=" + phone;
    List<TaxiRequest> requests = p.parse(url);
    Log.v("TAXICAB", url);
    
    
    if (requests.size() == 0)
    {
      
      Log.v("TAXICAB", "NOT WORK");
      return;
    }
    else
    {
      Log.v("TAXICAB", "WORKs");
      ((Button) findViewById(R.id.pickupPassengerButton)).setVisibility(8);
    }
    
  }
  
  public void goBackButton_Click(View v)
  {
    finish();
  }
  
  public void callPassengerButton_Click(View v)
  {
    String uri = "tel:" + taxiRequest.phone.trim() ;
    Intent intent = new Intent(Intent.ACTION_CALL);
    intent.setData(Uri.parse(uri));
    startActivity(intent);
  }
  
  public void pickupPassengerButton_Click(View v)
  {
    TelephonyManager tMgr =(TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
    String driverPhone = tMgr.getLine1Number();
    String params = "?driverPhone=" + driverPhone + "&phone=" + taxiRequest.phone;
    String url = "http://mozamagic.net/taxi/pickupPassengerByPhone/" + params;

    try
    {
      HttpClient client = new DefaultHttpClient();
      HttpGet get = new HttpGet(url);
      HttpResponse response = client.execute(get);
      ((TextView) findViewById(R.id.pickupPassengerButton)).setVisibility(8);
    }
    catch(IOException e)
    {
    }
  }
  
  
}
