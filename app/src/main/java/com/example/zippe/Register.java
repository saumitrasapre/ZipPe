package com.example.zippe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {

    TextView switchtologin;
    FirebaseAuth mAuth;
    EditText email,username,password,repeatpassword;
    Button signUpBtn;
    ProgressDialog pd;

    String Email,Password,RepeatPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        switchtologin=findViewById(R.id.switchtologin);
        email=findViewById(R.id.email);
        username=findViewById(R.id.username);
        password=findViewById(R.id.password);
        repeatpassword=findViewById(R.id.repeatPassword);
        signUpBtn=findViewById(R.id.signUpBtn);

        pd=new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);

        mAuth=FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser()!=null)
        {
            Intent intent=new Intent(getApplicationContext(),LandingScreen.class);
            startActivity(intent);
            finish();
        }

        switchtologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Email=email.getText().toString();
                Password=password.getText().toString();
                RepeatPassword=repeatpassword.getText().toString();
                try {
                    if (Password.length() > 0 && Email.length() > 0) {
                        pd.show();

                        mAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Sign Up Failed", Toast.LENGTH_SHORT).show();
                                    pd.dismiss();
                                    return;
                                    //Log.v("error",task.getResult().toString());
                                } else {
                                    Intent myintent = new Intent(getApplicationContext(), LandingScreen.class);
                                    startActivity(myintent);
                                    pd.dismiss();

                                    Toast.makeText(getApplicationContext(), "Sign Up successful...", Toast.LENGTH_SHORT).show();
                                    finish();
                                    return;
                                }
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "Fill All Fields", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    private void createAccount(String email, String password) {


        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();
                            sendEmailVerification();

                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }


                    }
                });
        // [END create_user_with_email]
    }

    private void sendEmailVerification() {
        // Disable button
        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        // Re-enable button

                        Log.v( "SSSS", String.valueOf(user.isEmailVerified()));

                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }
}
