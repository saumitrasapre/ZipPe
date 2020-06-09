package com.example.zippe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.*;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {
//HELLO 123 ANDROID TESTING
private TextView switchtosignup;
private EditText signInEmail,signInPassword;
private Button loginBtn,forgotpassword;
private ProgressDialog pd;
private FirebaseAuth mAuth;
private SignInButton googleSignIn;
private GoogleSignInClient mGoogleSignInClient;
private int RC_SIGN_IN_GOOGLE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        switchtosignup =(TextView)findViewById(R.id.switchtosignup);
        signInEmail=(EditText)findViewById(R.id.signinemail);
        signInPassword=(EditText)findViewById(R.id.signinpassword);
        loginBtn=(Button)findViewById(R.id.loginbtn);
        forgotpassword=(Button)findViewById(R.id.forgotpassword);
        googleSignIn=(SignInButton) findViewById(R.id.googleLogin);
        mAuth=FirebaseAuth.getInstance();

        pd=new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);

        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient=GoogleSignIn.getClient(this,gso);

        switchtosignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),Register.class);
                startActivity(intent);
                finish();
            }
        });
        googleSignIn.setSize(SignInButton.SIZE_ICON_ONLY);
        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });

       forgotpassword.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
            Intent intent=new Intent(getApplicationContext(),ForgotPassword.class);
            startActivity(intent);
           }
       });

        if(mAuth.getCurrentUser()!=null)
        {
            Intent intent=new Intent(getApplicationContext(),LandingScreen.class);
            startActivity(intent);
            finish();
        }
        GoogleSignInAccount account=GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(account!=null)
        {
            Intent intent=new Intent(getApplicationContext(),LandingScreen.class);
            startActivity(intent);
            finish();
        }

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String SignInemail = signInEmail.getText().toString();
                final String SignInpassword = signInPassword.getText().toString();

                try {
                    if (SignInpassword.length() > 0 && SignInemail.length() > 0) {

                        pd.show();
                        mAuth.signInWithEmailAndPassword(SignInemail, SignInpassword).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(getApplicationContext(), LandingScreen.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Error signing in...", Toast.LENGTH_SHORT).show();
                                    pd.dismiss();
                                    return;
                                }
                                pd.dismiss();
                            }
                        });
                    } else {
                        Toast.makeText(MainActivity.this, "Fill All Fields", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            });
}

    private void signInWithGoogle() {
        Intent signInIntent =mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN_GOOGLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN_GOOGLE)
        {
            Task<GoogleSignInAccount> task =GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {

        try {
            GoogleSignInAccount acc= completedTask.getResult(ApiException.class);

            FirebaseGoogleAuth(acc);
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),"Google Sign In Failed",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            FirebaseGoogleAuth(null);
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acc) {

        AuthCredential authCredential= GoogleAuthProvider.getCredential(acc.getIdToken(),null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(getApplicationContext(), LandingScreen.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(),"Signed In With Google",Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    //Toast.makeText(getApplicationContext(), "Error signing in...", Toast.LENGTH_SHORT).show();
                    //pd.dismiss();
                    return;
                }
            }
        });
    }
}
