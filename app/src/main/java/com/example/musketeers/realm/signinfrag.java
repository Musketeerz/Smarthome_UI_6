package com.example.musketeers.realm;

/**
 * Created by PRAVEEN on 24-03-2018.
 */


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class signinfrag extends Fragment {


    public signinfrag() {
        // Required empty public constructor
    }

    ArrayList<String> login_details=new ArrayList<>();
    public SQLiteDatabase db;
    DatabaseReference databaseReference;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View RootView = inflater.inflate(R.layout.signinlayout, container, false);
        final EditText adhaar,consumer;
        final Button sync;
        databaseReference = FirebaseDatabase.getInstance().getReference("USER LOGIN DETAILS");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child:dataSnapshot.getChildren()) {
                    String usrs = child.getValue(String.class);

                    login_details.add(usrs);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        sync =  RootView.findViewById(R.id.sync);
        adhaar =    RootView.findViewById(R.id.adhaar);
        consumer = RootView.findViewById(R.id.consumer);


        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String login=adhaar.getText().toString()+consumer.getText().toString();

                if (login_details.contains(login)) {
                    String sno="1";
                    db = getActivity().openOrCreateDatabase("REGISTRATION_STATUS", Context.MODE_PRIVATE, null);

                    db.execSQL("INSERT INTO reg VALUES('" + sno + "','" +login + "');");

                    Intent nxt = new Intent(getActivity(), DashboardActivity.class);
                    nxt.putExtra("KEY", login);
                    startActivity(nxt);
                } else {
                    Toast.makeText(getContext(),"Wrong Passcode",Toast.LENGTH_SHORT).show();
                }
            }
        });


        return RootView;
    }




}
