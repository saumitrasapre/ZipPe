package com.example.zippe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import Models.UserModel;
import de.hdodenhof.circleimageview.CircleImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.tooltip.Tooltip;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


public class Register extends AppCompatActivity {

    private TextView switchtologin;
    private FirebaseAuth mAuth;
    private EditText email,username,password,repeatpassword;
    private static final String TAG = "Register";
    private TextInputLayout email_layout,username_layout,password_layout,repeat_password_layout;
    private static final String KEY_EMAIL="email";
    private static final String KEY_USERNAME="username";
    private static final String KEY_UID="uid";
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private Button signUpBtn;
    private ImageButton cancel_profile_picture;
    private ProgressDialog pd;
    private UserModel user=new UserModel();
    private ImageView camera_icon;
    private CircleImageView choose_image;
    private StorageReference mStorageRef;
    private Uri mImageUri;
    private Tooltip tooltip;
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
        cancel_profile_picture=findViewById(R.id.cancel_profile_picture);

        camera_icon=findViewById(R.id.camera_icon);
        choose_image=findViewById(R.id.choose_image);
        mStorageRef= FirebaseStorage.getInstance().getReference("userUploads");

        email_layout=findViewById(R.id.email_layout);
        username_layout=findViewById(R.id.username_layout);
        password_layout=findViewById(R.id.password_layout);
        repeat_password_layout=findViewById(R.id.repeat_password_layout);

        pd=new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);

        mImageUri=Uri.parse("android.resource://"+getPackageName()+"/" + R.drawable.baseline_account_circle);

        mAuth=FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser()!=null)
        {
            Intent intent=new Intent(getApplicationContext(),LandingScreen.class);
            startActivity(intent);
            finish();
        }

        password.addTextChangedListener(registerTextWatcher);
        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {
                    String input=password.getText().toString().trim();
                    if(input.isEmpty())
                    {
                        showTooltip();
                    }
                }
                else
                {
                    tooltip.dismiss();
                }
            }
        });



        switchtologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        choose_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.startPickImageActivity(Register.this);

            }
        });

        cancel_profile_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageUri=Uri.parse("android.resource://"+getPackageName()+"/" + R.drawable.baseline_account_circle);
                choose_image.setImageDrawable(getDrawable(R.drawable.ic_baseline_person_24));
                cancel_profile_picture.setVisibility(View.GONE);
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
                                            createUserModel(v);
                                            saveData(v);
                                            uploadFile();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode== Activity.RESULT_OK)
        {
           Uri mUri=CropImage.getPickImageResultUri(this,data);
            if(CropImage.isReadExternalStoragePermissionsRequired(this,mUri))
            {
                mImageUri=mUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);
            }else
            {
                startCrop(mUri);
            }
        }

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK)
            {
                camera_icon.setVisibility(View.INVISIBLE);
                mImageUri=result.getUri();
                choose_image.setImageURI(mImageUri);
                cancel_profile_picture.setVisibility(View.VISIBLE);
            }
        }

    }

    private void startCrop(Uri mUri) {
        CropImage.activity(mUri).setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }

    private String getFileExtension(Uri uri)
    {
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(getApplicationContext().getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }
        System.out.println("File extension= "+extension);
        return extension;

    }

    private void uploadFile() {
        if(mImageUri!=null)
        {
            StorageReference fileReference=mStorageRef.child(user.getUID()+"."+getFileExtension(mImageUri));

            fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    user.setProfileUrl(uri.toString());
                                    db.collection("Users").document(user.getUID()).set(user);
                                    System.out.println(uri.toString());
                                }
                            });
                            Log.d("ImageUpload", "onSuccess: Image Upload Successful ");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("ImageUpload", "onFailure: Failed to upload image "+e.toString());
                        }
                    });
        }
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

    public void createUserModel(View view)
    {
        String emailString=email.getText().toString();
        String usernameString=username.getText().toString();
        String Uid=mAuth.getUid();
        user.setUID(Uid);
        user.setEmail(emailString);
        user.setUsername(usernameString);
    }

    public void saveData(View view)
    {
        Map<String,Object> userData=new HashMap<>();
        userData.put(KEY_UID,user.getUID());
        userData.put(KEY_EMAIL,user.getEmail());
        userData.put(KEY_USERNAME,user.getUsername());

        db.collection("Users").document(user.getUID()).set(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: User Data Uploaded");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: User data upload failed "+e.toString());
                    }
                });
    }

    private TextWatcher registerTextWatcher =new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {


        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String input=password.getText().toString().trim();
            if(input.isEmpty())
            {
                showTooltip();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

void showTooltip()
{

    tooltip=new Tooltip.Builder(password)
            .setText("Must contain least 1 digit, 1 uppercase, 1 lowercase & 1 special character")
            .setTextColor(Color.WHITE)
            .setBackgroundColor(ResourcesCompat.getColor(getResources(),R.color.greencolor,null))
            .setGravity(Gravity.BOTTOM)
            .setCornerRadius(8f)
            .setDismissOnClick(true)
            .show();
}


}
