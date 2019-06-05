package org.androidtown.login.Location;

public class LocationInfo {
    String name,synctime,state,phone;
    double lat,lng;

     public LocationInfo()
     {

     }

     public void setName(String name)
     {
         this.name = name;
     }
     public void setSynctime(String synctime)
     {
         this.synctime = synctime;
     }
     public void setState(String state)
     {
         this.state = state;
     }
     public void setLat(double lat)
     {
         this.lat = lat;
     }
     public void setLog(double lng)
     {
         this.lng = lng;
     }
     public void setPhone(String phone) { this.phone = phone; }

     public String getName()
     {
         return this.name;
     }
     public String getSynctime()
     {
         return this.synctime;
     }
     public String getState()
     {
         return this.state;
     }
     public double getLat()
     {
         return this.lat;
     }
     public double getLng()
     {
         return this.lng;
     }
     public String getPhone(){ return this.phone;}
}
