package location.com.arrowlocator.DrawerFragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import location.com.arrowlocator.Data.ConstantData;
import location.com.arrowlocator.R;
import location.com.arrowlocator.api_request.GetSelectedLocation;
import location.com.arrowlocator.api_request.MapRequest;
import location.com.arrowlocator.models.Location;
import location.com.arrowlocator.models.PrimaryLocation;
import location.com.arrowlocator.models.PrimaryLocationArray;

public class MapFragment extends Fragment implements OnMapReadyCallback, LocationListener {
    GoogleMap mGoogleMap;
    MapView mMapView;
    View mView;

    CardView bottomSheet;
    BottomSheetBehavior mBottomSheetBehavior;
    Button mNavigationButton;
    TextView mNameBar;
    TextView mDescriptionBar;
    ImageView mImageBar;

    Marker mMaker;
    LocationManager locationManager;
    List<MarkerOptions> markerOptionsList;
    Map<Marker,Integer> mapMarkers;

    android.location.Location myCurrentLocation;


    private final String TAG = "MapFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_maps, container, false);
        initializationBottomSheet();
        bottomSheetHandler();
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mMapView = (MapView) mView.findViewById(R.id.map);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }

        requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, requestCode);

            return;
        }

        mGoogleMap.setMyLocationEnabled(true);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

    }

    public void bottomSheetHandler() {
        bottomSheet = (CardView) mView.findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheet.setVisibility(View.GONE);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        mNavigationButton.setVisibility(View.VISIBLE);
                        break;
                    }
                    case BottomSheetBehavior.STATE_DRAGGING: {
                        mNavigationButton.setVisibility(View.GONE);
                        break;
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
    }


    public void initializationBottomSheet() {
        mNavigationButton = (Button) mView.findViewById(R.id.navigate_button);
        mNameBar = (TextView) mView.findViewById(R.id.bs_title);
        mImageBar = (ImageView) mView.findViewById(R.id.image_bar);
        mDescriptionBar = (TextView) mView.findViewById(R.id.description_bar);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        MapsInitializer.initialize(getActivity());
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);

        setMapBottomPadding();
        markerOptionsList = new ArrayList<>();
        mapMarkers = new HashMap<>();

        Bundle bundle = getArguments();
        if (bundle != null) {
            PrimaryLocationArray primaryLocationArray = (PrimaryLocationArray) bundle.getSerializable(ConstantData.TAG_SERIELIZE);

            for (int i = 0;i<primaryLocationArray.getPrimaryLocationsList().size();i++) {
                PrimaryLocation primaryLocation = primaryLocationArray.getPrimaryLocationsList().get(i);
                LatLng location = new LatLng(primaryLocation.getLat(), primaryLocation.getLon());
                MarkerOptions markerOptions = new MarkerOptions().position(location).title(primaryLocation.getName()).snippet(String.valueOf(primaryLocation.getId()));

                //select icon for map
                switch (primaryLocation.getFlag()) {
                    case 'H':
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.hotel_marker));
                        break;
                    case 'A':
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.arch_marker));
                        break;
                    case 'N':
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.park_marker));
                        break;
                }


                markerOptionsList.add(markerOptions);
                Marker marker = mGoogleMap.addMarker(markerOptions);
                //=============map makers with the location================
                mapMarkers.put(marker,i);//Hash map

                CameraPosition CamPossition = CameraPosition.builder().target(location).zoom(8).build();
                mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(CamPossition));
            }

            mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    mMaker = marker;
                    if (mGoogleMap.getCameraPosition().zoom < 12) {
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 12));
                    }
                    if(isNetworkAvailable(getContext())){
                        HttpAsyncTask httpAsyncTask = new HttpAsyncTask();
                        httpAsyncTask.execute(marker.getSnippet());
                    }else{
                        Toast.makeText(getContext(),"No Internet Connection",Toast.LENGTH_SHORT).show();
                    }


                    return false;
                }
            });
            mNavigationButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if(myCurrentLocation != null){
                       double markLog =  mMaker.getPosition().longitude;
                       double markLat = mMaker.getPosition().latitude;
                       mGoogleMap.clear();
                       mGoogleMap.addMarker(markerOptionsList.get(mapMarkers.get(mMaker)));
                       mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(mMaker.getPosition()));

                       mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(myCurrentLocation.getLatitude(),myCurrentLocation.getLongitude())));

                        System.out.println(markLog);
                        DirectionAsyncTask dirAsync = new DirectionAsyncTask();
                        dirAsync.execute(markLog,markLat);
                    }

                }
            });
        }

    }

    public void setMapBottomPadding(){
        final int dp=62;//Whatever padding you want to set in dp

        final int scale= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());

        mGoogleMap.setPadding(0, 0, 0, scale);//here i am setting bottom padding
    }

    public void setBottomSheet(Location location) {
        mNameBar.setText(location.getName());
        mDescriptionBar.setText(location.getDiscription());
        new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... urls) {
                return download_Image(urls[0]);
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                mImageBar.setImageBitmap(result);              // how do I pass a reference to mChart here ?
            }


            private Bitmap download_Image(String url) {
                //---------------------------------------------------
                Bitmap bm = null;
                try {
                    URL aURL = new URL(url);
                    URLConnection conn = aURL.openConnection();
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    BufferedInputStream bis = new BufferedInputStream(is);
                    bm = BitmapFactory.decodeStream(bis);
                    bis.close();
                    is.close();
                } catch (IOException e) {
                    Log.e("Hub", "Error getting the image from server : " + e.getMessage().toString());
                }
                return bm;
                //---------------------------------------------------
            }
        }.execute(location.getImageUrl());
    }
    //Checks availability if internet connection
    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }



    @Override
    public void onLocationChanged(android.location.Location location) {
        myCurrentLocation = location;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    class HttpAsyncTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            int id = Integer.valueOf(strings[0]);
            return GetSelectedLocation.GETALL(id);
        }

        @Override
        protected void onPostExecute(String s) {
            try{
                JSONObject response = new JSONObject(s);
                int id = response.getInt("id");
                String name = response.getString("name");
                double lat = response.getDouble("lat");
                double lon = response.getDouble("lon");
                String flag = response.getString("flag");
                String description = response.getString("discription");
                String imageURL = response.getString("imgUrl");

                bottomSheet.setVisibility(View.VISIBLE);
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                Location location = new Location(id,name,lat,lon,flag.charAt(0),description,imageURL);
                setBottomSheet(location);

            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    class DirectionAsyncTask extends AsyncTask<Double,Void,String>{

        @Override
        protected String doInBackground(Double... objects) {
            double lon = objects[0];
            double lat = objects[1];
            return MapRequest.GET_DERECTION(myCurrentLocation.getLatitude(),myCurrentLocation.getLongitude(),lat,lon);

//            return String.valueOf(lon);
        }

        @Override
        protected void onPostExecute(String s) {
            try{
                JSONObject mapResp = new JSONObject(s);
                JSONArray jSteps = mapResp.getJSONArray("routes").getJSONObject(0).getJSONArray("legs")
                        .getJSONObject(0).getJSONArray("steps");

                int count = jSteps.length();
                String polylinr_array[] = new String[count];
                JSONObject jsonObject ;

                for (int i =0; i<count;i++){
                    jsonObject = jSteps.getJSONObject(i);

                    String polyline = jsonObject.getJSONObject("polyline").getString("points");
                    polylinr_array[i] = polyline;
                }

                for(String polyline:polylinr_array){
                    PolylineOptions oPolyline = new PolylineOptions();
                    oPolyline.color(getResources().getColor(R.color.app_blue));
                    oPolyline.width(12);
                    oPolyline.addAll(PolyUtil.decode(polyline));

                    mGoogleMap.addPolyline(oPolyline);


                }
                mGoogleMap.animateCamera( CameraUpdateFactory.zoomTo( 9.0f ) );

            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

}
