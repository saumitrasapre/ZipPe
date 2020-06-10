package com.example.zippe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
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
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.*;
import com.google.firebase.auth.FirebaseUser;
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
    private FirebaseAuth.AuthStateListener authStateListener;

    private LoginButton fbloginButton;
    private CallbackManager mCallbackManager;
    private AccessTokenTracker accessTokenTracker;
    private static final String TAG="FacebookAuthentication";

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
        fbloginButton=(LoginButton)findViewById(R.id.fb_login_button);


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

        googleSignIn.setSize(SignInButton.SIZE_STANDARD);
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

        fbloginButton.setReadPermissions("email","public_profile");

        mCallbackManager= CallbackManager.Factory.create();
        fbloginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG,"onSuccess"+loginResult);
                handleFacebookToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG,"onCancel");

            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG,"onError"+error);

            }
        });

        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        };

        accessTokenTracker=new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if(currentAccessToken==null)
                {
                    mAuth.signOut();
                }
            }
        };

    }



    private void handleFacebookToken(AccessToken accessToken) {
        Log.d(TAG,"handleFacebookToken"+accessToken);

        AuthCredential credential= FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Log.d(TAG,"Sign in with credential successful");
                    FirebaseUser user=mAuth.getCurrentUser();
                    Intent intent = new Intent(getApplicationContext(), LandingScreen.class);
                    startActivity(intent);
                }
                else
                {
                    Log.d(TAG,"Sign in with credential failure",task.getException());
                    Toast.makeText(MainActivity.this,"Authentication Failed",Toast.LENGTH_SHORT).show();
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

        if(requestCode==RC_SIGN_IN_GOOGLE && resultCode == Activity.RESULT_OK  ) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        System.out.println("Facebook request code is "+requestCode);
        if(requestCode==64206 && resultCode == Activity.RESULT_OK ) {

            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {

        try {

            GoogleSignInAccount acc = completedTask.getResult(ApiException.class);
            if(acc != null) {
                FirebaseGoogleAuth(acc);
            }
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

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(authStateListener!=null)
        {
            mAuth.removeAuthStateListener(authStateListener);
        }
    }
}
