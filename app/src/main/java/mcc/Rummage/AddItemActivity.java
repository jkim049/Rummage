package mcc.Rummage;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rrubio04 on 5/13/2017.
 */

public class AddItemActivity extends AppCompatActivity {


    private Button addNewItemButton;
    private Spinner newItemTypeSpinner;
    private EditText newItemNameEditText;
    private EditText newItemPriceEditText;
    private EditText newItemDescriptionEditText;

    private String userName;

    private String newItemType, newItemName, newItemPrice, newItemDescription;

    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();
    private DatabaseReference itemsUserList = root.child("itemsUserList");
    private DatabaseReference gpsItemsList = root.child("gpsItemsList");


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_item);
        userName = getIntent().getExtras().get("user_name").toString();


        addNewItemButton = (Button) findViewById(R.id.addMyNewItemButton);

        newItemTypeSpinner = (Spinner) findViewById(R.id.newItemTypeSpinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.item_type_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newItemTypeSpinner.setAdapter(spinnerAdapter);


        newItemNameEditText = (EditText) findViewById((R.id.newItemNameEditText));
        newItemPriceEditText = (EditText) findViewById((R.id.newItemPriceEditText));
        newItemDescriptionEditText = (EditText) findViewById((R.id.newItemDescriptionEditText));
        newItemTypeSpinner.requestFocus();

        addNewItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(newItemTypeSpinner.getSelectedItem().toString().isEmpty()){
                    //((TextView)newItemTypeSpinner.getSelectedView()).setError("");
                    TextView errorText = (TextView)newItemTypeSpinner.getSelectedView();
                    errorText.setError("anything here, just to add the icon");
                    errorText.setTextColor(Color.RED);//just to highlight that this is an error
                    errorText.setText("Select a type.");//changes the selected item text to this
                    newItemTypeSpinner.requestFocus();
                }

                else if (newItemNameEditText.getText().toString().isEmpty()){
                    newItemNameEditText.requestFocus();
                    newItemNameEditText.setError("Enter an item name.");
                }

                else if (newItemPriceEditText.getText().toString().isEmpty()){
                    newItemPriceEditText.requestFocus();
                    newItemPriceEditText.setError("Enter an item price.");
                }

                else if (newItemDescriptionEditText.getText().toString().isEmpty()){
                    newItemDescriptionEditText.requestFocus();
                    newItemDescriptionEditText.setError("Enter an item description.");
                }

                else{

                    newItemType = newItemTypeSpinner.getSelectedItem().toString();
                    newItemName = newItemNameEditText.getText().toString();
                    newItemPrice = newItemPriceEditText.getText().toString();
                    newItemDescription = newItemDescriptionEditText.getText().toString();


                    final Item newItem = new Item(newItemType, newItemName, newItemPrice, newItemDescription, userName);


                    newItemTypeSpinner.setSelection(0);
                    newItemNameEditText.setText("");
                    newItemPriceEditText.setText("");
                    newItemDescriptionEditText.setText("");
                    newItemTypeSpinner.requestFocus();


                    final DatabaseReference myItems = itemsUserList.child(userName);
                    final String myItemKey = myItems.push().getKey();

                    final DatabaseReference myGPSItems = gpsItemsList.child(myItemKey);

                    Context context = getApplicationContext();
                    CharSequence text = "You have successfully added a new item!";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                    myItems.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {


                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("type", newItemType);
                            map.put("name", newItemName);
                            map.put("price", newItemPrice);
                            map.put("description", newItemDescription);
                            map.put("owner", userName);

                            myItems.child(myItemKey).updateChildren(map);


                            Map<String, Object> gpsmap = new HashMap<String, Object>();


                            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                            if (ActivityCompat.checkSelfPermission(AddItemActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AddItemActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if(location == null) {

                                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            }
                            double longitude = location.getLongitude();
                            double latitude = location.getLatitude();


                            gpsmap.put("latitude", latitude);
                            gpsmap.put("longitude", longitude);
                            gpsmap.put("type", newItemType);
                            gpsmap.put("name",newItemName);
                            gpsmap.put("price" ,newItemPrice);
                            gpsmap.put("description", newItemDescription);
                            gpsmap.put("owner",userName);

                            myGPSItems.updateChildren(gpsmap);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }


        });
    }
}
