package com.example.gettaxi;

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

public class WaitingActivity extends Activity
{
  private TaxiRequest taxiRequest;
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    Log.v("TAXICAB", "WAITING 1");
    setContentView(R.layout.waitingactivity);
    
    update();
  }
  
  public void goBackButton_Click(View v)
  {
    finish();
  }
  
  public void updateButton_Click(View v)
  {
    update();
  }
  
  public void cancelButton_Click(View v)
  {
    TelephonyManager tMgr =(TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
    String phone = tMgr.getLine1Number();
   
    String params = "?phone=" + phone;
    String url = "http://mozamagic.net/taxi/deleteTaxiRequestByPhone" + params;
 
    try
    {
      HttpClient client = new DefaultHttpClient();
      HttpGet get = new HttpGet(url);
      HttpResponse response = client.execute(get);
    }
    catch (Exception e)
    {
    }
    finish();
  }
  
  public void update()
  {
    TelephonyManager tMgr =(TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
    String phone = tMgr.getLine1Number();
    
    TaxiRequestsXMLParser p = new TaxiRequestsXMLParser();
    List<TaxiRequest> requests = p.parse("http://mozamagic.net/taxi/getTaxiRequestByPhone/?phone=" + phone);
    if (requests.size() == 0)
    {
      finish();
      return;
    }
    taxiRequest = requests.get(0);  
    Log.v("TAXICAB", taxiRequest.driverPhone);
    if (taxiRequest.driverPhone.equals("0"))
    {
      ((Button) findViewById(R.id.callDriverButton)).setVisibility(8);
    }
    else
    {
      ((Button) findViewById(R.id.callDriverButton)).setVisibility(0);
      ((TextView) findViewById(R.id.driverPhoneTextView)).setText("Taxi Driver Phone: " + taxiRequest.driverPhone);
      ((TextView) findViewById(R.id.stateTextView)).setText("Taxi Status: " + "Arriving soon..");
    }
    ((Button) findViewById(R.id.updateButton)).setPressed(false);
    
  }
  
  public void callDriverButton_Click(View v)
  {
    String uri = "tel:" + taxiRequest.driverPhone.trim() ;
    Intent intent = new Intent(Intent.ACTION_CALL);
    intent.setData(Uri.parse(uri));
    startActivity(intent);
  }
  
  
}
