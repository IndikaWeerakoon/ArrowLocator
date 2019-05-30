package location.com.arrowlocator;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import location.com.arrowlocator.Data.ConstantData;
import location.com.arrowlocator.DrawerFragment.MapFragment;
import location.com.arrowlocator.DrawerFragment.SearchFragment;
import location.com.arrowlocator.api_request.GetAllRequest;
import location.com.arrowlocator.models.PrimaryLocation;
import location.com.arrowlocator.models.PrimaryLocationArray;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "DrawerActivity";
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.TRANSPARENT);


        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new MapFragment())
                .commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_All) {
            if(isNetworkAvailable(this)){
                HttpAsyncTask asyncTask = new HttpAsyncTask();
                asyncTask.execute();
            }else{
                Toast.makeText(this,"No Internet Connection",Toast.LENGTH_SHORT).show();
            }

//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new MapFragment())
//            .commit();
            // Handle the camera action
        } else if (id == R.id.nav_arch) {
            SearchFragment searchFragment = new SearchFragment();
            Bundle bundle = new Bundle();
            bundle.putString(ConstantData.BUNDLE_TAG,ConstantData.ARCH_SITE);
            searchFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,searchFragment)
                    .commit();

        } else if (id == R.id.nav_hotel) {
            SearchFragment searchFragment = new SearchFragment();
            Bundle bundle = new Bundle();
            bundle.putString(ConstantData.BUNDLE_TAG,ConstantData.HOTELS);
            searchFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,searchFragment)
                    .commit();

        } else if (id == R.id.nav_national_park) {
            SearchFragment searchFragment = new SearchFragment();
            Bundle bundle = new Bundle();
            bundle.putString(ConstantData.BUNDLE_TAG,ConstantData.NATIONAL_PARK);
            searchFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,searchFragment)
                    .commit();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Checks availability if internet connection
    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    class HttpAsyncTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... strings) {
            String reuestResult = GetAllRequest.GETALL();
            return reuestResult;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONArray result = new JSONArray(s);
                List<PrimaryLocation> primaryLocationsList = new ArrayList<>();
                for (int i =0; i<result.length();i++){
                    int id = result.getJSONObject(i).getInt("id");
                    String name = result.getJSONObject(i).getString("name");
                    double lat = result.getJSONObject(i).getDouble("lat");
                    double lon = result.getJSONObject(i).getDouble("lon");
                    String flag = result.getJSONObject(i).getString("flag");

                    PrimaryLocation primaryLocation = new PrimaryLocation(id,name,lat,lon,flag.charAt(0));
                    primaryLocationsList.add(primaryLocation);
                }
                MapFragment mapFragment = new MapFragment();
                PrimaryLocationArray primaryLocationArray = new PrimaryLocationArray(primaryLocationsList);

                Bundle bundle = new Bundle();
                bundle.putSerializable(ConstantData.TAG_SERIELIZE,primaryLocationArray);
                mapFragment.setArguments(bundle);

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,mapFragment)
                    .commit();

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


}
