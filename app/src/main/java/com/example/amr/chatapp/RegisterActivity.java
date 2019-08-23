package com.example.amr.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class RegisterActivity extends AppCompatActivity {

    private Button createAccountButton;
    private EditText userEmail,userPassword;
    private TextView alreadyHaveAccount;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;

    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        RootRef = FirebaseDatabase.getInstance().getReference();

        InitialiseFields();

        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToLoginActivity();
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateNewAccount();
            }
        });
    }

    private void CreateNewAccount() {

        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();

        if(TextUtils.isEmpty(email)){

            Toast.makeText(this,"Please Enter your Email ..",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password)){

            Toast.makeText(this,"Please Enter your Password ..",Toast.LENGTH_SHORT).show();
        }
        else {

            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please wait, while we are creating new account for you... ");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()){

                                String deviceToken = FirebaseInstanceId.getInstance().getToken();

                                String currentUserID = mAuth.getCurrentUser().getUid();
                                RootRef.child("Users").child(currentUserID).setValue("");

                                RootRef.child("Users").child(currentUserID).child("device_token")
                                        .setValue(deviceToken);

                                SendUserToMainActivity();
                                Toast.makeText(RegisterActivity.this,"Account Created Successfully ",Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                            else {
                              String message = task.getException().toString();
                                Toast.makeText(RegisterActivity.this,"Error :" + message,Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }

    private void InitialiseFields() {

        createAccountButton = (Button) findViewById(R.id.register_button);
        userEmail = (EditText) findViewById(R.id.register_email);
        userPassword = (EditText) findViewById(R.id.register_password);
        alreadyHaveAccount = (TextView) findViewById(R.id.already_have_account_link);

        loadingBar = new ProgressDialog(this);
    }

    private void SendUserToLoginActivity() {

        Intent LoginIntent = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(LoginIntent);
    }

    private void SendUserToMainActivity() {

        Intent MainIntent = new Intent(RegisterActivity.this,MainActivity.class);
        MainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(MainIntent);
        finish();
    }
}
