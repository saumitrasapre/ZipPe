package com.example.zippe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;


public class Register extends AppCompatActivity {

    private TextView switchtologin;
    private FirebaseAuth mAuth;
    private EditText email,username,password,repeatpassword;
    private TextInputLayout email_layout,username_layout,password_layout,repeat_password_layout;
    private Button signUpBtn;
    private ProgressDialog pd;
    private static final Pattern PASSWORD_PATTERN=
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    //"(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{6,}" +               //at least 6 characters
                    "$");


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

        email_layout=findViewById(R.id.email_layout);
        username_layout=findViewById(R.id.username_layout);
        password_layout=findViewById(R.id.password_layout);
        repeat_password_layout=findViewById(R.id.repeat_password_layout);

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
                if (!validateEmail() | !validatePassword() | !validateUsername()|!validateRepeatPassword()) {
                    return;
                } else {
                    Email = email.getText().toString();
                    Password = password.getText().toString();
                    RepeatPassword = repeatpassword.getText().toString();
                    try {
                        if (Password.length() > 0 && Email.length() > 0) {
                            if (Password.equals(RepeatPassword)) {
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
                                Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Fill All Fields", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private boolean validateEmail()
    {
        String emailString=email.getText().toString().trim();
        if(emailString.isEmpty())
        {
            email_layout.setError("Field cannot be empty");
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(emailString).matches())
        {
            email_layout.setError("Invalid Email");
            return false;
        }
        else
        {
            email_layout.setError(null);
            return true;
        }
    }

    private boolean validateUsername()
    {
        String usernameString=username.getText().toString();
        if(usernameString.isEmpty())
        {
            username_layout.setError("Field cannot be empty");
            return false;
        }
        else if(usernameString.length()>15)
        {
            username_layout.setError("Username too long");
            return false;
        }
        else
        {
            username_layout.setError(null);
            return true;
        }
    }
    private boolean validatePassword()
    {
        String passwordString=password.getText().toString().trim();

        if(passwordString.isEmpty())
        {
            password_layout.setError("Field cannot be empty");
            return false;
        }
        else if(!PASSWORD_PATTERN.matcher(passwordString).matches())
        {
            password_layout.setError("Password too weak");
            return false;
        }
        else
        {
            password_layout.setError(null);
            return true;
        }
    }

    private boolean validateRepeatPassword()
    {
        String passwordString=password.getText().toString().trim();
        String retypePasswordString=repeatpassword.getText().toString().trim();
        if (retypePasswordString.isEmpty())
            {
                repeat_password_layout.setError("Field cannot be empty");
                return false;
            }
        else if(!retypePasswordString.equals(passwordString))
        {
            repeat_password_layout.setError("Passwords do not match");
            return false;
        }
        else
        {
            repeat_password_layout.setError(null);
            return true;
        }
    }

}
