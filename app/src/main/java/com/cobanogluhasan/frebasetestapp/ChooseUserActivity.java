package com.cobanogluhasan.frebasetestapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChooseUserActivity extends AppCompatActivity {

    private static final String TAG = "ChooseUserActivity";

    ListView usersListView;
    private ArrayList<String> emails = new ArrayList<>();
   private ArrayList<String> keys = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_user);

        usersListView = findViewById(R.id.usersListView);



        final ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, emails);
        usersListView.setAdapter(arrayAdapter);


       FirebaseDatabase.getInstance().getReference().child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
               String email = (String) snapshot.child("email").getValue().toString();
                String key = (String) snapshot.getKey().toString();
                emails.add(email);
                keys.add(key);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

       getIntent();

       usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Map<String, String> snapsMap = new HashMap<String, String>();
               snapsMap.put("from", FirebaseAuth.getInstance().getCurrentUser().getEmail());
               snapsMap.put("imageURL", getIntent().getStringExtra("imageURL"));
               snapsMap.put("imageName", getIntent().getStringExtra("imageName"));
               snapsMap.put("message", getIntent().getStringExtra("message"));

               FirebaseDatabase.getInstance().getReference().child("users").child(keys.get(position))
                       .child("snaps").push().setValue(snapsMap);

               Intent intent = new Intent(getApplicationContext(), SnapsActivity.class);
               intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //wipe out back button history
               startActivity(intent);

           }
       });


    }
}