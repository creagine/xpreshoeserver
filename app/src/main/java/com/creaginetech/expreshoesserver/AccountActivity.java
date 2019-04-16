package com.creaginetech.expreshoesserver;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.creaginetech.expreshoesserver.Common.Common;
import com.creaginetech.expreshoesserver.Model.Shop;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class AccountActivity extends AppCompatActivity implements View.OnClickListener {

    EditText edtShopName,edtShopAddress,edtShopPhone;
    ImageView shopImage;

    //select image
    Uri saveUri;
    private boolean isChanged = false;

    FirebaseAuth mAuth;
    FirebaseUser mFirebaseUser;
    FirebaseDatabase database;
    DatabaseReference mDatabaseReference;

    //firebase storage
    FirebaseStorage storage;
    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        mDatabaseReference = database.getReference("Shop").child(Common.currentUser); //tambahi child shopId

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        widgets();

        Toolbar toolbar = findViewById(R.id.toolbarAccount);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        shopImage.setOnClickListener(this);

        loadShopData();
    }

    private void widgets() {

        edtShopName = findViewById(R.id.edtShopName);
        edtShopAddress = findViewById(R.id.edtShopAddress);
        shopImage = findViewById(R.id.shopImage);
        edtShopPhone = findViewById(R.id.edtShopPhone);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.account_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.update_action:
                //TODO:SAVE ACCOUNT FUNCTION
                Toast.makeText(this, "save account clicked !", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.shopImage){
            Toast.makeText(this, "image clicked !", Toast.LENGTH_SHORT).show();
            //TODO : SELECT IMAGE FUCNTION
        }
    }

    private void loadShopData() {

        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Shop shop = dataSnapshot.getValue(Shop.class);

                Picasso.with(AccountActivity.this).load(shop.getShopImage()).into(shopImage);

                edtShopName.setText(shop.getShopName());
                edtShopAddress.setText(shop.getShopAddress());
                edtShopPhone.setText(shop.getShopPhone());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
