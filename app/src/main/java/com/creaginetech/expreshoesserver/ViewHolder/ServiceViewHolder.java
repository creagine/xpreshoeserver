package com.creaginetech.expreshoesserver.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.creaginetech.expreshoesserver.Interface.ItemClickListener;
import com.creaginetech.expreshoesserver.R;

public class ServiceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView serviceName,servicePrice;
    public ImageView serviceImage, fav_image, quick_cart;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public ServiceViewHolder(View itemView) {
        super(itemView);

        //edit sesuaikan service_item
        serviceName = (TextView)itemView.findViewById(R.id.service_name);
        serviceImage = (ImageView)itemView.findViewById(R.id.service_image);
        fav_image = (ImageView)itemView.findViewById(R.id.fav);
        servicePrice = (TextView) itemView.findViewById(R.id.service_price);
        quick_cart = (ImageView)itemView.findViewById(R.id.btn_quick_cart);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }


}
