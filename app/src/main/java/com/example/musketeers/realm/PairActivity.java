package com.example.musketeers.realm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PairActivity extends AppCompatActivity {

    TextView pairId;
    public static final String aadhar_name ="aadhar", econsumer_name = "econsumer" ;
    String aadhar, KEY;
    DatabaseReference databaseReference;
    ArrayList<String> name=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pair);

        pairId = findViewById(R.id.pair_id);
        aadhar = getIntent().getStringExtra(aadhar_name);
        KEY = getIntent().getStringExtra("KEY");

        pairId.setText(KEY.substring(0,2)+"-"+KEY.substring(2,4)+"-"+KEY.substring(4,6)+"-"+KEY.substring(6,8)+"-"+KEY.substring(8,10));

        databaseReference = FirebaseDatabase.getInstance().getReference(KEY.substring(0,2)+KEY.substring(2,4)+KEY.substring(4,6)+KEY.substring(6,8)+KEY.substring(8,10)).child("USER DETAILS");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child:dataSnapshot.getChildren()) {
                    String usrs = child.getValue(String.class);
                    name.add(usrs);
                }

                if(name.get(3).equals("true")) {
                    pair();
                } else {
                    name.clear();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    void pair() {
        Intent i = new Intent(this, DashboardActivity.class);
        i.putExtra("KEY",KEY);
        startActivity(i);
    }
}