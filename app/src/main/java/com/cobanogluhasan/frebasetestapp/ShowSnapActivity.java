package com.cobanogluhasan.frebasetestapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class ShowSnapActivity extends AppCompatActivity {

    private static final String TAG = "ShowSnapActivity";
    TextView fromTextView,messageTextView;
    ImageView snapImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_snap);

        setUpView();

        getIntent();

        fromTextView.setText(getIntent().getStringExtra("email"));
        messageTextView.setText(getIntent().getStringExtra("message"));

        ImageDownloader imageDownloader = new ImageDownloader();
        Bitmap bitmap ;

        try {
            bitmap = imageDownloader.execute(getIntent().getStringExtra("imageURL")).get();
            snapImageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        FirebaseDatabase.getInstance().getReference().child("users").child(SnapsActivity.auth.getCurrentUser().getUid())
                .child("snaps").child(getIntent().getStringExtra("snapKey")).removeValue();

        FirebaseStorage.getInstance().getReference().child("images").child(getIntent().getStringExtra("imageName")).delete();
    }


    private void setUpView() {
        fromTextView = findViewById(R.id.fromTextView);
        messageTextView = findViewById(R.id.messageTextView);
        snapImageView = findViewById(R.id.snapImageView);
    }


    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> //it'll return the image itself.
    {
        @Override
        protected Bitmap doInBackground(String... urls) {

            try {

                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection() ;
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                return bitmap;

            }catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


}