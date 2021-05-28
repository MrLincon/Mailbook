package com.whitespace.mailbook.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.whitespace.mailbook.R;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private CircleImageView userImage;
    private TextView userName, userEmail, name, email, phone1, phone2;
    private ImageView edit, copy;

    Dialog popup;

    private String userID;
    FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DocumentReference document_reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        fab = findViewById(R.id.floating_action_button);
        userImage = findViewById(R.id.user_image);
        userName = findViewById(R.id.user_name);
        userEmail = findViewById(R.id.user_email);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone1 = findViewById(R.id.phone1);
        phone2 = findViewById(R.id.phone2);
        edit = findViewById(R.id.edit);
        copy = findViewById(R.id.copy);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getUid();
        FirebaseUser user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        document_reference = db.collection("UserDetails").document(userID);

        loadData();

        popup = new Dialog(this);

        Glide.with(this)
                .load(user.getPhotoUrl())
                .into(userImage);

        userName.setText(user.getDisplayName());
        userEmail.setText(user.getEmail());

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(back);
                finish();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.setContentView(R.layout.my_card_popup);
                popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                ImageView save = popup.findViewById(R.id.save);
                ImageView close = popup.findViewById(R.id.close_popup);
                final EditText Name = popup.findViewById(R.id.add_name);
                final EditText Email = popup.findViewById(R.id.add_email);
                final EditText Phone1 = popup.findViewById(R.id.add_phone1);
                final EditText Phone2 = popup.findViewById(R.id.add_phone2);


                document_reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        if (documentSnapshot.exists()) {


                            String name = documentSnapshot.getString("name");
                            String email = documentSnapshot.getString("email");
                            String phone1 = documentSnapshot.getString("phone1");
                            String phone2 = documentSnapshot.getString("phone2");

                            Name.setText(name);
                            Email.setText(email);
                            Phone1.setText(phone1);
                            Phone2.setText(phone2);

                        } else {
                            Toast.makeText(ProfileActivity.this, "Something wrong!", Toast.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popup.dismiss();
                    }
                });

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String name = Name.getText().toString().trim();
                        String email = Email.getText().toString().trim();
                        String phone1 = Phone1.getText().toString().trim();
                        String phone2 = Phone2.getText().toString().trim();

                        if (!name.isEmpty()  && !email.isEmpty() && !phone1.isEmpty()) {

                            final String id = document_reference.getId();
                            Map<String, Object> userMap = new HashMap<>();

                            userMap.put("name", name);
                            userMap.put("email",email);
                            userMap.put("phone1",phone1);
                            userMap.put("phone2",phone2);
                            userMap.put("user_id", userID);
                            userMap.put("id", id);
                            userMap.put("timestamp", FieldValue.serverTimestamp());
                            document_reference.set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(ProfileActivity.this, "Adding..", Toast.LENGTH_LONG).show();
                                    popup.dismiss();
                                    restartApp();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            Toast.makeText(ProfileActivity.this, "You must fill all the fields!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                popup.show();
            }
        });

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Name = name.getText().toString().trim();
                String Email = email.getText().toString().trim();
                String Phone1 = phone1.getText().toString().trim();
                String Phone2 = phone2.getText().toString().trim();

                ClipboardManager clipboard = (ClipboardManager) getSystemService(getApplicationContext().CLIPBOARD_SERVICE);

                if (!Name.isEmpty()  && !Email.isEmpty() && !Phone1.isEmpty() && !Phone2.isEmpty()){
                    ClipData clip = ClipData.newPlainText("userDetails", "Name: "+Name+"\n"+"E-mail: "+Email+"\n"+"Phone: "+Phone1+", "+Phone2);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(ProfileActivity.this, "Copied", Toast.LENGTH_LONG).show();
                }else if (!Name.isEmpty()  && !Email.isEmpty() && !Phone1.isEmpty()){
                    ClipData clip = ClipData.newPlainText("userDetails", "Name: "+Name+"\n"+"E-mail: "+Email+"\n"+"Phone: "+Phone1);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(ProfileActivity.this, "Copied", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(ProfileActivity.this, "Copied", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadData() {
        document_reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.exists()) {


                    String Name = documentSnapshot.getString("name");
                    String Email = documentSnapshot.getString("email");
                    String Phone1 = documentSnapshot.getString("phone1");
                    String Phone2 = documentSnapshot.getString("phone2");

                    name.setText(Name);
                    email.setText(Email);
                    phone1.setText(Phone1);
                    phone2.setText(Phone2);

                } else {
                    Toast.makeText(ProfileActivity.this, "Something wrong!", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void restartApp() {
        Intent restart = new Intent(ProfileActivity.this, ProfileActivity.class);
        startActivity(restart);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent back = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(back);
        finish();
    }
}