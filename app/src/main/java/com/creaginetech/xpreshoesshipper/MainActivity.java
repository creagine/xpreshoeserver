package com.creaginetech.xpreshoesshipper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.creaginetech.xpreshoesshipper.Common.Common;
import com.creaginetech.xpreshoesshipper.Model.Shipper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class MainActivity extends AppCompatActivity {

    MaterialEditText edtPhoneShipper,edtPasswordShipper;
    Button btnSignInShipper;

    FirebaseDatabase database;
    DatabaseReference shippers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtPhoneShipper = (MaterialEditText)findViewById(R.id.edtPhoneShipper);
        edtPasswordShipper = (MaterialEditText)findViewById(R.id.edtPasswordShipper);
        btnSignInShipper = (Button)findViewById(R.id.btnSignInShipper);

        //Firebase
        database = FirebaseDatabase.getInstance();
        shippers = database.getReference(Common.SHIPPER_TABLE);

        btnSignInShipper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginShipper(edtPhoneShipper.getText().toString(),edtPasswordShipper.getText().toString());
            }
        });
    }

    private void loginShipper(String phoneShipper, final String passwordShipper) {
        shippers.child(phoneShipper)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists())
                        {
                            Shipper shipper = dataSnapshot.getValue(Shipper.class);
                            if (shipper.getPasswordShipper().equals(passwordShipper))
                            {
                                //if login success
                                startActivity(new Intent(MainActivity.this,HomeActivity.class));
                                Common.currentShipper = shipper;
                                finish();
                            }
                            else {
                                Toast.makeText(MainActivity.this, "Wrong Password !", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Your shipper a phone not exists", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
