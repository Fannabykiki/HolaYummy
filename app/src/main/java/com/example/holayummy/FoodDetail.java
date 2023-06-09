package com.example.holayummy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.holayummy.Common.Common;
import com.example.holayummy.Model.Food;
import com.example.holayummy.Model.Rating;
import com.example.holayummy.Database.Database;
import com.example.holayummy.Model.Order;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.Queue;

public class FoodDetail extends AppCompatActivity {

    private ValueEventListener mListener;
    TextView food_name, food_price,food_description;
    ImageView food_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart, btnRating;
    RatingBar ratingBar;
    RatingBar ratingBarAverage;
    TextView showRating, rateCount;
    EditText review;
    Button submit;
    float rateValue;
    String temp;
    String foodId="";


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference foods = database.getReference("Foods");
    DatabaseReference ratingTbl = database.getReference("Rating" + "/" + foodId);

    Food currentFood;


    private void bindingView() {
        btnCart = (FloatingActionButton) findViewById(R.id.btnCart);
        btnRating = (FloatingActionButton) findViewById(R.id.btn_rating);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        food_description = (TextView) findViewById(R.id.food_description);
        food_name = (TextView) findViewById(R.id.food_name);
        food_price = (TextView) findViewById(R.id.food_price);
        food_image = (ImageView) findViewById(R.id.img_food);
        ratingBar = findViewById(R.id.ratingBar);
        ratingBarAverage = findViewById(R.id.ratingBarAverage);
        review = findViewById(R.id.review);
        submit = findViewById(R.id.submitBtn);
        rateCount = findViewById(R.id.rateCount);
    }

    private void bindingAction(){
        EditText editText = findViewById(R.id.itemQuan);
        Button buttonPlus = findViewById(R.id.addBtn);
        Button buttonMinus = findViewById(R.id.removeBtn);
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Database(getBaseContext()).addToCart(new Order(
                        foodId,currentFood.getName(),editText.getText().toString(),currentFood.getPrice(),currentFood.getDiscount()
                ));
                Toast.makeText(FoodDetail.this, "Added to cart", Toast.LENGTH_SHORT).show();
            }
        });
        buttonPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value = Integer.parseInt(editText.getText().toString());
                value++;
                if (value < 0) {
                    value = 0;
                }
                editText.setText(String.valueOf(value));
            }
        });

        buttonMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value = Integer.parseInt(editText.getText().toString());
                value--;
                if (value < 0) {
                    value = 0;
                }
                editText.setText(String.valueOf(value));
            }
        });
        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                rateValue = ratingBar.getRating();
                if(rateValue <=1 && rateValue>=0){
                    rateCount.setText("Bad "+ rateValue + "/5");
                } else if (rateValue<=2&& rateValue >1) {
                    rateCount.setText("Ok "+ rateValue + "/5");
                }else if (rateValue<=3&& rateValue >2) {
                    rateCount.setText("Good "+ rateValue + "/5");
                }else if (rateValue<=4&& rateValue >3) {
                    rateCount.setText("Very Good"+ rateValue + "/5");
                }else if (rateValue<=5&& rateValue >4) {
                    rateCount.setText("Best "+ rateValue + "/5");
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Rating rating = new Rating(Common.currentUser.getPhone(),foodId,String.valueOf(rateValue),String.valueOf(review.getText()));


                mListener = ratingTbl.child(Common.currentUser.getPhone()+foodId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ratingTbl.child(Common.currentUser.getPhone()).removeEventListener(mListener);
                        if(snapshot.child(Common.currentUser.getPhone()).exists()){
                            ratingTbl.child(Common.currentUser.getPhone()).removeValue();

                            ratingTbl.child(Common.currentUser.getPhone()).setValue(rating);
                        } else {
                            ratingTbl.child(Common.currentUser.getPhone()).setValue(rating);
                        }
                        Toast.makeText(FoodDetail.this, "Thanks for rating", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }

                });

            }
        });
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        database = FirebaseDatabase.getInstance();

        bindingView();
        bindingAction();

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        if(getIntent()!= null){
            foodId = getIntent().getStringExtra("FoodId");
        }
        if(!foodId.isEmpty()){

           if(Common.isConnectedToInternet(getBaseContext())){
               getDetailFood(foodId);
               getRatingFood(foodId);
           } else {
               Toast.makeText(this, "Please check connection!!", Toast.LENGTH_SHORT).show();
               return;
           }
        }

    }

    private void getRatingFood(String foodId) {
        Query foodRating = ratingTbl.orderByChild("foodId").equalTo(foodId);

        foodRating.addValueEventListener(new ValueEventListener() {
            int count =0, sum = 0;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                    for(DataSnapshot postSnapshot:snapshot.getChildren()){

                        Rating item = postSnapshot.getValue(Rating.class);
                        sum+=Double.parseDouble(item.getRateValue());
                        count++;
                    }


                if(count!=0){
                    float average = sum/count;
                    ratingBarAverage.setRating(average);

                }





            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getDetailFood(String foodId) {
        foods.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Food food = snapshot.getValue(Food.class);

                    if (food != null) {
                        currentFood = snapshot.getValue(Food.class);
                        Picasso.with(getBaseContext()).load(food.getImage()).into(food_image);
                        collapsingToolbarLayout.setTitle(food.getName());
                        food_price.setText(food.getPrice());
                        food_name.setText(food.getName());
                        food_description.setText(food.getDescription());
                    } else {
                        Log.e("hhaha","Food là null");
                    }
                } else {
                    // Xử lý trường hợp dataSnapshot không tồn tại (ví dụ: hiển thị thông báo lỗi)

                    Toast.makeText(FoodDetail.this, foodId, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mListener != null){
            ratingTbl.removeEventListener(mListener);
        }
    }
}
