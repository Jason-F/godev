package com.example.gettaxi;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class DriverProfileActivity extends Activity
{
  public TaxiDriver driver;
  
  protected void onCreate(Bundle savedInstanceState)
  {
    Log.v("TAXICAB", "STARTED");
    super.onCreate(savedInstanceState);
    setContentView(R.layout.driverprofileactivity);
    Log.v("TAXICAB", "DONE");
    
    driver = new TaxiDriver();

    Bundle extras = getIntent().getExtras();
    driver.phone = extras.getString("phone");
    driver.name = extras.getString("name");
    driver.latitude = extras.getString("latitude");
    driver.longitude = extras.getString("longitude");
    driver.numPassed = extras.getString("numPassed");
    driver.numFailed = extras.getString("numFailed");
    driver.address = extras.getString("address");
    Log.v("TAXICAB", "DONE 2");
    
    ((TextView) findViewById(R.id.nameTextView)).setText("Name: " + driver.name);
    ((TextView) findViewById(R.id.phoneTextView)).setText("Phone " + driver.phone);
    ((TextView) findViewById(R.id.gpsTextView)).setText("Coordinates: [ "+ driver.latitude +", " + driver.longitude + " ]");
    ((TextView) findViewById(R.id.goodTextView)).setText("Good Reviews: " + driver.numPassed);
    ((TextView) findViewById(R.id.badTextView)).setText("Bad Reviews: " + driver.numFailed);
    ((TextView) findViewById(R.id.addressTextView)).setText("Address: " + driver.address);
   }
  
  public void goBackButton_Click(View v)
  {
    finish();
  }
  
  public void callDriverButton_Click(View v)
  {
    String uri = "tel:" + driver.phone.trim() ;
    Intent intent = new Intent(Intent.ACTION_CALL);
    intent.setData(Uri.parse(uri));
    startActivity(intent);
  }
  
  
}
