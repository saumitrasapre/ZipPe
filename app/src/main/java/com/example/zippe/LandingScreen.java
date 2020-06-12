package com.example.zippe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class LandingScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //Button logOut;
    ProgressDialog pd;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.viewprofile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
                break;

            case R.id.logout:
                pd.show();
                FirebaseAuth.getInstance().signOut();
                mGoogleSignInClient.signOut();
                LoginManager.getInstance().logOut();
                Toast.makeText(getApplicationContext(),"You have been logged out",Toast.LENGTH_SHORT).show();
                pd.dismiss();
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
                break;

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_screen);

        //logOut=findViewById(R.id.logoutbtn);
        toolbar=findViewById(R.id.toolbar);
        drawerLayout=findViewById(R.id.drawer_layout);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView=findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        pd=new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);

        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient= GoogleSignIn.getClient(this,gso);

        if(savedInstanceState==null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ProfileFragment()).commit();
            navigationView.setCheckedItem(R.id.viewprofile);
        }



       /* logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.show();
                FirebaseAuth.getInstance().signOut();
                mGoogleSignInClient.signOut();
                LoginManager.getInstance().logOut();
                Toast.makeText(getApplicationContext(),"You have been logged out",Toast.LENGTH_SHORT).show();
                pd.dismiss();
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        });*/
    }
}