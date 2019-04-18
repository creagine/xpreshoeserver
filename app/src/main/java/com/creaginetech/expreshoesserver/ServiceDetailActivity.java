package com.creaginetech.expreshoesserver;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.creaginetech.expreshoesserver.Common.Common;
import com.creaginetech.expreshoesserver.Model.Service;
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

public class ServiceDetailActivity extends AppCompatActivity implements View.OnClickListener {

    EditText service_name,service_price,service_description;
    ImageView service_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    Button btnUpdateItem,btnDeleteItem;
    Toolbar toolbar;

    String serviceId="";

    String imgUri;

    //select image
    Uri saveUri;
    private boolean isChanged = false;


    FirebaseAuth mAuth;
    FirebaseUser mFirebaseUser;
    FirebaseDatabase database;
    DatabaseReference service;

    //firebase storage
    FirebaseStorage storage;
    StorageReference storageRef;

    Service currentService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_detail);

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        service = database.getReference("Service").child(mFirebaseUser.getUid()); //tambahi child shopId

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        service_description = findViewById(R.id.service_description);
        service_name = findViewById(R.id.service_name);
        service_price = findViewById(R.id.service_price);
        service_image = findViewById(R.id.img_service);
        btnUpdateItem = findViewById(R.id.btnUpdateItem);
        btnDeleteItem = findViewById(R.id.btnDeleteItem);
//
//        Toolbar toolbar = findViewById(R.id.toolbar2);
//        setSupportActionBar(toolbar);

        btnUpdateItem.setOnClickListener(this);
        btnDeleteItem.setOnClickListener(this);
        service_image.setOnClickListener(this);

        //Collapsing Tool bar
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        serviceId = Common.serviceSelected;
        getDetailService(serviceId);
    }

    private void getDetailService(String serviceId) {

        service.child(serviceId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentService = dataSnapshot.getValue(Service.class);

                //Set Image
                Picasso.with(getBaseContext()).load(currentService.getServiceImage())
                        .into(service_image);
                Common.currentImage = currentService.getServiceImage();

//                collapsingToolbarLayout.setTitle(currentService.getServiceName());
                service_price.setText(currentService.getPrice());
                service_name.setText(currentService.getServiceName());
                service_description.setText(currentService.getDescription());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.btnUpdateItem){
            updateItem();
        } else if (i == R.id.btnDeleteItem){
            deleteItem();
        }else if (i == R.id.img_service){
            selectImage();
        }
    }

    //[START TO OPEN STORAGE TO SELECT IMAGE] - image profile - 1
    private void selectImage() {

        //for check permission if sdk low from marsmalllow
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(ServiceDetailActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(ServiceDetailActivity.this, "Permission denied !", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(ServiceDetailActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            } else {
                // start picker to get image for cropping and then use the image in cropping activity
                bringImagePicture();
            }
        } else {
            bringImagePicture();
        }

    }
    //[END TO OPEN STORAGE TO SELECT IMAGE]

    //Crop image | image profile - 2
    private void bringImagePicture() {
        // start picker to get image for cropping and then use the image in cropping activity
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(ServiceDetailActivity.this);
    }

    //[START DELETE ITEM]
    private void deleteItem() {

        //init Alert Dialog to delete Item - 1 (Alert dialog)
        final AlertDialog.Builder builder = new AlertDialog.Builder(ServiceDetailActivity.this);
        builder.setCancelable(false);
        builder.setTitle("Delete Item");
        builder.setMessage("Are you sure want to delete this item ?");
        //set Listeners for dialog button - 2 (Alert dialog)
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //DELETE ITEM
                final StorageReference imageFolder = storage.getReferenceFromUrl(currentService.getServiceImage());
                imageFolder.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Intent homeIntent = new Intent(ServiceDetailActivity.this,HomeNewActivity.class);
                        startActivity(homeIntent);
                        finish();
                        service.child(serviceId).removeValue();
                        Toast.makeText(ServiceDetailActivity.this, "Item Deleted !", Toast.LENGTH_SHORT).show();


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ServiceDetailActivity.this, "Failed to delete item", Toast.LENGTH_SHORT).show();
                    }
                });
                //END delete item

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        //create the alert dialog to show it - 3 (Alert dialog)
        builder.create().show();
    }
    //[END DELETE ITEM]

    //[START UPDATE ITEM]
    private void updateItem() {
        //if user change all item include change service image
        if (saveUri != null && isChanged) {

                final ProgressDialog mDialog = new ProgressDialog(ServiceDetailActivity.this);
                mDialog.show();

                String imageName = UUID.randomUUID().toString();
                final StorageReference imageFolder = storageRef.child("expreshoes/serviceImages/" + imageName);
//            final StorageReference imageFolder = storage.getReferenceFromUrl(currentService.getServiceImage());
                imageFolder.putFile(saveUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                Toast.makeText(ServiceDetailActivity.this, "Uploaded !!!", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(ServiceDetailActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                final StorageReference imageFolder = storage.getReferenceFromUrl(currentService.getServiceImage());
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
    //[END UPDATE ITEM]

    //[START push to update database]
    private void pushToDatabase(){

        //[START to Validate items if empty before push to update]
        String serviceName = service_name.getText().toString();
        String servicePrice = service_price.getText().toString();
        String serviceDesc = service_description.getText().toString();

        if (TextUtils.isEmpty(serviceName) ){
            Toast.makeText(ServiceDetailActivity.this, "Service Name is Empty !", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(servicePrice)){
            Toast.makeText(ServiceDetailActivity.this, "Service Price is Empty !", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(serviceDesc)){
            Toast.makeText(ServiceDetailActivity.this, "Service Description is Empty !", Toast.LENGTH_SHORT).show();
            return;
        }
        //[END to Validate items if empty before push to update]

        Map<String,Object> update_item = new HashMap<>();
        update_item.put("serviceName",service_name.getText().toString());
        update_item.put("price",service_price.getText().toString());
        update_item.put("description",service_description.getText().toString());
        update_item.put("serviceImage",imgUri); //save image URL string

        //START DELETE LAST IMAGE
        if (!imgUri.equals(Common.currentImage)){
            final StorageReference imageFolder = storage.getReferenceFromUrl(Common.currentImage);
            imageFolder.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ServiceDetailActivity.this, "Failed to update item", Toast.LENGTH_SHORT).show();
                }
            });
        }
        //END DELETE LAST IMAGE

        service.child(serviceId).updateChildren(update_item).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    Toast.makeText(ServiceDetailActivity.this, "Item was updated !", Toast.LENGTH_SHORT).show();
                Intent homeIntent = new Intent(ServiceDetailActivity.this,HomeNewActivity.class);
                startActivity(homeIntent);
                finish();
            }
        });
    }
    //[END push to update database]

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
                service_image.setImageURI(saveUri);
                // if image selected
                isChanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
    //[END TO OPEN STORAGE TO SELECT IMAGE]

}
