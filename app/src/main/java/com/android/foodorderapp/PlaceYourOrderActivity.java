package com.android.foodorderapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.foodorderapp.adapters.PlaceYourOrderAdapter;
import com.android.foodorderapp.model.Menu;
import com.android.foodorderapp.model.RestaurantModel;

public class PlaceYourOrderActivity extends AppCompatActivity {

    private EditText inputName,inputCardNumber, inputCardExpiry, inputCardPin ;
    private RecyclerView cartItemsRecyclerView;
    private TextView tvSubtotalAmount, buttonPlaceYourOrder;
    private SwitchCompat switchVisa;
    private boolean isVisaOn;
    private PlaceYourOrderAdapter placeYourOrderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_your_order);

        RestaurantModel restaurantModel = getIntent().getParcelableExtra("RestaurantModel");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(restaurantModel.getName());
        actionBar.setSubtitle(restaurantModel.getAddress());
        actionBar.setDisplayHomeAsUpEnabled(true);

        inputName = findViewById(R.id.inputName);

        inputCardNumber = findViewById(R.id.inputCardNumber);
        inputCardExpiry = findViewById(R.id.inputCardExpiry);
        inputCardPin = findViewById(R.id.inputCardPin);
        tvSubtotalAmount = findViewById(R.id.tvSubtotalAmount);

        buttonPlaceYourOrder = findViewById(R.id.buttonPlaceYourOrder);
        switchVisa = findViewById(R.id.switchVisa);

        cartItemsRecyclerView = findViewById(R.id.cartItemsRecyclerView);

        buttonPlaceYourOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlaceOrderButtonClick(restaurantModel);
            }
        });

        switchVisa.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    inputCardNumber.setVisibility(View.VISIBLE);
                    inputCardExpiry.setVisibility(View.VISIBLE);
                    inputCardPin.setVisibility(View.VISIBLE);
                    isVisaOn = true;
                } else {
                    inputCardNumber.setVisibility(View.GONE);
                    inputCardExpiry.setVisibility(View.GONE);
                    inputCardPin.setVisibility(View.GONE);

                    isVisaOn = false;
                }
                calculateTotalAmount(restaurantModel);
            }
        });
        initRecyclerView(restaurantModel);
        calculateTotalAmount(restaurantModel);
    }

    private void calculateTotalAmount(RestaurantModel restaurantModel) {
        float totalAmount = 0f;


        for(Menu m : restaurantModel.getMenus()) {
            totalAmount += m.getPrice() * m.getTotalInCart();
        }

        tvSubtotalAmount.setText("₪"+String.format("%.2f", totalAmount));

    }

    private void onPlaceOrderButtonClick(RestaurantModel restaurantModel) {
        if(TextUtils.isEmpty(inputName.getText().toString())) {
            inputName.setError("الرجاء إدخال اسمك  ");
            return;
        } else if( isVisaOn &&TextUtils.isEmpty(inputCardNumber.getText().toString())) {
            inputCardNumber.setError("الرجاء إدخال رقم البطاقة الاكترونية ");
            return;
        }else if(isVisaOn && TextUtils.isEmpty(inputCardExpiry.getText().toString())) {
            inputCardExpiry.setError("الرجاء إدخال تاريخ انتهاء البطاقة ");
            return;
        }else if( isVisaOn && TextUtils.isEmpty(inputCardPin.getText().toString())) {
            inputCardPin.setError("الرجاء إدخال رقم التعريف الشخصي للبطاق ة /cvv  ");
            return;
        }
        //start success activity..
        Intent i = new Intent(PlaceYourOrderActivity.this, OrderSucceessActivity.class);
        i.putExtra("RestaurantModel", restaurantModel);
        startActivityForResult(i, 1000);
    }

    private void initRecyclerView(RestaurantModel restaurantModel) {
        cartItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        placeYourOrderAdapter = new PlaceYourOrderAdapter(restaurantModel.getMenus());
        cartItemsRecyclerView.setAdapter(placeYourOrderAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == 1000) {
            setResult(Activity.RESULT_OK);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home :
                finish();
            default:
                //do nothing
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        finish();
    }
}