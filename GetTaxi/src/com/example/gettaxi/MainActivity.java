package com.example.gettaxi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends Activity
{
  private static final String TAG = "TAXICAB";
  
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.startactivity);
  }
  
  public void passengerButton_Click(View v)
  {
    Intent intent = new Intent(this, PassengerActivity.class);
    startActivity(intent);
  }
  
  public void driverButton_Click(View v)
  {
    Intent intent = new Intent(this, DriverActivity.class);
    startActivity(intent);
  }
}
