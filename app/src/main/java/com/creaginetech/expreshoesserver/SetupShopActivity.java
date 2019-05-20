package com.creaginetech.expreshoesserver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.creaginetech.expreshoesserver.Common.Common;
import com.creaginetech.expreshoesserver.Model.Shop;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class SetupShopActivity extends AppCompatActivity {

    DatabaseReference shopRef;

    //firebase storage
    FirebaseStorage storage;
    StorageReference storageReference;

    FirebaseUser user;

    EditText edtAlamatShop, edtPhoneShop;
    Button btnSave, btnSelectShopImage, btnSelectLocation;
    ImageView imgShop;

    //select image
    Uri saveUri;

    String imgUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_shop);
        setTitle("Setup New Barbershop");

        //TODO crop foto
        //TODO foto default

        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // get reference to 'order'
        shopRef = FirebaseDatabase.getInstance().getReference("Shop");

        edtAlamatShop = findViewById(R.id.editTextAlamatShop);
        edtPhoneShop = findViewById(R.id.editTextPhone);
        btnSave = findViewById(R.id.buttonFinishService);
        btnSelectShopImage = findViewById(R.id.buttonSelectImage);
        btnSelectLocation = findViewById(R.id.buttonSelectLocation);

        imgShop = findViewById(R.id.imageViewShop);

        Toast.makeText(this, Common.locationSelected + " " + Common.locationName, Toast.LENGTH_LONG).show();

        btnSelectLocation.setText(Common.locationName);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (saveUri == null){
                    Toast.makeText(getApplicationContext(), "Choose image", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (edtAlamatShop.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Enter Shop address", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (edtPhoneShop.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Enter Shop phone number", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (Common.locationName.equals("Select location")){
                    Toast.makeText(getApplicationContext(), "Choose location", Toast.LENGTH_SHORT).show();
                    return;
                }

                saveShop();

            }
        });

        btnSelectShopImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectImage();

            }
        });

        btnSelectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(SetupShopActivity.this, ShopLocationActivity.class);
                startActivity(intent);

            }
        });

    }

    private void selectImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select picture"), Common.PICK_IMAGE_REQUEST);

    }

    //SELECT IMAGE
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null)
        {
            saveUri = data.getData();
            btnSelectShopImage.setText("Image Selected !");
            imgShop.setImageURI(saveUri);

        }
    }

    public void saveShop(){

        if (saveUri != null)
        {

            final ProgressDialog mDialog = new ProgressDialog(SetupShopActivity.this);
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("expreshoes/shopImages/"+imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Toast.makeText(SetupShopActivity.this, "Uploaded !!!", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    mDialog.dismiss();

                                    imgUri = uri.toString();

                                    pushToDatabase();

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(SetupShopActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            double progress = (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uploaded "+progress+" %");

                        }
                    });
        }

    }

    private void pushToDatabase(){

        String shopAddressDetail = edtAlamatShop.getText().toString().trim();
        String phoneShop = edtPhoneShop.getText().toString().trim();
        String idShop = user.getUid();
        String shopLatlng = Common.locationSelected;
        String shopAddress = Common.locationName;

        shopRef.child(idShop).child("shopImage").setValue(imgUri);
        shopRef.child(idShop).child("shopAddress").setValue(shopAddress);
        shopRef.child(idShop).child("shopAddressDetail").setValue(shopAddressDetail);
        shopRef.child(idShop).child("shopPhone").setValue(phoneShop);
        shopRef.child(idShop).child("shopLatlng").setValue(shopLatlng);

        Intent intent = new Intent(SetupShopActivity.this, HomeNewActivity.class);
        Toast.makeText(SetupShopActivity.this,
                "Selamat data anda sudah tersimpan", Toast.LENGTH_LONG).show();
        startActivity(intent);
        finish();

    }

}
