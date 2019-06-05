package org.androidtown.login;
import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class RequestHttpURLConnection {

    public String request(String address, String coordinate){

        // HttpURLConnection 참조 변수.
        HttpURLConnection con = null;

        String clientId = "mj2j1zp44f";//애플리케이션 클라이언트 아이디값";
        String clientSecret = "0F3deCgfnMB7OERUs7XEFrwA7WMlCknuCT2BB0jO";//애플리케이션 클라이언트 시크릿값";
        try {
            String addr = URLEncoder.encode(address, "UTF-8");
            String apiURL = "https://naveropenapi.apigw.ntruss.com/map-place/v1/search?query=" + addr + "&coordinate=" + coordinate;//"https://naveropenapi.apigw.ntruss.com/map-place/v1/search?query=" + addr + "&coordinate=127.1054328,37.3595963"; //json
            //String apiURL = "https://openapi.naver.com/v1/map/geocode.xml?query=" + addr; // xml
            URL url = new URL(apiURL);
            con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
            con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);
            int responseCode = con.getResponseCode();
            String s ="";
            if(responseCode==200) { // 정상 호출
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = br.readLine()) != null) {
                    s += (inputLine);
                }
                br.close();
                s = doJSONParser(s);
            } else {  // 에러 발생
                s = ("API 호출 에러 발생 : 에러코드=" + responseCode);
            }
        return  s;
        } catch (MalformedURLException e) { // for URL.
            e.printStackTrace();
        } catch (IOException e) { // for openConnection().
            e.printStackTrace();
        } finally {
            if (con != null)
                con.disconnect();
        }

        return null;

    }

    String doJSONParser(String str){
        StringBuffer sb = new StringBuffer();
        StringBuffer sbn = new StringBuffer();

        try {
            JSONObject jobject = new JSONObject(str);   // JSONArray 생성
            JSONArray jarray = jobject.getJSONArray("places");
            for(int i=0; i < jarray.length(); i++){
                JSONObject jObject = jarray.getJSONObject(i);  // JSONObject 추출
                String address = jObject.getString("road_address");
                String name = jObject.getString("name");
                String phone = jObject.getString("phone_number");
                String longitude = jObject.getString("x");
                String latitude = jObject.getString("y");
                int distance = jObject.getInt("distance");

                if(distance<1000) {
                    sb.append(
                            "이름 : " + name +
                                    " 거리 : " + distance + "M" +
                            " 주소 : " + address +
                                    " 번호 : " + phone + "\n" + latitude + "," + longitude + "\n"
                    );
                }
                else {
                    double Distance = (double)distance/1000;
                    Distance = Math.round(Distance*1000)/1000.0;
                    sb.append(
                            "이름 : " + name +
                                    " 거리 : " + Distance + "KM" +
                            " 주소 : " + address +
                                    " 번호 : " + phone + "\n" + latitude + "," + longitude + "\n"
                    );
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return sb.toString();
    }
}