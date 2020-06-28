package com.example.zippe;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public  class ProfileFragment extends Fragment {

    private CircleImageView profilePageImage;
    private TextView profileUsername,profileLocation,profileEmail;
    private Button profileResetPassword,profileDeleteAccount;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private DocumentReference userRef=db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
    private static final String KEY_EMAIL="email";
    private static final String KEY_USERNAME="username";
    private static final String KEY_UID="uid";
    private static final String KEY_PROFILE_IMAGE="profileUrl";
    private FusedLocationProviderClient client;
    private FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private ProgressDialog pd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_profile,container,false);
        profilePageImage=view.findViewById(R.id.profilePageImage);
        profileUsername=view.findViewById(R.id.profileUsername);
        profileLocation=view.findViewById(R.id.profileLocation);
        profileEmail=view.findViewById(R.id.profileEmail);
        profileResetPassword=view.findViewById(R.id.profileresetPassword);
        profileDeleteAccount=view.findViewById(R.id.profiledeleteAccount);
        client = LocationServices.getFusedLocationProviderClient(getContext());
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Profile");
        setHasOptionsMenu(true);

        pd=new ProgressDialog(getActivity());
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);

        loadUserData();

        profileResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = user.getEmail();
                pd.show();
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "We have sent you instructions to reset your password", Toast.LENGTH_SHORT).show();
                            pd.dismiss();

                        } else {
                            Toast.makeText(getContext(), "User Not Registered", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });



        return view;
    }

    private void loadUserData() {

        userRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists())
                        {
                            profileEmail.setText(documentSnapshot.getString(KEY_EMAIL));
                            profileUsername.setText(documentSnapshot.getString(KEY_USERNAME));
                            Picasso.get().load(documentSnapshot.getString(KEY_PROFILE_IMAGE)).into(profilePageImage);
                        }
                        else {
                            Log.d("loadUserData", "Document does not exist ");
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d("loadUserData", "Failed to retrieve user data "+e.toString());
                    }
                });

        requestPermission();
        if (ActivityCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            client.getLastLocation().addOnSuccessListener((Activity) getContext(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                        List<Address> addresses = null;
                        try {
                            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            profileLocation.setText(addresses.get(0).getSubLocality()+" , "+addresses.get(0).getLocality());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.d("location", "onSuccess: " + addresses.get(0).getSubLocality());
                        Log.d("location", "onSuccess: PinCode1 is " + addresses.get(0).getLocality());
                    }
                }
            });
        }
    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions(getActivity(),new String[]{ACCESS_FINE_LOCATION},1);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.profile_appbar_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}


