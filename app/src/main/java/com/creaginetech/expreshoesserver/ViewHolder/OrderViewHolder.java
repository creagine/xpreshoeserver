package com.creaginetech.expreshoesserver.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.creaginetech.expreshoesserver.Interface.ItemClickListener;
import com.creaginetech.expreshoesserver.R;

public class OrderViewHolder extends RecyclerView.ViewHolder{
    public TextView txtOrderId,txtOrderStatus,txtOrderPhone,txtOrderAddress,txtOrderDate;

    public Button btnEdit,btnRemove,btnDetail,btnDirection;



    public OrderViewHolder(View itemView) {
        super(itemView);

        txtOrderAddress = (TextView)itemView.findViewById(R.id.order_address);
        txtOrderId = (TextView)itemView.findViewById(R.id.order_id);
        txtOrderStatus = (TextView)itemView.findViewById(R.id.order_status);
        txtOrderPhone = (TextView)itemView.findViewById(R.id.order_phone);
        txtOrderDate = (TextView)itemView.findViewById(R.id.order_date);

        btnEdit = (Button)itemView.findViewById(R.id.btnEdit);
        btnDetail = (Button)itemView.findViewById(R.id.btnDetail);
        btnRemove = (Button)itemView.findViewById(R.id.btnRemove);
        btnDirection = (Button)itemView.findViewById(R.id.btnDirection);
  }
}
