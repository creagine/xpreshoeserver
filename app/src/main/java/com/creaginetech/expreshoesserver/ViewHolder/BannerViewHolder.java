package com.creaginetech.expreshoesserver.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.creaginetech.expreshoesserver.Common.Common;
import com.creaginetech.expreshoesserver.Interface.ItemClickListener;
import com.creaginetech.expreshoesserver.R;

public class BannerViewHolder extends RecyclerView.ViewHolder implements
//        View.OnClickListener,
        View.OnCreateContextMenuListener
{

    public TextView banner_name;
    public ImageView banner_image;

//    private ItemClickListener itemClickListener;

    public BannerViewHolder(View itemView){
        super(itemView);

        banner_name = (TextView)itemView.findViewById(R.id.banner_name);
        banner_image = (ImageView)itemView.findViewById(R.id.banner_image);

        itemView.setOnCreateContextMenuListener(this);
//        itemView.setOnClickListener(this); //Remove because we dont need click to item in Recycler Banner so we will remove this event
    }

//    public void setItemClickListener(ItemClickListener itemClickListener) {
//        this.itemClickListener = itemClickListener;
//    }
//
//    @Override
//    public void onClick(View view){
//        itemClickListener.onClick(view,getAdapterPosition(),false);
//    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Select the action");

        contextMenu.add(0,0,getAdapterPosition(), Common.UPDATE);
        contextMenu.add(0,1,getAdapterPosition(), Common.DELETE);
    }
}
