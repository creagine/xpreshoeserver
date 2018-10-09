package com.creaginetech.expreshoesserver;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.creaginetech.expreshoesserver.Common.Common;
import com.creaginetech.expreshoesserver.Model.Shipper;
import com.creaginetech.expreshoesserver.ViewHolder.ShipperViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;

public class ShipperManagement extends AppCompatActivity {

    FloatingActionButton fabAdd;

    FirebaseDatabase database;
    DatabaseReference shippers;

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Shipper,ShipperViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipper_management);

        //Init View
        fabAdd = (FloatingActionButton)findViewById(R.id.fab_addShipper);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateShipperLayout();
            }
        });

        recyclerView = (RecyclerView)findViewById(R.id.recycler_shipper);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Firebase
        database = FirebaseDatabase.getInstance();
        shippers = database.getReference(Common.SHIPPERS_TABLE);

        //Load all shippers
        loadAllShippers();
        
    }

    private void loadAllShippers() {
        FirebaseRecyclerOptions<Shipper> allShipper = new FirebaseRecyclerOptions.Builder<Shipper>()
                .setQuery(shippers,Shipper.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Shipper, ShipperViewHolder>(allShipper) {
            @Override
            protected void onBindViewHolder(@NonNull ShipperViewHolder holder, final int position, @NonNull final Shipper model) {
                holder.shipper_phone.setText(model.getPhoneShipper());
                holder.shipper_name.setText(model.getNameShipper());

                holder.btn_editShipper.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showEditShipperDialog(adapter.getRef(position).getKey(),model);
                    }
                });

                holder.btn_removeShipper.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showRemoveShipperDialog(adapter.getRef(position).getKey());
                    }
                });

            }

            @Override
            public ShipperViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.shipper_layout,parent,false);
                return new ShipperViewHolder(itemView);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    private void showRemoveShipperDialog(String key) {
        shippers.child(key)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ShipperManagement.this, "Remove success", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ShipperManagement.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        adapter.notifyDataSetChanged();
    }

    private void showEditShipperDialog(String key,Shipper model) {
        AlertDialog.Builder create_shipper_dialog= new AlertDialog.Builder(ShipperManagement.this);
        create_shipper_dialog.setTitle("Update Shipper");

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.create_shipper_layout,null);

        final MaterialEditText edtNameShipper = (MaterialEditText)view.findViewById(R.id.edtNameShipper);
        final EditText edtPhoneShipper = (EditText)view.findViewById(R.id.edtPhoneShipper);
        final MaterialEditText edtPasswordShipper = (MaterialEditText)view.findViewById(R.id.edtPasswordShipper);

        //set data
        edtNameShipper.setText(model.getNameShipper());
        edtPhoneShipper.setText(model.getPhoneShipper());
        edtPasswordShipper.setText(model.getPasswordShipper());

        create_shipper_dialog.setView(view);

        create_shipper_dialog.setIcon(R.drawable.ic_local_shipping_black_24dp);

        create_shipper_dialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                Map<String,Object> update = new HashMap<>();
                update.put("nameShipper",edtNameShipper.getText().toString());
                update.put("phoneShipper",edtPhoneShipper.getText().toString());
                update.put("passwordShipper",edtPasswordShipper.getText().toString());

                shippers.child(edtPhoneShipper.getText().toString())
                        .updateChildren(update)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ShipperManagement.this, "Shipper Updated !", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ShipperManagement.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        create_shipper_dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        create_shipper_dialog.show();
    }

    private void showCreateShipperLayout() {

        AlertDialog.Builder create_shipper_dialog= new AlertDialog.Builder(ShipperManagement.this);
        create_shipper_dialog.setTitle("Create Shipper");

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.create_shipper_layout,null);

        final MaterialEditText edtNameShipper = (MaterialEditText)view.findViewById(R.id.edtNameShipper);
        final EditText edtPhoneShipper = (EditText)view.findViewById(R.id.edtPhoneShipper);
        final MaterialEditText edtPasswordShipper = (MaterialEditText)view.findViewById(R.id.edtPasswordShipper);

        create_shipper_dialog.setView(view);

        create_shipper_dialog.setIcon(R.drawable.ic_local_shipping_black_24dp);

        create_shipper_dialog.setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                Shipper shipper = new Shipper();
                shipper.setNameShipper(edtNameShipper.getText().toString());
                shipper.setPhoneShipper(edtPhoneShipper.getText().toString());
                shipper.setPasswordShipper(edtPasswordShipper.getText().toString());

                shippers.child(edtPhoneShipper.getText().toString())
                        .setValue(shipper)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ShipperManagement.this, "Shipper create !", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ShipperManagement.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        create_shipper_dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        create_shipper_dialog.show();
    }
}
