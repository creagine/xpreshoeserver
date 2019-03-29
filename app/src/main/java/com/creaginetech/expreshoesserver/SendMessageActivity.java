package com.creaginetech.expreshoesserver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.creaginetech.expreshoesserver.Common.Common;
import com.creaginetech.expreshoesserver.Model.MyResponse;
import com.creaginetech.expreshoesserver.Model.Notification;
import com.creaginetech.expreshoesserver.Model.Sender;
import com.creaginetech.expreshoesserver.Remote.APIService;
import com.rengwuxian.materialedittext.MaterialEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendMessageActivity extends AppCompatActivity {

    MaterialEditText edtMessage,edtTitle;
    Button btnSend;

    APIService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        mService = Common.getFCMClient();

        edtMessage = (MaterialEditText)findViewById(R.id.edtMessage);
        edtTitle = (MaterialEditText)findViewById(R.id.edtTitle);

        btnSend = (Button)findViewById(R.id.btnSend);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Create message
                Notification notification = new Notification(edtTitle.getText().toString(),edtMessage.getText().toString());

                Sender toTopic = new Sender();
                toTopic.to = new StringBuilder("/topics/").append(Common.topicName).toString();
                toTopic.notification = notification;

                mService.sendNotification(toTopic)
                        .enqueue(new Callback<MyResponse>() {
                            @Override
                            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                if (response.isSuccessful())
                                    Toast.makeText(SendMessageActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<MyResponse> call, Throwable t) {
                                Toast.makeText(SendMessageActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}
