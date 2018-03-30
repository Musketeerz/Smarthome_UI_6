package com.example.musketeers.realm;

import android.app.Activity;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Locale;

public class WordAdapter extends ArrayAdapter<Word> {
    String reply;
    DatabaseReference databaseReference;
    TextToSpeech tts;

    public WordAdapter(Activity context, ArrayList<Word> words) {
        super(context, 0, words);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Word currentWord = getItem(position);
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        tts = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.UK);
                }
            }
        });

        ImageView thumb = listItemView.findViewById(R.id.a_thumb);
        thumb.setImageResource(currentWord.getaThumb());

        final TextView name = listItemView.findViewById(R.id.a_text);
        name.setText(currentWord.getaName());

        Switch swit = listItemView.findViewById(R.id.a_switch);
        swit.setTag(currentWord);
        swit.setChecked(currentWord.getaSwitch());

        ToggleButton eco = listItemView.findViewById(R.id.a_eco);
        eco.setTag(currentWord);
        eco.setChecked(currentWord.getaEco());

        swit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Word currentWord = (Word) view.getTag();
                if (currentWord.getaSwitch()) {
                    reply = currentWord.getaName() + " turned off";
                    databaseReference = FirebaseDatabase.getInstance().getReference(DashboardActivity.passcode_pass).child("DEVICE STATUS");
                    databaseReference.child(currentWord.getaName().toUpperCase()).setValue((currentWord.getaName()) + "_false");
                } else {
                    reply = currentWord.getaName() + " turned on";
                    databaseReference = FirebaseDatabase.getInstance().getReference(DashboardActivity.passcode_pass).child("DEVICE STATUS");
                    databaseReference.child(currentWord.getaName().toUpperCase()).setValue((currentWord.getaName()) + "_true");
                }

                Toast.makeText(getContext(), reply, Toast.LENGTH_SHORT).show();
                tts.speak(reply, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        eco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Word currentWord = (Word) view.getTag();
                if (currentWord.getaEco()) {
                    reply = "eco mode of " + currentWord.getaName() + " turned off";
                    databaseReference = FirebaseDatabase.getInstance().getReference(DashboardActivity.passcode_pass).child("ECOMODE STATUS");
                    databaseReference.child(currentWord.getaName().toUpperCase()).setValue((currentWord.getaName()) + "_false");
                } else {
                    reply = "eco mode of " + currentWord.getaName() + " turned on";
                    databaseReference = FirebaseDatabase.getInstance().getReference(DashboardActivity.passcode_pass).child("ECOMODE STATUS");
                    databaseReference.child(currentWord.getaName().toUpperCase()).setValue((currentWord.getaName()) + "_true");
                }
                Toast.makeText(getContext(), reply, Toast.LENGTH_SHORT).show();
                tts.speak(reply, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        return listItemView;
    }
}