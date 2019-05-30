package location.com.arrowlocator.DrawerFragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import location.com.arrowlocator.Data.ConstantData;
import location.com.arrowlocator.R;
import location.com.arrowlocator.adapter.RecyclerAdapter;
import location.com.arrowlocator.api_request.GetAllRequest;
import location.com.arrowlocator.models.PrimaryLocation;
import location.com.arrowlocator.models.PrimaryLocationArray;


public class SearchFragment extends Fragment {
    private View mView;

    private RecyclerView recyclerView;
    private RecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private LinearLayout clickAll;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_search,container,false);


        return  mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        clickAll = (LinearLayout) mView.findViewById(R.id.all_click) ;

        recyclerView = (RecyclerView) mView.findViewById(R.id.location_list);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        //classifing the data
        Bundle bundle = getArguments();
        if(bundle != null){
            String type = bundle.getString(ConstantData.BUNDLE_TAG);
            String flag = null;

            switch (type){
                case ConstantData.NATIONAL_PARK: flag = "N";break;
                case ConstantData.ARCH_SITE: flag = "A";break;
                case ConstantData.HOTELS: flag = "H";break;
            }

            if(isNetworkAvailable(getContext())){
                HttpAsyncTask httpAsyncTask = new HttpAsyncTask();
                httpAsyncTask.execute(flag);
            }else {
                Toast.makeText(getContext(),"No Internet Connection",Toast.LENGTH_SHORT).show();
            }

        }



    }

    //Checks availability if internet connection
    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    class HttpAsyncTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            return GetAllRequest.GET_ALL_SELECTED(strings[0].charAt(0));
        }

        @Override
        protected void onPostExecute(String s) {
            try{
                JSONArray result = new JSONArray(s);
                final List<PrimaryLocation> primaryLocationsList = new ArrayList<>();
                for (int i =0; i<result.length();i++){
                    int id = result.getJSONObject(i).getInt("id");
                    String name = result.getJSONObject(i).getString("name");
                    double lat = result.getJSONObject(i).getDouble("lat");
                    double lon = result.getJSONObject(i).getDouble("lon");
                    String flag = result.getJSONObject(i).getString("flag");

                    PrimaryLocation primaryLocation = new PrimaryLocation(id,name,lat,lon,flag.charAt(0));
                    primaryLocationsList.add(primaryLocation);

                }
                clickAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PrimaryLocationArray plArray = new PrimaryLocationArray(primaryLocationsList);
                        MapFragment mapFragment = new MapFragment();
                        Bundle bundle  = new Bundle();
                        bundle.putSerializable(ConstantData.TAG_SERIELIZE,plArray);
                        mapFragment.setArguments(bundle);

                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,mapFragment)
                                .commit();

                    }
                });


                mAdapter = new RecyclerAdapter(primaryLocationsList);
                recyclerView.setAdapter(mAdapter);

                mAdapter.setOnItemClickListner(new RecyclerAdapter.OnItemClickListner() {
                    @Override
                    public void OnItemClick(int position) {
                        PrimaryLocation primaryLocation = primaryLocationsList.get(position);

                        List<PrimaryLocation> arrayLocation = new ArrayList<>();
                        arrayLocation.add(primaryLocation);

                        PrimaryLocationArray pArray = new PrimaryLocationArray(arrayLocation);

                        MapFragment mapFragment = new MapFragment();
                        Bundle bundle  = new Bundle();
                        bundle.putSerializable(ConstantData.TAG_SERIELIZE,pArray);
                        mapFragment.setArguments(bundle);

                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,mapFragment)
                                .commit();
                    }
                });

            }catch (JSONException e){
                e.printStackTrace();
            }

        }
    }
}
