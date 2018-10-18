package com.creaginetech.expreshoesserver.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.creaginetech.expreshoesserver.Interface.ItemClickListener;
import com.creaginetech.expreshoesserver.R;

import info.hoang8f.widget.FButton;

public class ShipperViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView shipper_name,shipper_phone;
    public Button btn_editShipper,btn_removeShipper;
    private ItemClickListener itemClickListener;

    public ShipperViewHolder(View itemView) {
        super(itemView);

        shipper_name = (TextView)itemView.findViewById(R.id.shipper_name);
        shipper_phone = (TextView)itemView.findViewById(R.id.shipper_phone);
        btn_editShipper = (Button)itemView.findViewById(R.id.btnEditShipper);
        btn_removeShipper = (Button)itemView.findViewById(R.id.btnRemoveShipper);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);

    }
}
