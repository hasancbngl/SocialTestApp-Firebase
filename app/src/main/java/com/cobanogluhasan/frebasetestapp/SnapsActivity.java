package com.cobanogluhasan.frebasetestapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SnapsActivity extends AppCompatActivity {

   public static FirebaseAuth auth=FirebaseAuth.getInstance();
    ListView snapsListView;
    private   ArrayList<String> emails = new ArrayList<>();
    private ArrayList<DataSnapshot> snaps= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snaps);

        snapsListView= findViewById(R.id.snapsListView);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,emails);
        snapsListView.setAdapter(adapter);

        FirebaseDatabase.getInstance().getReference().child("users").child(auth.getCurrentUser().getUid())
                .child("snaps").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                emails.add(snapshot.child("from").getValue().toString());

             if(snapshot!=null) snaps.add(snapshot);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) { }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                int index=0;
                for(DataSnapshot snap: snaps) {
                    if(snap.getKey()==snapshot.getKey()) {
                        
                        snaps.remove(index);
                        emails.remove(index);
                    }
                    index++;
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });


        snapsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DataSnapshot snapshot = snaps.get(position);

                Intent intent = new Intent(getApplicationContext(),ShowSnapActivity.class);
                intent.putExtra("imageName", snapshot.child("imageName").getValue().toString());
                intent.putExtra("imageURL", snapshot.child("imageURL").getValue().toString());
                intent.putExtra("message", snapshot.child("message").getValue().toString());
                intent.putExtra("snapKey", snapshot.getKey().toString());
                intent.putExtra("email", emails.get(position));
                startActivity(intent);
            }
        });



    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainActivity.mAuth.signOut();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         super.onOptionsItemSelected(item);

         switch (item.getItemId()) {
             case R.id.logOut:
                 finish();
                 MainActivity.mAuth.signOut();
                 return true;

             case R.id.createSnap:
                 Intent intent=new Intent(this,CreateSnapActivity.class);
                 startActivity(intent);
                 return true;

             default:return false;
         }
    }
}