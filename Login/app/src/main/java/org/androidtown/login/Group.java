package org.androidtown.login;

import android.telephony.mbms.StreamingServiceInfo;

import java.util.ArrayList;

public class Group {
    public String title, promisePlace, date, time, startTime, penalty = "알람울리기";
    public  double promiseLongitude = 0.0, promiseLatitude = 0.0;
    public int personnel =  1;
    public Group(){}
  public  static ArrayList people = new ArrayList();
    public void setTitle(String ttl){
        title = ttl;
    }

    public void setPromisePlace(String p){
        promisePlace = p;
    }

    public void setPromiseLongitude(double p){
        promiseLongitude = p;
    }

    public void setPromiseLatitude(double p){ promiseLatitude = p; }

    public void setDate(String d){
        date = d;
    }

    public void setTime(String tm){
        time = tm;
    }

    public void setStartTime(String stm){startTime = stm;}

    public void setPersonnel(int per){personnel = per;}

    public void setPenalty(String pn){
        penalty = pn;
    }



    static void addPerson(String person){
        people.add(person);
    }

    public String getTitle(){
        return title;
    }

    public String getPromisePlace(){
        return promisePlace ;
    }

    public double getPromiseLongitude(){
        return promiseLongitude ;
    }

    public double getPromiseLatitude(){
        return promiseLatitude ;
    }

    public int getPersonnel(){
        return personnel ;
    }

    public String getDate(){
        return date;
    }

    public String getStartTime() { return startTime; }

    public String getTime(){
        return time;
    }



    public boolean isChanged(){
        return (title != null) && (promisePlace != null)
                && (date != null) && (time != null) && (penalty != null)
                && (promiseLongitude != 0.0) && (promiseLatitude != 0.0)
                && (personnel != 0);
    }

}
