package com.example.gettaxi;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class TaxiDriversXMLParser
{
  public List<TaxiDriver> parse(String url_string)
  {
    List<TaxiDriver> taxiDrivers = new ArrayList<TaxiDriver>();
    try
    {
      XMLParser parser = new XMLParser();
      String xml = parser.getXmlFromUrl(url_string);
      Document doc = parser.getDomElement(xml);
       
      NodeList nl = doc.getElementsByTagName("taxiDriver");
       
      for (int i = 0; i < nl.getLength(); i++)
      {
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
      
    }     
    catch (Exception e)
    {
    }
    
    return taxiDrivers;
  }
}