package com.creaginetech.xpreshoesshipper.Common;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;

import com.creaginetech.xpreshoesshipper.Model.Request;
import com.creaginetech.xpreshoesshipper.Model.Shipper;
import com.creaginetech.xpreshoesshipper.Model.ShippingInformation;
import com.creaginetech.xpreshoesshipper.Remote.IGeoCoordinates;
import com.creaginetech.xpreshoesshipper.Remote.RetrofitClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Common {
    public static final String SHIPPER_TABLE = "Shippers";
    public static final String ORDER_NEED_SHIP_TABLE = "OrderNeedShip" ;
    public static final String SHIPPER_INFO_TABLE = "ShippingOrders" ;

    public static final String baseUrl = "https://maps.googleapis.com";


    public static Request currentRequest;
    public static String currentKey;


    public static final int REQUEST_CODE = 1000 ;

    public static Shipper currentShipper;

    public static String convertCodeToStatus(String code)
    {
        if(code.equals("0"))
            return "Placed";
        else if (code.equals("1"))
            return "On my way";
        else if (code.equals("2"))
            return "Shipping";
        else
            return "Shipped";
    }

    public static String getDate(long time)
    {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        StringBuilder date = new StringBuilder(
                android.text.format.DateFormat.format("dd-MM-yyyy HH:mm"
                        ,calendar)
                        .toString());
        return date.toString();
    }

    public static void createShippingOrder(String key, String phoneShipper, Location mLastLocation) {

        ShippingInformation shippingInformation = new ShippingInformation();
        shippingInformation.setOrderId(key);
        shippingInformation.setShipperPhone(phoneShipper);
        shippingInformation.setLat(mLastLocation.getLatitude());
        shippingInformation.setLng(mLastLocation.getLongitude());

        //Create new item on ShipperInformation table
        FirebaseDatabase.getInstance()
                .getReference(SHIPPER_INFO_TABLE)
                .child(key)
                .setValue(shippingInformation)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ERROR",e.getMessage());
                    }
                });
    }

    public static void updateShippingInformation(String currentKey, Location mLastLocation) {
        Map<String,Object> update_location = new HashMap<>();
        update_location.put("lat",mLastLocation.getLatitude());
        update_location.put("lng",mLastLocation.getLongitude());

        FirebaseDatabase.getInstance()
                .getReference(SHIPPER_INFO_TABLE)
                .child(currentKey)
                .updateChildren(update_location)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ERROR",e.getMessage());
                    }
                });
    }

    public static IGeoCoordinates getGeoCodeService(){
        return RetrofitClient.getClient(baseUrl).create(IGeoCoordinates.class);
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int newWidth, int newHeight)
    {
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth,newHeight,Bitmap.Config.ARGB_8888);

        float scaleX = newWidth/(float)bitmap.getWidth();
        float scaleY = newHeight/(float)bitmap.getHeight();
        float pivotX=0,pivotY=0;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX,scaleY,pivotX,pivotY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap,0,0,new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;
    }

}
