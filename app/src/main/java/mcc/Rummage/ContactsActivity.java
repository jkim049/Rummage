package mcc.Rummage;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ContactsActivity extends AppCompatActivity {


    private Button  addButton;
    private EditText nameTextField;

    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_of_contacts = new ArrayList<>();
    private String userName,receiverName;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();
    private DatabaseReference users = root.child("chatUserList");
    private ContactList adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        userName = getIntent().getExtras().get("user_name").toString();
        addButton = (Button) findViewById(R.id.btn_add_contact);
        nameTextField = (EditText) findViewById(R.id.name_text_field);
        listView = (ListView) findViewById(R.id.listView);

       // request_user_name();

       // arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list_of_rooms);

       // listView.setAdapter(arrayAdapter);

        adapter = new ContactList(ContactsActivity.this, list_of_contacts);

        listView.setAdapter(adapter);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check if the reciever is in the userlist
                receiverName = nameTextField.getText().toString();
                nameTextField.setText("");
                final DatabaseReference receiver = root.child("chatUserList").child(receiverName);


                receiver.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue()==null){
                          //Toast.makeText(getApplicationContext(),"No such user", Toast.LENGTH_LONG).show();
                            nameTextField.setError("No such user");
                        }else if(receiverName.equals(userName)){
                           //Toast.makeText(getApplicationContext(),"Please enter other user's name", Toast.LENGTH_LONG).show();
                            nameTextField.setError("Cannot chat with yourself");
                        }
                        else{
                            //add the receiver the current user's list
                            DatabaseReference users = FirebaseDatabase.getInstance().getReference().
                                    getRoot().child("chatUserList");
                            Map<String, Object> map = new HashMap<String, Object>();

                            map.put(receiverName,"");
                            users.child(userName).updateChildren(map);
                            map.clear();

                            //add the user to the receiver's list
                            map.put(userName, "");
                            users.child(receiverName).updateChildren(map);


                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });

        users.child(userName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Set<String> set = new HashSet<String>();
                Iterator i = dataSnapshot.getChildren().iterator();

                while (i.hasNext()){
                    DataSnapshot item = ((DataSnapshot)i.next());
                    set.add(item.getKey());
                }

                list_of_contacts.clear();
                list_of_contacts.addAll(set);

               adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getApplicationContext(),ChattingActivity.class);
                //intent.putExtra("receiver_name",((TextView)view).getText().toString() );
                intent.putExtra("receiver_name",list_of_contacts.get(i));
                intent.putExtra("user_name",userName);
                startActivity(intent);
            }
        });

    }


    private void request_user_name() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter name:");

        final EditText input_field = new EditText(this);

        builder.setView(input_field);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                userName = input_field.getText().toString();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                request_user_name();
            }
        });

        builder.show();
    }
}
