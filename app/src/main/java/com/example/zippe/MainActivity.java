package com.example.zippe;

import androidx.annotation.NonNull;
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

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.common.*;

public class MainActivity extends AppCompatActivity {
//HELLO 123 ANDROID TESTING
TextView switchtosignup;
EditText signInEmail,signInPassword;
Button loginBtn,forgotpassword;
private ProgressDialog pd;
FirebaseAuth mAuth;
ImageView googlesignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        switchtosignup =(TextView)findViewById(R.id.switchtosignup);
        signInEmail=(EditText)findViewById(R.id.signinemail);
        signInPassword=(EditText)findViewById(R.id.signinpassword);
        loginBtn=(Button)findViewById(R.id.loginbtn);
        forgotpassword=(Button)findViewById(R.id.forgotpassword);
        googlesignInButton=(ImageView) findViewById(R.id.googleLogin);
        mAuth=FirebaseAuth.getInstance();

        pd=new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);

        switchtosignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),Register.class);
                startActivity(intent);
                finish();
            }
        });
        googlesignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
    }
