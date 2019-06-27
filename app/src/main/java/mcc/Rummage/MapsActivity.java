package mcc.Rummage;

import android.*;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback { //GoogleMap.InfoWindowAdapter

    public GoogleMap mMap;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();
    private DatabaseReference gpsItemsList = root.child("gpsItemsList");
    private ArrayList<Item> gpsItemArrayList = new ArrayList<>();
    private ArrayList<String> itemArrayList = new ArrayList<>();

    public void addMarker(){

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        runtime_permissions();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



    }

    public boolean runtime_permissions(){
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            return true;
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        //mMap.setOnMapLongClickListener(this);
        //mMap.setInfoWindowAdapter(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //mMap.setMyLocationEnabled(true);

        gpsItemsList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // GET ALL ITEM KEYS
                for(DataSnapshot item: dataSnapshot.getChildren()){
                    itemArrayList.add(item.getKey());
                }

                Log.d("CREATION", "size" + itemArrayList.size());
                for(String item: itemArrayList) {
                    final String thisItem = item;
                    DatabaseReference itemInfo = gpsItemsList;

                    itemInfo.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {


                            for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){

                                //Item userItem = postSnapshot.getValue(Item.class);
                                Double userItemLatitude = (Double.parseDouble(postSnapshot.child("latitude").getValue().toString()));
                                Log.d("CREATION", "latitude" + userItemLatitude);
                                Double userItemLongitude = (Double.parseDouble(postSnapshot.child("longitude").getValue().toString()));
                                Log.d("CREATION", "longitude" + userItemLongitude);
                                String userItemName = postSnapshot.child("name").getValue().toString();
                                String userItemPrice = postSnapshot.child("price").getValue().toString();
                                String userItemDescription = postSnapshot.child("description").getValue().toString();
                                LatLng latLng = new LatLng(userItemLatitude, userItemLongitude);

                                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(latLng.toString());
                                MarkerOptions marker = new MarkerOptions().position(new LatLng(userItemLatitude, userItemLongitude))
                                        .title(userItemName)
                                        .snippet("Price: " + userItemPrice);
                                //populateMarkerView(markerOptions, userItemName, userItemPrice, userItemDescription );

                                //markerOptions.draggable(true);
                                mMap.addMarker(marker);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    //Get current longitude and latitue
    public void getCurrentLocation(View view){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location == null) {

            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        LatLng latLng = new LatLng(latitude, longitude);
        //mMap.addMarker(new MarkerOptions().position(latLng).title("My Marker"));
        goToLocation(latitude, longitude, 15);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

    }

/*
    public void onMapLongClick(LatLng latLng) {

        //Add marker on LongClick position
        MarkerOptions markerOptions =
                new MarkerOptions().position(latLng).title(latLng.toString());
        markerOptions.draggable(true);

        mMap.addMarker(markerOptions);
    }
*/

    //@Override
   // public View getInfoWindow(Marker marker) {
    //    //return prepareInfoView(marker);
    //    return null;
   // }

    //public View getInfoContents(Marker marker) {
    //    return prepareInfoView(marker);
    //}
//    private View populateMarkerView(MarkerOptions marker, String name, String price, String description){
//        LinearLayout infoView = new LinearLayout(MapsActivity.this);
//        LinearLayout.LayoutParams infoViewParams = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
//        );
//
//        infoView.setOrientation(LinearLayout.HORIZONTAL);
//        infoView.setLayoutParams(infoViewParams);
//
//        ImageView infoImageView = new ImageView(MapsActivity.this);
//        Drawable drawable = getResources().getDrawable(android.R.drawable.ic_dialog_map);
//        infoImageView.setImageDrawable(drawable);
//        infoView.addView(infoImageView);
//
//        LinearLayout subInfoView = new LinearLayout(MapsActivity.this);
//        LinearLayout.LayoutParams subInfoViewParams = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
//        );
//        subInfoView.setOrientation(LinearLayout.VERTICAL);
//        subInfoView.setLayoutParams(subInfoViewParams);
//
//        TextView subInfoName = new TextView(MapsActivity.this);
//        subInfoName.setText("Item: " + name );
//        //subInfoName.setText("Lat: " + marker.getPosition().latitude + "\nWorked!");
//        TextView subInfoPrice = new TextView(MapsActivity.this);
//        subInfoPrice.setText("Price: " + price );
//
//        TextView subInfoDescription = new TextView(MapsActivity.this);
//        subInfoDescription.setText("Description: " + description );
//        //subInfoDescription.setText("Lng: " + marker.getPosition().longitude + "\n\n\n\nWorked!");
//        subInfoView.addView(subInfoName);
//        subInfoView.addView(subInfoPrice);
//        subInfoView.addView(subInfoDescription);
//        infoView.addView(subInfoView);
//
//        //Marker markerTemp = mMap.addMarker(marker)
//        return infoView;
//
//    }

    private View prepareInfoView(Marker marker) {

        LinearLayout infoView = new LinearLayout(MapsActivity.this);
        LinearLayout.LayoutParams infoViewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );

        infoView.setOrientation(LinearLayout.HORIZONTAL);
        infoView.setLayoutParams(infoViewParams);

        ImageView infoImageView = new ImageView(MapsActivity.this);
        Drawable drawable = getResources().getDrawable(android.R.drawable.ic_dialog_map);
        infoImageView.setImageDrawable(drawable);
        infoView.addView(infoImageView);

        LinearLayout subInfoView = new LinearLayout(MapsActivity.this);
        LinearLayout.LayoutParams subInfoViewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        subInfoView.setOrientation(LinearLayout.VERTICAL);
        subInfoView.setLayoutParams(subInfoViewParams);

        TextView subInfoName = new TextView(MapsActivity.this);
        subInfoName.setText("Item: \n\n" );
        //subInfoName.setText("Lat: " + marker.getPosition().latitude + "\nWorked!");
        TextView subInfoDescription = new TextView(MapsActivity.this);
        //subInfoDescription.setText("Description: \n\n");
        subInfoDescription.setText("Lng: " + marker.getPosition().longitude + "\n\n\n\nWorked!");
        subInfoView.addView(subInfoName);
        subInfoView.addView(subInfoDescription);
        infoView.addView(subInfoView);

        return infoView;


    }

    public void onSearch(View view) {
        EditText myLocation_ET = (EditText)findViewById(R.id.searchText);
        String myLocation = myLocation_ET.getText().toString();


        List<Address> addressList = null;
        if(myLocation != null || !myLocation.equals("")){
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(myLocation, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Address address = addressList.get(0);

            double myLat = address.getLatitude();
            double myLong = address.getLongitude();

            LatLng latLng = new LatLng(myLat, myLong);
            //mMap.addMarker(new MarkerOptions().position(latLng).title("My Marker"));
            goToLocation(myLat, myLong, 15);
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

        }

    }
    public void goToLocation(double latitude, double longitude, float zoom){
        LatLng latLng = new LatLng(latitude, longitude);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mMap.moveCamera(update);
    }


}