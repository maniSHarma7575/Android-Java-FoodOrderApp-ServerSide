package com.example.bhandaraserver;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bhandaraserver.Common.Common;
import com.example.bhandaraserver.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignIn extends AppCompatActivity {

    EditText edtphone,edtpassword;
    Button btnsignin;
    FirebaseDatabase db;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        edtpassword=(MaterialEditText)findViewById(R.id.edtPassword);
        edtphone=(MaterialEditText)findViewById(R.id.edtPhone);
        btnsignin=(Button)findViewById(R.id.insign);
        db=FirebaseDatabase.getInstance();
        users=db.getReference("User");
        btnsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUser(edtphone.getText().toString(),edtpassword.getText().toString());

            }
        });

    }

    private void signInUser(String phone, String password) {
        final ProgressDialog mdialog=new ProgressDialog(SignIn.this);
        mdialog.setMessage("Please wait...!");
        mdialog.show();

        final String localphone=phone;
        final String localpassword=password;
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(localphone).exists())
                {
                    mdialog.dismiss();
                    User user=dataSnapshot.child(localphone).getValue(User.class);
                    user.setPhone(localphone);
                    if(Boolean.parseBoolean(user.getIsStaff()))
                    {
                        if(user.getPassword().equals(localpassword))
                        {
                            Intent login=new Intent(SignIn.this, Activity.class);
                            Common.currentUser=user;
                            startActivity(login);
                            finish();
                        }
                        else
                        {
                            Toast.makeText(SignIn.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(SignIn.this, "Please Login with staff account", Toast.LENGTH_SHORT).show();
                    }

                }
                else
                {
                    mdialog.dismiss();
                    Toast.makeText(SignIn.this, "User dosen't exists in the database", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
