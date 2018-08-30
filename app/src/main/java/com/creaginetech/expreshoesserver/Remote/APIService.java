package com.creaginetech.expreshoesserver.Remote;

import com.creaginetech.expreshoesserver.Model.MyResponse;
import com.creaginetech.expreshoesserver.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAxMMjcE0:APA91bGzfwH0e9d4HtOj9j9iDlAzPGXyp5CPcvIDtNIQEszt31Ogu30o4-Z7QUHz_F5A3CPU1y9JbsxjFEI0kGVTw0fXXbhPZImnJWmltU1y22oBTSBArw3ugsUw0LL46HByUSUZuZ-h"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
