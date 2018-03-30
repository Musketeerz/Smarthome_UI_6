package com.example.musketeers.realm;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.test.mock.MockPackageManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

import me.relex.circleindicator.CircleIndicator;

public class RegisterActivity extends FragmentActivity {


    ViewPager viewPager;
    private PagerAdapter mPagerAdapter;

    EditText et1, et2, et4;
    LinearLayout et3;
    ImageView loc, reg;
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
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;
    public SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        viewPager = (ViewPager) findViewById(R.id.vp);
        FragmentManager fragmnetManager = getSupportFragmentManager();
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
         CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        viewPager.setAdapter(mPagerAdapter);

        indicator.setViewPager(viewPager);

        et1 = findViewById(R.id.nameField);
        et2 = findViewById(R.id.aadharField);
        et3 = findViewById(R.id.locationField);
        et4 = findViewById(R.id.econsumerField);
       // fab =RootView. findViewById(R.id.speak);
        //reg = findViewById(R.id.reg);

        myDialog = new Dialog(this);

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
            if (ActivityCompat.checkSelfPermission(this, mPermission) != MockPackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{mPermission}, REQUEST_CODE_PERMISSION);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        loc = findViewById(R.id.gps);

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.UK);
                }
            }
        });
    }

    public void location(View view) {
        gps = new GPSTracker(RegisterActivity.this);
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            location = latitude + "," + longitude;
            loc.setImageResource(R.drawable.tick1);
        } else {
            gps.showSettingsAlert();
        }
    }

    public void register(View view) {
        name = et1.getText().toString();
        aadhar = et2.getText().toString();
        econsumer = et4.getText().toString();
        if ((!name.isEmpty()) && (!aadhar.isEmpty()) && (aadhar.length() > 11) && (!econsumer.isEmpty()) && (econsumer.length() > 9) && (location != null)) {
            passcode_pass=et2.getText().toString().substring(0,5)+et4.getText().toString().substring(5,10);
            db = openOrCreateDatabase("REGISTRATION_STATUS", Context.MODE_PRIVATE, null);
            db.execSQL("INSERT INTO reg VALUES('1','" + passcode_pass + "');");

            Intent i = new Intent(this, PairActivity.class);
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
            Toast.makeText(getApplicationContext(), "Enter Valid Credentials", Toast.LENGTH_SHORT).show();
        }
    }

    public void talk(View view) {
        switch (field) {
            case 1:
                tts.speak("Please tell your name", TextToSpeech.QUEUE_FLUSH, null);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for(int progress = 0; progress<100; progress++) {
                            try {
                                Thread.sleep(15);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        promptSpeechInput();
                    }
                }).start();
                break;
            case 2:
                tts.speak("Please tell your 12 digit aadhar number", TextToSpeech.QUEUE_FLUSH, null);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for(int progress = 0; progress<100; progress++) {
                            try {
                                Thread.sleep(25);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        promptSpeechInput();
                    }
                }).start();
                break;
            case 3:
                tts.speak("Please tell your 10 digit electricity consumer number", TextToSpeech.QUEUE_FLUSH, null);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for(int progress = 0; progress<100; progress++) {
                            try {
                                Thread.sleep(35);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        promptSpeechInput();
                    }
                }).start();
                break;
            case 4:
                ano = et2.getText().toString();
                eno = et4.getText().toString();
                for (int i=0; i<ano.length(); i++)
                    an = an + ano.charAt(i) + " ";
                for (int i=0; i<eno.length(); i++)
                    en = en + eno.charAt(i) + " ";
                tts.speak("Please confirm your input \n" + "\nName: \n" + et1.getText().toString() + "\n Aadhar number: \n" + an + "\n Electricity Consumer number: \n" + en + "\n Would you like to register?", TextToSpeech.QUEUE_FLUSH, null);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for(int progress = 0; progress<100; progress++) {
                            try {
                                Thread.sleep(180);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        promptSpeechInput();
                    }
                }).start();
                break;
        }
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), getString(R.string.speech_not_supported), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    command = result.get(0).toLowerCase();
                    makeToast(command);
                }
                break;
            }
        }
    }

    private void makeToast(String cmd) {
        switch (field) {
            case 1:
                et1.setText(cmd);
                break;
            case 2:
                if (cmd.length() > 11) {
                    cmd = cmd.replace(" ", "");
                    et2.setText(cmd);
                } else
                    talk(fab);
                break;
            case 3:
                if (cmd.length() > 9) {
                    cmd = cmd.replace(" ","");
                    et4.setText(cmd);
                } else
                    talk(fab);
                location(et3);
                break;
           /* case 4:
                if (cmd.equals("yes"))
                    register(reg);
                break; */
        }
        Toast.makeText(getApplicationContext(), cmd, Toast.LENGTH_SHORT).show();
        field ++;
        if (field < 5)
            talk(fab);
        else {
            field = 1;
        }
    }

  /* public void existing_user(View view) {
        TextView sync;
        final EditText adhaar,consumer;
        myDialog.setContentView(R.layout.login_popup);
        sync = myDialog.findViewById(R.id.sync);
        adhaar = myDialog.findViewById(R.id.adhaar);
        consumer = myDialog.findViewById(R.id.consumer);

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String login=adhaar.getText().toString()+consumer.getText().toString();

                if (login_details.contains(login)) {
                    String sno="1";
                    db = openOrCreateDatabase("REGISTRATION_STATUS", Context.MODE_PRIVATE, null);

                    db.execSQL("INSERT INTO reg VALUES('" + sno + "','" +login + "');");

                    Intent nxt = new Intent(RegisterActivity.this, DashboardActivity.class);
                    nxt.putExtra("KEY", login);
                    startActivity(nxt);
                } else {
                    Toast.makeText(getApplicationContext(),"Wrong Passcode",Toast.LENGTH_SHORT).show();
                }
            }
        });
    } */

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent nxt = new Intent(Intent.ACTION_MAIN);
            nxt.addCategory(Intent.CATEGORY_HOME);
            nxt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(nxt);
        }
        return super.onKeyDown(keyCode, event);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int i) {

            android.support.v4.app.Fragment frag = null;

            if (i == 0) {
                frag = new registerfrag();
            }
            if (i == 1) {
                frag = new signinfrag();
            }


            return frag;


        }


        @Override
        public int getCount() {
            return 2;
        }


    }
}