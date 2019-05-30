package location.com.arrowlocator.api_request;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import location.com.arrowlocator.Data.ConstantData;

public class GetSelectedLocation {
    private final static String URL_SELECTED_LOC= "http://"+ ConstantData.IP_ADDRESS+":8080/location/id/";
    public static String GETALL(int id){

        InputStream inputStream = null;
        String result = null;
        String url = URL_SELECTED_LOC+String.valueOf(id);
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
