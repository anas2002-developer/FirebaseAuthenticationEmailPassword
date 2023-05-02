package com.anas.fdbauthxprofile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

public class HomeActivity extends AppCompatActivity {

    EditText eName, eAge;
    TextView txtEmail, txtUid, txtName, txtAge;
    ImageView IMG;
    Button btnUpdate;

    FirebaseAuth mAuth;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();

        IMG = findViewById(R.id.IMG);
        btnUpdate = findViewById(R.id.btnUpdate);
        txtEmail = findViewById(R.id.txtEmail);
        txtUid = findViewById(R.id.txtUid);

        eName = findViewById(R.id.eName);
        eAge = findViewById(R.id.eAge);

        txtEmail.setText(mAuth.getCurrentUser().getEmail());
        txtUid.setText(mAuth.getCurrentUser().getUid());

        loadDetails();
        loadPROFILE();

        IMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dexter.withActivity(HomeActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                Intent iGallery = new Intent(Intent.ACTION_PICK);
                                iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(iGallery, 200);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).check();

            }
        });


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = eName.getText().toString();
                String age = eAge.getText().toString();
                MODEL model = new MODEL(name, age);


                FirebaseDatabase fdb = FirebaseDatabase.getInstance();
                DatabaseReference root = fdb.getReference();

                root.child("WOMENSAFETY").child("PROFILE").child(mAuth.getCurrentUser().getUid()).setValue(model);


                FirebaseStorage fdbs = FirebaseStorage.getInstance();
                StorageReference roots = fdbs.getReference().child("PROFILE_IMG/" + mAuth.getCurrentUser().getUid());

                roots.putFile(uri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(HomeActivity.this, "success", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(HomeActivity.this, "failure", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == 200) {
                uri = data.getData();
                IMG.setImageURI(uri);
                Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();

            }
        } else {
            Toast.makeText(this, "failure", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadPROFILE() {

        FirebaseStorage fdbs = FirebaseStorage.getInstance();
        StorageReference roots = fdbs.getReference().child("PROFILE_IMG/" + mAuth.getCurrentUser().getUid());

        roots.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Glide.with(HomeActivity.this)
                        .load(uri.toString())
                        .placeholder(R.drawable.loading)
                        .into(IMG);

                Toast.makeText(HomeActivity.this, "success PROFILE", Toast.LENGTH_SHORT).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(HomeActivity.this, "failure Profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void loadDetails() {

        FirebaseDatabase fdb = FirebaseDatabase.getInstance();
        DatabaseReference root = fdb.getReference();

        root.child("WOMENSAFETY").child("PROFILE").child(mAuth.getCurrentUser().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()){

                                if (task.getResult().exists()){
                                    DataSnapshot snapshot = task.getResult();
                                    String name = String.valueOf(snapshot.child("name").getValue());
                                    String age = String.valueOf(snapshot.child("age").getValue());

                                    eName.setText(name);
                                    eAge.setText(age);

                                    Toast.makeText(HomeActivity.this, "success details", Toast.LENGTH_SHORT).show();
                                }


                            }
                            else {
                                Toast.makeText(HomeActivity.this, "failure details", Toast.LENGTH_SHORT).show();
                            }

                    }
                });


    }

}