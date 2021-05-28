package com.whitespace.mailbook.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.whitespace.mailbook.Class.BottomSheetAdd;
import com.whitespace.mailbook.Class.BottomSheetFeed;
import com.whitespace.mailbook.Class.ThemeSettings;
import com.whitespace.mailbook.Models.Feed;
import com.whitespace.mailbook.Models.FeedAdapter;
import com.whitespace.mailbook.Models.FeedRecyclerDecoration;
import com.whitespace.mailbook.R;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private FloatingActionButton fab;
    private ChipGroup categories;
    private RecyclerView recyclerview;
    private LottieAnimationView anim_empty;
    Dialog popup;

    private String item_id, name, email, category;
    String Category = "All";

    private String userID;
    FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DocumentReference document_reference;
    private CollectionReference feed;

    private FeedAdapter adapter;

    ThemeSettings themeSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Theme Settings
        themeSettings = new ThemeSettings(this);
        if (themeSettings.loadNightModeState() == false) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {

            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        //...............
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_navigation);
        fab = findViewById(R.id.floating_action_button);
        categories = findViewById(R.id.categories);
        anim_empty = findViewById(R.id.anim_empty);

        recyclerview = findViewById(R.id.recyclerview);
        int topPadding = getResources().getDimensionPixelSize(R.dimen.topPadding);
        int bottomPadding = getResources().getDimensionPixelSize(R.dimen.bottomPadding);
        recyclerview.addItemDecoration(new FeedRecyclerDecoration(topPadding, bottomPadding));

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getUid();
        db = FirebaseFirestore.getInstance();

        document_reference = db.collection("Feed").document();

        popup = new Dialog(this);


        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.nav_home:
                        break;
                    case R.id.nav_profile:
                        Intent profile = new Intent(MainActivity.this, ProfileActivity.class);
                        startActivity(profile);
                        finish();
                        break;
                    case R.id.nav_settings:
                        Intent settings = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(settings);
                        break;
                }
                return true;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetAdd bottomSheetAdd = new BottomSheetAdd();
                bottomSheetAdd.show(getSupportFragmentManager(), "Add to feed");
            }
        });

        categories.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup chipGroup, int i) {

                Chip chip = chipGroup.findViewById(i);
                Category = (String) chip.getChipText();
                loadData();
                anim_empty.setProgress(0);
            }
        });

        loadData();

    }

    private void loadData() {
        feed = db.collection("Feed");

        if(Category.equals("All")){

            Query check = feed.whereEqualTo("user_id", userID)
                    .limit(1);

            check.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.getResult().isEmpty()){
                        anim_empty.setVisibility(View.VISIBLE);
                    }else{
                        anim_empty.setVisibility(View.GONE);
                    }
                }
            });

            Query query = feed.whereEqualTo("user_id", userID)
                    .orderBy("name", Query.Direction.ASCENDING);

            PagedList.Config config = new PagedList.Config.Builder()
                    .setInitialLoadSizeHint(10)
                    .setPageSize(15)
                    .build();

            FirestorePagingOptions<Feed> options = new FirestorePagingOptions.Builder<Feed>()
                    .setQuery(query, config, Feed.class)
                    .build();

            adapter = new FeedAdapter(options);
            recyclerview.setHasFixedSize(true);
            recyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            recyclerview.setAdapter(adapter);
            adapter.startListening();
        }else {

            Query check = feed.whereEqualTo("user_id", userID)
                    .whereEqualTo("category",Category)
                    .limit(1);

            check.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.getResult().isEmpty()){
                        anim_empty.setVisibility(View.VISIBLE);
                    }else{
                        anim_empty.setVisibility(View.GONE);
                    }
                }
            });

            Query query = feed.whereEqualTo("user_id", userID)
                    .whereEqualTo("category", Category)
                    .orderBy("name", Query.Direction.ASCENDING);

            PagedList.Config config = new PagedList.Config.Builder()
                    .setInitialLoadSizeHint(10)
                    .setPageSize(15)
                    .build();

            FirestorePagingOptions<Feed> options = new FirestorePagingOptions.Builder<Feed>()
                    .setQuery(query, config, Feed.class)
                    .build();

            adapter = new FeedAdapter(options);
            recyclerview.setHasFixedSize(true);
            recyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            recyclerview.setAdapter(adapter);
            adapter.startListening();

        }




        adapter.setOnItemClickListener(new FeedAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot) {
                item_id = documentSnapshot.getId();
                Feed feed = documentSnapshot.toObject(Feed.class);
                name = feed.getName();
                email = feed.getEmail();
                category = feed.getCategory();


                BottomSheetFeed bottomSheetFeed = new BottomSheetFeed();
                bottomSheetFeed.show(getSupportFragmentManager(), "Feed");
            }
        });
    }

    public String getItemID() {
        return item_id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getCategory() {
        return category;
    }


    @Override
    protected void onStart() {
        super.onStart();
        loadData();
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}