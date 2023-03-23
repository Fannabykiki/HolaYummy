package com.example.holayummy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.holayummy.Common.Common;
import com.example.holayummy.Database.Database;
import com.example.holayummy.Model.Order;
import com.example.holayummy.Model.Request;
import com.example.holayummy.ViewHolder.CartAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Cart extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference requests;
    TextView txtTotal;
    Button btnPlace;
    List<Order> cart = new ArrayList<>();

    CartAdapter adapter;
    private void bindingView() {
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");
        recyclerView = findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        txtTotal = findViewById(R.id.total);
        btnPlace = findViewById(R.id.btnPlaceOrder);
    }

    private void bindingAction() {
        loadListFood();
        btnPlace.setOnClickListener(view -> showAlertDialog());
    }

    private void showAlertDialog() {
        AlertDialog.Builder alerDiaglog = new AlertDialog.Builder(Cart.this);
        alerDiaglog.setTitle("One more step!!");
        alerDiaglog.setMessage("Enter your address:");

        LayoutInflater inflater = this.getLayoutInflater();
        View order_address_comment = inflater.inflate(R.layout.order_address_comment,null);
        MaterialEditText editAddress = (MaterialEditText)order_address_comment.findViewById(R.id.edtAddress);
        MaterialEditText editComment = (MaterialEditText)order_address_comment.findViewById(R.id.edtComment);

        alerDiaglog.setView(order_address_comment);
        alerDiaglog.setIcon(R.drawable.baseline_shopping_cart_24);

        alerDiaglog.setPositiveButton("Yes", (dialogInterface, i) -> {
            Request request = new Request(
                    Common.currentUser.getPhone(),
                    Common.currentUser.getName(),
                    editAddress.getText().toString(),
                    txtTotal.getText().toString(),
                    "0",
                    editComment.getText().toString(),
                    cart
            );
            requests.child(String.valueOf(System.currentTimeMillis())).setValue(request);
            new Database(getBaseContext()).cleanCart();
            Toast.makeText(Cart.this, "Thank for ordering", Toast.LENGTH_SHORT).show();
            finish();
        });
        alerDiaglog.setNegativeButton("NO", (dialogInterface, i) -> dialogInterface.dismiss());
        alerDiaglog.show();
    }

    private void loadListFood() {
        cart = new Database(this).getCarts();
        adapter = new CartAdapter(cart,this);
        recyclerView.setAdapter(adapter);

        int total = 0;
        for (Order order:cart){
            total += (Integer.parseInt(order.getPrice())*(Integer.parseInt(order.getQuantity())));
            Locale locale = new Locale("en","US");
            NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
             txtTotal.setText(fmt.format(total));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        bindingView();
        bindingAction();
    }
}