package location.com.arrowlocator.api_request;

import android.util.Log;

import com.google.android.gms.maps.model.Marker;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MapRequest {

    public static String GET_DERECTION(double myLat,double myLng,double desLat,double desLng){
//        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=6.851838,79.902467&destination=6.895642,79.894398&key=AIzaSyD8fFAZeie6zS8zd_zZiG22_bBokuvov-M";
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin="+String.valueOf(myLat)+","+String.valueOf(myLng)+
                "&destination="+String.valueOf(desLat)+","+String.valueOf(desLng)+"&key=AIzaSyD8fFAZeie6zS8zd_zZiG22_bBokuvov-M";

        //        StringBuilder urlBuilder = new StringBuilder();
//        urlBuilder.append("https://maps.googleapis.com/maps/api/directions/json?");
//        urlBuilder.append("origin="+currrent.getPosition().latitude+","+currrent.getPosition().longitude);
//        urlBuilder.append("&destination="+destination.getPosition().latitude+","+destination.getPosition().longitude);
//        urlBuilder.append("&key=" +  key);

        InputStream inputStream = null;
        String result = null;

        try{

            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);

            String json = "";

            httpGet.addHeader("Content-Type","application/json");


            HttpResponse httpResponse = httpClient.execute(httpGet);
            inputStream = httpResponse.getEntity().getContent();

            if(inputStream!=null){
                result = convertInputStreamToString(inputStream);
            }else {
                result = "Did not work!";
            }
        } catch (Exception ex){
            Log.d("InputStream", ex.getLocalizedMessage());
        }
        return result;

    }
    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
}
