package mcc.Rummage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by rrubio04 on 5/13/2017.
 */

public class MyItemsActivity extends AppCompatActivity {

    private Button addNewItemButton;

    private ListView listView;
    private ArrayList<Item> itemArrayList = new ArrayList<>();
    private String userName;


    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();
    private DatabaseReference itemsUserList = root.child("itemsUserList");

    private ItemList adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_items);

        userName = getIntent().getExtras().get("user_name").toString();

        listView = (ListView) findViewById(R.id.myItemsList);
        addNewItemButton = (Button) findViewById(R.id.addNewItemButton);

        adapter = new ItemList(MyItemsActivity.this, itemArrayList );
        listView.setAdapter(adapter);


        addNewItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),AddItemActivity.class);
                intent.putExtra("user_name",userName);
                startActivity(intent);
            }
        });


        final DatabaseReference myItems = itemsUserList.child(userName);

        myItems.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                itemArrayList.clear();
                
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    String myItemKey = postSnapshot.getKey();
                    Item myItem = postSnapshot.getValue(Item.class);
                    String myItemType = postSnapshot.child("type").getValue().toString();
                    String myItemName = postSnapshot.child("name").getValue().toString();
                    String myItemPrice = postSnapshot.child("price").getValue().toString();
                    String myItemDescription = postSnapshot.child("description").getValue().toString();

                    myItem.setItemType(myItemType);
                    myItem.setItemName(myItemName);
                    myItem.setItemPrice(myItemPrice);
                    myItem.setItemDescription(myItemDescription);
                    myItem.setItemOwner(userName);

                    itemArrayList.add(myItem);

                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


    }

}
