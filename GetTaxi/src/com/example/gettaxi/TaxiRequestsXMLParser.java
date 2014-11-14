package com.example.gettaxi;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.util.Log;

public class TaxiRequestsXMLParser
{
  public List<TaxiRequest> parse(String url_string)
  {
    List<TaxiRequest> taxiRequests = new ArrayList<TaxiRequest>();
    try
    {
      XMLParser parser = new XMLParser();
      String xml = parser.getXmlFromUrl(url_string); // getting XML
      Log.v("TAXICAB", "PARSE: " + url_string);
      Document doc = parser.getDomElement(xml); // getting DOM element
       
      NodeList nl = doc.getElementsByTagName("taxiRequest");
       
      for (int i = 0; i < nl.getLength(); i++)
      {
        Log.v("TAXICAB", "LOOPING");
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
      Log.v("TAXICAB", "THANK YOU");
    }
    catch (Exception e)
    {
      Log.v("TAXICAB", "THIS IS BAD");
    }
    
    Log.v("TAXICAB", "PARSER: " + taxiRequests.size());
    
    return taxiRequests;
  }
}
