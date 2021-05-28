package com.whitespace.mailbook.Class;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.whitespace.mailbook.Activity.MainActivity;
import com.whitespace.mailbook.Activity.ProfileActivity;
import com.whitespace.mailbook.R;

import java.util.HashMap;
import java.util.Map;

public class BottomSheetFeed extends BottomSheetDialogFragment {

    TextView name, email, title;
    FloatingActionButton fab_email, fab_edit, fab_delete;

    Dialog popup;

    private FirebaseFirestore db;
    private DocumentReference document_ref;

    public BottomSheetFeed() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_feed, container, false);

        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        title = view.findViewById(R.id.title);
        fab_email = view.findViewById(R.id.fab_email);
        fab_edit = view.findViewById(R.id.fab_edit);
        fab_delete = view.findViewById(R.id.fab_delete);

        MainActivity activity = (MainActivity) getActivity();
        String ItemID = activity.getItemID();
        String Name = activity.getName();
        String Email = activity.getEmail();
        String Category = activity.getCategory();

        name.setText(Name);
        email.setText(Email);
        title.setText(Category);

        popup = new Dialog(getActivity());

        db = FirebaseFirestore.getInstance();
        document_ref = db.collection("Feed").document(ItemID);

        fab_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent feedback = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + email.getText().toString()));

                try {
                    startActivity(Intent.createChooser(feedback, "Choose an e-mail client"));
                } catch (android.content.ActivityNotFoundException e) {
                    Toast.makeText(getActivity(), "There is no e-mail clint installed!", Toast.LENGTH_SHORT).show();

                }
            }
        });

        fab_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetEdit bottomSheetEdit = new BottomSheetEdit();
                bottomSheetEdit.show(getFragmentManager(), "Edit");
            }
        });

        fab_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.setContentView(R.layout.delete_popup);
                popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                ImageView delete = popup.findViewById(R.id.delete);
                ImageView close = popup.findViewById(R.id.close_popup);

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popup.dismiss();
                    }
                });

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        document_ref.delete();
                        Intent delete = new Intent(getActivity(), MainActivity.class);
                        startActivity(delete);
                        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                });
                popup.show();
            }
        });

        return view;
    }
}
