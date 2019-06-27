package mcc.Rummage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by rrubio04 on 5/12/2017.
 */

public class HomeActivity extends AppCompatActivity {
    private Button myButton;
    private Button allButton;
    private Button messagesButton;
    private Button gpsButton;

    private String userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        userName = getIntent().getExtras().get("user_name").toString();

        myButton = (Button) findViewById(R.id.myButton);
        allButton = (Button) findViewById(R.id.allButton);
        messagesButton = (Button) findViewById(R.id.messagesButton);
        gpsButton = (Button) findViewById(R.id.gpsButton);




        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MyItemsActivity.class);
                intent.putExtra("user_name",userName);
                startActivity(intent);
            }
        });

        allButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),AllItemsActivity.class);
                startActivity(intent);
            }
        });
        messagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ContactsActivity.class);
                intent.putExtra("user_name",userName);
                startActivity(intent);
            }
        });
        gpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                startActivity(intent);
            }
        });


    }

}
