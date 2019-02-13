package com.creaginetech.expreshoesserver;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.creaginetech.expreshoesserver.Common.Common;
import com.creaginetech.expreshoesserver.Interface.ItemClickListener;
import com.creaginetech.expreshoesserver.Model.Service;
import com.creaginetech.expreshoesserver.Model.Shop;
import com.creaginetech.expreshoesserver.ViewHolder.ServiceViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;

public class HomeNewActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView shopName;
    ImageView shopImage;

    //Firebase database
    FirebaseDatabase database;
    DatabaseReference serviceRef, shopRef;

    //firebase storage
    FirebaseStorage storage;
    StorageReference storageReference;

    FirebaseRecyclerAdapter<Service,ServiceViewHolder> adapter;

    //View
    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;

    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_new);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Init Firebase
        database = FirebaseDatabase.getInstance();
        serviceRef = database.getReference("Service").child(Common.currentUser);
        shopRef = database.getReference("Shop").child(Common.currentUser);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        drawer = findViewById(R.id.drawer_layout);
        FloatingActionButton fab = findViewById(R.id.fab);
        NavigationView navigationView = findViewById(R.id.nav_view);
        recycler_menu = findViewById(R.id.recycler_menu);

        //fab
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeNewActivity.this, AddServiceActivity.class);
                startActivity(intent);
            }
        });

        //drawer layout
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //nav view
        navigationView.setNavigationItemSelectedListener(this);

        //Set Name for user
        View headerView = navigationView.getHeaderView(0);
        shopName = headerView.findViewById(R.id.TextViewShopName);
        shopImage = headerView.findViewById(R.id.CircleImageViewShopImage);

        //Init recycler
        recycler_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_menu.setLayoutManager(layoutManager);

        getShop();

        loadMenu();

    }

    private void getShop() {
        shopRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Shop currentshop = dataSnapshot.getValue(Shop.class);

                if (dataSnapshot.child("shopImage").exists()){

                    //Set Image
                    Picasso.with(HomeNewActivity.this).load(currentshop.getShopImage())
                            .into(shopImage);

                    shopName.setText(currentshop.getShopName());

                } else {

                    Toast.makeText(HomeNewActivity.this,
                            "Silahkan lengkapi data shop anda terlebih dahulu",
                            Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(HomeNewActivity.this, SetupShopActivity.class);
                    startActivity(intent);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadMenu() {

        FirebaseRecyclerOptions<Service> options = new FirebaseRecyclerOptions.Builder<Service>()
                .setQuery(serviceRef,Service.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Service, ServiceViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ServiceViewHolder viewHolder, int position, @NonNull Service model) {
                viewHolder.serviceName.setText(model.getServiceName());
                viewHolder.servicePrice.setText(NumberFormat.getInstance(Locale.GERMAN).format(Integer.parseInt(model.getPrice())));
                Picasso.with(HomeNewActivity.this).load(model.getServiceImage())
                        .into(viewHolder.serviceImage);

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //send Category Id and Start new Activity
                        Intent serviceDetail = new Intent(HomeNewActivity.this,ServiceDetailActivity.class);
                        serviceDetail.putExtra("ServiceId",adapter.getRef(position).getKey());
                        startActivity(serviceDetail);
                    }
                });
            }

            @Override
            public ServiceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.service_item,parent,false);
                return new ServiceViewHolder(itemView);
            }
        };
        adapter.startListening();

        adapter.notifyDataSetChanged(); //refresh data if have data changed
        recycler_menu.setAdapter(adapter);

    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.startListening();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_orders) {

            Intent orderIntent = new Intent(HomeNewActivity.this,OrderStatusActivity.class);
            startActivity(orderIntent);

        } else if (id == R.id.nav_banner) {
            Intent bannerIntent = new Intent(HomeNewActivity.this,BannerActivity.class);
            startActivity(bannerIntent);

        } else if (id == R.id.nav_message) {
            Intent orderIntent = new Intent(HomeNewActivity.this,SendMessageActivity.class);
            startActivity(orderIntent);
//
//        } else if (id == R.id.nav_log_out) {
//            //Logout
//            Intent signIn = new Intent(HomeActivity.this,SignInActivity.class);
//            signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(signIn);
//
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Update and delete
    //coding press ctrl+o
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getTitle().equals(Common.UPDATE))
        {
//            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        else if (item.getTitle().equals(Common.DELETE))
        {
            deleteService(adapter.getRef(item.getOrder()).getKey());
        }

        return super.onContextItemSelected(item);
    }

    private void deleteService(final String key) {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked

                        serviceRef.child(key).removeValue();
                        Toast.makeText(HomeNewActivity.this, "Service deleted !!!", Toast.LENGTH_SHORT).show();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure want to delete this Service ?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }

}
