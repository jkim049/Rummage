package mcc.Rummage;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Larry on 4/27/17.
 */


public class ChattingActivity extends AppCompatActivity {

        private Button btn_send_msg;
        private EditText input_msg;
        private TextView chat_conversation;

        private String senderName,receiverName;
        private DatabaseReference senderRef;
        private DatabaseReference receiverRef;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.chat_room);

            btn_send_msg = (Button) findViewById(R.id.btn_send);
            input_msg = (EditText) findViewById(R.id.msg_input);
            chat_conversation = (TextView) findViewById(R.id.textView);

            senderName = getIntent().getExtras().get("user_name").toString();
            receiverName = getIntent().getExtras().get("receiver_name").toString();
            setTitle(receiverName);
            senderRef = FirebaseDatabase.getInstance().getReference().getRoot().child("chatUserList").child(senderName);
            receiverRef = FirebaseDatabase.getInstance().getReference().getRoot().child("chatUserList").child(receiverName);

            btn_send_msg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String temp_key1 = senderRef.child(receiverName).push().getKey();
                    String temp_key2 = receiverRef.child(senderName).push().getKey();


                    Map<String,Object> map1 = new HashMap<String, Object>();
                    map1.put("name",senderName);
                    map1.put("msg",input_msg.getText().toString());
                    senderRef.child(receiverName).child(temp_key1).updateChildren(map1);

                    //Map<String,Object> map = new HashMap<String, Object>();
                    //FirebaseDatabase.getInstance().getReference().getRoot().child("userList").updateChildren(map);


                    Map<String,Object> map2 = new HashMap<String, Object>();
                    map2.put("name",senderName);
                    map2.put("msg",input_msg.getText().toString());
                    receiverRef.child(senderName).child(temp_key2).updateChildren(map2);

                    input_msg.setText("");
                }
            });


            senderRef.child(receiverName).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    updateChatMessaging(dataSnapshot);


                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                   updateChatMessaging(dataSnapshot);

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        @Override
        protected void onStop(){
            super.onStop();
            Log.v("tag","On Stop");
        }



        private String chat_msg,chat_user_name;
        private void updateChatMessaging(DataSnapshot dataSnapshot) {

            Iterator i = dataSnapshot.getChildren().iterator();

            while (i.hasNext()){
                chat_msg = (String) ((DataSnapshot)i.next()).getValue();
                chat_user_name = (String) ((DataSnapshot)i.next()).getValue();
                chat_conversation.append("@"+chat_user_name +": "+chat_msg +" \n\n");
            }


        }
}

