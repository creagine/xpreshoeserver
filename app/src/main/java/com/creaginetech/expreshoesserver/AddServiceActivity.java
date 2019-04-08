package com.creaginetech.expreshoesserver;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.creaginetech.expreshoesserver.Common.Common;
import com.creaginetech.expreshoesserver.Model.Service;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.UUID;

public class AddServiceActivity extends AppCompatActivity {

    //Firebase database
    FirebaseDatabase database;
    DatabaseReference serviceRef;

    //firebase storage
    FirebaseStorage storage;
    StorageReference storageRef;

    //Add new Menu Layout
    EditText edtName,edtPrice,edtDesc;
    Button btnSelect,btnSave,btnCancel;
    ImageView imgService;

    //select image
    Uri saveUri;
    private boolean isChanged = false;

    String imgUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service);
        setTitle("Setup New Service");

        //Init Firebase
        database = FirebaseDatabase.getInstance();
        serviceRef = database.getReference("Service").child(Common.currentUser);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        edtName = findViewById(R.id.edtName);
        edtPrice = findViewById(R.id.edtPrice);
        edtDesc = findViewById(R.id.edtDesc);
        btnSelect = findViewById(R.id.btnSelect);
        btnSave = findViewById(R.id.buttonSave);
        btnCancel = findViewById(R.id.buttonCancel);
        imgService = findViewById(R.id.imageViewService);

        //Event for button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (saveUri == null){
                    Toast.makeText(getApplicationContext(), "Choose image", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (edtName.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Enter Service name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (edtPrice.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Enter Service price", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (edtDesc.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Enter Service description", Toast.LENGTH_SHORT).show();
                    return;
                }

                saveService();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(AddServiceActivity.this, HomeNewActivity.class);
                startActivity(intent);

            }
        });

    }

    private void selectImage() {

//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent,"Select picture"),Common.PICK_IMAGE_REQUEST);

        //for check permission if sdk low from marsmalllow
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(AddServiceActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(AddServiceActivity.this, "Permission denied !", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(AddServiceActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            } else {
                // start picker to get image for cropping and then use the image in cropping activity
                bringImagePicture();
            }
        } else {
            bringImagePicture();
        }


    }

    //Crop image | image profile - 2
    private void bringImagePicture() {
        // start picker to get image for cropping and then use the image in cropping activity
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(AddServiceActivity.this);
    }

    //SELECT IMAGE
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
//                && data != null && data.getData() != null)
//        {
//            saveUri = data.getData();
//            btnSelect.setText("Image Selected !");
//            imgService.setImageURI(saveUri);
//
//        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                saveUri = result.getUri();
                //set circleImage for change the picture profile
                imgService.setImageURI(saveUri);
                // if image selected
                isChanged = true;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    public void saveService(){

        if (saveUri != null && isChanged) {


                final ProgressDialog mDialog = new ProgressDialog(AddServiceActivity.this);
                mDialog.show();

                String imageName = UUID.randomUUID().toString();
                final StorageReference imageFolder = storageRef.child("expreshoes/serviceImages/" + imageName);
                imageFolder.putFile(saveUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                Toast.makeText(AddServiceActivity.this, "Uploaded !!!", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(AddServiceActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                mDialog.setMessage("Uploaded " + progress + " %");

                            }
                        });
            }


    }

    private void pushToDatabase(){

        String namaService = edtName.getText().toString().trim();
        String priceService = edtPrice.getText().toString().trim();
        String descService = edtDesc.getText().toString().trim();
        String idService = serviceRef.push().getKey();

        Service newService = new Service(namaService, imgUri, descService, priceService);
        serviceRef.child(idService).setValue(newService);

        Intent intent = new Intent(AddServiceActivity.this, HomeNewActivity.class);
        Toast.makeText(AddServiceActivity.this,
                "New Service was added", Toast.LENGTH_LONG).show();
        startActivity(intent);

    }

}
