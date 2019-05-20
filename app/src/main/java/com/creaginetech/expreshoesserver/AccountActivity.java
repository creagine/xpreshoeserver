package com.creaginetech.expreshoesserver;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.creaginetech.expreshoesserver.Common.Common;
import com.creaginetech.expreshoesserver.Model.Shop;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AccountActivity extends AppCompatActivity implements View.OnClickListener {

    EditText edtShopName,edtShopAddress,edtShopPhone;
    Button btnSelectLocation;
    ImageView shopImage;

    String imgUri;

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

    Shop shop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        //TODO BUGS ketika sudah pilih gambar, click save langsung upload ke storage padahal
        // edittext masih kosong

        //TODO default image ketika belum upload foto

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

        btnSelectLocation.setOnClickListener(this);

        loadShopData();
    }

    private void widgets() {

        edtShopName = findViewById(R.id.edtShopName);
        edtShopAddress = findViewById(R.id.edtShopAddress);
        shopImage = findViewById(R.id.shopImage);
        edtShopPhone = findViewById(R.id.edtShopPhone);
        btnSelectLocation = findViewById(R.id.buttonSelectLocation);

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
               updateAccount();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.shopImage){
            selectImage();
        }

        if (i == R.id.buttonSelectLocation){
            Intent intent = new Intent(AccountActivity.this, ShopLocationActivity.class);
            startActivity(intent);
        }

    }

    private void loadShopData() {

        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                shop = dataSnapshot.getValue(Shop.class);

                Picasso.with(AccountActivity.this).load(shop.getShopImage()).into(shopImage);
                Common.currentShopImage = shop.getShopImage();

                edtShopName.setText(shop.getShopName());
                edtShopAddress.setText(shop.getShopAddress());
                edtShopPhone.setText(shop.getShopPhone());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //[START TO OPEN STORAGE TO SELECT IMAGE] - image profile - 1
    private void selectImage() {

        //for check permission if sdk low from marsmalllow
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(AccountActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(AccountActivity.this, "Permission denied !", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(AccountActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            } else {
                // start picker to get image for cropping and then use the image in cropping activity
                bringImagePicture();
            }
        } else {
            bringImagePicture();
        }

    }
    //[END TO OPEN STORAGE TO SELECT IMAGE]

    //[START to Crop image ] image profile - 2
    private void bringImagePicture() {
        // start picker to get image for cropping and then use the image in cropping activity
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(AccountActivity.this);
    }
    //[END to Crop image ]

    //[START TO OPEN STORAGE TO SELECT IMAGE]
    /*set image crop to image profile circle | image profile - 3
        to image profile - 4 Add to manifest permission and
         add this activity xml in Manifest
     <activity
        android:name="com.theartofdev.edmodo.cropper.CropImageActivity"
        android:theme="@style/Base.Theme.AppCompat" />
        */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                saveUri = result.getUri();
                //set circleImage for change the picture
                shopImage.setImageURI(saveUri);
                // if image selected
                isChanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
    //[END TO OPEN STORAGE TO SELECT IMAGE]

    //[START UPDATE ITEM]
    private void updateAccount() {
        //if user change all item include change service image
        if (saveUri != null && isChanged) {

            final ProgressDialog mDialog = new ProgressDialog(AccountActivity.this);
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageRef.child("expreshoes/shopImages/" + imageName);
//            final StorageReference imageFolder = storage.getReferenceFromUrl(currentService.getServiceImage());
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Toast.makeText(AccountActivity.this, "Uploaded !!!", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    mDialog.dismiss();

                                    //img to string
                                    imgUri = uri.toString();
                                    //push update to database
                                    pushToDatabase();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(AccountActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uploaded " + progress + " %");
                        }
                    });
        } else {
            //if saveUri == null
            //if user just change items service without change the service image
            final StorageReference imageFolder = storage.getReferenceFromUrl(shop.getShopImage());
            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    //img to string
                    imgUri = uri.toString();
                    //save to database
                    pushToDatabase();
                }
            });
        }
    }
    //[END UPDATE ACOUNT]


    //[START push to update database]
    private void pushToDatabase() {

        //[START to Validate items if empty before push to update]
        String shopName = edtShopName.getText().toString();
        String shopAddressDetail = edtShopAddress.getText().toString();
        String shopPhone = edtShopPhone.getText().toString();

        if (TextUtils.isEmpty(shopName) ){
            Toast.makeText(AccountActivity.this, "Shop Name is Empty !", Toast.LENGTH_SHORT).show();
            return;
        } if (TextUtils.isEmpty(shopAddressDetail)){
            Toast.makeText(AccountActivity.this, "Shop Address is Empty !", Toast.LENGTH_SHORT).show();
            return;
        } if (TextUtils.isEmpty(shopPhone)){
            Toast.makeText(AccountActivity.this, "Shop Phone is Empty !", Toast.LENGTH_SHORT).show();
            return;
        } if (Common.locationName.equals("Select location")){
            Toast.makeText(getApplicationContext(), "Choose location", Toast.LENGTH_SHORT).show();
            return;
        }
        //[END to Validate items if empty before push to update]

        Map<String,Object> update_item = new HashMap<>();
        update_item.put("shopName",edtShopName.getText().toString());
        update_item.put("shopAddressDetail",edtShopAddress.getText().toString());
        update_item.put("shopAddress",Common.locationName);
        update_item.put("shopPhone",edtShopPhone.getText().toString());
        update_item.put("shopImage",imgUri); //save image URL string
        update_item.put("shopLatlng",Common.locationSelected);

        //START DELETE LAST IMAGE
        if (!imgUri.equals(Common.currentShopImage)){
            final StorageReference imageFolder = storage.getReferenceFromUrl(Common.currentShopImage);
            imageFolder.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AccountActivity.this, "Failed to update shop", Toast.LENGTH_SHORT).show();
                }
            });
        }
        //END DELETE LAST IMAGE

        mDatabaseReference.updateChildren(update_item).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    Toast.makeText(AccountActivity.this, "Shop was updated !", Toast.LENGTH_SHORT).show();
                Intent homeIntent = new Intent(AccountActivity.this,HomeNewActivity.class);
                startActivity(homeIntent);
                finish();
            }
        });

    }
    //[END push to update database]

}
