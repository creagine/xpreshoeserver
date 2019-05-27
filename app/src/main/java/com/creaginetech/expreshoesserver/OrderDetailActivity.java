package com.creaginetech.expreshoesserver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.creaginetech.expreshoesserver.Common.Common;
import com.creaginetech.expreshoesserver.ViewHolder.OrderDetailAdapter;

public class OrderDetailActivity extends AppCompatActivity {

    TextView order_id, order_phone, order_address, order_total, order_comment;
    String order_id_value="";
    RecyclerView listFoods;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        //cp 21
        order_id = findViewById(R.id.order_id);
        order_phone = findViewById(R.id.order_phone);
        order_address = findViewById(R.id.order_address);
        order_total = findViewById(R.id.order_total);
        order_comment = findViewById(R.id.order_comment );

        listFoods = findViewById(R.id.lstFoods);
        listFoods.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        listFoods.setLayoutManager(layoutManager);

        if (getIntent() != null)
            order_id_value = getIntent().getStringExtra("OrderId");

        //set value
        order_id.setText(order_id_value);
        order_phone.setText(Common.currentRequest.getPhone());
        order_total.setText(Common.currentRequest.getTotal());
        order_address.setText(Common.currentRequest.getAddress());
        order_comment.setText(Common.currentRequest.getComment());

        //TODO CEK INI< TERAPIN DI ORDER DETAIL CLIENT
        OrderDetailAdapter adapter = new OrderDetailAdapter(Common.currentRequest.getItems());
        adapter.notifyDataSetChanged();
        listFoods.setAdapter(adapter);

    }
}
