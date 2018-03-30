package com.example.musketeers.realm;

import android.*;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.test.mock.MockPackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

import static com.example.musketeers.realm.DashboardActivity.passcode_pass;

/**
 * Created by PRAVEEN on 24-03-2018.
 */
public class registerfrag extends android.support.v4.app.Fragment {
    public registerfrag() {
        // Required empty public constructor
    }

    Button reg;
    EditText et1, et2, et4;
    LinearLayout et3;
    ImageView loc;
    FloatingActionButton fab;
    String name, aadhar, econsumer, location = null;
    String ano, eno, an = "", en = "";
    private String command;
    int field = 1;
    DatabaseReference databaseReference;
    Dialog myDialog;
    ArrayList<String> login_details=new ArrayList<>();
    String passcode_pass;
    double latitude, longitude;

    GPSTracker gps;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private TextToSpeech tts;

    private static final int REQUEST_CODE_PERMISSION = 2;
    String mPermission = android.Manifest.permission.ACCESS_FINE_LOCATION;
    public SQLiteDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View RootView = inflater.inflate(R.layout.registerayout, container, false);
        reg = RootView.findViewById(R.id.reg);
        et1 = RootView.findViewById(R.id.nameField);
        et2 = RootView.findViewById(R.id.aadharField);
        et3 =RootView. findViewById(R.id.locationField);
        et4 = RootView.findViewById(R.id.econsumerField);
        // fab =RootView. findViewById(R.id.speak);
        reg = RootView.findViewById(R.id.reg);
        loc = RootView.findViewById(R.id.gps);



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

               try {
                   if (ActivityCompat.checkSelfPermission(getActivity(), mPermission) != MockPackageManager.PERMISSION_GRANTED) {
                       ActivityCompat.requestPermissions(getActivity(), new String[]{mPermission}, REQUEST_CODE_PERMISSION);
                   }
               } catch (Exception e) {
                   e.printStackTrace();
               }


             /*  tts = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
                   @Override
                   public void onInit(int i) {
                       if (i != TextToSpeech.ERROR) {
                           tts.setLanguage(Locale.UK);
                       }
                   }
               }); */

         et3.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

                 gps = new GPSTracker(getActivity());
                 if (gps.canGetLocation()) {
                     latitude = gps.getLatitude();
                     longitude = gps.getLongitude();
                     location = latitude + "," + longitude;

                     loc.setImageResource(R.drawable.tick1);

                 } else {
                     gps.showSettingsAlert();
                 }

             }
         });
          /*  public void location(View view) {
                gps = new GPSTracker(getActivity());
                if (gps.canGetLocation()) {
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                    location = latitude + "," + longitude;
                    loc.setImageResource(R.drawable.tick1);
                } else {
                    gps.showSettingsAlert();
                }
            } */


reg.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) { name = et1.getText().toString();
        aadhar = et2.getText().toString();
        econsumer = et4.getText().toString();
        if ((!name.isEmpty()) && (!aadhar.isEmpty()) && (aadhar.length() > 11) && (!econsumer.isEmpty()) && (econsumer.length() > 9) && (location != null)) {
            passcode_pass=et2.getText().toString().substring(0,5)+et4.getText().toString().substring(5,10);
            db =getActivity().openOrCreateDatabase("REGISTRATION_STATUS", Context.MODE_PRIVATE, null);
            db.execSQL("INSERT INTO reg VALUES('1','" + passcode_pass + "');");

            Intent i = new Intent(getActivity(), PairActivity.class);
            i.putExtra(PairActivity.aadhar_name, aadhar);
            i.putExtra(PairActivity.econsumer_name, econsumer);

            String KEY = aadhar.substring(0,5) + econsumer.substring(5,10);
            i.putExtra("KEY",KEY);

            startActivity(i);

            databaseReference = FirebaseDatabase.getInstance().getReference(KEY).child("USER DETAILS");
            databaseReference.child("NAME").setValue(et1.getText().toString());
            databaseReference.child("ADHAAR NUMBER").setValue(et2.getText().toString());
            databaseReference.child("CONSUMER NUMBER").setValue(et4.getText().toString());
            databaseReference.child("LOC LATITUDE").setValue("" + latitude);
            databaseReference.child("LOC LONGITUDE").setValue("" + longitude);
            databaseReference.child("PAIR STATUS").setValue("false");

            databaseReference = FirebaseDatabase.getInstance().getReference("USER LOGIN DETAILS");
            databaseReference.child(KEY).setValue(KEY);

            databaseReference = FirebaseDatabase.getInstance().getReference(KEY).child("ECOMODE STATUS");
            databaseReference.child("WATER HEATER").setValue("Water Heater_false");
            databaseReference.child("IRON BOX").setValue("Iron Box_false");
            databaseReference.child("OUTSIDE LIGHT").setValue("Outside Light_false");
            databaseReference.child("BEDROOM LIGHT").setValue("Bedroom Light_false");
            databaseReference.child("WATER MOTOR").setValue("Water Motor_false");
            databaseReference.child("BEDROOM FAN").setValue("Bedroom Fan_false");
            databaseReference.child("WASHING MACHINE").setValue("Washing Machine_false");

            databaseReference = FirebaseDatabase.getInstance().getReference(KEY).child("DEVICE STATUS");
            databaseReference.child("WATER HEATER").setValue("Water Heater_false");
            databaseReference.child("IRON BOX").setValue("Iron Box_false");
            databaseReference.child("OUTSIDE LIGHT").setValue("Outside Light_false");
            databaseReference.child("BEDROOM LIGHT").setValue("Bedroom Light_false");
            databaseReference.child("WATER MOTOR").setValue("Water Motor_false");
            databaseReference.child("BEDROOM FAN").setValue("Bedroom Fan_false");
            databaseReference.child("WASHING MACHINE").setValue("Washing Machine_false");
        } else {
            Toast.makeText(getContext(), "Enter Valid Credentials", Toast.LENGTH_SHORT).show();
        }

    }
});

               Toast.makeText(getContext(),"hello" ,Toast.LENGTH_SHORT).show();





        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.registerayout, container, false);
    return RootView;
    }


  }


