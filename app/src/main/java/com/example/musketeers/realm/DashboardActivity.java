package com.example.musketeers.realm;

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcelable;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnCompleteListener<Void> {

    private ToggleButton eco;
    DrawerLayout drawer;
    private TextToSpeech tts;
    ArrayList<String> device_status = new ArrayList<>();
    ArrayList<String> name_voice = new ArrayList<>();
    ArrayList<String> name1 = new ArrayList<>();

    ArrayList<Word> appliances = new ArrayList<>();

    ArrayList<Integer> a_thumb = new ArrayList<>();
    ArrayList<String> a_name = new ArrayList<>();
    ArrayList<Boolean> a_switch = new ArrayList<>();
    ArrayList<Boolean> a_eco = new ArrayList<>();

    WordAdapter adapter;
    ListView applianceListView;
    TextView name;

    private final int REQ_CODE_SPEECH_INPUT = 100;
    private String command, reply;
    DatabaseReference databaseReference;
    public static String passcode_pass = "1234556789";
    public int pass = 0, all = 0;
    public SQLiteDatabase db;
    Cursor c;
    String sno = "1", pro_name;
    Dialog myDialog;

    GPSTracker gps;
    double latitude, longitude;
    private static final String PACKAGE_NAME = "com.google.android.gms.location.Geofence";
    static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";
    private static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;
    static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    static final float GEOFENCE_RADIUS_IN_METERS = 100; // 1 mile, 1.6 km, 1609 m
    private static final String TAG = DashboardActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    @Override
    public void onComplete(@NonNull Task<Void> task) {

    }

    private enum PendingGeofenceTask {
        ADD, NONE
    }

    private GeofencingClient mGeofencingClient;
    private ArrayList<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;
    private PendingGeofenceTask mPendingGeofenceTask = PendingGeofenceTask.NONE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_main);
        passcode_pass = getIntent().getStringExtra("KEY");

        myDialog = new Dialog(this);
        myDialog.setContentView(R.layout.activity_dashboard_main);

        try {
            db = openOrCreateDatabase("REGISTRATION_STATUS", Context.MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS reg(sno VARCHAR,passcode VARCHAR);");
            c = db.rawQuery("SELECT * FROM reg", null);
            if (c.getCount() == 0) {
                Intent nxt = new Intent(DashboardActivity.this,RegisterActivity.class);
                startActivity(nxt);
                return;
            }
            else {
                String a = "1";
                c = db.rawQuery("SELECT * FROM reg WHERE sno='" + a + "'", null);
                if (c.moveToFirst()) {
                    passcode_pass = c.getString(1);
                    Log.d("pass", "pass: " + passcode_pass);
                }
            }
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Database Failure", Toast.LENGTH_SHORT).show();
        }

        mGeofenceList = new ArrayList<>();
        mGeofencePendingIntent = null;
        populateGeofenceList();
        mGeofencingClient = LocationServices.getGeofencingClient(this);

        name = findViewById(R.id.name);

        eco = findViewById(R.id.eco);
        applianceListView = findViewById(R.id.list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.UK);
                }
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference(passcode_pass).child("USER DETAILS");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child:dataSnapshot.getChildren()) {
                    String usrs = child.getValue(String.class);
                    name1.add(usrs);
                }
                if(name1.get(5).equals("false")) {
                    db = openOrCreateDatabase("REGISTRATION_STATUS", Context.MODE_PRIVATE, null);
                    c = db.rawQuery("SELECT * FROM reg WHERE sno='" + sno + "'", null);
                    if (c.moveToFirst()) {
                        db.execSQL("DELETE FROM reg WHERE sno='" + sno + "'");
                        showMessage("Logged Out from Home Control", "Synchronisation Failed");
                        Intent nxt=new Intent(DashboardActivity.this,RegisterActivity.class);
                        startActivity(nxt);
                    }
                } else {
                    latitude = Double.parseDouble(name1.get(2));
                    longitude = Double.parseDouble(name1.get(3));
                    name1.clear();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        databaseReference= FirebaseDatabase.getInstance().getReference(passcode_pass).child("DEVICE STATUS");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = 0;
                for (DataSnapshot child:dataSnapshot.getChildren()) {
                    String usrs = child.getValue(String.class);
                    device_status.clear();
                    device_status.add(usrs);

                    String[] check = usrs.split("_");
                    Boolean swit = Boolean.parseBoolean(check[1]);
                    if (pass < 2) {
                        a_name.add(count, check[0]);
                        name_voice.add(count, check[0].toLowerCase());
                        a_switch.add(count, swit);
                        Log.d("Initial", "initial: " + pass);
                    } else {
                        a_name.set(count, check[0]);
                        name_voice.set(count, check[0].toLowerCase());
                        a_switch.set(count, swit);
                        appliances.clear();
                        adapter.notifyDataSetChanged();
                        Log.d("change", "onDataChange: " + pass);
                    }
                    Log.d("Appliances", "Appliance: " + a_name.get(count));

                    switch (check[0]) {
                        case "Water Heater":
                            if (pass < 2)
                                a_thumb.add(count, R.drawable.waterheater);
                            break;
                        case "Iron Box":
                            if (pass < 2)
                                a_thumb.add(count, R.drawable.iron);
                            break;
                        case "Bedroom Light":
                            if (pass < 2)
                                a_thumb.add(count, R.drawable.lightbulb);
                            break;
                        case "Bedroom Fan":
                            if (pass < 2)
                                a_thumb.add(count, R.drawable.fan);
                            break;
                        case "Washing Machine":
                            if (pass < 2)
                                a_thumb.add(count, R.drawable.wash);
                            break;
                        case "Water Motor":
                            if (pass < 2)
                                a_thumb.add(count, R.drawable.water);
                            break;
                        case "Outside Light":
                            if (pass < 2)
                                a_thumb.add(count, R.drawable.streetlamps);
                            break;
                    }
                    count++;
                }
                all++;
                pass++;
                refresh();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReference= FirebaseDatabase.getInstance().getReference(passcode_pass).child("ECOMODE STATUS");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = 0;
                for (DataSnapshot child:dataSnapshot.getChildren()) {
                    String usrs = child.getValue(String.class);
                    device_status.clear();
                    device_status.add(usrs);

                    String[] check = usrs.split("_");

                    Boolean swit = Boolean.parseBoolean(check[1]);
                    if (pass < 2)
                        a_eco.add(count, swit);
                    else {
                        a_eco.set(count, swit);
                        appliances.clear();
                        adapter.notifyDataSetChanged();
                    }
                    Log.d("init", "init: " + pass);
                    Log.d("eco", "eco: " + a_eco.get(count));
                    count++;
                }
                all++;
                pass++;
                refresh();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void refresh() {
        Log.d("INSIDE", "onDataChange: ");
        int count_ = 0;
        Log.d("size", "size: " + a_thumb.size() + "," + a_name.size() + "," + a_switch.size() + "," + a_eco.size());
        if (all > 1) {
            while (count_ < a_name.size()) {
                Word appliance = new Word(a_thumb.get(count_), a_name.get(count_), a_switch.get(count_), a_eco.get(count_));
                Log.d("Inside", "Inside: " + a_name.get(count_));
                count_++;
                appliances.add(appliance);
            }
            adapter = new WordAdapter(DashboardActivity.this, appliances);
            Parcelable state = applianceListView.onSaveInstanceState();
            applianceListView.setAdapter(adapter);
            applianceListView.onRestoreInstanceState(state);
        }
    }

    private void populateGeofenceList() {
        mGeofenceList.add(new Geofence.Builder()
                .setRequestId("Home")
                .setCircularRegion(
                        latitude,
                        longitude,
                        GEOFENCE_RADIUS_IN_METERS
                )
                .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!checkPermissions()) {
            requestPermissions();
        } else {
            performPendingGeofenceTask();
        }
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(DashboardActivity.this,
                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            ActivityCompat.requestPermissions(DashboardActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void performPendingGeofenceTask() {
        if (mPendingGeofenceTask == PendingGeofenceTask.ADD) {
            addGeofences();
        }
    }

    @SuppressWarnings("MissingPermission")
    private void addGeofences() {
        if (!checkPermissions()) {
            showSnackbar(getString(R.string.insufficient_permissions));
            return;
        }
        mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnCompleteListener(this);
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        mGeofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    private void showSnackbar(final String text) {
        View container = findViewById(android.R.id.content);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    public void ecoMode(View view) {
        if (eco.isChecked()) {
            databaseReference= FirebaseDatabase.getInstance().getReference(passcode_pass).child("ECOMODE STATUS");
            databaseReference.child("WATER HEATER").setValue("Water Heater_true");
            databaseReference.child("IRON BOX").setValue("Iron Box_true");
            databaseReference.child("OUTSIDE LIGHT").setValue("Outside Light_true");
            databaseReference.child("BEDROOM LIGHT").setValue("Bedroom Light_true");
            databaseReference.child("WATER MOTOR").setValue("Water Motor_true");
            databaseReference.child("BEDROOM FAN").setValue("Bedroom Fan_true");
            databaseReference.child("WASHING MACHINE").setValue("Washing Machine_true");
        } else {
            databaseReference= FirebaseDatabase.getInstance().getReference(passcode_pass).child("ECOMODE STATUS");
            databaseReference.child("WATER HEATER").setValue("Water Heater_false");
            databaseReference.child("IRON BOX").setValue("Iron Box_false");
            databaseReference.child("OUTSIDE LIGHT").setValue("Outside Light_false");
            databaseReference.child("BEDROOM LIGHT").setValue("Bedroom Light_false");
            databaseReference.child("WATER MOTOR").setValue("Water Motor_false");
            databaseReference.child("BEDROOM FAN").setValue("Bedroom Fan_false");
            databaseReference.child("WASHING MACHINE").setValue("Washing Machine_false");
        }
        adapter.notifyDataSetChanged();
    }

    public void talk(View view) {
        promptSpeechInput();
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    command = result.get(0).toLowerCase();
                    makeToast(command);
                }
                break;
            }

        }
    }

    private void makeToast(String cmd) {
        int got = 0;
        for (int i = 0; i < name_voice.size(); i++) {
            if (got == 1)
                break;
            String name_voice_ = name_voice.get(i);
            if (cmd.contains("eco")) {
                if (cmd.contains(name_voice_)) {
                    if (cmd.contains("on")) {
                        if (a_eco.get(i)) {
                            got = 1;
                            reply = "eco mode of " + name_voice_ + " already turned on";
                        }
                        else {
                            got = 1;
                            reply = "eco mode of " + name_voice_ + " turned on";
                            databaseReference = FirebaseDatabase.getInstance().getReference(passcode_pass).child("ECOMODE STATUS");
                            databaseReference.child(name_voice_.toUpperCase()).setValue(a_name.get(i) + "_true");
                        }
                    } else if (cmd.contains("off")) {
                        if (!a_eco.get(i)) {
                            got = 1;
                            reply = "eco mode of " + name_voice_ + " already turned off";
                        }
                        else {
                            got = 1;
                            reply = "eco mode of " + name_voice_ + " turned off";
                            databaseReference = FirebaseDatabase.getInstance().getReference(passcode_pass).child("ECOMODE STATUS");
                            databaseReference.child(name_voice_.toUpperCase()).setValue(a_name.get(i) + "_false");
                        }
                    } else {
                        reply = "Pardon! Speak Again.";
                    }
                } else if (cmd.contains("on")) {
                    got = 1;
                    eco.setChecked(true);
                    ecoMode(eco);
                    reply = "eco mode turned on";
                } else if (cmd.contains("off")) {
                    got = 1;
                    eco.setChecked(false);
                    ecoMode(eco);
                    reply = "eco mode turned off";
                } else {
                    reply = "Pardon! Speak Again.";
                }
            } else if (cmd.contains(name_voice_)) {
                if (cmd.contains("on")) {
                    if (a_switch.get(i)) {
                        got = 1;
                        reply = name_voice_ + " already turned on";
                    }
                    else {
                        got = 1;
                        reply = name_voice_ + " turned on";
                        databaseReference = FirebaseDatabase.getInstance().getReference(passcode_pass).child("DEVICE STATUS");
                        databaseReference.child(name_voice_.toUpperCase()).setValue(a_name.get(i) + "_true");
                    }
                } else if (cmd.contains("off")) {
                    if (!a_switch.get(i)) {
                        got = 1;
                        reply = name_voice_ + " already turned off";
                    }
                    else {
                        got = 1;
                        reply = name_voice_ + " turned off";
                        databaseReference = FirebaseDatabase.getInstance().getReference(passcode_pass).child("DEVICE STATUS");
                        databaseReference.child(name_voice_.toUpperCase()).setValue(a_name.get(i) + "_false");
                    }
                } else {
                    Log.d("pardon", "pardon: in");
                    reply = "Pardon! Speak Again.";
                }
            } else {
                Log.d("pardon", "pardon: out");
                reply = "Pardon! Speak Again.";
            }
        }
        Toast.makeText(getApplicationContext(), reply, Toast.LENGTH_SHORT).show();
        tts.speak(reply, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_eco:
                break;
            case R.id.nav_analysis:
                Intent i = new Intent(this, AnalysisActivity.class);
                startActivity(i);
                break;
            case R.id.nav_settings:
                break;
            case R.id.nav_logout:
                db = openOrCreateDatabase("REGISTRATION_STATUS", Context.MODE_PRIVATE, null);
                c = db.rawQuery("SELECT * FROM reg WHERE sno='" + sno + "'", null);
                if (c.moveToFirst()) {
                    db.execSQL("DELETE FROM reg WHERE sno='" + sno + "'");
                    showMessage("Success", "Successfully Logged Out");
                    Intent nxt=new Intent(DashboardActivity.this,RegisterActivity.class);
                    startActivity(nxt);
                }
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}
