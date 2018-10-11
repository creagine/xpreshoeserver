package com.creaginetech.xpreshoesshipper.Common;

import com.creaginetech.xpreshoesshipper.Model.Shipper;

import java.util.Calendar;
import java.util.Locale;

public class Common {
    public static final String SHIPPER_TABLE = "Shippers";
    public static final String ORDER_NEED_SHIP_TABLE = "OrderNeedShip" ;

    public static final int REQUEST_CODE = 1000 ;

    public static Shipper currentShipper;

    public static String convertCodeToStatus(String code)
    {
        if(code.equals("0"))
            return "Placed";
        else if (code.equals("1"))
            return "On my way";
        else
            return "Shipping";
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
}
