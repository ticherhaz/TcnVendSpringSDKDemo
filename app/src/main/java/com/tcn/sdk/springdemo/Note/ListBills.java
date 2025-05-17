package com.tcn.sdk.springdemo.Note;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.tcn.sdk.springdemo.R;

import java.util.ArrayList;

public class ListBills extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_bills);

        Intent intent = getIntent();
        ArrayList<String> billListArray = intent.getStringArrayListExtra("NF_Bills");

        ArrayAdapter<String> itemsAdaptor = new
                ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, billListArray);

        ListView listView = findViewById(R.id.bill_list);
        listView.setAdapter(itemsAdaptor);
    }
}
