package com.creaginetech.expreshoesserver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ServiceDetailActivity extends AppCompatActivity implements View.OnClickListener {

    EditText service_name,service_price,service_description;
    ImageView service_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    Button btnUpdateItem,btnDeleteItem;

    String serviceId="";

    String imgUri;

    //select image
    Uri saveUri;

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

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select picture"),Common.PICK_IMAGE_REQUEST);

    }

    private void deleteItem() {


//        final StorageReference imageFolder = storageRef.child("expreshoes/serviceImages/"+currentService.getServiceImage());
        final StorageReference imageFolder = storage.getReferenceFromUrl(currentService.getServiceImage());

        imageFolder.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Intent homeIntent = new Intent(ServiceDetailActivity.this,HomeNewActivity.class);
                startActivity(homeIntent);
                service.child(serviceId).removeValue();
                Toast.makeText(ServiceDetailActivity.this, "Item Deleted !", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ServiceDetailActivity.this, "Failed to delete item", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void updateItem() {
        if (saveUri != null)
        {

            final ProgressDialog mDialog = new ProgressDialog(ServiceDetailActivity.this);
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageRef.child("expreshoes/serviceImages/"+imageName);
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

                                    //save to database
                                    pushToDatabase();

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(ServiceDetailActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uploaded "+progress+" %");

                        }
                    });
        }

    }

    private void pushToDatabase(){

        Map<String,Object> update_item = new HashMap<>();
        update_item.put("serviceName",service_name.getText().toString());
        update_item.put("price",service_price.getText().toString());
        update_item.put("description",service_description.getText().toString());
        update_item.put("serviceImage",imgUri);

        service.child(serviceId).updateChildren(update_item).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    Toast.makeText(ServiceDetailActivity.this, "Item was updated !", Toast.LENGTH_SHORT).show();
                Intent homeIntent = new Intent(ServiceDetailActivity.this,HomeNewActivity.class);
                startActivity(homeIntent);
            }
        });

    }

    //SELECT IMAGE
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null)
        {
            saveUri = data.getData();
            service_image.setImageURI(saveUri);

        }
    }



}
