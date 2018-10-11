package com.creaginetech.xpreshoesshipper.Service;

import com.creaginetech.xpreshoesshipper.Common.Common;
import com.creaginetech.xpreshoesshipper.Model.Token;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        sendTokenToServer(refreshedToken);
    }

    private void sendTokenToServer(String refreshedToken) {
        if (Common.currentShipper != null) {
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference tokens = db.getReference("Tokens");
            Token token = new Token(refreshedToken, true);
            tokens.child(Common.currentShipper.getPhoneShipper()).setValue(token);
        }
    }
}