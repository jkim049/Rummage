package mcc.Rummage;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by rrubio04 on 5/13/2017.
 */

public class AllItemsActivity extends AppCompatActivity {

    private ImageButton searchButton;
    private Button clearFilterButton;
    private EditText searchEditText;
    private Spinner allItemsSpinner;

    private ListView allItemsListView;
    private ArrayList<Item> itemArrayList = new ArrayList<>();
    private ArrayList<Item> allItemArrayList= new ArrayList<>();
    ArrayList<String> userNamesList = new ArrayList<>();

    //private String userName;


    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();
    private DatabaseReference itemsUserList = root.child("itemsUserList");

    private ItemList adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_items);



        searchButton = (ImageButton) findViewById(R.id.searchButton);
        clearFilterButton = (Button) findViewById(R.id.clearFilterButton);
        searchEditText = (EditText) findViewById(R.id.searchEditText);
        allItemsListView = (ListView) findViewById(R.id.allItemsList);

        allItemsSpinner = (Spinner) findViewById(R.id.allItemsSpinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.item_type_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        allItemsSpinner.setAdapter(spinnerAdapter);

        adapter = new ItemList(AllItemsActivity.this, itemArrayList );
        allItemsListView.setAdapter(adapter);


        //ArrayList<Item> allItemArrayList = new ArrayList<Item>(itemArrayList.size());

        Iterator<Item> iterator = itemArrayList.iterator();
           while(iterator.hasNext()){

               allItemArrayList.add(iterator.next().clone());
           }


//        for(Item item: itemArrayList){
//            allItemArrayList.add(item);
//        }

//        allItemArrayList.addAll(itemArrayList);
//        //allItemArrayList.addAll(itemArrayList);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filterString = searchEditText.getText().toString().toLowerCase();
                String typeFilter = allItemsSpinner.getSelectedItem().toString();

                allItemArrayList.clear();
                Iterator<Item> iterator = itemArrayList.iterator();
                while(iterator.hasNext()){

                    allItemArrayList.add(iterator.next().clone());
                }
                // STORE ALL INITIAL ITEMS
//                allItemArrayList.clear();
//                allItemArrayList.addAll(itemArrayList);

                ArrayList<Item> filteredItemList = new ArrayList<>();
                for(Item item: itemArrayList){
                    String itemString = (item.getItemName() + " " + item.getItemDescription() +" " +item.getItemPrice() + " " + item.getItemPrice() + " " + item.getItemOwner() ).toLowerCase();

                    if(itemString.contains(filterString) && item.getItemType().equals(typeFilter)){
                        filteredItemList.add(item);
                    }
                }

                itemArrayList.clear();
                itemArrayList.addAll(filteredItemList);

                adapter.notifyDataSetChanged();
            }
        });


        clearFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEditText.setText("");
                allItemsSpinner.setSelection(0);
                itemArrayList.clear();
                itemArrayList.addAll(allItemArrayList);
                adapter.notifyDataSetChanged();
            }
        });


        itemsUserList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // GET ALL USER NAMES
                for(DataSnapshot userName: dataSnapshot.getChildren()){
                    userNamesList.add(userName.getKey());
                }

                // FOR EACH USER, ITERATE THROUGH ITEMS
                for(String user: userNamesList) {
                    final String thisUser = user;
                    DatabaseReference usersItems = itemsUserList.child(user);

                    usersItems.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {


                            for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){

                                Item userItem = postSnapshot.getValue(Item.class);
                                String userItemType = postSnapshot.child("type").getValue().toString();
                                String userItemName = postSnapshot.child("name").getValue().toString();
                                String userItemPrice = postSnapshot.child("price").getValue().toString();
                                String userItemDescription = postSnapshot.child("description").getValue().toString();
                                Log.d("CREATION", userItemName + " " +userItemPrice + " " + userItemDescription);
                                userItem.setItemType(userItemType);
                                userItem.setItemName(userItemName);
                                userItem.setItemPrice(userItemPrice);
                                userItem.setItemDescription(userItemDescription);
                                userItem.setItemOwner(thisUser);

                                itemArrayList.add(userItem);

                            }
                            adapter.notifyDataSetChanged();
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
}
