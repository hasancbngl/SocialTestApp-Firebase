package com.cobanogluhasan.frebasetestapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import  java.net.URL;
import java.util.Objects;
import java.util.UUID;

public class CreateSnapActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CreateSnapActivity";
    EditText messageEditText;
    Button nextButton,chooseImageButton;
    ImageView imageView;
    String imageName = UUID.randomUUID().toString() + ".jpg";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_snap);

        setUpView();

    }

    private void setUpView() {
        messageEditText = findViewById(R.id.messageEditText);
        nextButton  =findViewById(R.id.nextButton);
        chooseImageButton  =findViewById(R.id.chooseImageButton);
        imageView = findViewById(R.id.imageView);

        chooseImageButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
    }


    private void getPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chooseImageButton:
                getPermission();
                break;
            case R.id.nextButton:
                uploadImage();
                break;
        }
    }

    private void uploadImage() {

        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        final UploadTask uploadTask =  FirebaseStorage.getInstance().getReference().child("images").child( imageName).putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(CreateSnapActivity.this, "Upload failed!!", Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                Toast.makeText(CreateSnapActivity.this, "Upload successfull",Toast.LENGTH_SHORT).show();

                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                while ((!uri.isComplete()));
                Uri url = uri.getResult();

                String fileLink = url.toString();
                Log.i(TAG, "onSuccess:url " + fileLink);

                String message = messageEditText.getText().toString().trim();

                Intent intent = new Intent(getApplicationContext(), ChooseUserActivity.class);
                intent.putExtra("imageURL", fileLink);
                intent.putExtra("imageName", imageName);
                intent.putExtra("message", message);

                startActivity(intent);
            }
        });
    }



    private void getPermission() {

        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
        else getPhoto();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri imageUri=data.getData();

        if(requestCode == 1 && resultCode==RESULT_OK && data!=null) {

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageUri);
                imageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==1) {
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto();
            }
        }
    }


}