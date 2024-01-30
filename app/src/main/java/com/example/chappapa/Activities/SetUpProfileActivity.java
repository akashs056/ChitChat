package com.example.chappapa.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.example.chappapa.Models.User;
import com.example.chappapa.databinding.ActivitySetUpProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class SetUpProfileActivity extends AppCompatActivity {
    ActivitySetUpProfileBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    ProgressDialog dialog;

    Uri selectedImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivitySetUpProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();
        dialog=new ProgressDialog(this);
        dialog.setMessage("Updating Profile....");
        dialog.setCancelable(false);

        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,01);
            }
        });

        binding.Logincontinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=binding.nameBox.getText().toString();
                if (name.isEmpty()){
                    binding.nameBox.setError("Please enter a name");
                    return;
                }
                dialog.show();
                if (selectedImage!=null){
                    StorageReference reference=storage.getReference().child("profiles").child(auth.getUid());
                    reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()){
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imageuri=uri.toString();
                                        String uid=auth.getUid();
                                        String name=binding.nameBox.getText().toString();
                                        String phone=auth.getCurrentUser().getPhoneNumber();

                                        User user=new User(uid,name,phone,imageuri);
                                        database.getReference().child("Users")
                                                .child(auth.getUid())
                                                .setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        dialog.dismiss();
                                                        Intent intent=new Intent(SetUpProfileActivity.this, MainActivity.class);
                                                        startActivity(intent);
                                                        finishAffinity();
                                                    }});
                                    }
                                });
                            }
                        }
                    });
                }else{
                    String uid=auth.getUid();
                    String phone=auth.getCurrentUser().getPhoneNumber();

                    User user=new User(uid,name,phone,"No image Selected");
                    database.getReference().child("Users")
                            .child(auth.getUid())
                            .setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    dialog.dismiss();
                                    Intent intent=new Intent(SetUpProfileActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finishAffinity();
                                }
                            });
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data!=null && data.getData()!=null){
            binding.profileImage.setImageURI(data.getData());
            selectedImage=data.getData();

        }
    }
}