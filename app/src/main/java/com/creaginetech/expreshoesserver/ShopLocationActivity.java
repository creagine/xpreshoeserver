package com.creaginetech.expreshoesserver;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.creaginetech.expreshoesserver.Common.Common;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;

public class ShopLocationActivity extends AppCompatActivity {

    Button btnSelectMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_location);

        btnSelectMap = findViewById(R.id.buttonSelectFromMap);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                String lat = String.valueOf(place.getLatLng().latitude);
                String lng = String.valueOf(place.getLatLng().longitude);
                String latlng = lat+","+lng;

                Common.locationSelected = latlng;

                Common.locationName = place.getName().toString();

                Intent intent = new Intent(ShopLocationActivity.this, SetupShopActivity.class);
                startActivity(intent);
                finish();

            }

            @Override
            public void onError(Status status) {

            }
        });

        btnSelectMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ShopLocationActivity.this, SelectFromMapActivity.class);
                startActivity(intent);

            }
        });

    }
}
