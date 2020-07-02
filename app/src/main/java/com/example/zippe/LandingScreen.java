package com.example.zippe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.api.Distribution;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class LandingScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ProgressDialog pd;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference userRef = db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
    private Toolbar toolbar;
    private CircleImageView profileImage;
    private DrawerLayout drawerLayout;
    private TextView drawer_email, drawer_username;
    private GoogleSignInClient mGoogleSignInClient;

    private static final String KEY_EMAIL = "email";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_UID = "uid";
    private static final String KEY_PROFILE_IMAGE = "profileUrl";

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.viewprofile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
                break;
            case R.id.viewhome:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeFragment()).commit();
                break;

            case R.id.viewcart:
                Intent myintent = new Intent(getApplicationContext(), barcode.class);
                pd.show();
                db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Cart").limit(1).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "Visit a store first", Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                            item.setChecked(false);
                        } else if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> mList = queryDocumentSnapshots.getDocuments();
                            myintent.putExtra("Store_id", (String) mList.get(0).get("storeId"));
                            startActivity(myintent);
                            item.setChecked(false);
                            pd.dismiss();
                        }

                    }
                });

                break;

            case R.id.logout:
                pd.show();
                FirebaseAuth.getInstance().signOut();
                mGoogleSignInClient.signOut();
                LoginManager.getInstance().logOut();
                Toast.makeText(getApplicationContext(), "You have been logged out", Toast.LENGTH_SHORT).show();
                pd.dismiss();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
                break;

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_screen);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        drawer_email = navigationView.getHeaderView(0).findViewById(R.id.drawer_email);
        drawer_username = navigationView.getHeaderView(0).findViewById(R.id.drawer_username);
        setSupportActionBar(toolbar);
        profileImage = navigationView.getHeaderView(0).findViewById(R.id.profileImage);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.greencolor));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);


        //loadUserData();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.viewhome);
        }


    }

    void loadUserData() {
        userRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            drawer_email.setText(documentSnapshot.getString(KEY_EMAIL));
                            drawer_username.setText(documentSnapshot.getString(KEY_USERNAME));
                        } else {
                            Log.d("loadUserData", "Document does not exist ");
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d("loadUserData", "Failed to retrieve user data " + e.toString());
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        userRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d("loadUserData", "Failed to retrieve user data " + e.toString());
                    return;
                } else if (documentSnapshot.exists()) {
                    drawer_email.setText(documentSnapshot.getString(KEY_EMAIL));
                    drawer_username.setText(documentSnapshot.getString(KEY_USERNAME));
                    Picasso.get().load(documentSnapshot.getString(KEY_PROFILE_IMAGE)).into(profileImage);
                } else {
                    Log.d("loadUserData", "Document does not exist ");
                }
            }
        });
    }

}