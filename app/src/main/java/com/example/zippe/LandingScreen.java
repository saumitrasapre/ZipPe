package com.example.zippe;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class LandingScreen extends AppCompatActivity {

    Button logOut;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_screen);

        logOut=findViewById(R.id.logoutbtn);

        pd=new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);


        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.show();
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getApplicationContext(),"You have been logged out",Toast.LENGTH_SHORT).show();
                pd.dismiss();
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}