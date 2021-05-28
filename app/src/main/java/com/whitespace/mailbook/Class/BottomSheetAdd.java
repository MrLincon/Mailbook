package com.whitespace.mailbook.Class;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.whitespace.mailbook.Activity.MainActivity;
import com.whitespace.mailbook.R;

import java.util.HashMap;
import java.util.Map;

public class BottomSheetAdd extends BottomSheetDialogFragment {

    ChipGroup categories;
    LinearLayout save;
    EditText Name, Email;
    String Category = "Default";

    private String userID;
    FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DocumentReference document_reference;

    public BottomSheetAdd() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_add, container, false);

        categories = view.findViewById(R.id.categories);
        save = view.findViewById(R.id.save);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getUid();
        db = FirebaseFirestore.getInstance();

        Name = view.findViewById(R.id.add_name);
        Email = view.findViewById(R.id.add_email);

        document_reference = db.collection("Feed").document();

        categories.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup chipGroup, int i) {

                Chip chip = chipGroup.findViewById(i);
                Category = (String) chip.getChipText();

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = Name.getText().toString().trim();
                String email = Email.getText().toString().trim();
                String category = Category;

                if (!name.isEmpty()  && !email.isEmpty()) {

                    final String id = document_reference.getId();
                    Map<String, Object> userMap = new HashMap<>();

                    userMap.put("name", name);
                    userMap.put("email",email);
                    userMap.put("category",category);
                    userMap.put("user_id", userID);
                    userMap.put("id", id);
                    userMap.put("timestamp", FieldValue.serverTimestamp());
                    document_reference.set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getActivity(), "Adding..", Toast.LENGTH_SHORT).show();
                            Intent saved = new Intent(getActivity(), MainActivity.class);
                            startActivity(saved);
                            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "You must fill all the fields!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}
